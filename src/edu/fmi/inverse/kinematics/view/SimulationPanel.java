package edu.fmi.inverse.kinematics.view;

import static edu.fmi.inverse.kinematics.SimulationModel.HEIGHT_FRAME;
import static edu.fmi.inverse.kinematics.SimulationModel.WIDTH_FRAME;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import edu.fmi.inverse.kinematics.AngleUtil;
import edu.fmi.inverse.kinematics.ModelListener;
import edu.fmi.inverse.kinematics.Segment;
import edu.fmi.inverse.kinematics.SimulationModel;

public class SimulationPanel extends JPanel implements ModelListener {

	/**
	 * {@value}
	 */
	private static final long serialVersionUID = -3388697450030097056L;

	/**
	 * {@value}
	 */
	private static final int WIDTH_SEGMENT = 6;

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

	private SimulationModel model;

	private final Map<Integer, Color> colors = new HashMap<Integer, Color>();

	private void addCells() {
		setLayout(new GridLayout(NUM_CELLS, NUM_ROWS));
		for (int i = 0; i < NUM_CELLS * NUM_ROWS; ++i) {
			add(createCell());
		}
	}

	public SimulationPanel() {
		super();
		final Dimension dimension = new Dimension(WIDTH_FRAME, HEIGHT_FRAME);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);

		addCells();
		colors.put(0, Color.YELLOW);
		colors.put(1, Color.BLUE);
		colors.put(2, Color.CYAN);
		colors.put(3, Color.DARK_GRAY);
		colors.put(4, Color.GREEN);
		colors.put(5, Color.MAGENTA);
		colors.put(6, Color.ORANGE);
		colors.put(7, Color.GRAY);
		colors.put(8, Color.WHITE);
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

		if (model == null) {
			return;
		}

		graphics.setColor(Color.RED);
		graphics.fillArc(model.getTargetX(), model.getTargetY(),
				model.getTargetWidth(), model.getTargetHeight(), 0, 360);

		final List<Segment> segments = model.getSegments();

		double angle = 0;
		for (int i = 0; i < segments.size(); ++i) {
			final Segment segment = segments.get(i);
			angle += segment.angle;
			angle = AngleUtil.simplifyAngle(angle);
			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			graphics.setColor(colors.get(i));

			if (i == 0) {
				segment.startX = 0;
				segment.startY = 0;
				segment.endX = segment.startX + segment.getLength() * cosAngle;
				segment.endY = segment.startY + segment.getLength() * sinAngle;
			} else {
				final Segment previousSegment = segments.get(i - 1);
				segment.startX = previousSegment.endX;
				segment.endX = (int) (segment.startX + segment.getLength()
						* cosAngle);
				segment.startY = previousSegment.endY;
				segment.endY = (int) (segment.startY + segment.getLength()
						* sinAngle);
			}

			drawSegment(graphics, segment);
		}

		graphics.setColor(Color.BLACK);
		graphics.fillRect(model.getStartX(), model.getStartY(),
				model.getStartWidth(), model.getStartHeight());
	}

	private void drawSegment(Graphics graphics, final Segment segment) {
		final Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setStroke(new BasicStroke(WIDTH_SEGMENT));
		graphics2D.drawLine((int) (segment.startX + WIDTH_FRAME / 2),
				(int) (segment.startY + HEIGHT_FRAME / 2),
				(int) (segment.endX + WIDTH_FRAME / 2),
				(int) (segment.endY + HEIGHT_FRAME / 2));
	}

	@Override
	public void onModelChanged(SimulationModel model) {
		this.model = model;
		invalidate();
		paint(getGraphics());
	}
}
