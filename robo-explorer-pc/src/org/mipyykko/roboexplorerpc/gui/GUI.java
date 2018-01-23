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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.mipyykko.roboexplorerpc.logic.Logic;
import org.mipyykko.roboexplorerpc.model.RobotData;
import org.mipyykko.roboexplorerpc.model.RobotMap;

/**
 * Graafinen käyttöliittymä.
 * On saanut suuria vaikutteita esimerkkiprojekteista.
 * 
 * @author mipyykko
 *
 */
public class GUI extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 2L;

	private JButton connectButton = new JButton("Connect to NXT");
	private JLabel connectStatusText = new JLabel("not connected");
	
	private JTextField headingTextField= new JTextField(3);
	private JTextField xTextField = new JTextField(3);
	private JTextField yTextField  = new JTextField(3);

	private DefaultListModel headingHistoryModel = new DefaultListModel();
	private JList headingHistoryList = new JList(headingHistoryModel);
	
	int height = 600;
	int width = 800;
	
	private RobotCanvas canvas;

	private RobotMap map;
	
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
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 2));
		topPanel.add(connectButton);
		topPanel.add(connectStatusText);
		connectButton.addActionListener(this);

		JPanel coordPanel = new JPanel();
		coordPanel.setLayout(new GridLayout(3, 1));
		coordPanel.add(headingTextField);
		coordPanel.add(xTextField);
		coordPanel.add(yTextField);
		
		topPanel.add(coordPanel);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainPanel.setLayout(new GridLayout(2, 0));
		mainPanel.add(topPanel);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(100, 600));
		headingHistoryModel.addElement("0");
		leftPanel.add(headingHistoryList);

		canvas = new RobotCanvas(width, height, map);
		frame.add(canvas, BorderLayout.CENTER);
		canvas.createImage();
		canvas.setBackground(Color.white);
		canvas.setRobotData(robotData);
//		canvasGraphics = canvas.getGraphics();

		frame.add(mainPanel, BorderLayout.NORTH);
		frame.add(leftPanel, BorderLayout.WEST);
		frame.pack();
		
		repaint();
	}

	public void updateConnectStatus() {
		connectButton.setText(logic.getConnection().connected() ? "Disconnect" : "Connect to NXT");
		connectStatusText.setText(logic.getConnection().connected() ? "connected" : "not connected");
		switch (logic.getConnection().getStatus()) {
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
			if (!logic.getConnection().connected()) {
				connectStatusText.setText("connecting...");
				Thread t = new Thread(new Runnable() {
					public void run() {
						logic.getConnection().connect();
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
					logic.getConnection().disconnect();
				}
			}
			System.out.println("connect clicked and logic run");
		}
		System.out.println("getting out of actionlistener");
		revalidate();
		repaint();
	}
	
	/**
	 * Päivitetään kartta.
	 */
	private void updateCanvas() {
		System.out.println("updateCanvas");
		if (newestRobotData != null) {
			headingHistoryModel.addElement(newestRobotData.getPose().getHeading());
			float curX = newestRobotData.getPose().getX();
			float curY = newestRobotData.getPose().getY();
			float curHeading = newestRobotData.getPose().getHeading();
			headingTextField.setText("" + (int) curHeading);
			xTextField.setText("" + (int) curX);
			yTextField.setText("" + (int) curY);
			canvas.createImage();
			canvas.setBackground(Color.white);
		}
		canvas.repaint();
		frame.repaint();
		repaint();
	}

	public void update(RobotData data) {
		System.out.println("received update");
		newestRobotData = data;
		robotData.add(newestRobotData);
		updateConnectStatus();
		updateCanvas();
	}
	
	public void setMap(RobotMap map) {
		this.map = map;
	}
}
