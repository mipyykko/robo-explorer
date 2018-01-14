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
	
	private Reader reader = new Reader();
	
	private boolean isUpdating = false;
	
	private GUI gui;
	
	public Logic(GUI gui) {
		this.conn = new NXTConnector();
		this.status = Status.OK;
		this.gui = gui;
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
	
//	public void run() {
//	}
	
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
	
	class Reader extends Thread {

		boolean running = false;
		
		public void run() {
			running = true;
			while (running) {
				int code = 0;
				boolean ok = false;
				if (!isUpdating) {
					try {
						code = dis.readInt();
	//					if (code == 1) {
							isUpdating = true;
							pose = new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat());
							System.out.println("heading: " + pose.getHeading() + " X " + pose.getX() + " Y " + pose.getY());
							int readingCount = dis.readInt();
							curReadings = new RangeReadings(readingCount);
							for (int i = 0; i < readingCount; i++) {
								RangeReading rr = new RangeReading(dis.readFloat(), dis.readFloat());
								curReadings.add(rr);
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
							ok = true;
							isUpdating = false;
							/*setChanged();
							notifyObservers(robotData);*/
							System.out.println("received ok data");
	//					}
					} catch (Exception e) {
						ok = false;
					}
					if (ok) {
						RobotData robotData = new RobotData()
							.pose(pose)
							.readings(curReadings)
							.particles(curParticles)
							.build();
						gui.update(robotData);
					}
				}
//				try {
//					Thread.sleep(100);
////					Thread.yield();
//				} catch (Exception e) {
//					System.out.println("hmm?");
//				}
			}
		}
	}
	
	
}
