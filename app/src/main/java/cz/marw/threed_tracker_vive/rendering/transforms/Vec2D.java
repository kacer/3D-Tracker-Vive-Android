package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Locale;
import java.util.Objects;
import java9.util.Optional;

/**
 * 2D vector over real numbers (final double-precision), equivalent to 2D affine
 * point, immutable
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */


public class Vec2D {
	private final double x, y;

	/**
	 * Creates a zero vector
	 */
	public Vec2D() {
		x = y = 0.0;
	}

	/**
	 * Creates a vector with the given coordinates
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public Vec2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a vector with all coordinates set to the given value
	 * 
	 * @param value
	 *            coordinate
	 */
	public Vec2D(final double value) {
		this.x = value;
		this.y = value;
	}
	
	/**
	 * Creates a vector by cloning the give one
	 * 
	 * @param v
	 *            vector to be cloned
	 */
	public Vec2D(final Vec2D v) {
		x = v.x;
		y = v.y;
	}
	

	/**
	 * Returns the x coordinate
	 * 
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate
	 * 
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns a clone of this vector with the x coordinate replaced by the
	 * given value
	 * 
	 * @param x
	 *            x coordinate
	 * @return new Vec2D instance
	 */
	public Vec2D withX(double x) {
		return new Vec2D(x, this.getY());
	}
	
	/**
	 * Returns a clone of this vector with the y coordinate replaced by the
	 * given value
	 * 
	 * @param y
	 *            y coordinate
	 * @return new Vec2D instance
	 */
	public Vec2D withY(double y) {
		return new Vec2D(this.getX(), y);
	}

	/**
	 * Returns the result of vector addition of the given vector
	 * 
	 * @param v
	 *            vector to add
	 * @return new Vec2D instance
	 */
	public Vec2D add(final Vec2D v) {
		return new Vec2D(x + v.x, y + v.y);
	}


	/**
	 * Returns the result of vector subtraction of the given vector
	 * 
	 * @param v
	 *            vector to subtract
	 * @return new Vec2D instance
	 */
	public Vec2D sub(final Vec2D v) {
		return new Vec2D(x - v.x, y - v.y);
	}
	
	/**
	 * Returns the result of scalar multiplication
	 * 
	 * @param d
	 *            scalar value of type double
	 * @return new Vec2D instance
	 */
	public Vec2D mul(final double d) {
		return new Vec2D(x * d, y * d);
	}

	/**
	 * Returns the result of element-wise multiplication with the given vector
	 * 
	 * @param v
	 *            2D vector 
	 * @return new Vec2D instance
	 */
	public Vec2D mul(final Vec2D v) {
		return new Vec2D(x * v.x, y * v.y);
	}

	/**
	 * Returns the result of dot-product with the given vector
	 * 
	 * @param v
	 *            2D vector 
	 * @return double-precision floating point value
	 */
	public double dot(final Vec2D v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Returns a collinear unit vector (by dividing all vector components by
	 * vector length) if possible (nonzero length), empty Optional otherwise
	 * 
	 * @return new Optional<Vec2D> instance
	 */
	public Optional<Vec2D> normalized() {
		final double len = length();
		if (len == 0.0)
			return Optional.empty();
		return Optional.of(new Vec2D(x / len, y / len));
	}


	/**
	 * Returns the vector opposite to this vector
	 * 
	 * @return new Vec2D instance
	 */
	public Vec2D opposite() {
		return new Vec2D(-x, -y);
	}
	
	/**
	 * Returns the length of this vector
	 * 
	 * @return double-precision floating point value
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Compares this object against the specified object.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null) && (obj instanceof Vec2D) 
				&& (new Double(((Vec2D) obj).getX()).equals(getX()))
				&& (new Double(((Vec2D) obj).getY()).equals(getY()));
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getX(), this.getY());
	}

	/**
	 * Compares this Vec2D against the specified Vec2D with epsilon.
	 * 
	 * @param vec
	 *            the vector to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both numbers are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec2D vec, double epsilon) {
		return (this == vec) || (vec != null) 
				&& Compare.eEquals(getX(), vec.getX(), epsilon)
				&& Compare.eEquals(getY(), vec.getY(), epsilon);
	}

	/**
	 * Compares this Vec2D against the specified Vec2D with epsilon.
	 * 
	 * @param vec
	 *            the vector to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec2D vec) {
		return eEquals(vec, Compare.EPSILON);
	}
	
	/**
	 * Returns String representation of this vector
	 * 
	 * @return comma separated floating-point values in brackets
	 */
	public String toString() {
		return toString("%4.1f");
	}

	/**
	 * Returns String representation of this vector with coordinates formated
	 * according to the given format, see
	 * {@link java.lang.String#format(String, Object...)}
	 * 
	 * @param format
	 *            String format applied to each coordinate
	 * @return comma separated floating-point values in brackets
	 */
	public String toString(String format) {
		return String.format(Locale.US, "(" + format + "," + format + ")",	x, y);
	}
}
