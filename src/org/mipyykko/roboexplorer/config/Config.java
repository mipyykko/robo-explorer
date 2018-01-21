package org.mipyykko.roboexplorer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

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
	final String ULTRASONIC_SENSOR_DEFAULT_PORT = "S4";
	
	final String TOUCH_SENSOR_DEFAULT_PORT = "S3";
	
	final String LIGHT_SENSOR_DEFAULT_PORT = "S2";
	
	final String WHEEL_DEFAULT_DIAMETER = "2.24";
	final String TRACK_DEFAULT_WIDTH = new Double(12 / 2.54).toString();
	
	final String TRAVEL_DEFAULT_SPEED = "5";
	final String ROTATE_DEFAULT_SPEED = "25";
	final String TRAVEL_DEFAULT_DISTANCE = "10";
	final String DISTANCE_DEFAULT_THRESHOLD = "10";
	
	final String MAP_DEFAULT_WIDTH = "100";
	final String MAP_DEFAULT_HEIGHT = "100";
	
	final Hashtable<String, String> defaultValues = new Hashtable<String, String>() {
		{
			put("leftMotorPort", LEFT_MOTOR_DEFAULT_PORT);
			put("rightMotorPort", RIGHT_MOTOR_DEFAULT_PORT);
			put("ultrasonicMotorPort", ULTRASONIC_MOTOR_DEFAULT_PORT);
			put("ultrasonicSensorPort", ULTRASONIC_SENSOR_DEFAULT_PORT);
			put("touchSensorPort", TOUCH_SENSOR_DEFAULT_PORT);
			put("lightSensorPort", LIGHT_SENSOR_DEFAULT_PORT);
			put("wheelDiameter", WHEEL_DEFAULT_DIAMETER);
			put("trackWidth", TRACK_DEFAULT_WIDTH);
			put("travelSpeed", TRAVEL_DEFAULT_SPEED);
			put("rotateSpeed", ROTATE_DEFAULT_SPEED);
			put("travelDistance", TRAVEL_DEFAULT_DISTANCE);
			put("distanceThreshold", DISTANCE_DEFAULT_THRESHOLD);
			put("mapWidth", MAP_DEFAULT_WIDTH);
			put("mapHeight", MAP_DEFAULT_HEIGHT);
			put("debug", "true");
			
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
		
	public String[] getConfigKeys() {
		List<String> keys = new ArrayList<String>();
		Enumeration<String> vals = defaultValues.keys();
		while (vals.hasMoreElements()) {
			keys.add(vals.nextElement());
		}
		return (String[]) keys.toArray();
	}
}
