package org.mipyykko.roboexplorerpc.model;

import java.util.List;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.navigation.Pose;

public class RobotData {

	private Pose pose;
	private RangeReadings readings;
	private List<MCLParticle> particles;
	
	public RobotData() {
		
	}
	
	public RobotData pose(Pose pose) {
		this.pose = pose;
		return this;
	}
		
	public RobotData readings(RangeReadings readings) {
		this.readings = readings;
		return this;
	}
	
	public RobotData particles(List<MCLParticle> particles) {
		this.particles = particles;
		return this;
	}
	
	public RobotData build() {
		return this;
	}

	public RangeReadings getReadings() {
		return readings;
	}
	
	public Pose getPose() {
		return pose;
	}
	
	public List<MCLParticle> getParticles() {
		return particles;
	}
	
}
