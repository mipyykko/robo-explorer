package org.mipyykko.roboexplorer.logic;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.TouchSensor;
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
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.FusorDetector;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.robotics.objectdetection.TouchFeatureDetector;

import org.mipyykko.roboexplorer.comm.Command;
import org.mipyykko.roboexplorer.comm.RoboConnection;
import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.ui.Menu;

/**
 * Pääluokka.
 * 
 * @author mipyykko
 *
 */
public class Explorer implements FeatureListener {

	private Config config;
	private Menu menu;
	
	private NXTRegulatedMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;
	private TouchSensor touchSensor;
	private LightSensor lightSensor;
	
	private DifferentialPilot pilot;
	private RangeScanner scanner;

	int x, y;
	double travelSpeed, rotateSpeed, travelDistance, distanceThreshold;
	final int xsize = 40;
	final int ysize = 40;
	float heading;
	
	private MCLPoseProvider poseProvider;
	private MCLParticleSet particles;
	private RangeMap map;
	private RangeReadings rangeReadings;
	
	private RangeReading maxReading = null;
	
	private RoboConnection connection;
	
	private boolean moveFinished = false;
	
	private boolean DEBUG;
	
	public Explorer() {
		this.config = new Config();
		this.DEBUG = !config.get("debug").isEmpty();
		this.connection = new RoboConnection(this);
		
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
	
	/**
	 * Alustaa moottorit ja sensorit
	 */
	
	private void init() {
		this.leftMotor = new NXTRegulatedMotor(config.getMotorPort("leftMotorPort"));
		this.rightMotor = new NXTRegulatedMotor(config.getMotorPort("rightMotorPort"));
		this.ultrasonicMotor = new NXTRegulatedMotor(config.getMotorPort("ultrasonicMotorPort"));
		this.ultrasonicSensor = new UltrasonicSensor(config.getSensorPort("ultrasonicSensorPort"));
		this.touchSensor = new TouchSensor(config.getSensorPort("touchSensorPort"));
		this.lightSensor = new LightSensor(config.getSensorPort("lightSensorPort"));
		this.travelSpeed = config.getDouble("travelSpeed");
		this.rotateSpeed = config.getDouble("rotateSpeed");
		this.travelDistance = config.getDouble("travelDistance");
		this.distanceThreshold = config.getDouble("distanceThreshold");

		pilot = new DifferentialPilot(config.getDouble("wheelDiameter"), config.getDouble("trackWidth"), leftMotor, rightMotor, false);
		scanner = new RotatingRangeScanner(ultrasonicMotor, ultrasonicSensor);
		
		float[] angles = new float[]{90, 45, 0, -45, -90};
		scanner.setAngles(angles);

//		FeatureDetector ultraDetector = new RangeFeatureDetector(scanner.getRangeFinder(), 10, 500);
//		FeatureDetector bumperDetector = new TouchFeatureDetector(touchSensor);
//		
//		FusorDetector detector = new FusorDetector();
//		detector.addDetector(ultraDetector);
//		detector.addDetector(bumperDetector);
//		detector.addListener(this);
		
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

		while (poseProvider.isBusy()) {}
		
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		pilot.setAcceleration(10);
	}
	
	/**
	 * Skannaa ympäristöään annettujen asetusten perusteella ja lähettää tiedot koneelle
	 */
	private void scan() {
		rangeReadings = scanner.getRangeValues();
		
		maxReading = null;
		
//		for (int i = 0; i < rangeReadings.getNumReadings(); i++) {
//			RangeReading r = rangeReadings.get(i);
//			System.out.print(r.getAngle() + ": " + r.getRange() + " ");
//			if (maxReading == null || (maxReading != null && r.getRange() > maxReading.getRange())) {
//				maxReading = r;
//			}
//		}
		
		System.out.println();

		if (!rangeReadings.incomplete()) {
			System.out.println("got ok readings");
			poseProvider.update(rangeReadings);
		}
		
		while (poseProvider.isBusy()) {};
		
		connection.sendData(Command.SEND_DATA, poseProvider, rangeReadings);
	}
	
	private void run() {
		System.out.println("running");
		
//		poseProvider.generateParticles();
//		System.out.println("particles generated");
		
		heading = 90; // magic number?
		
		scan();
		
		while (!Button.ESCAPE.isPressed()) {
			// System.out.println("in the loop");
			
//			if (maxReading == null || maxReading.getRange() == -1) {
//				pilot.rotate(-180 + (float) Math.random() * 360);
//				continue;
//			}
//
////			if (!rangeReadings.incomplete()) {
////				if (heading > 0) {
////					while (heading > 22.5) heading -= 45;
////					correction = -heading;
////				} else {
////					heading = -heading;
////					while (heading > 22.5) heading -= 45;
////					correction = heading;
////				}
////				System.out.println("correction: " + correction);
////			}
//			float toRotate = maxReading.getAngle();
//			pilot.rotate(toRotate);
//			pilot.travel(maxReading.getRange() / 4, true);
			
//			while (pilot.isMoving() && !pilot.isStalled()) {
//				float fwd = scanner.getRangeFinder().getRange();
//				if (fwd < 10) {
//					System.out.println("about to hit something!");
//					pilot.stop();
//					
//					break;
//				}
//			}
//			heading += toRotate;

//			if (pilot.isStalled()) {
//				pilot.travel(-scanner.getRangeFinder().getRange());
//			}
			// test for 90-degree turns
//			pilot.travel(50 / 2.54);
//			pilot.rotate(-90, false);
//			while (pilot.isMoving()) {}

			while (pilot.isMoving()) {}
			
			if (moveFinished) {
				poseProvider.estimatePose();
				Pose pose = poseProvider.getEstimatedPose();
				
				while (poseProvider.isBusy()) {}
				
	//			Button.waitForAnyPress();
	
	//			heading -= 90;
	
	//			while (heading < -180) heading += 360;
	//			while (heading > 180) heading -= 360;
	//			
	////			System.out.println("expected heading: " + heading);
	////			System.out.println("got heading: " + pose.getHeading());
	////			Button.waitForAnyPress();
	//			
	//			float correction = heading - pose.getHeading();
	//			while (correction < -180) correction += 360;
	//			while (correction > 180) correction -= 360;
	//			pilot.rotate(correction);
	//
	//			while (pilot.isMoving()) {}
				
				x = (int) (pose.getX() / travelDistance);
				y = (int) (pose.getY() / travelDistance);
				heading = (int) pose.getHeading();
	
				moveFinished = false;
				
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
				
				scan();
			}
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// waah
			}
			
		}
		if (DEBUG) {
			RConsole.close();
		}
		connection.sendData(Command.QUIT, 0);
		connection.close();
	}

