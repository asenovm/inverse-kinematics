package edu.fmi.inverse.kinematics;

public class Segment {

	private double length;

	public double angle;

	public double startX;

	public double startY;

	public double endX;

	public double endY;
	
	public double sinAngle;
	
	public double cosAngle;

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
