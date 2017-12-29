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

/**
 * Robotin asetukset ja niihin liittyvät levyoperaatiot.
 * 
 * @author mipyykko
 *
 */
public class Config {

	final MotorPort LEFT_MOTOR_DEFAULT_PORT = MotorPort.B;
	final MotorPort RIGHT_MOTOR_DEFAULT_PORT = MotorPort.A;
	final MotorPort ULTRASONIC_MOTOR_DEFAULT_PORT = MotorPort.C;
	
	final SensorPort ULTRASONIC_SENSOR_DEFAULT_PORT = SensorPort.S2;
	
	final double WHEEL_DEFAULT_DIAMETER = 2.24f;
	final double TRACK_DEFAULT_WIDTH = 4.8f;
	
	private MotorPort leftMotorPort, rightMotorPort, ultrasonicMotorPort;
	private SensorPort ultrasonicSensorPort;
	
	private double wheelDiameter, trackWidth;
	
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
		this.wheelDiameter = WHEEL_DEFAULT_DIAMETER;
		this.trackWidth = TRACK_DEFAULT_WIDTH;
	}

	private void readConfig() {
		try {
			this.leftMotorPort = convert.motor(config.getProperty("leftMotorPort"));
			this.rightMotorPort = convert.motor(config.getProperty("rightMotorPort"));
			this.ultrasonicMotorPort = convert.motor(config.getProperty("ultrasonicMotorPort"));
			this.ultrasonicSensorPort = convert.sensor(config.getProperty("ultrasonicSensorPort"));
			this.wheelDiameter = Double.parseDouble(config.getProperty("wheelDiameter"));
			this.trackWidth = Double.parseDouble(config.getProperty("trackWidth"));
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
			config.setProperty("wheelDiameter", Double.toString(this.wheelDiameter));
			config.setProperty("trackWidth", Double.toString(this.trackWidth));
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

	public double getWheelDiameter() {
		return wheelDiameter;
	}

	public void setWheelDiameter(double wheelDiameter) {
		this.wheelDiameter = wheelDiameter;
		config.setProperty("wheelDiameter", Double.toString(wheelDiameter));
	}

	public double getTrackWidth() {
		return trackWidth;
	}

	public void setTrackWidth(double trackWidth) {
		this.trackWidth = trackWidth;
		config.setProperty("trackWidth", Double.toString(trackWidth));
	}
	
	
}