	public void scanAhead() {
		while (pilot.isMoving() && !pilot.isStalled() && !touchSensor.isPressed()) {
			float fwd = scanner.getRangeFinder().getRange();
			if (fwd < 10) {
				System.out.println("about to hit something!");
				pilot.stop();
				connection.sendData(Command.STOP_OBSTACLE, fwd);
				break;
			}
			if (touchSensor.isPressed()) {
				break;
			}
		}

		if (touchSensor.isPressed()) {
			pilot.stop();
			poseProvider.estimatePose();
			
			while(poseProvider.isBusy()) {}
			
			connection.sendData(Command.STOP_BUMP, poseProvider);
			return;
		}
		
		if (pilot.isStalled()) {
			connection.sendData(Command.STOP_STALLED, 0);
			pilot.travel(-scanner.getRangeFinder().getRange());
			while (pilot.isMoving()) {};
		}		
	}

	public void rotate(float angle) {
		moveFinished = false;
		
		pilot.rotate(angle);
		moveFinished = true;
	}
	
	public void travel(float distance) {
		moveFinished = false;
		
		pilot.travel(distance, true);

		scanAhead();
		
		moveFinished = true;
	}
	
	public void rotateTravel(float angle, float distance) {
		moveFinished = false;
		
		if (angle <= -0.01f || angle >= 0.01f) pilot.rotate(angle);
		pilot.travel(distance, true);
		scanAhead();
		moveFinished = true;
	}

	public void back(float distance) {
		moveFinished = false;
		
		pilot.travel(-distance, false);
		
		moveFinished = true;
	}
	public void stop() {
		pilot.stop();
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		if (!pilot.isMoving()) {
			return;
		}
		
		detector.enableDetection(false);

		pilot.stop();

		RangeReadings rrs = feature.getRangeReadings();
		System.out.print("detected something at ");
		for (RangeReading rr : rrs) {
			System.out.print(rr.getAngle() + ": " + rr.getRange() + ", ");
		}
		System.out.println();
		Button.waitForAnyPress();
		detector.enableDetection(true);
	}
}
