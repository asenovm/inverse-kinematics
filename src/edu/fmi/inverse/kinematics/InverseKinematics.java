package edu.fmi.inverse.kinematics;

import javax.swing.SwingUtilities;

import edu.fmi.inverse.kinematics.controller.OptionsMenuController;
import edu.fmi.inverse.kinematics.view.SimulationView;

public class InverseKinematics implements OptionsMenuController {

	private final SimulationView view;

	private final SimulationModel model;

	public InverseKinematics() {
		model = new SimulationModel();
		view = new SimulationView();

		view.setController(this);
		view.addMouseMotionListener(model);
		model.setOnChangedListener(view);
	}

	@Override
	public void addSegment() {
		model.addSegment();
	}

	@Override
	public void removeSegment() {
		model.removeSegment();
	}

	@Override
	public void increaseSegmentLength() {
		model.increaseSegmentLength();
	}

	@Override
	public void decreaseSegmentLength() {
		model.decreaseSegmentLength();
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final InverseKinematics inverseKinematics = new InverseKinematics();
			}
		});
	}
}
