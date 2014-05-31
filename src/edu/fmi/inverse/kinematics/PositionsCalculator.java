package edu.fmi.inverse.kinematics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PositionsCalculator {

	public static interface PositionListener {
		void onPositionChanged();
	}

	public void calculatePositions(final Point target, final Point start,
			final List<Segment> segments) {
		// calculate the bone angles
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

		int count = 0;
		System.out.println("x and y are " + target.x + " " + target.y);
		while (CalcIK_2D_CCD(ccdBones, target.x - 400, target.y - 300, 1) == CCDResult.Processing
				|| count < 1000) {
			++count;
		}
		// System.out.println("res is " + CalcIK_2D_CCD(ccdBones, target.x,
		// target.y, 1));
		// iterate CCD until limit is reached or we find a valid solution
		// while (CalcIK_2D_CCD(ccdBones, target.x, target.y, 0.0001) !=
		// CCDResult.Success) {
		// System.out.println("loop");
		// blank
		// }

		// extract the new bone data from the results
		for (int boneIdx = 0; boneIdx < segments.size(); ++boneIdx) {
			final Segment segment = segments.get(boneIdx);
			segment.angle = ccdBones.get(boneIdx).angle;
			segment.startX = (int) ccdBones.get(boneIdx).x;
			segment.startY = (int) ccdBones.get(boneIdx).y;
		}

		// while (CalcIK_2D_CCD(bones, target.x, target.y, 0.0001) !=
		// CCDResult.Success) {
		// for (int i = 0; i < bones.size(); ++i) {
		// final Bone_2D_CCD bone = bones.get(i);
		// final Segment segment = segments.get(i);
		// final double angle = bone.angle - 0.85;
		// final double newEndX = segment.startX - segment.length()
		// * Math.cos(angle);
		// final double newEndY = segment.startY - segment.length()
		// * Math.sin(angle);
		// segment.endX = newEndX;
		// segment.endY = newEndY;
		// if (i < bones.size() - 1) {
		// segments.get(i + 1).startX = segment.endX;
		// segments.get(i + 1).startY = segment.endY;
		// }
		// }
		// listener.onPositionChanged();
		// }
	}

	public static double simplifyAngle(double angle) {
		angle = angle % (2.0 * Math.PI);
		if (angle < -Math.PI)
			angle += (2.0 * Math.PI);
		else if (angle > Math.PI)
			angle -= (2.0 * Math.PI);
		return angle;
	}

	// /***************************************************************************************
	// / Bone_2D_CCD_World
	// / This class is used internally by the CalcIK_2D_CCD function to
	// represent a bone in
	// / world space.
	// /***************************************************************************************
	private class Bone_2D_CCD_World {
		public double x; // x position in world space
		public double y; // y position in world space
		public double angle; // angle in world space
		public double cosAngle; // sine of angle
		public double sinAngle; // cosine of angle

		@Override
		public String toString() {
			return "Bone_2D_CCD_World [x=" + x + ", y=" + y + ", angle="
					+ angle + ", cosAngle=" + cosAngle + ", sinAngle="
					+ sinAngle + "]";
		}

	}

	// /***************************************************************************************
	// / CCDResult
	// / This enum represents the resulting state of a CCD iteration.
	// /***************************************************************************************
	public enum CCDResult {
		Success, // the target was reached
		Processing, // still trying to reach the target
		Failure; // failed to reach the target
	}

	public class Bone_2D_CCD {
		public double x; // x position in parent space
		public double y; // y position in parent space
		public double angle; // angle in parent space

		@Override
		public String toString() {
			return "Bone_2D_CCD [x=" + x + ", y=" + y + ", angle=" + angle
					+ "]";
		}

	}

	// /***************************************************************************************
	// / CalcIK_2D_CCD
	// / Given a bone chain located at the origin, this function will perform a
	// single cyclic
	// / coordinate descent (CCD) iteration. This finds a solution of bone
	// angles that places
	// / the final bone in the given chain at a target position. The supplied
	// bone angles are
	// / used to prime the CCD iteration. If a valid solution does not exist,
	// the angles will
	// / move as close to the target as possible. The user should resupply the
	// updated angles
	// / until a valid solution is found (or until an iteration limit is met).
	// /
	// / returns: CCDResult.Success when a valid solution was found.
	// / CCDResult.Processing when still searching for a valid solution.
	// / CCDResult.Failure when it can get no closer to the target.
	// /***************************************************************************************
	public CCDResult CalcIK_2D_CCD(List<Bone_2D_CCD> bones, double targetX,
			double targetY, double arrivalDist) {
		// Set an epsilon value to prevent division by small numbers.
		final double epsilon = 0.0001;

		// Set max arc length a bone can move the end effector an be considered
		// no motion
		// so that we can detect a failure state.
		final double trivialArcLength = 0.00001;

		int numBones = bones.size();

		double arrivalDistSqr = arrivalDist * arrivalDist;

		// ===
		// Generate the world space bone data.
		List<Bone_2D_CCD_World> worldBones = new ArrayList<Bone_2D_CCD_World>();

		// Start with the root bone.
		Bone_2D_CCD_World rootWorldBone = new Bone_2D_CCD_World();
		rootWorldBone.x = bones.get(0).x;
		rootWorldBone.y = bones.get(0).y;
		rootWorldBone.angle = bones.get(0).angle;
		rootWorldBone.cosAngle = Math.cos(rootWorldBone.angle);
		rootWorldBone.sinAngle = Math.sin(rootWorldBone.angle);
		worldBones.add(rootWorldBone);

		// Convert child bones to world space.
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

		// ===
		// Track the end effector position (the final bone)
		double endX = worldBones.get(numBones - 1).x;
		double endY = worldBones.get(numBones - 1).y;

		// ===
		// Perform CCD on the bones by optimizing each bone in a loop
		// from the final bone to the root bone
		boolean modifiedBones = false;
		for (int boneIdx = numBones - 2; boneIdx >= 0; --boneIdx) {
			// Get the vector from the current bone to the end effector
			// position.
			double curToEndX = endX - worldBones.get(boneIdx).x;
			double curToEndY = endY - worldBones.get(boneIdx).y;
			double curToEndMag = Math.sqrt(curToEndX * curToEndX + curToEndY
					* curToEndY);

			// Get the vector from the current bone to the target position.
			double curToTargetX = targetX - worldBones.get(boneIdx).x;
			double curToTargetY = targetY - worldBones.get(boneIdx).y;
			double curToTargetMag = Math.sqrt(curToTargetX * curToTargetX
					+ curToTargetY * curToTargetY);

			// Get rotation to place the end effector on the line from the
			// current
			// joint position to the target postion.
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

			// Clamp the cosine into range when computing the angle (might be
			// out of range
			// due to floating point error).
			double rotAng = Math.acos(Math.max(-1, Math.min(1, cosRotAng)));
			if (sinRotAng < 0.0)
				rotAng = -rotAng;

			// Rotate the end effector position.
			endX = worldBones.get(boneIdx).x + cosRotAng * curToEndX
					- sinRotAng * curToEndY;
			endY = worldBones.get(boneIdx).y + sinRotAng * curToEndX
					+ cosRotAng * curToEndY;

			// Rotate the current bone in local space (this value is output to
			// the user)
			bones.get(boneIdx).angle = simplifyAngle(bones.get(boneIdx).angle
					+ rotAng);

			// Check for termination
			double endToTargetX = (targetX - endX);
			double endToTargetY = (targetY - endY);
			if (endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr) {
				// We found a valid solution.
				return CCDResult.Success;
			}

			// Track if the arc length that we moved the end effector was
			// a nontrivial distance.
			if (!modifiedBones
					&& Math.abs(rotAng) * curToEndMag > trivialArcLength) {
				modifiedBones = true;
			}
		}

		// We failed to find a valid solution during this iteration.
		if (modifiedBones)
			return CCDResult.Processing;
		else
			return CCDResult.Failure;
	}

}