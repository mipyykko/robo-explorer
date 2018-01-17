package org.mipyykko.roboexplorerpc.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import lejos.pc.comm.NXTConnector;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorerpc.gui.GUI;
import org.mipyykko.roboexplorerpc.model.RobotData;
import org.mipyykko.roboexplorerpc.model.RobotMap;

public class Logic /*extends Observable*/ {

	public enum Status {
		OK, CONNECTION_ERROR, UNKNOWN_ERROR;
	}
	
	private NXTConnector conn;
	private boolean connected;
	private Status status;
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private RangeReadings curReadings;
	private Pose pose;
	private List<MCLParticle> curParticles;
	
	private RobotMap map;
	
	private Reader reader = new Reader();
	
	private boolean isUpdating = false, isSending = false;
	
	private GUI gui;
	
	private int width = 100, height = 100;
	
	public Logic(GUI gui) {
		this.conn = new NXTConnector();
		this.status = Status.OK;
		this.gui = gui;
		gui.setMap(new RobotMap(width, height));
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
//		try {
//			Thread.sleep(100);
//			String received = dis.readUTF();
//			if (received.substring(0,5).equals("hello")) {
//				dos.writeUTF("hullo");
//				dos.flush();
//				reader.run();
//				Thread.yield();
//			} else {
//				status = Status.CONNECTION_ERROR;
//				return;
//			}
//		} catch (Exception e) {
//			status = Status.CONNECTION_ERROR;
//		}
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
	
	public void run() {
		
	}
	
	public boolean connected() {
		return connected;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public boolean isUpdating() {
		return isUpdating;
	}
	
	public void setGUI(GUI gui) {
		this.gui = gui;
	}
	
	public void quit() {
		reader.running = false;
	}
	
	public boolean decideMove(RobotData robotData) {
		if (robotData == null) return false;

		RangeReadings rangeReadings = robotData.getReadings();
		RangeReading maxReading = null;
		
		for (int i = 0; i < rangeReadings.getNumReadings(); i++) {
			RangeReading r = rangeReadings.get(i);
			System.out.print(r.getAngle() + ": " + r.getRange() + " ");
			if (maxReading == null || (maxReading != null && r.getRange() > maxReading.getRange())) {
				maxReading = r;
			}
		}
		
		if (maxReading == null || maxReading.getRange() == -1) {
			return sendData(Command.ROTATE, -180 + (float) Math.random() * 360);
		}
		
		float toRotate = maxReading.getAngle();
		return sendData(Command.ROTATE_MOVE, toRotate, maxReading.getRange() / 4);
	}
	
	public boolean sendData(Command command, float... values) {
		try {
			while (isSending) {
				Thread.sleep(100);
				Thread.yield();
			}
		} catch (Exception e) {
			
		}
		
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

	public boolean receiveData() {
		boolean ok = false;
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
				System.out.print(rr.getAngle() + ": " + rr.getRange()+ " ");
			}
			int particleCount = dis.readInt();
			curParticles = new ArrayList<MCLParticle>();
			for (int i = 0; i < particleCount; i++) {
				MCLParticle mp = new MCLParticle(new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat()));
				curParticles.add(mp);
			}
			System.out.println("received particles " + particleCount);
			System.out.println();
			/*setChanged();
			notifyObservers(robotData);*/
			System.out.println("received ok data");
			ok = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		if (ok) {
			RobotData robotData = new RobotData()
				.pose(pose)
				.readings(curReadings)
				.particles(curParticles)
				.build();
			decideMove(robotData);
			gui.update(robotData);
			try {
				while (isSending) {
					Thread.sleep(100);
					Thread.yield();
				}
			} catch (Exception e) {}
			
			isUpdating = false;
			ok = false;
		}
		return false;
	}

	class Reader extends Thread {

		boolean running = false;
		

		public void run() {
			running = true;
			while (running) {
				int code = 0;
				boolean ok = false;
				if (!isUpdating && !isSending) {
					try {
						code = dis.readInt();
						Command command = Command.values()[code];
						switch (command) {
							case SEND_DATA:
								while (isSending || isUpdating) {
									Thread.sleep(100);
								}
								receiveData();
								break;
							case STOP_OBSTACLE:
								System.out.println("obstacle at " + dis.readFloat());
								break;
							case QUIT:
								disconnect();
								break;
						}
					} catch (Exception e) {
						ok = false;
					}
				}
			}
		}
	}
	
	
}
