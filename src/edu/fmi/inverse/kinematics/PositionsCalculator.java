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
		List<Bone_2D_CCD> ccdBones = new LinkedList<Bone_2D_CCD>();
		for (int boneIdx = 0; boneIdx <= segments.size(); ++boneIdx) {
			Bone_2D_CCD newCcdBone = new Bone_2D_CCD();
			newCcdBone.angle = (boneIdx < segments.size()) ? segments.get(
					boneIdx).getAngle() : 0;
			newCcdBone.x = (boneIdx > 0) ? segments.get(boneIdx - 1)
					.getLength() : 0;
			newCcdBone.y = 0;

			ccdBones.add(newCcdBone);
		}

		int iterations = 0;
		while (CalcIK_2D_CCD(ccdBones, target.x - 400, target.y - 300, 1) == CCDResult.PROCESSING
				|| iterations < ITERATION_COUNT_MAX) {
			++iterations;
		}

		for (int boneIdx = 0; boneIdx < segments.size(); ++boneIdx) {
			final Segment segment = segments.get(boneIdx);
			segment.angle = ccdBones.get(boneIdx).angle;
			segment.startX = (int) ccdBones.get(boneIdx).x;
			segment.startY = (int) ccdBones.get(boneIdx).y;
		}
	}

	public static double simplifyAngle(double angle) {
		angle = angle % (2.0 * Math.PI);
		if (angle < -Math.PI)
			angle += (2.0 * Math.PI);
		else if (angle > Math.PI)
			angle -= (2.0 * Math.PI);
		return angle;
	}

	private class Bone_2D_CCD_World {
		public double x;
		public double y;
		public double angle;
		public double cosAngle;
		public double sinAngle;
	}

	public enum CCDResult {
		SUCCESS, PROCESSING, FAILURE;
	}

	public class Bone_2D_CCD {
		public double x;
		public double y;
		public double angle;
	}

	public CCDResult CalcIK_2D_CCD(List<Bone_2D_CCD> bones, double targetX,
			double targetY, double arrivalDist) {

		final double epsilon = 0.0001;
		final double trivialArcLength = 0.00001;
		int numBones = bones.size();
		double arrivalDistSqr = arrivalDist * arrivalDist;

		List<Bone_2D_CCD_World> worldBones = new ArrayList<Bone_2D_CCD_World>();

		Bone_2D_CCD_World rootWorldBone = new Bone_2D_CCD_World();
		rootWorldBone.x = bones.get(0).x;
		rootWorldBone.y = bones.get(0).y;
		rootWorldBone.angle = bones.get(0).angle;
		rootWorldBone.cosAngle = Math.cos(rootWorldBone.angle);
		rootWorldBone.sinAngle = Math.sin(rootWorldBone.angle);
		worldBones.add(rootWorldBone);

		for (int boneIdx = 1; boneIdx < numBones; ++boneIdx) {
			Bone_2D_CCD_World prevWorldBone = worldBones.get(boneIdx - 1);
			Bone_2D_CCD curLocalBone = bones.get(boneIdx);

			Bone_2D_CCD_World newWorldBone = new Bone_2D_CCD_World();
			newWorldBone.x = prevWorldBone.x + prevWorldBone.cosAngle
					* curLocalBone.x - prevWorldBone.sinAngle * curLocalBone.y;
			newWorldBone.y = prevWorldBone.y + prevWorldBone.sinAngle
					* curLocalBone.x + prevWorldBone.cosAngle * curLocalBone.y;
			newWorldBone.angle = prevWorldBone.angle + curLocalBone.angle;
			newWorldBone.cosAngle = Math.cos(newWorldBone.angle);
			newWorldBone.sinAngle = Math.sin(newWorldBone.angle);
			worldBones.add(newWorldBone);
		}

		double endX = worldBones.get(numBones - 1).x;
		double endY = worldBones.get(numBones - 1).y;

		boolean modifiedBones = false;
		for (int boneIdx = numBones - 2; boneIdx >= 0; --boneIdx) {
			double curToEndX = endX - worldBones.get(boneIdx).x;
			double curToEndY = endY - worldBones.get(boneIdx).y;
			double curToEndMag = Math.sqrt(curToEndX * curToEndX + curToEndY
					* curToEndY);

			double curToTargetX = targetX - worldBones.get(boneIdx).x;
			double curToTargetY = targetY - worldBones.get(boneIdx).y;
			double curToTargetMag = Math.sqrt(curToTargetX * curToTargetX
					+ curToTargetY * curToTargetY);

			double cosRotAng;
			double sinRotAng;
			double endTargetMag = (curToEndMag * curToTargetMag);
			if (endTargetMag <= epsilon) {
				cosRotAng = 1;
				sinRotAng = 0;
			} else {
				cosRotAng = (curToEndX * curToTargetX + curToEndY
						* curToTargetY)
						/ endTargetMag;
				sinRotAng = (curToEndX * curToTargetY - curToEndY
						* curToTargetX)
						/ endTargetMag;
			}

			double rotAng = Math.acos(Math.max(-1, Math.min(1, cosRotAng)));
			if (sinRotAng < 0.0) {
				rotAng = -rotAng;
			}

			endX = worldBones.get(boneIdx).x + cosRotAng * curToEndX
					- sinRotAng * curToEndY;
			endY = worldBones.get(boneIdx).y + sinRotAng * curToEndX
					+ cosRotAng * curToEndY;

			bones.get(boneIdx).angle = simplifyAngle(bones.get(boneIdx).angle
					+ rotAng);

			double endToTargetX = (targetX - endX);
			double endToTargetY = (targetY - endY);
			if (endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr) {
				return CCDResult.SUCCESS;
			}

			modifiedBones = !modifiedBones
					&& Math.abs(rotAng) * curToEndMag > trivialArcLength;
		}

		if (modifiedBones) {
			return CCDResult.PROCESSING;
		} else {
			return CCDResult.FAILURE;
		}
	}

}