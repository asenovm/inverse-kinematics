package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimulationModel implements MouseMotionListener {

	private static final int LENGTH_SEGMENT_UPDATE = 10;

	/**
	 * {@value}
	 */
	public static final int HEIGHT_FRAME = 600;

	/**
	 * {@value}
	 */
	public static final int WIDTH_FRAME = 800;

	/**
	 * {@value}
	 */
	private static final int HEIGHT_ARM_START = 15;

	/**
	 * {@value}
	 */
	private static final int WIDTH_ARM_START = 15;

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
		target = new Point(WIDTH_FRAME / 2 + 3 * Segment.LENGTH_SEGMENT
				- start.x, (HEIGHT_FRAME - HEIGHT_TARGET) / 2 - start.y);
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
		final int eventX = event.getX();
		final int eventY = event.getY();

		if (!isPointWithinSimulationBoundaries(eventX, eventY)) {
			return;
		}

		target.x = eventX - start.x;
		target.y = eventY - start.y;
		updateSegments();
	}

	private void updateSegments() {
		calculator.calculatePositions(target, start, segments);
		listener.onModelChanged(this);
	}

	private boolean isPointWithinSimulationBoundaries(final int x, final int y) {
		return x >= WIDTH_ARM_START && x <= WIDTH_FRAME - WIDTH_ARM_START
				&& y >= HEIGHT_ARM_START
				&& y <= HEIGHT_FRAME - HEIGHT_ARM_START;
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

	public void addSegment() {
		segments.add(new Segment());
		updateSegments();
	}

	public void removeSegment() {
		segments.remove(segments.size() - 1);
		updateSegments();
	}

	public void increaseSegmentLength() {
		Segment.LENGTH_SEGMENT += LENGTH_SEGMENT_UPDATE;
		updateSegments();
	}

	public void decreaseSegmentLength() {
		Segment.LENGTH_SEGMENT -= LENGTH_SEGMENT_UPDATE;
		updateSegments();
	}
}
