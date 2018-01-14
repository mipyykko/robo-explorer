package org.mipyykko.roboexplorerpc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import lejos.robotics.RangeReading;
import lejos.robotics.localization.MCLParticle;

import org.mipyykko.roboexplorerpc.logic.Logic;
import org.mipyykko.roboexplorerpc.model.RobotData;

public class GUI extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 2L;

	private JButton connectButton = new JButton("Connect to NXT");
	private JLabel connectStatusText = new JLabel("not connected");
	
	private JLabel headingTextLabel = new JLabel(" ");
	private JLabel xTextLabel = new JLabel(" ");
	private JLabel yTextLabel = new JLabel(" ");

	private DefaultListModel headingHistoryModel = new DefaultListModel();
	private JList headingHistoryList = new JList(headingHistoryModel);
	
	int height = 600;
	int width = 800;
	
	private RobotCanvas canvas;
	private BufferedImage canvasImage;
	private Graphics2D canvasGraphics;


	private float lastX = -1, lastY = -1;
	private RobotData newestRobotData;
	private List<RobotData> robotData = new ArrayList<RobotData>();
	
	private Logic logic = new Logic(this);
	
	private JFrame frame;
	
	public GUI() {
		buildGUI();
		frame.setVisible(true);
		Thread.yield();
	}
	
	private void buildGUI() {
		frame = new JFrame();
		frame.setTitle("RoboExplorer GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(width, height);
		
		JPanel connectPanel = new JPanel();
		connectPanel.add(connectButton);
		connectPanel.add(connectStatusText);
		connectButton.addActionListener(this);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainPanel.setLayout(new GridLayout(2, 0));
		mainPanel.add(connectPanel);
		
		JPanel coordPanel = new JPanel();
		coordPanel.setLayout(new GridLayout(3, 1));
		coordPanel.add(headingTextLabel);
		coordPanel.add(xTextLabel);
		coordPanel.add(yTextLabel);
		coordPanel.setPreferredSize(new Dimension(120, 600));
		headingHistoryModel.addElement("0");
		coordPanel.add(headingHistoryList);
		
		mainPanel.add(coordPanel);
		canvas = new RobotCanvas(width, height);
		frame.add(canvas, BorderLayout.CENTER);
		canvasImage = canvas.getImage();
		canvas.setBackground(Color.white);
		canvasGraphics = canvas.getGraphics();

		frame.add(mainPanel, BorderLayout.NORTH);
		frame.add(coordPanel, BorderLayout.WEST);

		frame.pack();
		
		repaint();
//		frame.add(this);
	}

	public void updateConnectStatus() {
		connectButton.setText(logic.connected() ? "Disconnect" : "Connect to NXT");
		connectStatusText.setText(logic.connected() ? "connected" : "not connected");
		switch (logic.getStatus()) {
			case OK:
				connectStatusText.setText("connected");
				break;
			case CONNECTION_ERROR:
				connectStatusText.setText("connection error");
				break;
			case UNKNOWN_ERROR:
				connectStatusText.setText("unknown error");
				break;
			default:
				break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		repaint();
		if (ae.getSource() == connectButton) {
			if (!logic.connected()) {
				connectStatusText.setText("connecting...");
				Thread t = new Thread(new Runnable() {
					public void run() {
						logic.connect();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								updateConnectStatus();
							}
						});
					}
				});
				t.start();
			} else {
				connectStatusText.setText("disconnecting...");
				synchronized(this) {
					logic.disconnect();
				}
			}
			System.out.println("connect clicked and logic run");
		}
		System.out.println("getting out of actionlistener");
		revalidate();
		repaint();
	}
	
	private void updateCanvas() {
		System.out.println("updateCanvas");
		if (newestRobotData != null) {
			headingHistoryModel.addElement(newestRobotData.getPose().getHeading());
			float curX = 100 + (newestRobotData.getPose().getX() * 2);
			float curY = 100 + (newestRobotData.getPose().getY() * 2);
			float curHeading = newestRobotData.getPose().getHeading() + 180;
			headingTextLabel.setText("" + (int) curHeading);
			xTextLabel.setText("" + (int) curX);
			yTextLabel.setText("" + (int) curY);
			if (canvasGraphics == null) {
				canvas.createImage();
				canvas.setBackground(Color.white);
				canvasGraphics = canvas.getGraphics();
			}
			canvasGraphics.setColor(canvas.getBackground());
			canvasGraphics.drawRect(0, 0, canvas.getWidth(), canvas.getHeight());
			canvasGraphics.setColor(Color.blue);
			int idx = 0;
			for (RobotData data : robotData) {
				float x = 100 + (data.getPose().getX() * 2);
				float y = 100 + (data.getPose().getY() * 2);
				float heading = data.getPose().getHeading();
				if (idx++ > 0) {
					canvasGraphics.drawLine((int) lastX, (int) lastY, (int) x, (int) y);
				}
				lastX = x;
				lastY = y;

			}
			canvasGraphics.setColor(Color.pink);
			canvasGraphics.drawOval((int) curX, (int) curY, 4, 4);
			canvasGraphics.setColor(Color.red);
			for (RangeReading r : newestRobotData.getReadings()) {
				if (r.getRange() == -1) continue;
				System.out.println(r.getRange());
				float rAngle = r.getAngle();
				int endX = (int) (curX + Math.cos(Math.toRadians(curHeading + rAngle)) * r.getRange() * 2);
				int endY = (int) (curY + Math.sin(Math.toRadians(curHeading + rAngle)) * r.getRange() * 2);
				canvasGraphics.drawLine((int) curX, (int) curY, endX, endY);
			}
			canvasGraphics.setColor(Color.yellow);
			for (MCLParticle mp : newestRobotData.getParticles()) {
				float pX = 100 + mp.getPose().getX() * 2;
				float pY = 100 + mp.getPose().getY() * 2;
				canvasGraphics.drawLine((int) pX, (int) pY, (int) pX, (int) pY);
			}
			System.out.println(newestRobotData.getPose().getX() + " " + newestRobotData.getPose().getY());
			canvas.repaint();
		}
		frame.repaint();
		repaint();
	}

	public void update(RobotData data) {
		System.out.println("received update");
		newestRobotData = data;//(RobotData) o;
		robotData.add(newestRobotData);
		updateCanvas();

	}
}
