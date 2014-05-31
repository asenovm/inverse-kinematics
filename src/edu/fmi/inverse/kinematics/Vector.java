package edu.fmi.inverse.kinematics;

public class Vector {
	public final double x;

	public final double y;

	public Vector(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public Vector substract(final Vector vector) {
		return new Vector(this.x - vector.x, this.y - vector.y);
	}

	public Vector multiply(final double scalar) {
		return new Vector(this.x * scalar, this.y * scalar);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double dot(final Vector vector) {
		return x * vector.x + y * vector.y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

}
