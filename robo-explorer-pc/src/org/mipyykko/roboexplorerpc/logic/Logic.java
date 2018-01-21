package org.mipyykko.roboexplorerpc.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashSet;

import lejos.geom.Point;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorerpc.conn.RoboConnection;
import org.mipyykko.roboexplorerpc.gui.GUI;
import org.mipyykko.roboexplorerpc.model.RobotData;
import org.mipyykko.roboexplorerpc.model.RobotMap;

public class Logic /*extends Observable*/ {

	public enum Status {
		OK, CONNECTION_ERROR, UNKNOWN_ERROR;
	}
	
//	private NXTConnector conn;
//	private boolean connected;
//	private Status status;
//	
//	private DataInputStream dis;
//	private DataOutputStream dos;
//	
//	private RangeReadings curReadings;
//	private Pose pose;
//	private List<MCLParticle> curParticles;

	private RoboConnection connection;
	private RobotMap map;
	
//	private Reader reader = new Reader();
	
//	private boolean isUpdating = false, isSending = false;
	
	private GUI gui;
	
	private int width = 100, height = 100;
	private int cellSize = 2;

	public Logic(GUI gui) {
//		this.conn = new NXTConnector();
//		this.status = Status.OK;
		this.connection = new RoboConnection();
		connection.setLogic(this);
		this.gui = gui;
		this.map = new RobotMap(width, height);
		gui.setMap(map);
	}
		
	public void setGUI(GUI gui) {
		this.gui = gui;
	}
	
	public int scaleCoord(int coord) {
		return coord / cellSize + 20;
	}
	
	public boolean updateMap(RobotData robotData) {
		if (robotData == null) return false;
		
		RangeReadings rangeReadings = robotData.getReadings();
		Pose currentPose = robotData.getPose();
		
		System.out.println("updating map...");
		
		for (RangeReading rr : rangeReadings) {
			System.out.format("%f: %f\n", rr.getAngle(), rr.getRange());
			if (rr.getRange() == -1) continue;
			int curX = scaleCoord((int) currentPose.getX());
			int curY = scaleCoord((int) currentPose.getY());

			float curHeading = currentPose.getHeading() + 180;
			float startAngle = (float) Math.toRadians(curHeading + rr.getAngle() - 10);
			float endAngle = (float) Math.toRadians(curHeading + rr.getAngle() + 10);

			System.out.format("start %f end %f\n", startAngle, endAngle);
			HashSet<Point> updated = new HashSet<Point>();
			float theta = startAngle;
			
			while (theta <= endAngle) {
				System.out.format("theta %f\n", theta);
				int readingX = scaleCoord((int) (curX + (rr.getRange() / cellSize) * Math.sin(theta)));
				int readingY = scaleCoord((int) (curY + (rr.getRange() / cellSize) * Math.cos(theta)));

				theta += 0.05f;
				
				System.out.format("current %d %d, reading %d %d\n", curX, curY, readingX, readingY);
				
				// Bresenham's line algorithm implementation source:
				// https://stackoverflow.com/questions/11678693/all-cases-covered-bresenhams-line-algorithm
				
				int w = readingX - curX;
				int h = readingY - curY;
				int dx1 = (w < 0 ? -1 : (w > 0 ? 1 : 0));
				int dy1 = (h < 0 ? -1 : (h > 0 ? 1 : 0));
				int dx2 = (w < 0 ? -1 : (w > 0 ? 1 : 0));
				int dy2 = 0;
				
				int longest = Math.abs(w);
				int shortest = Math.abs(h);
				
				if (longest <= shortest) {
					longest = Math.abs(h);
					shortest = Math.abs(w);
					dy2 = (h < 0 ? -1 : (h > 0 ? 1 : 0));
					dx2 = 0;
				}
				
				int num = longest / 2;
				
				int x = curX;
				int y = curY;
				
				System.out.format("before line draw, longest %d, shortest %d, d1: %d, %d d2: %d, %d\n",
						longest, shortest, dx1, dy1, dx2, dy2);
				
				for (int i = 0; i <= longest; i++) {
					if (x >= 0 && y >= 0 && x <= map.getWidth() && y <= map.getHeight() &&
						!updated.contains(new Point(x, y))) {
						updated.add(new Point(x, y));
						float curValue = map.getValue(x, y);
						System.out.print(x + "," + y + " curValue " + curValue + " ");
						if (x != readingX && y != readingY) {
							map.setFree(x, y);
						} else {
							map.setOccupied(x, y);
						}
						System.out.println("nextValue " + map.getValue(x, y));
					}
					num += shortest;
					if (num >= longest) {
						num -= longest;
						x += dx1;
						y += dy1;
					} else {
						x += dx2;
						y += dy2;
					}
				}
				
			}
		}
		return true;
	}
	
