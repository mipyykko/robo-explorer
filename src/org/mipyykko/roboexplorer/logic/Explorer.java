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
public class Explorer {

	private Config config;
	private Menu menu;
	
	private NXTRegulatedMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;
	private TouchSensor touchSensor;
	
	private DifferentialPilot pilot;
	private RangeScanner scanner;

	int x, y;
	double travelSpeed, rotateSpeed, travelDistance, distanceThreshold;
	int width;
	int height;
	float heading;
	
	private MCLPoseProvider poseProvider;
	private RangeMap map;
	private RangeReadings rangeReadings;
	
	private RoboConnection connection;
	
	private boolean moveFinished = false;
	
	private boolean DEBUG;
	
	public Explorer() {
		this.config = new Config();
		this.DEBUG = !config.get("debug").isEmpty();
		this.connection = new RoboConnection(this);
		
		LCD.clear();
		LCD.drawString("waiting for bt...    ", 0, 0);
		if (!connection.open()) {
			LCD.drawString("connection error!    ", 0, 0);
			Button.waitForAnyPress();
			System.exit(1);
		}
		
		this.menu = new Menu(this.config);
		this.x = 0;
		this.y = 0;
		this.heading = 0;
	}
	
	/**
	 * Näyttää valikon, alustaa ja käynnistää robotin
	 */
	public void start() {
		while (menu.startMenu() > 0) {}
		init();
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
		this.travelSpeed = config.getDouble("travelSpeed");
		this.rotateSpeed = config.getDouble("rotateSpeed");
		this.travelDistance = config.getDouble("travelDistance");
		this.distanceThreshold = config.getDouble("distanceThreshold");
		this.width = (int) config.getDouble("mapWidth");
		this.height = (int) config.getDouble("mapHeight");
		
		pilot = new DifferentialPilot(
				config.getDouble("wheelDiameter"), 
				config.getDouble("trackWidth"), 
				leftMotor, rightMotor, false);
		scanner = new RotatingRangeScanner(ultrasonicMotor, ultrasonicSensor);
		
		float[] angles = new float[]{90, 45, 0, -45, -90};
		scanner.setAngles(angles);

		/* Robotti ei varsinaisesti käytä tätä karttaa mihinkään
		 * nykyisessä tilassa, mutta MCLPoseProviderille annetaan 
		 * kuitenkin tietyn kokoinen alue jonka rajalla on seinät.
		 * Lokalisaatio paranisi, jos karttaan lisättäisiin seiniä
		 * sen mukaan, miten pc-klientin puolella niiden todennäköiset
		 * paikat selviäisivät.
		 */
		map = new LineMap(new Line[]{
				new Line(0,0, width,0), 
				new Line(width, 0, width, height), 
				new Line(width, height, 0, height),
				new Line(0, height, 0, 0)}, 
				new Rectangle(0, 0, width, height));

		poseProvider = new MCLPoseProvider(pilot, scanner, map, 200, 10);
		
		rangeReadings = new RangeReadings(angles.length);

		poseProvider.setInitialPose(new Pose((float) (width / 2), (float) (height / 2), 0), 1, 1);		

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
		
		if (!rangeReadings.incomplete()) {
			poseProvider.update(rangeReadings);
		}
		
		while (poseProvider.isBusy()) {};
		
		connection.sendData(Command.SEND_DATA, poseProvider, rangeReadings);
	}
	
	private void run() {
		scan();
		
		while (!Button.ESCAPE.isPressed()) {

			while (pilot.isMoving()) {}
			
			if (moveFinished) {
				poseProvider.estimatePose();
				Pose pose = poseProvider.getEstimatedPose();
				
				while (poseProvider.isBusy()) {}
				
				x = (int) (pose.getX() / travelDistance);
				y = (int) (pose.getY() / travelDistance);
				heading = (int) pose.getHeading();
	
				moveFinished = false;
				
				LCD.clear();
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
				e.printStackTrace();
			}
			
		}
		if (DEBUG) {
			RConsole.close();
		}
		connection.sendData(Command.QUIT, 0);
		connection.close();
	}

	/**
	 * Pitää huolen, ettei robotti törmäile ajon aikana aikanaan edessään oleviin
	 * esteisiin. Lähettää tiedon mahdollisista esteistä (tai juuttumisesta) 
	 * tietokoneelle.
	 */
	public void scanAhead() {
		System.out.println("scan ahead");
		
		while (pilot.isMoving() && !pilot.isStalled() && !touchSensor.isPressed()) {
			float fwd = scanner.getRangeFinder().getRange();
			if (fwd < distanceThreshold) {
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
		}		
	}

	/**
	 * Wrapperit robotin liikkumismetodeille.
	 */
	
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
		
		if (angle <= -0.01f || angle >= 0.01f) {
			pilot.rotate(angle);
		}
		pilot.travel(distance, true);
		scanAhead();
		
		moveFinished = true;
	}

	public void back(float distance) {
		moveFinished = false;
		
		pilot.travel(-distance, true);
		
		while (pilot.isMoving()) {}
		
		moveFinished = true;
	}
	
	public void stop() {
		pilot.stop();
		moveFinished = true;
	}

}
