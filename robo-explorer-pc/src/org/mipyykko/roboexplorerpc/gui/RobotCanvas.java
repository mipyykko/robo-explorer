package org.mipyykko.roboexplorerpc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class RobotCanvas extends JPanel {
	
	private BufferedImage image;
	private Graphics2D graphics;
	private int width, height;
	
	
	public RobotCanvas(int width, int height) {
		setBackground(Color.white);
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
	}

	public void createImage() {
		if (image == null) {
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
			this.graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, width, height);
		}
	}
	
	
	@Override
	public Dimension getPreferredSize() {
		return image == null ? new Dimension(width, height) : new Dimension(image.getWidth(), image.getHeight());
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (image != null) {
			createImage();
		}
		g.drawImage(image, 0, 0, this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			createImage();
		}
		g.drawImage(image, 0, 0, this);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public Graphics2D getGraphics() {
		return graphics;
	}

}
