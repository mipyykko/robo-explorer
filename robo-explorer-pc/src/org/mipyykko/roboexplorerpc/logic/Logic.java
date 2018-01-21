package org.mipyykko.roboexplorerpc.logic;

import java.util.HashSet;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorerpc.conn.RoboConnection;
import org.mipyykko.roboexplorerpc.gui.GUI;
import org.mipyykko.roboexplorerpc.model.RobotData;
import org.mipyykko.roboexplorerpc.model.RobotMap;

/**
 * Robotin ja klientin logiikkaa.
 * On saanut suuria vaikutteita esimerkkiprojekteista.
 * 
 * @author lego
 *
 */
public class Logic {

	public enum Status {
		OK, CONNECTION_ERROR, UNKNOWN_ERROR;
	}
	
	private RoboConnection connection;
	private RobotMap map;
	
	private GUI gui;
	
	private int width = 100, height = 100;
	private int cellSize = 2;

	public Logic(GUI gui) {
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
	
	/** 
	 * Päivitetään karttaa saadun sensoridatan perusteella.
	 * 
	 */
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
			float theta = startAngle;
			
			HashSet<Point> updated = new HashSet<Point>();
			
			/* Käydään läpi suorat robotin nykyisestä sijainnista
			 * 20 asteen mittaiselle ympyrän kaarelle. 
			 */
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
								
				for (int i = 0; i <= longest; i++) {
					if (x >= 0 && y >= 0 && x <= map.getWidth() && y <= map.getHeight() &&
						!updated.contains(new Point(x, y))) {
						updated.add(new Point(x, y));
						if (x != readingX && y != readingY) {
							map.setFree(x, y);
						} else {
							map.setOccupied(x, y);
						}
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
	
	/**
	 * Päätetään robotin seuraava siirto.
	 */
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
		
		/*
		 * Tällä hetkellä oletusarvoisena on pyrkiä menemään eteenpäin, sitten
		 * oikealle ja sitten vasemmalle. 
		 */
		for (float angle : angles) {
			float reading = rangeReadings.getRange(angle);
			if (reading != -1) {
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
	
	public RoboConnection getConnection() {
		return connection;
	}
	
}
