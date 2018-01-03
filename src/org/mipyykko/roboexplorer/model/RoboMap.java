package org.mipyykko.roboexplorer.model;

/**
 * 
 * Kartta ja siihen liittyvät metodit.
 * 
 * @author mipyykko
 *
 */
public class RoboMap {

	private int height, width;
	private int[][] mapData;
	private int[][] countData;
	
	public RoboMap(int height, int width) throws Exception {
		// the robot coordinates are the "right way" on the y-axis eg. y increases upwards
		if (height < 0 || width < 0) {
			throw new Exception("out of bounds");
		}
		this.mapData = new int[height][width];
		this.countData = new int[height][width];
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int[][] getMapData() {
		return mapData;
	}

	public void setMapData(int[][] data) {
		this.mapData = data;
	}
	
	public void incCount(int x, int y) {
		countData[y][x]++;
	}
	
	public void incMap(int x, int y) {
		mapData[y][x]++;
	}
	
	public void decMap(int x, int y) {
		mapData[y][x]--;
	}
	
	public void setMapValue(int x, int y, int value) /*throws Exception*/ {
		if (x < 0 || y < 0 || x > this.width|| y > this.height) {
			//throw new Exception("out of bounds!");
		}
		mapData[y][x] = value;
	}
	
	public int getMapValue(int x, int y) /*throws Exception*/ {
		if (x < 0 || y < 0 || x > this.width|| y > this.height) {
			//throw new Exception("out of bounds!");
		}
		return mapData[y][x];
	}
	
	public int getCountValue(int x, int y) {
		return countData[y][x];
	}
	
	public boolean occupied(int x, int y) {
		if (countData[y][x] == 0) {
			return false; 
		}
		return (countData[y][x] > 0 && mapData[y][x] > 0);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sb.append(occupied(x, y) ? "*" : " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
