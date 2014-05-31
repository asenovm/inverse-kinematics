package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PositionsCalculator {

	private static final int ITERATION_COUNT_MAX = 1000;

	public static interface PositionListener {
		void onPositionChanged();
	}

	public void calculatePositions(final Point target, final Point start,
			final List<Segment> segments) {
		List<Segment> ccdBones = new LinkedList<Segment>();
		for (int i = 0; i <= segments.size(); ++i) {
			Segment segment = new Segment(50);
			segment.angle = getAngleForSegment(segments, i);
			segment.startX = getStartXForSegment(segments, i);
			segment.startY = 0;
			ccdBones.add(segment);
		}

		int iterations = 0;
		while (CalcIK_2D_CCD(ccdBones, target.x - 400, target.y - 300, 1) == CalculationState.PROCESSING
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

	public enum CalculationState {
		SUCCESS, PROCESSING, FAILURE;
	}

	public CalculationState CalcIK_2D_CCD(List<Segment> bones, double targetX,
			double targetY, double arrivalDist) {

		final double epsilon = 0.0001;
		final double trivialArcLength = 0.00001;
		double arrivalDistSqr = arrivalDist * arrivalDist;

		List<Segment> worldBones = new ArrayList<Segment>();

		Segment rootWorldBone = new Segment(50);
		rootWorldBone.startX = bones.get(0).startX;
		rootWorldBone.startY = bones.get(0).startY;
		rootWorldBone.angle = bones.get(0).angle;
		rootWorldBone.cosAngle = Math.cos(rootWorldBone.angle);
		rootWorldBone.sinAngle = Math.sin(rootWorldBone.angle);
		worldBones.add(rootWorldBone);

		for (int i = 1; i < bones.size(); ++i) {
			Segment prevWorldBone = worldBones.get(i - 1);
			Segment curLocalBone = bones.get(i);

			Segment newWorldBone = new Segment(50);
			newWorldBone.startX = prevWorldBone.startX + prevWorldBone.cosAngle
					* curLocalBone.startX - prevWorldBone.sinAngle
					* curLocalBone.startY;
			newWorldBone.startY = prevWorldBone.startY + prevWorldBone.sinAngle
					* curLocalBone.startX + prevWorldBone.cosAngle
					* curLocalBone.startY;
			newWorldBone.angle = prevWorldBone.angle + curLocalBone.angle;
			newWorldBone.cosAngle = Math.cos(newWorldBone.angle);
			newWorldBone.sinAngle = Math.sin(newWorldBone.angle);
			worldBones.add(newWorldBone);
		}

		double endX = worldBones.get(bones.size() - 1).startX;
		double endY = worldBones.get(bones.size() - 1).startY;

		boolean isModified = false;

		for (int i = bones.size() - 2; i >= 0; --i) {
			double curToEndX = endX - worldBones.get(i).startX;
			double curToEndY = endY - worldBones.get(i).startY;
			double curToEndLength = Math.sqrt(curToEndX * curToEndX + curToEndY
					* curToEndY);

			double curToTargetX = targetX - worldBones.get(i).startX;
			double curToTargetY = targetY - worldBones.get(i).startY;
			double curToTargetLength = Math.sqrt(curToTargetX * curToTargetX
					+ curToTargetY * curToTargetY);

			double cosRotationAngle;
			double sinRotationAngle;

			double endTargetMag = (curToEndLength * curToTargetLength);

			if (endTargetMag <= epsilon) {
				cosRotationAngle = 1;
				sinRotationAngle = 0;
			} else {
				cosRotationAngle = (curToEndX * curToTargetX + curToEndY
						* curToTargetY)
						/ endTargetMag;
				sinRotationAngle = (curToEndX * curToTargetY - curToEndY
						* curToTargetX)
						/ endTargetMag;
			}

			double rotationAngle = Math.acos(Math.max(-1,
					Math.min(1, cosRotationAngle)));
			if (sinRotationAngle < 0.0) {
				rotationAngle = -rotationAngle;
			}

			endX = worldBones.get(i).startX + cosRotationAngle * curToEndX
					- sinRotationAngle * curToEndY;
			endY = worldBones.get(i).startY + sinRotationAngle * curToEndX
					+ cosRotationAngle * curToEndY;

			bones.get(i).angle = AngleUtil.simplifyAngle(bones.get(i).angle
					+ rotationAngle);

			double endToTargetX = (targetX - endX);
			double endToTargetY = (targetY - endY);
			
			if (endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr) {
				return CalculationState.SUCCESS;
			}

			isModified = !isModified
					&& Math.abs(rotationAngle) * curToEndLength > trivialArcLength;
		}

		if (isModified) {
			return CalculationState.PROCESSING;
		} else {
			return CalculationState.FAILURE;
		}
	}

}