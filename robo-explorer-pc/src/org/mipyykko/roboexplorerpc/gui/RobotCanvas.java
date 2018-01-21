package org.mipyykko.roboexplorerpc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import lejos.robotics.RangeReading;
import lejos.robotics.localization.MCLParticle;

import org.mipyykko.roboexplorerpc.model.RobotData;
import org.mipyykko.roboexplorerpc.model.RobotMap;

public class RobotCanvas extends JPanel {
	
	private BufferedImage image;
	private int width, height;
	private List<RobotData> robotData;
	
	private int gridSpace = 10;
	
	private RobotMap map;
	
	public RobotCanvas(int width, int height, RobotMap map) {
		setBackground(Color.white);
		this.width = width;
		this.height = height;
		this.map = map;
		this.setPreferredSize(new Dimension(width, height));
		createImage();
	}
	
	public void createImage() {
		if (image == null) {
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		}
		drawGrid(image.getGraphics());
	}
	
	public void drawGrid(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		g.setColor(Color.lightGray);
		
		for (int x = 0; x < width / gridSpace; x++) {
			g.drawLine(x * gridSpace, 0, x * gridSpace, height);
		}
		for (int y = 0; y < height / gridSpace; y++) {
			g.drawLine(0, y * gridSpace, width, y * gridSpace);
		}
		
		if (map == null) return;
		
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				float val = map.getValue(x, y);
				int c = Math.max(0, Math.min(255, 128 + (int) (val * 128)));
				g.setColor(new Color(c, c, c));
				g.fillRect(x * gridSpace + 1, y * gridSpace + 1, gridSpace - 2, gridSpace - 2);
			}
		}
	}
	
	public void drawPath(Graphics g) {
		if (robotData == null || robotData.isEmpty()) {
			return;
		}
		
		RobotData newestRobotData = robotData.get(robotData.size() - 1);
		
		int idx = 0;
		float lastX = 0, lastY = 0;
		float curX = 100 + (newestRobotData.getPose().getX() * 2);
		float curY = 100 + (newestRobotData.getPose().getY() * 2);
		float curHeading = newestRobotData.getPose().getHeading() + 180;

		g.setColor(Color.blue);
		for (RobotData data : robotData) {
			// this needs to get relative to the grid!
			float x = 100 + (data.getPose().getX() * 2);
			float y = 100 + (data.getPose().getY() * 2);
			float heading = data.getPose().getHeading();
			if (idx++ > 0) {
				g.drawLine((int) lastX, (int) lastY, (int) x, (int) y);
			}
			lastX = x;
			lastY = y;
		}

		g.setColor(Color.pink);
		g.drawOval((int) curX, (int) curY, 4, 4);
		
		g.setColor(Color.red);
		for (RangeReading r : newestRobotData.getReadings()) {
			if (r.getRange() == -1) continue;
			System.out.println(r.getRange());
			float rAngle = r.getAngle();
			int endX = (int) (curX + Math.cos(Math.toRadians(curHeading + rAngle)) * r.getRange() * 2);
			int endY = (int) (curY + Math.sin(Math.toRadians(curHeading + rAngle)) * r.getRange() * 2);
			g.drawLine((int) curX, (int) curY, endX, endY);
		}
		
		g.setColor(Color.yellow);
		for (MCLParticle mp : newestRobotData.getParticles()) {
			float pX = 100 + mp.getPose().getX() * 2;
			float pY = 100 + mp.getPose().getY() * 2;
			g.drawLine((int) pX, (int) pY, (int) pX, (int) pY);
		}
		
		System.out.println(newestRobotData.getPose().getX() + " " + newestRobotData.getPose().getY());
	}

	@Override
	public Dimension getPreferredSize() {
		return image == null ? new Dimension(width, height) : new Dimension(image.getWidth(), image.getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			createImage();
		}
		drawGrid(g);
		//drawPath(g);
		g.drawImage(image, 0, 0, this);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
//	public Graphics2D getGraphics() {
//		return graphics;
//	}

	public void setMap(RobotMap map) {
		this.map = map;
	}
	
	public void setRobotData(List<RobotData> robotData) {
		this.robotData = robotData;
	}
}
