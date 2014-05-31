package edu.fmi.inverse.kinematics;

public class Segment {

	public static final int LENGTH_SEGMENT = 60;

	private double length;

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

	public double getAngle() {
		return angle;
	}

	@Override
	public String toString() {
		return "Segment [length=" + length + ", angle=" + angle + ", startX="
				+ startX + ", startY=" + startY + ", endX=" + endX + ", endY="
				+ endY + "]";
	}

}
