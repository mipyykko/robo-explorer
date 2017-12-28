package org.mipyykko.roboexplorer.logic;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.model.Map;
import org.mipyykko.roboexplorer.ui.Menu;

public class Explorer {

	private Config config;
	private Menu menu;
	private Map map;
	
	private NXTRegulatedMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;

	int x, y, movement;
	final int xsize = 20;
	final int ysize = 20;
	int heading;
	
	public Explorer() {
		this.config = new Config();
		this.menu = new Menu(this.config);
		try {
			this.map = new Map(xsize, ysize);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.x = 0;
		this.y = 0;
		this.movement = 1;
		this.heading = 0;
	}
	
	public void start() {
		while (menu.startMenu() > 0) {
			// nawt
		}
		init();
		run();
	}
	
	private void init() {
		this.leftMotor = new NXTRegulatedMotor(config.getLeftMotorPort());
		this.rightMotor = new NXTRegulatedMotor(config.getRightMotorPort());
		this.ultrasonicMotor = new NXTRegulatedMotor(config.getUltrasonicMotorPort());
		this.ultrasonicSensor = new UltrasonicSensor(config.getUltrasonicSensorPort());
	}
	
	private void run() {
		int ultrasonicMotorStart = ultrasonicMotor.getTachoCount();
		DifferentialPilot dp = new DifferentialPilot(2.1f, 4.4f, leftMotor, rightMotor, false);
		dp.setTravelSpeed(5);
		
		while (!Button.ESCAPE.isPressed()) {
			ultrasonicMotor.rotate(-90);
			ultrasonicMotor.waitComplete();
			double left = ultrasonicSensor.getRange();
			ultrasonicMotor.rotate(90);
			ultrasonicMotor.waitComplete();
			double forward = ultrasonicSensor.getRange();
			ultrasonicMotor.rotate(90);
			ultrasonicMotor.waitComplete();
			double right = ultrasonicSensor.getRange();
			ultrasonicMotor.rotate(-90);

			LCD.clear();
			LCD.drawString("forward: ", 0, 0);
			LCD.drawString("left: ", 0, 1);
			LCD.drawString("right: ", 0, 2);
			LCD.drawString("x, y: ", 0, 3);
			LCD.drawString("heading: ", 0, 4);
			LCD.drawInt((int) forward, 10, 0);
			LCD.drawInt((int) left, 10, 1);
			LCD.drawInt((int) right, 10, 2);
			LCD.drawString(x + " " + y, 10, 3);
			LCD.drawInt(heading, 10, 4);
			
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
			
			if (forward > 10 && forward < 255) {
				dp.travel(5);
//				leftMotor.setSpeed(60);
//				rightMotor.setSpeed(60);
//				leftMotor.rotateTo(movement * 360, true);
//				rightMotor.rotateTo(movement * 360, true);
//				while (leftMotor.isMoving() && rightMotor.isMoving()) {
//					Thread.yield();
//				}
				switch (heading) {
					case 0: 
						y++;
						break;
					case 180:
						y--;
						break;
					case 90:
						x++;
						break;
					case 270:
						x--;
						break;
					default:
						// what?
				}
			} else if (x < xsize && right > 10 && right < 255) {
				changeHeading(90);
				dp.rotate(90);
//				leftMotor.setSpeed(60);
//				leftMotor.rotateTo(90);
				// turn right
			} else if (x > 0 && left > 10 && left < 255) {
				changeHeading(-90);
				dp.rotate(-90);
//				rightMotor.setSpeed(60);
//				rightMotor.rotateTo(90);
				// turn left
			} else {
				changeHeading(180);
				dp.rotate(-180);
//				rightMotor.setSpeed(60);
//				rightMotor.rotateTo(180);
				// turn back
			}
		}
	}
	
	private void setData(double left, double right, double forward) throws Exception {
		if (x > 0) {
			map.setValue(x - 1, y, (int) left);
		}
		if (x < xsize) {
			map.setValue(x + 1, y, (int) right);
		}
		if (y < ysize) {
			map.setValue(x, y+1, (int) forward);
		}
	}
	
	private void changeHeading(int h) {
		heading += h;
		if (heading < 0) { 
			heading = 360 - heading;
		}
		if (heading > 360) {
			heading = heading - 360;
		}
	}
}
