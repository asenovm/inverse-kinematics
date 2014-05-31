package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimulationModel implements
		edu.fmi.inverse.kinematics.PositionsCalculator.PositionListener {

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
	private static final int HEIGHT_FRAME = 600;

	/**
	 * {@value}
	 */
	private static final int WIDTH_FRAME = 800;

	private final List<Segment> segments;

	private final Point target;

	private final Point start;

	private ModelListener listener;

	public SimulationModel() {
		start = new Point((WIDTH_FRAME - WIDTH_ARM_START) / 2,
				(HEIGHT_FRAME - HEIGHT_ARM_START) / 2);
		target = new Point(start.x + 170, start.y);

		segments = new LinkedList<Segment>();
		segments.add(new Segment(50));
		segments.add(new Segment(50));
		segments.add(new Segment(50));

		new PositionsCalculator().calculatePositions(target, start, segments);
	}

	public void setOnChangedListener(final ModelListener listener) {
		this.listener = listener;
		listener.onModelChanged(this);
	}

	public List<Segment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	@Override
	public void onPositionChanged() {
		if (listener != null) {
			listener.onModelChanged(this);
		}
	}
}
