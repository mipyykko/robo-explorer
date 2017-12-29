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
		DifferentialPilot dp = new DifferentialPilot(config.getWheelDiameter(), config.getTrackWidth(), leftMotor, rightMotor, false);
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
			
			if (forward > 10 && forward < 255 && map.getValueFromHeading(x, y, heading) > 10) {
				dp.travel(5);

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
			} 
			// TODO: relativeLeft & relativeRight here, now it goes off the map in some cases
			else if (x < xsize && right > 10 && right < 255 && map.getValueFromHeading(x, y, heading + 90) > 10) {
				changeHeading(90);
				dp.rotate(-90); // positive = left
				// turn right
			} else if (x > 0 && left > 10 && left < 255 && map.getValueFromHeading(x, y, heading - 90) > 10) {
				changeHeading(-90);
				dp.rotate(90);
				// turn left
			} else {
				changeHeading(180);
				dp.rotate(-180);
				// turn back
			}
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
	
	private void setData(double left, double right, double forward) throws Exception {
		
		Pair relativeLeft, relativeRight, relativeForward;
		int relativeX = x;
		int relativeY = y;
		int relativeXsize = xsize;
		int relativeYsize = ysize;
		
		switch (heading) {
			case 0:
			default:
				relativeLeft = new Pair(-1, 0);
				relativeRight = new Pair(1, 0);
				relativeForward = new Pair(0, 1);
				break;
			case 90:
				relativeLeft = new Pair(0, 1);
				relativeRight = new Pair(0, -1);
				relativeForward = new Pair(1, 0);
				relativeX = y;
				relativeY = x;
				relativeXsize = ysize;
				relativeYsize = xsize;
				break;
			case 180:
				relativeLeft = new Pair(1, 0);
				relativeRight = new Pair(-1, 0);
				relativeForward = new Pair(0, -1);
				break;
			case 270:
				relativeLeft = new Pair(0, -1);
				relativeRight = new Pair(0, 1);
				relativeForward = new Pair(-1, 0);
				relativeX = y;
				relativeY = x;
				relativeXsize = ysize;
				relativeYsize = xsize;
				break;
		}
		if (relativeX > 0 && relativeX < relativeXsize && map.getValue(x + relativeLeft.x, y + relativeLeft.y) < left) {
			map.setValue(x + relativeLeft.x, y + relativeLeft.y, (int) left);
		}
		if (relativeX < relativeXsize && relativeX > 0 && map.getValue(x + relativeRight.x, y + relativeRight.y) < right) {
			map.setValue(x + relativeRight.x, y + relativeRight.y, (int) right);
		}
		if (relativeY < relativeYsize && relativeY > 0 && map.getValue(x + relativeForward.x, y + relativeForward.y) < forward) {
			map.setValue(x + relativeForward.x, y + relativeForward.y, (int) forward);
		}
	}
	
	private void changeHeading(int h) {
		heading += h;
		if (heading < 0) { 
			heading = 360 + heading;
		} else if (heading >= 360) {
			heading = heading - 360;
		}
	}
}
