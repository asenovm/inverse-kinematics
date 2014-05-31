package edu.fmi.inverse.kinematics;

public class Segment {

	private double length;

	public double angle;

	public int startX;

	public int startY;

	public int endX;

	public int endY;

	public Segment(final double length) {
		this.length = length;
	}

	public double getLength() {
		return length;
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
