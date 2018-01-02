package org.mipyykko.roboexplorer.util;

import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;

/**
 * Apuluokka asetuksia varten.
 * 
 * @author mipyykko
 *
 */
public class Convert {
	
	public MotorPort motor(String port) {
//		if (port.isEmpty()) {
//			throw new Exception("no motorport in config");
//		}
		
		switch (port.charAt(0)) {
			case 'A':
				return MotorPort.A;
			case 'B':
				return MotorPort.B;
			case 'C':
				return MotorPort.C;
			default:
				return null;
//				throw new Exception("invalid motorport config");
		}
	}
	
	public String motor(MotorPort motorPort) {
		return motorPort.toString();
	}
	
	public SensorPort sensor(String port) {
//		if (port.isEmpty()) {
//			throw new Exception("no sensorport in config");
//		}
		
		switch (port.charAt(1)) {
			case '1':
				return SensorPort.S1;
			case '2':
				return SensorPort.S2;
			case '3':
				return SensorPort.S3;
			case '4':
				return SensorPort.S4;
			default:
				return null;
//				throw new Exception("invalid sensorport config");
		}
	}
	
	public String sensor(SensorPort port) {
		return port.toString();
	}
}
