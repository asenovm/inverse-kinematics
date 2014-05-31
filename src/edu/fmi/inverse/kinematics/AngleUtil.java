package edu.fmi.inverse.kinematics;

public class AngleUtil {

	private AngleUtil() {
		// blank
	}

	public static double simplifyAngle(double angle) {
		angle = angle % (2.0 * Math.PI);
		if (angle < -Math.PI) {
			return angle + 2 * Math.PI;
		} else if (angle > Math.PI) {
			return angle - 2 * Math.PI;
		}
		return angle;
	}

}
