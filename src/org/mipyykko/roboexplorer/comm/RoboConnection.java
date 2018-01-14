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

public class RoboConnection {

	private BTConnection connection;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public RoboConnection() {}
	
	public boolean open() {
		connection = Bluetooth.waitForConnection();
		if (connection != null) {
			dis = new DataInputStream(connection.openDataInputStream());
			dos = new DataOutputStream(connection.openDataOutputStream());
		}
		return connection != null;
	}
	
	public void close() {
		try {
			dos.close();
			dis.close();
			connection.close();
		} catch (Exception e) {
			
		}
	}
	
	public boolean sendData(MCLPoseProvider poseProvider, RangeReadings rangeReadings) {
		try {
			dos.writeInt(1); // magic number
			Pose pose = poseProvider.getEstimatedPose();
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
		} catch (Exception e) {
			//e.printStackTrace();
			//Button.waitForAnyPress();
			//System.out.println("couldn't send data!");
			return false;
		}
		return true;
	}
}
