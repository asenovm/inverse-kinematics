package edu.fmi.inverse.kinematics;

public class Segment {

	public static int LENGTH_SEGMENT = 60;

	public double angle;

	public double startX;

	public double startY;

	public double endX;

	public double endY;

	public double sinAngle;

	public double cosAngle;

	public double getLength() {
		return LENGTH_SEGMENT;
	}

}
