package org.mipyykko.roboexplorerpc.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import lejos.pc.comm.NXTConnector;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorerpc.logic.Command;
import org.mipyykko.roboexplorerpc.logic.Logic;
import org.mipyykko.roboexplorerpc.model.RobotData;

/**
 * Huolehtii yhteyksistä robotin kanssa.
 * Yhteyslogiikka on saanut suuria vaikutteita esimerkkiprojekteista.
 * 
 * @author mipyykko
 *
 */
public class RoboConnection {

	public enum Status {
		OK, CONNECTION_ERROR, UNKNOWN_ERROR;
	}
	
	private NXTConnector conn;
	private boolean connected;
	private Status status;
	
	private DataInputStream dis;
	private DataOutputStream dos;

	private Reader reader = new Reader();

	private Logic logic;
	
	private boolean isUpdating = false, isSending = false;

	public RoboConnection() {
		this.conn = new NXTConnector();
		this.status = Status.OK;
	}
	
	public void connect() {
		connected = conn.connectTo("btspp://");
		System.out.println("tried connect");
		if (!connected) {
			status = Status.CONNECTION_ERROR;
			return;
		}
		dis = new DataInputStream(conn.getInputStream());
		dos = new DataOutputStream(conn.getOutputStream());
		if (!reader.running) {
			reader.run();
		}
	}

	public void disconnect() {
		try {
			reader.running = false;
			dos.close();
			dis.close();
			conn.close();
			connected = false;
			status = Status.OK;
		} catch (Exception e) {
			status = Status.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * Lähettää komentoja parametreineen robotille.
	 * 
	 */
	public boolean sendData(Command command, float... values) {
		try {
			while (isSending) {
				Thread.sleep(100);
				Thread.yield();
			}
		} catch (Exception e) {}
		
		isSending = true;
		try {
			System.out.print("Sent command " + command.toString() + " with values ");
			dos.writeInt(command.ordinal());
			dos.writeInt(values.length);
			for (float f : values) {
				dos.writeFloat(f);
				System.out.print(f);
			}
			System.out.println();
			dos.flush();
		} catch (Exception e) {
			isSending = false;
			return false;
		}
		isSending = false;
		return true;
	}

	public RobotData receiveBumpData() {
		boolean ok = false;
		Pose pose;
		
		try {
			while (isUpdating || isSending) {
				Thread.sleep(100);
				Thread.yield();
			}
			isUpdating = true;
			pose = new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat());
//			map.setOccupied(scaleCoord((int) pose.getX()), scaleCoord((int) pose.getY()));
			ok = true;
		} catch (Exception e) {
			return null;
		}

		if (ok) {
			RobotData robotData = new RobotData()
				.pose(pose)
				.build();
			isUpdating = false;
			ok = false;
			return robotData;
		}
		
		isUpdating = false;
		return null;
	}

	public RobotData receiveScanData() {
		boolean ok = false;
		Pose pose;
		RangeReadings curReadings;
		List<MCLParticle> curParticles;
		
		try {
			while (isUpdating || isSending) {
				Thread.sleep(100);
				Thread.yield();
			}
			isUpdating = true;
			pose = new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat());
			System.out.println("heading: " + pose.getHeading() + " X " + pose.getX() + " Y " + pose.getY());
			int readingCount = dis.readInt();
			curReadings = new RangeReadings(readingCount);
			for (int i = 0; i < readingCount; i++) {
				RangeReading rr = new RangeReading(dis.readFloat(), dis.readFloat());
				curReadings.set(i, rr);
			}
			int particleCount = dis.readInt();
			curParticles = new ArrayList<MCLParticle>();
			for (int i = 0; i < particleCount; i++) {
				MCLParticle mp = new MCLParticle(new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat()));
				curParticles.add(mp);
			}
			System.out.println("received ok data");
			ok = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			isUpdating = false;
			return null;
		}
		if (ok) {
			RobotData robotData = new RobotData()
				.pose(pose)
				.readings(curReadings)
				.particles(curParticles)
				.build();

			isUpdating = false;
			ok = false;
			return robotData;
		}
		
		isUpdating = false;
		return null;
	}

	public boolean connected() {
		return connected;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setLogic(Logic logic) {
		this.logic = logic;
	}

	public boolean isUpdating() {
		return isUpdating;
	}

	public void quit() {
		reader.running = false;
	}

	/**
	 * Robotilta tulevaa viestiliikennettä hoitava säie.
	 * 
	 * @author mipyykko
	 *
	 */
	class Reader extends Thread {

		boolean running = false;
		
		public void run() {
			running = true;
			while (running) {
				int code = 0;
				boolean ok = false;
				RobotData robotData = null;

				if (!isUpdating && !isSending) {
					try {
						code = dis.readInt();
						Command command = Command.values()[code];
						switch (command) {
							case SEND_DATA:
								robotData = receiveScanData();
								logic.updateMap(robotData);
								logic.decideMove(robotData);
								logic.updateGUI(robotData);
								break;
							case STOP_OBSTACLE:
								System.out.println("obstacle at " + dis.readFloat());
								break;
							case STOP_BUMP:
								robotData = receiveBumpData();
								logic.decideMove(robotData);
								logic.updateMap(robotData);
								System.out.println("bumped");
								break;
							case STOP_STALLED:
								System.out.println("stalled");
								break;
							case QUIT:
								disconnect();
								break;
						}
						while (isSending || isUpdating) {
							Thread.sleep(100);
							Thread.yield();
						}
					} catch (EOFException e) {
						running = false;
					} catch (Exception e) {
						ok = false;
						e.printStackTrace();
					}
				}
			}
		}
	}
}
