package org.mipyykko.roboexplorer.logic;

import lejos.nxt.NXTMotor;
import lejos.nxt.UltrasonicSensor;

import org.mipyykko.roboexplorer.config.Config;

public class Robot {

	private Config config;
	
	private NXTMotor leftMotor, rightMotor, ultrasonicMotor;
	private UltrasonicSensor ultrasonicSensor;
	
	public Robot(Config config) {
		this.config = config;
		this.leftMotor = new NXTMotor(config.getLeftMotorPort());
		this.rightMotor = new NXTMotor(config.getRightMotorPort());
		this.ultrasonicMotor = new NXTMotor(config.getUltrasonicMotorPort());
		this.ultrasonicSensor = new UltrasonicSensor(config.getUltrasonicSensorPort());
	}
}
