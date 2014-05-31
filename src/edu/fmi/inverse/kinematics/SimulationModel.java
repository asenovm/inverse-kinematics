package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimulationModel implements MouseMotionListener {

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
	private static final int HEIGHT_ARM_START = 5;

	/**
	 * {@value}
	 */
	private static final int WIDTH_ARM_START = 5;

	/**
	 * {@value}
	 */
	private static final int HEIGHT_TARGET = 15;

	/**
	 * {@value}
	 */
	private static final int WIDTH_TARGET = 15;

	private final List<Segment> segments;

	private final PositionsCalculator calculator;

	private final Point target;

	private final Point start;

	private ModelListener listener;

	public SimulationModel() {
		segments = new LinkedList<Segment>();
		segments.add(new Segment());
		segments.add(new Segment());
		segments.add(new Segment());

		calculator = new PositionsCalculator();

		start = new Point((WIDTH_FRAME - WIDTH_ARM_START) / 2,
				(HEIGHT_FRAME - HEIGHT_ARM_START) / 2);
		target = new Point(WIDTH_FRAME / 2 + 3 * Segment.LENGTH_SEGMENT - start.x,
				(HEIGHT_FRAME - HEIGHT_TARGET) / 2 - start.y);
	}

	public void setOnChangedListener(final ModelListener listener) {
		this.listener = listener;
		listener.onModelChanged(this);
	}

	public List<Segment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// blank
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		target.x = event.getX() - start.x;
		target.y = event.getY() - start.y;
		calculator.calculatePositions(target, start, segments);
		listener.onModelChanged(this);
	}

	public int getTargetX() {
		return target.x + start.x;
	}

	public int getTargetY() {
		return target.y + start.y;
	}

	public int getStartX() {
		return start.x;
	}

	public int getStartY() {
		return start.y;
	}

	public int getStartWidth() {
		return WIDTH_ARM_START;
	}

	public int getStartHeight() {
		return HEIGHT_ARM_START;
	}

	public int getTargetWidth() {
		return WIDTH_TARGET;
	}

	public int getTargetHeight() {
		return HEIGHT_TARGET;
	}
}
