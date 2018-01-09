package org.mipyykko.roboexplorer.logic;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.*;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.mapping.NXTNavigationModel;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.model.RoboMap;
import org.mipyykko.roboexplorer.ui.Menu;

/**
 * Pääluokka.
 * 
 * @author mipyykko
 *
 */
public class Explorer {

	private Config config;
	private Menu menu;
	private RoboMap map;
	
	private NXTRegulatedMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;

	int x, y;
	double travelSpeed, rotateSpeed, travelDistance, distanceThreshold;
	final float xsize = 40;
	final float ysize = 40;
	int heading;
	
	private boolean DEBUG;
	
	public Explorer() {
		this.config = new Config();
		this.DEBUG = !config.get("debug").isEmpty();

		if (DEBUG) {
			RConsole.openUSB(30);
		}
		this.menu = new Menu(this.config);
		try {
			this.map = new RoboMap((int) xsize, (int) ysize);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.x = 0;
		this.y = 0;
		this.heading = 0;
	}
	
	/**
	 * Näyttää valikon, alustaa ja käynnistää robotin
	 */
	public void start() {
		while (menu.startMenu() > 0) {
			// nawt
		}
		init();
		run();
	}
	
	private void init() {
		this.leftMotor = new NXTRegulatedMotor(config.getMotorPort("leftMotorPort"));
		this.rightMotor = new NXTRegulatedMotor(config.getMotorPort("rightMotorPort"));
		this.ultrasonicMotor = new NXTRegulatedMotor(config.getMotorPort("ultrasonicMotorPort"));
		this.ultrasonicSensor = new UltrasonicSensor(config.getSensorPort("ultrasonicSensorPort"));
		this.travelSpeed = config.getDouble("travelSpeed");
		this.rotateSpeed = config.getDouble("rotateSpeed");
		this.travelDistance = config.getDouble("travelDistance");
		this.distanceThreshold = config.getDouble("distanceThreshold");
	}
	
	private void run() {
		DifferentialPilot pilot = new DifferentialPilot(config.getDouble("wheelDiameter"), config.getDouble("trackWidth"), leftMotor, rightMotor, false);
		// TODO: replace with MCLPoseProvider
		OdometryPoseProvider poseProvider = new OdometryPoseProvider(pilot);
		// start from the center
		Pose pose = new Pose((float) ((xsize / 2) * travelDistance), (float) ((ysize / 2) * travelDistance), 0);
		poseProvider.setPose(pose);
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		RangeScanner scanner = new RotatingRangeScanner(ultrasonicMotor, ultrasonicSensor);
		scanner.setAngles(new float[]{90, 0, -90});
		
		while (!Button.ESCAPE.isPressed()) {
			RangeReadings readings = scanner.getRangeValues();

			double left = readings.getRange(0);
			double forward = readings.getRange(1);
			double right = readings.getRange(2);

			x = (int) (poseProvider.getPose().getX() / travelDistance);
			y = (int) (poseProvider.getPose().getY() / travelDistance);
			heading = (int) poseProvider.getPose().getHeading();

			LCD.clear();
			LCD.drawString("forward: ", 0, 0);
			LCD.drawString("left: ", 0, 1);
			LCD.drawString("right: ", 0, 2);
			LCD.drawString("x, y: ", 0, 3);
			LCD.drawString("heading: ", 0, 4);
			LCD.drawString("poseX: ", 0, 5);
			LCD.drawString("poseY: ", 0, 6);
			LCD.drawInt((int) forward, 10, 0);
			LCD.drawInt((int) left, 10, 1);
			LCD.drawInt((int) right, 10, 2);
			LCD.drawString(x + " " + y, 10, 3);
			LCD.drawString(Double.toString(poseProvider.getPose().getHeading()), 10, 4);
			LCD.drawString(poseProvider.getPose().getX() + "", 7, 5);
			LCD.drawString(poseProvider.getPose().getY() + "", 7, 6);
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// waah
			}
			
			try {
				setData(left, right, forward);
			} catch (Exception e) {
				// ....
			}
		
			Pair[] relatives = getRelatives();
			Pair relativeLeft = relatives[0];
			Pair relativeRight = relatives[1];
			Pair relativeForward = relatives[2];
			
			// can go off the map 
			if (!map.occupied(x + relativeForward.x, y + relativeForward.y)) {
				pilot.travel(5);
			} else if (!map.occupied(x + relativeRight.x, y + relativeRight.y)) {
				pilot.rotate(-90);
			} else if (!map.occupied(x + relativeLeft.x, y + relativeLeft.y)) {
				pilot.rotate(90);
			} else {
				pilot.rotate(180);
			}
			if (DEBUG) {
				RConsole.println(map.toString());
			}
		}
		if (DEBUG) {
			RConsole.close();
		}
	}

	// TODO: horrible!
	class Pair {
		int x, y;
		
		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private Pair[] getRelatives() {
		Pair relativeLeft = new Pair(0, 1);
		Pair relativeRight = new Pair(0, -1);
		Pair relativeForward = new Pair(1, 0);
		
		if (heading >= 0 && heading < 10 || heading < -170) {
			// default
		}
		if (heading >= 80 && heading < 100) {
			relativeLeft = new Pair(1, 0);
			relativeRight = new Pair(-1, 0);
			relativeForward = new Pair(0, -1);
		}
		if (heading > 170 || (heading > -10 && heading < 0)) {
			relativeLeft = new Pair(0, -1);
			relativeRight = new Pair(0, 1);
			relativeForward = new Pair(-1, 0);
		}
		if (heading <= -10 && heading > -30) {
			relativeLeft = new Pair(-1, 0);
			relativeRight = new Pair(1, 0);
			relativeForward = new Pair(0, 1);
		}
		return new Pair[]{relativeLeft, relativeRight, relativeForward};
	}
	
	private void setData(double left, double right, double forward) throws Exception {
		Pair[] relatives = getRelatives();
		Pair relativeLeft = relatives[0];
		Pair relativeRight = relatives[1];
		Pair relativeForward = relatives[2];
		
		map.incCount(x + relativeLeft.x, y + relativeLeft.y);
		map.incCount(x + relativeForward.x, y + relativeForward.y);
		map.incCount(x + relativeRight.x, y + relativeRight.y);
		
		if (left >= 0 && left <= distanceThreshold) {
			map.incMap(x + relativeLeft.x, y + relativeLeft.y);
		} else {
			map.decMap(x + relativeLeft.x, y + relativeLeft.y);
		}
		if (forward >= 0 && forward <= distanceThreshold) {
			map.incMap(x + relativeForward.x, y + relativeForward.y);
		} else {
			map.decMap(x + relativeForward.x, y + relativeForward.y);
		}
		if (right >= 0 && right <= distanceThreshold) {
			map.incMap(x + relativeRight.x, y + relativeRight.y);
		} else {
			map.decMap(x + relativeRight.x, y + relativeRight.y);
		}
	}
}
