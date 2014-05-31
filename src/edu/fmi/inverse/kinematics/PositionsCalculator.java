package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PositionsCalculator {

	/**
	 * {@value}
	 */
	private static final double LENGTH_TRIVIAL_ARC = 0.00001;

	/**
	 * {@value}
	 */
	private static final double EPSILON = 0.0001;

	/**
	 * {@value}
	 */
	private static final int ITERATION_COUNT_MAX = 50;

	public void calculatePositions(final Point target, final Point start,
			final List<Segment> segments) {
		List<Segment> ccdBones = new LinkedList<Segment>();
		for (int i = 0; i <= segments.size(); ++i) {
			Segment segment = new Segment();
			segment.angle = getAngleForSegment(segments, i);
			segment.startX = getStartXForSegment(segments, i);
			segment.startY = 0;
			ccdBones.add(segment);
		}

		int iterations = 0;
		while (calculatePositions(ccdBones, target.x, target.y) == CalculationState.PROCESSING
				|| iterations < ITERATION_COUNT_MAX) {
			++iterations;
		}

		for (int i = 0; i < segments.size(); ++i) {
			final Segment segment = segments.get(i);
			segment.angle = ccdBones.get(i).angle;
			segment.startX = (int) ccdBones.get(i).startX;
			segment.startY = (int) ccdBones.get(i).startY;
		}
	}

	private int getStartXForSegment(final List<Segment> segments, int i) {
		return (int) ((i > 0) ? segments.get(i - 1).getLength() : 0);
	}

	private double getAngleForSegment(final List<Segment> segments, int i) {
		return (i < segments.size()) ? segments.get(i).getAngle() : 0;
	}

	private CalculationState calculatePositions(List<Segment> segments,
			double targetX, double targetY) {

		double arrivalDistSqr = 1;

		List<Segment> resultSegments = getResultSegments(segments);

		double endX = resultSegments.get(segments.size() - 1).startX;
		double endY = resultSegments.get(segments.size() - 1).startY;

		boolean isModified = false;

		for (int i = segments.size() - 2; i >= 0; --i) {
			double currentToEndX = endX - resultSegments.get(i).startX;
			double currentToEndY = endY - resultSegments.get(i).startY;
			double currentToEndLength = Math.sqrt(currentToEndX * currentToEndX
					+ currentToEndY * currentToEndY);

			double currentToTargetX = targetX - resultSegments.get(i).startX;
			double currentToTargetY = targetY - resultSegments.get(i).startY;
			double currentToTargetLength = Math.sqrt(currentToTargetX
					* currentToTargetX + currentToTargetY * currentToTargetY);

			double cosRotationAngle;
			double sinRotationAngle;

			double endTargetLength = (currentToEndLength * currentToTargetLength);

			if (endTargetLength <= EPSILON) {
				cosRotationAngle = 1;
				sinRotationAngle = 0;
			} else {
				cosRotationAngle = (currentToEndX * currentToTargetX + currentToEndY
						* currentToTargetY)
						/ endTargetLength;
				sinRotationAngle = (currentToEndX * currentToTargetY - currentToEndY
						* currentToTargetX)
						/ endTargetLength;
			}

			double rotationAngle = Math.acos(Math.max(-1,
					Math.min(1, cosRotationAngle)));
			if (sinRotationAngle < 0.0) {
				rotationAngle = -rotationAngle;
			}

			endX = resultSegments.get(i).startX + cosRotationAngle
					* currentToEndX - sinRotationAngle * currentToEndY;
			endY = resultSegments.get(i).startY + sinRotationAngle
					* currentToEndX + cosRotationAngle * currentToEndY;

			segments.get(i).angle = AngleUtil
					.simplifyAngle(segments.get(i).angle + rotationAngle);

			double endToTargetX = (targetX - endX);
			double endToTargetY = (targetY - endY);

			if (endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr) {
				return CalculationState.SUCCESS;
			}

			isModified = !isModified
					&& Math.abs(rotationAngle) * currentToEndLength > LENGTH_TRIVIAL_ARC;
		}

		if (isModified) {
			return CalculationState.PROCESSING;
		} else {
			return CalculationState.FAILURE;
		}
	}

	private List<Segment> getResultSegments(List<Segment> segments) {
		List<Segment> resultSegments = new ArrayList<Segment>();

		Segment endSegment = new Segment();
		endSegment.startX = segments.get(0).startX;
		endSegment.startY = segments.get(0).startY;
		endSegment.angle = segments.get(0).angle;
		endSegment.cosAngle = Math.cos(endSegment.angle);
		endSegment.sinAngle = Math.sin(endSegment.angle);
		resultSegments.add(endSegment);

		for (int i = 1; i < segments.size(); ++i) {
			Segment previousSegment = resultSegments.get(i - 1);
			Segment currentSegment = segments.get(i);

			Segment resultSegment = new Segment();
			resultSegment.startX = previousSegment.startX
					+ previousSegment.cosAngle * currentSegment.startX
					- previousSegment.sinAngle * currentSegment.startY;
			resultSegment.startY = previousSegment.startY
					+ previousSegment.sinAngle * currentSegment.startX
					+ previousSegment.cosAngle * currentSegment.startY;
			resultSegment.angle = previousSegment.angle + currentSegment.angle;
			resultSegment.cosAngle = Math.cos(resultSegment.angle);
			resultSegment.sinAngle = Math.sin(resultSegment.angle);
			resultSegments.add(resultSegment);
		}
		return resultSegments;
	}

}