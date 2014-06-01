package edu.fmi.inverse.kinematics.view;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

import edu.fmi.inverse.kinematics.ModelListener;
import edu.fmi.inverse.kinematics.SimulationModel;
import edu.fmi.inverse.kinematics.controller.OptionsMenuController;

public class SimulationView extends JFrame implements ModelListener {

	/**
	 * {@value}
	 */
	private static final long serialVersionUID = 7161434511170496662L;

	/**
	 * {@value}
	 */
	private static final String TITLE_FRAME = "Inverse Kinamatics - CCD simulation";

	private final SimulationPanel simulationPanel;

	private final OptionsPanel optionsPanel;

	public SimulationView(String title, GraphicsConfiguration gc) {
		super(title, gc);

		setLayout(new BorderLayout());

		simulationPanel = new SimulationPanel();
		optionsPanel = new OptionsPanel();

		add(simulationPanel, BorderLayout.PAGE_START);
		add(optionsPanel, BorderLayout.PAGE_END);

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	public SimulationView() throws HeadlessException {
		this(TITLE_FRAME, null);
	}

	public SimulationView(GraphicsConfiguration gc) {
		this(TITLE_FRAME, gc);
	}

	public SimulationView(String title) throws HeadlessException {
		this(title, null);
	}

	@Override
	public void onModelChanged(SimulationModel model) {
		simulationPanel.onModelChanged(model);
	}

	public void setController(final OptionsMenuController controller) {
		optionsPanel.setController(controller);
	}

}
