package edu.fmi.inverse.kinematics.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.fmi.inverse.kinematics.ModelListener;
import edu.fmi.inverse.kinematics.PositionsCalculator;
import edu.fmi.inverse.kinematics.Segment;
import edu.fmi.inverse.kinematics.SimulationModel;

public class SimulationView extends JFrame implements ModelListener {

	/**
	 * {@value}
	 */
	private static final int HEIGHT_ITEM = 15;

	/**
	 * {@value}
	 */
	private static final int WIDTH_ITEM = 15;

	/**
	 * {@value}
	 */
	private static final int HEIGHT_ARM_START = 5;

	/**
	 * {@value}
	 */
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
	private static final int WIDTH_FRAME = 800;

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

	private int targetX = 150;

	private int targetY = 150;

	private List<Segment> segments;

	public SimulationView(String title, GraphicsConfiguration gc) {
		super(title, gc);

		segments = new LinkedList<Segment>();

		final Dimension dimension = new Dimension(WIDTH_FRAME, HEIGHT_FRAME);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);

		addCells();

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent event) {
				System.out.println("moved");
				targetX = event.getX();
				targetY = event.getY();
				paint(getGraphics());
				final int centerX = (WIDTH_FRAME - WIDTH_ARM_START) / 2;
				final int centerY = (HEIGHT_FRAME - HEIGHT_ARM_START) / 2;
				new PositionsCalculator().calculatePositions(new Point(targetX,
						targetY), new Point(centerX, centerY), segments);
			}

			@Override
			public void mouseDragged(MouseEvent event) {
				// blank
			}
		});
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

		final int centerX = (WIDTH_FRAME - WIDTH_ARM_START) / 2;
		final int centerY = (HEIGHT_FRAME - HEIGHT_ARM_START) / 2;
		graphics.fillRect(centerX, centerY, WIDTH_ARM_START, HEIGHT_ARM_START);

		graphics.setColor(Color.RED);
		graphics.fillArc(targetX, targetY, WIDTH_ITEM, HEIGHT_ITEM, 0, 360);

		double angle = 0;
		for (int i = 0; i < segments.size(); ++i) {
			final Segment segment = segments.get(i);
			angle += segment.getAngle();
			angle = PositionsCalculator.simplifyAngle(angle);
			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			if (i == 0) {
				segment.startX = 0;
				segment.startY = 0;
				segment.endX = (int) (segment.startX + segment.getLength()
						* cosAngle);
				segment.endY = (int) (segment.startY + segment.getLength()
						* sinAngle);
				graphics.setColor(Color.BLUE);
				graphics.drawLine(segment.startX + WIDTH_FRAME / 2,
						segment.startY + HEIGHT_FRAME / 2, segment.endX
								+ WIDTH_FRAME / 2, segment.endY + HEIGHT_FRAME
								/ 2);
			} else {
				final Segment previousSegment = segments.get(i - 1);
				segment.startX = previousSegment.endX;
				segment.endX = (int) (segment.startX + segment.getLength()
						* cosAngle);
				segment.startY = previousSegment.endY;
				segment.endY = (int) (segment.startY + segment.getLength()
						* sinAngle);
				if (i == 1) {
					graphics.setColor(Color.RED);
				} else {
					graphics.setColor(Color.CYAN);
				}
				graphics.drawLine(segment.startX + WIDTH_FRAME / 2,
						segment.startY + HEIGHT_FRAME / 2, segment.endX
								+ WIDTH_FRAME / 2, segment.endY + HEIGHT_FRAME
								/ 2);
			}
		}
	}

	@Override
	public void onModelChanged(SimulationModel model) {
		segments = model.getSegments();
		invalidate();
		paint(getGraphics());
	}
}
