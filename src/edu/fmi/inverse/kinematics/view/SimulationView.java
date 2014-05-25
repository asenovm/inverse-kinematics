package edu.fmi.inverse.kinematics.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SimulationView extends JFrame {

	private static final int HEIGHT_ARM_START = 5;

	private static final int WIDTH_ARM_START = 5;

	/**
	 * {@value}
	 */
	private static final long serialVersionUID = -3388697450030097056L;

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
	private static final int WIDTH_FRAME = 600;

	/**
	 * {@value}
	 */
	private static final int NUM_CELLS = 5;

	/**
	 * {@value}
	 */
	private static final int NUM_ROWS = 5;

	/**
	 * {@value}
	 */
	private static final int WIDTH_CELL = WIDTH_FRAME / NUM_CELLS;

	/**
	 * {@value}
	 */
	private static final int HEIGHT_CELL = HEIGHT_FRAME / NUM_ROWS;

	/**
	 * {@value}
	 */
	private static final Color COLOR_BORDER = Color.getHSBColor(0, 0, 26.7f);

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

		addCells();
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

	private void addCells() {
		setLayout(new GridLayout(NUM_CELLS, NUM_ROWS));
		for (int i = 0; i < NUM_CELLS * NUM_ROWS; ++i) {
			add(createCell());
		}
	}

	private JPanel createCell() {
		final JPanel cell = new JPanel();
		cell.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
		final Dimension cellDimension = new Dimension(WIDTH_CELL, HEIGHT_CELL);
		cell.setPreferredSize(cellDimension);
		cell.setMinimumSize(cellDimension);
		cell.setMaximumSize(cellDimension);
		return cell;
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.fillRect((WIDTH_FRAME - WIDTH_ARM_START) / 2,
				(HEIGHT_FRAME - HEIGHT_ARM_START) / 2, WIDTH_ARM_START,
				HEIGHT_ARM_START);
	}
}