	public boolean decideMove(RobotData robotData) {
		if (robotData == null) return false;

		Pose pose = robotData.getPose();
		int curX = scaleCoord((int) pose.getX());
		int curY = scaleCoord((int) pose.getY());
		float curHeading = pose.getHeading() + 180;
		
		RangeReadings rangeReadings = robotData.getReadings();
		
		if (rangeReadings == null) {
			return connection.sendData(Command.BACK, 10);
		}
		float angles[] = new float[]{0, -90, 90};
		
		for (float angle : angles) {
			float reading = rangeReadings.getRange(angle);
			if (reading != -1) {
				System.out.format("angle %f ok with %f?\n", angle, reading);
				int futureX = scaleCoord((int) (curX + Math.cos(Math.toRadians(curHeading + angle)) * cellSize));
				int futureY = scaleCoord((int) (curY + Math.sin(Math.toRadians(curHeading + angle)) * cellSize));
				if (map.isFree(futureX, futureY) && reading > 10) {
					return connection.sendData(Command.ROTATE_MOVE, angle, 10);
				}
			}
		}
		
		RangeReading maxReading = null;
		
		for (int i = 0; i < rangeReadings.getNumReadings(); i++) {
			RangeReading r = rangeReadings.get(i);
			System.out.print(r.getAngle() + ": " + r.getRange() + " ");
			if (maxReading == null || (maxReading != null && r.getRange() > maxReading.getRange())) {
				maxReading = r;
			}
		}
		
		if (maxReading == null || maxReading.getRange() == -1) {
			return connection.sendData(Command.BACK, 10);
		}
		
		float toRotate = maxReading.getAngle();
		return connection.sendData(Command.ROTATE_MOVE, toRotate, 10);
	}
	
	public void updateGUI(RobotData data) {
		gui.update(data);
	}
	
//	public boolean receiveScanData() {
//		boolean ok = false;
//		try {
//			while (isUpdating || isSending) {
//				Thread.sleep(100);
//				Thread.yield();
//			}
//			isUpdating = true;
//			pose = new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat());
//			System.out.println("heading: " + pose.getHeading() + " X " + pose.getX() + " Y " + pose.getY());
//			int readingCount = dis.readInt();
//			curReadings = new RangeReadings(readingCount);
//			for (int i = 0; i < readingCount; i++) {
//				RangeReading rr = new RangeReading(dis.readFloat(), dis.readFloat());
//				curReadings.set(i, rr);
//				System.out.print(rr.getAngle() + ": " + rr.getRange()+ " ");
//			}
//			int particleCount = dis.readInt();
//			curParticles = new ArrayList<MCLParticle>();
//			for (int i = 0; i < particleCount; i++) {
//				MCLParticle mp = new MCLParticle(new Pose(dis.readFloat(), dis.readFloat(), dis.readFloat()));
//				curParticles.add(mp);
//			}
//			System.out.println("received particles " + particleCount);
//			System.out.println();
//			/*setChanged();
//			notifyObservers(robotData);*/
//			System.out.println("received ok data");
//			ok = true;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//			isUpdating = false;
//			return false;
//		}
//		if (ok) {
//			RobotData robotData = new RobotData()
//				.pose(pose)
//				.readings(curReadings)
//				.particles(curParticles)
//				.build();
//			updateMap(robotData);
//			decideMove(robotData);
//			gui.update(robotData);
//			try {
//				while (isSending) {
//					Thread.sleep(100);
//					Thread.yield();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			isUpdating = false;
//			ok = false;
//			return true;
//		}
//		return false;
//	}

	public RoboConnection getConnection() {
		return connection;
	}
	
}
