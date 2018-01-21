package org.mipyykko.roboexplorer.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.navigation.Pose;

import org.mipyykko.roboexplorer.logic.Explorer;

public class RoboConnection {

	private BTConnection connection;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private Explorer explorer;
	
	private Reader reader = new Reader();
	
	public RoboConnection(Explorer explorer) {
		this.explorer = explorer;
	}
	
	public boolean open() {
		connection = Bluetooth.waitForConnection();
		if (connection != null) {
			dis = new DataInputStream(connection.openDataInputStream());
			dos = new DataOutputStream(connection.openDataOutputStream());
			if (!reader.running) {
				reader.start();
			}
		}
		return connection != null;
	}
	
	public void close() {
		try {
			reader.running = false;
			dos.close();
			dis.close();
			connection.close();
		} catch (Exception e) {
			
		}
	}
	
	public boolean readData() {
		float angle, distance;
		
		try {
			int c = dis.readInt();
			Command command = Command.values()[c];
			
			switch (command) {
				case ROTATE:
					dis.readInt(); // no error check...
					angle = dis.readFloat();
					explorer.rotate(angle);
					break;
				case MOVE:
					dis.readInt();
					distance = dis.readFloat();
					explorer.travel(distance);
					break;
				case ROTATE_MOVE:
					dis.readInt();
					angle = dis.readFloat();
					distance = dis.readFloat();
					explorer.rotateTravel(angle, distance);
					break;
				case BACK:
					dis.readInt();
					distance = dis.readFloat();
					explorer.back(distance);
					break;
				case STOP:
					dis.readInt();
					explorer.stop();
					break;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public boolean sendData(Command command, Object... values) /*MCLPoseProvider poseProvider, RangeReadings rangeReadings)*/ {
		MCLPoseProvider poseProvider;
		Pose pose;
		
		try {
			switch (command) {
				case SEND_DATA:
					poseProvider = (MCLPoseProvider) values[0];
					RangeReadings rangeReadings = (RangeReadings) values[1];
					dos.writeInt(command.ordinal());
					pose = poseProvider.getEstimatedPose();
					dos.writeFloat(pose.getX());
					dos.writeFloat(pose.getY());
					dos.writeFloat(pose.getHeading());
					RangeReadings rr = rangeReadings;
					if (rr != null && rr.getNumReadings() > 0) {
						dos.writeInt(rr.getNumReadings());
						for (int i = 0; i < rr.getNumReadings(); i++) {
							dos.writeFloat(rr.getAngle(i));
							dos.writeFloat(rr.getRange(i));
						}
					} else {
						dos.writeInt(0);
					}
					MCLParticleSet mps = poseProvider.getParticles();
					dos.writeInt(mps.numParticles());
					for (int i = 0; i < mps.numParticles(); i++) {
						MCLParticle mp = mps.getParticle(i);
						dos.writeFloat(mp.getPose().getX());
						dos.writeFloat(mp.getPose().getY());
						dos.writeFloat(mp.getPose().getHeading());
					}
					dos.flush();
					System.out.println("sent data");
					break;
				case STOP_OBSTACLE:
					dos.writeInt(command.ordinal());
					dos.writeFloat((Float) values[0]);
					dos.flush();
					break;
				case STOP_BUMP:
					dos.writeInt(command.ordinal());
					poseProvider = (MCLPoseProvider) values[0];
					pose = poseProvider.getEstimatedPose();
					dos.writeFloat(pose.getX());
					dos.writeFloat(pose.getY());
					dos.writeFloat(pose.getHeading());
					dos.flush();
				case STOP_STALLED:
					dos.writeInt(command.ordinal());
					dos.flush();
					break;
					
			}
		} catch (Exception e) {
			//e.printStackTrace();
			//Button.waitForAnyPress();
			//System.out.println("couldn't send data!");
			return false;
		}
		return true;
	}
	
	class Reader extends Thread {
		
		public boolean running = false;
		
		public void run() {
			running = true;
			while (running) {
				readData();
				Thread.yield();
			}
		}
	}
}
