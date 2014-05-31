package edu.fmi.inverse.kinematics;

import javax.swing.SwingUtilities;

import edu.fmi.inverse.kinematics.view.SimulationView;

public class InverseKinematics {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final SimulationModel model = new SimulationModel();
				final SimulationView view = new SimulationView();
				model.setOnChangedListener(view);
			}
		});
	}
}
