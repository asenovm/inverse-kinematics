package edu.fmi.inverse.kinematics.view;

import static edu.fmi.inverse.kinematics.SimulationModel.WIDTH_FRAME;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.fmi.inverse.kinematics.controller.OptionsMenuController;

public class OptionsPanel extends JPanel {

	/**
	 * {@value}
	 */
	private static final long serialVersionUID = 2759467912980869382L;

	/**
	 * {@value}
	 */
	private static final int HEIGHT_MENU = 60;

	private OptionsMenuController controller;

	public static class SimpleMenuController implements OptionsMenuController {
		@Override
		public void addSegment() {
			// blank
		}

		@Override
		public void removeSegment() {
			// blank
		}

		@Override
		public void increaseSegmentLength() {
			// blank
		}

		@Override
		public void decreaseSegmentLength() {
			// blank
		}
	}

	private class AddSegmentActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			controller.addSegment();
		}
	}

	private class RemoveSegmentActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			controller.removeSegment();
		}
	}

	private class IncreaseLengthActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			controller.increaseSegmentLength();
		}
	}

	private class DecreaseLengthActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			controller.decreaseSegmentLength();
		}
	}

	public OptionsPanel() {
		super(new FlowLayout(FlowLayout.CENTER));

		controller = new SimpleMenuController();

		final Dimension panelDimension = new Dimension(WIDTH_FRAME, HEIGHT_MENU);
		setPreferredSize(panelDimension);
		setMinimumSize(panelDimension);
		setMaximumSize(panelDimension);

		createAndAddButton(Label.ADD_SEGMENT, new AddSegmentActionListener());
		createAndAddButton(Label.REMOVE_SEGMENT,
				new RemoveSegmentActionListener());
		createAndAddButton(Label.INCREASE_LENGTH,
				new IncreaseLengthActionListener());
		createAndAddButton(Label.DECREASE_LENGTH,
				new DecreaseLengthActionListener());
	}

	private void createAndAddButton(final String text,
			final ActionListener listener) {
		final JButton button = new JButton(text);
		button.setFocusPainted(false);
		button.addActionListener(listener);
		add(button);
	}

	public void setController(final OptionsMenuController controller) {
		this.controller = controller;
	}

}
