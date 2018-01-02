package org.mipyykko.roboexplorer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;

import org.mipyykko.roboexplorer.util.Convert;

/**
 * Robotin asetukset ja niihin liittyvät levyoperaatiot.
 * 
 * @author mipyykko
 *
 */
public class Config {

	final String LEFT_MOTOR_DEFAULT_PORT = "B";
	final String RIGHT_MOTOR_DEFAULT_PORT = "A";

	final String ULTRASONIC_MOTOR_DEFAULT_PORT = "C";
	final String ULTRASONIC_SENSOR_DEFAULT_PORT = "S2";
	
	final String WHEEL_DEFAULT_DIAMETER = "2.24";
	final String TRACK_DEFAULT_WIDTH = "4.8";
	
	final Hashtable<String, String> defaultValues = new Hashtable<String, String>() {
		{
			put("leftMotorPort", LEFT_MOTOR_DEFAULT_PORT);
			put("rightMotorPort", RIGHT_MOTOR_DEFAULT_PORT);
			put("ultrasonicMotorPort", ULTRASONIC_MOTOR_DEFAULT_PORT);
			put("ultrasonicSensorPort", ULTRASONIC_SENSOR_DEFAULT_PORT);
			put("wheelDiameter", WHEEL_DEFAULT_DIAMETER);
			put("trackWidth", TRACK_DEFAULT_WIDTH);
			
		}
	};
	
	private Properties config;
	private Convert convert = new Convert();
	
	public Config() {
		this.config = new Properties();
		setDefaultConfig();
		loadConfig();
	}
	
	private void setDefaultConfig() {
		Enumeration<String> vals = defaultValues.keys();
		while (vals.hasMoreElements()) {
			String key = vals.nextElement();
			config.setProperty(key, defaultValues.get(key));
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

	public String get(String key) {
		return config.getProperty(key);
	}
	
	public double getDouble(String key) {
		return Double.parseDouble(config.getProperty(key));
	}
	
	public MotorPort getMotorPort(String key) {
		return convert.motor(config.getProperty(key));
	}
	
	public SensorPort getSensorPort(String key) {
		return convert.sensor(config.getProperty(key));
	}

	public boolean set(String key, String value) {
		config.setProperty(key, value);
		return true;
	}
	
	public boolean setDouble(String key, Double value) {
		config.setProperty(key, Double.toString(value));
		return true;
	}
		
}
