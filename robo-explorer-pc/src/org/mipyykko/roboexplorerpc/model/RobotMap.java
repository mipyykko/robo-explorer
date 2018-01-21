package org.mipyykko.roboexplorerpc.model;

/**
 * Kartta ja sen käsittely.

 * @author mipyykko
 *
 */
public class RobotMap {

	final private float IS_OCCUPIED = 0.9f;
	final private float IS_FREE = 0.3f;
	
	private float[][] data;
	private int width, height;
	private float prior = (float) Math.log(0.5 / (1 - 0.5));
	
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
	
	/**
	 * Päivitetään koordinaatin todennäköisyyttä olla varattu.
	 */
	public void setOccupied(int x, int y) {
		data[y][x] = data[y][x] - prior + (float) (Math.log(0.6 / (1 - 0.6)));
	}

	/**
	 * Päivitetään koordinaatin todennäköisyyttä olla vapaa.
	 */
	public void setFree(int x, int y) {
		data[y][x] = data[y][x] - prior + (float) (Math.log(0.3 / (1 - 0.3)));
	}
	
	public boolean isOccupied(int x, int y) {
		return data[y][x] > IS_OCCUPIED;
	}
	
	public boolean isFree(int x, int y) {
		return data[y][x] < IS_FREE;
		
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
