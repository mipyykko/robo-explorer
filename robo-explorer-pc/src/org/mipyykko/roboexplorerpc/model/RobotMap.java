package org.mipyykko.roboexplorerpc.model;

public class RobotMap {

	private float[][] data;
	private int width, height;
	
	public RobotMap(int width, int height) {
		this.width = width;
		this.height = height;
		data = new float[height][width];
	}
	
	public void setValue(int x, int y, float value) {
		data[y][x] = value;
	}
	
	public float getValue(int x, int y) {
		return data[y][x];
	}
	
	public void setData(float[][] data) {
		this.data = data;
	}
	
	public float[][] getData() {
		return data;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
