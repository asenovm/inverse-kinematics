package edu.fmi.inverse.kinematics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimulationModel {

	private final List<Segment> segments;

	public SimulationModel() {
		segments = new LinkedList<Segment>();
		segments.add(new Segment());
		segments.add(new Segment());
		segments.add(new Segment());
	}

	public void setOnChangedListener(final ModelListener listener) {
		listener.onModelChanged(this);
	}

	public List<Segment> getSegments() {
		return Collections.unmodifiableList(segments);
	}
}
