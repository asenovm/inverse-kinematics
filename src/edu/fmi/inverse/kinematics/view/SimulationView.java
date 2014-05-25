package edu.fmi.inverse.kinematics.view;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class SimulationView extends JFrame {

	/**
	 * {@value}
	 */
	private static final String FRAME_TITLE = "Inverse Kinamatics - CCD simulation";

	/**
	 * {@value}
	 */
	private static final int HEIGHT_FRAME = 600;

	/**
	 * {@value}
	 */
	private static final int WIDTH_FRAME = 800;

	/**
	 * {@value}
	 */
	private static final long serialVersionUID = -3388697450030097056L;

	public SimulationView(String title, GraphicsConfiguration gc) {
		super(title, gc);

		final Dimension dimension = new Dimension(WIDTH_FRAME, HEIGHT_FRAME);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	public SimulationView() throws HeadlessException {
		this(FRAME_TITLE, null);
	}

	public SimulationView(GraphicsConfiguration graphicsConfiguration) {
		this(FRAME_TITLE, graphicsConfiguration);
	}

	public SimulationView(String title) throws HeadlessException {
		this(title, null);
	}

}
