package org.mipyykko.roboexplorer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.mipyykko.roboexplorer.util.Convert;

import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;

public class Config {

	final MotorPort LEFT_MOTOR_DEFAULT_PORT = MotorPort.A;
	final MotorPort RIGHT_MOTOR_DEFAULT_PORT = MotorPort.B;
	final MotorPort ULTRASONIC_MOTOR_DEFAULT_PORT = MotorPort.C;
	
	final SensorPort ULTRASONIC_SENSOR_DEFAULT_PORT = SensorPort.S1;
	
	private MotorPort leftMotorPort, rightMotorPort, ultrasonicMotorPort;
	private SensorPort ultrasonicSensorPort;
	
	private Properties config;
	private Convert convert = new Convert();
	
	public Config() {
		Properties config = new Properties();
		setDefaultConfig();
		loadConfig();
		readConfig();
	}
	
	private void setDefaultConfig() {
		this.leftMotorPort = LEFT_MOTOR_DEFAULT_PORT;
		this.rightMotorPort = RIGHT_MOTOR_DEFAULT_PORT;
		this.ultrasonicMotorPort = ULTRASONIC_MOTOR_DEFAULT_PORT;
		this.ultrasonicSensorPort = ULTRASONIC_SENSOR_DEFAULT_PORT;
	}

	private void readConfig() {
		try {
			this.leftMotorPort = convert.motor(config.getProperty("leftMotorPort"));
			this.rightMotorPort = convert.motor(config.getProperty("rightMotorPort"));
			this.ultrasonicMotorPort = convert.motor(config.getProperty("ultrasonicMotorPort"));
			this.ultrasonicSensorPort = convert.sensor(config.getProperty("ultrasonicSensorPort"));
		} catch (Exception e) {
			// something
		}
	}
	
	private void writeConfig() {
		try {
			config.setProperty("leftMotorPort", convert.motor(this.leftMotorPort));
			config.setProperty("rightMotorPort", convert.motor(this.rightMotorPort));
			config.setProperty("ultrasonicMotorPort", convert.motor(this.ultrasonicMotorPort));
			config.setProperty("ultrasonicSensorPort", convert.sensor(this.ultrasonicSensorPort));
		} catch (Exception e) {
			// something
		}
	}
	
	private void loadConfig() {
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(new File("roboexplorer.properties"));
			config.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			setDefaultConfig();
			saveConfig();
		}
	}
	
	private void saveConfig() {
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream(new File("roboexplorer.properties"));
			config.store(outputStream, null);
			outputStream.close();
		} catch (Exception e) {
			// do something
		}
	}

	public MotorPort getLeftMotorPort() {
		return leftMotorPort;
	}

	public void setLeftMotorPort(MotorPort leftMotorPort) {
		this.leftMotorPort = leftMotorPort;
		config.setProperty("leftMotorPort", convert.motor(leftMotorPort));
	}

	public MotorPort getRightMotorPort() {
		return rightMotorPort;
	}

	public void setRightMotorPort(MotorPort rightMotorPort) {
		this.rightMotorPort = rightMotorPort;
		config.setProperty("rightMotorPort", convert.motor(rightMotorPort));
	}

	public MotorPort getUltrasonicMotorPort() {
		return ultrasonicMotorPort;
	}

	public void setUltrasonicMotorPort(MotorPort ultrasonicMotorPort) {
		this.ultrasonicMotorPort = ultrasonicMotorPort;
		config.setProperty("ultrasonicMotorPort", convert.motor(ultrasonicMotorPort));
	}

	public SensorPort getUltrasonicSensorPort() {
		return ultrasonicSensorPort;
	}

	public void setUltrasonicSensorPort(SensorPort ultrasonicSensorPort) {
		this.ultrasonicSensorPort = ultrasonicSensorPort;
		config.setProperty("ultrasonicSensorPort", convert.sensor(ultrasonicSensorPort));
	}
	
	
}
