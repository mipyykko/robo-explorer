package org.mipyykko.roboexplorer.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorer.comm.RoboConnection;
import org.mipyykko.roboexplorer.config.Config;
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
	
	private NXTRegulatedMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;

	private DifferentialPilot pilot;
	private RangeScanner scanner;
	
	int x, y;
	double travelSpeed, rotateSpeed, travelDistance, distanceThreshold;
	final int xsize = 40;
	final int ysize = 40;
	int heading;
	
	private MCLPoseProvider poseProvider;
	private MCLParticleSet particles;
	private RangeMap map;
	private RangeReadings rangeReadings;
	
	private RangeReading maxReading = null;
	
	private RoboConnection connection;
	
	private boolean DEBUG;
	
	public Explorer() {
		this.config = new Config();
		this.DEBUG = !config.get("debug").isEmpty();
		this.connection = new RoboConnection();
		
//		if (DEBUG) {
//			RConsole.open();
//			System.setOut(RConsole.getPrintStream());
//			System.out.println("Here!");
//		}
		LCD.clear();
		LCD.drawString("waiting for bt...", 0, 0);
		if (!connection.open()) {
			LCD.drawString("connection error!", 0, 0);
			Button.waitForAnyPress();
			System.exit(1);
		}
		
		this.menu = new Menu(this.config);
//		try {
//			this.map = new RoboMap((int) xsize, (int) ysize);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
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
		System.out.println("out of the menu");
		init();
		System.out.println("after init");
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

		pilot = new DifferentialPilot(config.getDouble("wheelDiameter"), config.getDouble("trackWidth"), leftMotor, rightMotor, false);
		scanner = new RotatingRangeScanner(ultrasonicMotor, ultrasonicSensor);
		float[] angles = new float[]{90, 45, 0, -45, -90};
		scanner.setAngles(angles);
	
		map = new LineMap(new Line[]{
				new Line(0,0, xsize,0), 
				new Line(xsize, 0, xsize, ysize), 
				new Line(xsize, ysize, 0, ysize),
				new Line(0, ysize, 0, 0)}, 
				new Rectangle(0, 0, xsize, ysize));
		System.out.println("new map");
		poseProvider = new MCLPoseProvider(pilot, scanner, map, 200, 10);
		System.out.println("pose provider");
		
		rangeReadings = new RangeReadings(angles.length);

		poseProvider.setInitialPose(new Pose((float) (xsize / 2), (float) (ysize / 2), 0), 1, 1);
		particles = poseProvider.getParticles();

		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		pilot.setAcceleration(40);
	}
	
	private void scan() {
		rangeReadings = scanner.getRangeValues();
		
		maxReading = null;
		
		for (int i = 0; i < rangeReadings.getNumReadings(); i++) {
			RangeReading r = rangeReadings.get(i);
			System.out.print(r.getAngle() + ": " + r.getRange() + " ");
			if (maxReading == null || (maxReading != null && r.getRange() > maxReading.getRange())) {
				maxReading = r;
			}
		}
		
		System.out.println();

		if (!rangeReadings.incomplete()) {
			System.out.println("got ok readings");
			poseProvider.update(rangeReadings);
		}
		
		while (poseProvider.isBusy());
		
		connection.sendData(poseProvider, rangeReadings);
	}
	
	private void run() {
		System.out.println("running");
		
//		poseProvider.generateParticles();
//		System.out.println("particles generated");
		
		while (!Button.ESCAPE.isPressed()) {
			System.out.println("in the loop");
			
			scan();
			
			if (maxReading == null || maxReading.getRange() == -1) {
				pilot.rotate(-180 + (float) Math.random() * 360);
				continue;
			}

			float correction = 0;
			if (!rangeReadings.incomplete()) {
				if (heading > 0) {
					while (heading > 22.5) heading -= 45;
					correction = -heading;
				} else {
					heading = -heading;
					while (heading > 22.5) heading -= 45;
					correction = heading;
				}
				System.out.println("correction: " + correction);
			}
			pilot.rotate(maxReading.getAngle() + correction);
			pilot.travel(maxReading.getRange() / 4, true);
			while (pilot.isMoving() && !pilot.isStalled()) {
				float fwd = scanner.getRangeFinder().getRange();
				if (fwd < 10) {
					System.out.println("about to hit something!");
					pilot.stop();
					break;
				}
			}
			if (pilot.isStalled()) {
				pilot.travel(-scanner.getRangeFinder().getRange());
			}
			
			poseProvider.estimatePose();
			Pose pose = poseProvider.getEstimatedPose();
			
			x = (int) (pose.getX() / travelDistance);
			y = (int) (pose.getY() / travelDistance);
			heading = (int) pose.getHeading();

			LCD.clear();
			LCD.drawString("forward: ", 0, 0);
			LCD.drawString("left: ", 0, 1);
			LCD.drawString("right: ", 0, 2);
			LCD.drawString("x, y: ", 0, 3);
			LCD.drawString("heading: ", 0, 4);
			LCD.drawString("poseX: ", 0, 5);
			LCD.drawString("poseY: ", 0, 6);
			LCD.drawString(x + " " + y, 10, 3);
			LCD.drawString(Double.toString(heading), 10, 4);
			LCD.drawString(pose.getX() + "", 7, 5);
			LCD.drawString(pose.getY() + "", 7, 6);
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// waah
			}
			
		}
		if (DEBUG) {
			RConsole.close();
		}
		connection.close();
	}

	// TODO: horrible!
