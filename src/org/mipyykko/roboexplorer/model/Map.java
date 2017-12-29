package org.mipyykko.roboexplorer.model;

public class Map {

	private int height, width;
	private int[][] data;
	
	public Map(int height, int width) throws Exception {
		// the robot coordinates are the "right way" on the y-axis eg. y increases upwards
		if (height < 0 || width < 0) {
			throw new Exception("out of bounds");
		}
		this.data = new int[height][width];
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

	public int[][] getData() {
		return data;
	}

	public void setData(int[][] data) {
		this.data = data;
	}
	
	public void setValue(int x, int y, int value) /*throws Exception*/ {
		if (x < 0 || y < 0 || x > this.width|| y > this.height) {
			//throw new Exception("out of bounds!");
		}
		data[y][x] = value;
	}
	
	public int getValue(int x, int y) /*throws Exception*/ {
		if (x < 0 || y < 0 || x > this.width|| y > this.height) {
			//throw new Exception("out of bounds!");
		}
		return data[y][x];
	}
	
	public int getValueFromHeading(int x, int y, int heading)/* throws Exception */{
		if (heading < 0) { 
			heading = 360 + heading;
		} else if (heading >= 360) {
			heading = heading - 360;
		}
		switch (heading) {
			case 0:
				if (y > 0) {
					return getValue(x, y - 1);
				} 
				return 255;
			case 90:
				if (x < width) {
					return getValue(x + 1, y);
				}
				return 255;
			case 180:
				if (y < height) {
					return getValue(x, y + 1);
				}
				return 255;
			case 270:
				if (x > 0) {
					return getValue(x - 1, y);
				}
				return 255;
			default:
				return 255;
		}
	}
}