//	class Pair {
//		int x, y;
//		
//		Pair(int x, int y) {
//			this.x = x;
//			this.y = y;
//		}
//	}
//	
//	private Pair[] getRelatives() {
//		Pair relativeLeft = new Pair(0, 1);
//		Pair relativeRight = new Pair(0, -1);
//		Pair relativeForward = new Pair(1, 0);
//		
//		if (heading >= 0 && heading < 10 || heading < -170) {
//			// default
//		}
//		if (heading >= 80 && heading < 100) {
//			relativeLeft = new Pair(1, 0);
//			relativeRight = new Pair(-1, 0);
//			relativeForward = new Pair(0, -1);
//		}
//		if (heading > 170 || (heading > -10 && heading < 0)) {
//			relativeLeft = new Pair(0, -1);
//			relativeRight = new Pair(0, 1);
//			relativeForward = new Pair(-1, 0);
//		}
//		if (heading <= -10 && heading > -30) {
//			relativeLeft = new Pair(-1, 0);
//			relativeRight = new Pair(1, 0);
//			relativeForward = new Pair(0, 1);
//		}
//		return new Pair[]{relativeLeft, relativeRight, relativeForward};
//	}
//	
//	private void setData(double left, double right, double forward) throws Exception {
//		Pair[] relatives = getRelatives();
//		Pair relativeLeft = relatives[0];
//		Pair relativeRight = relatives[1];
//		Pair relativeForward = relatives[2];
//		
//		map.incCount(x + relativeLeft.x, y + relativeLeft.y);
//		map.incCount(x + relativeForward.x, y + relativeForward.y);
//		map.incCount(x + relativeRight.x, y + relativeRight.y);
//		
//		if (left >= 0 && left <= distanceThreshold) {
//			map.incMap(x + relativeLeft.x, y + relativeLeft.y);
//		} else {
//			map.decMap(x + relativeLeft.x, y + relativeLeft.y);
//		}
//		if (forward >= 0 && forward <= distanceThreshold) {
//			map.incMap(x + relativeForward.x, y + relativeForward.y);
//		} else {
//			map.decMap(x + relativeForward.x, y + relativeForward.y);
//		}
//		if (right >= 0 && right <= distanceThreshold) {
//			map.incMap(x + relativeRight.x, y + relativeRight.y);
//		} else {
//			map.decMap(x + relativeRight.x, y + relativeRight.y);
//		}
//	}
}
