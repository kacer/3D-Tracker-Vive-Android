package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Locale;
import java.util.Objects;

/**
 * 1D vector over real numbers (final double-precision), equivalent to 1D affine
 * point, immutable
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */

public class Vec1D {
	private final double x;

	/**
	 * Creates a zero vector
	 */
	public Vec1D() {
		x = 0.0;
	}

	/**
	 * Creates a vector with the given coordinate
	 * 
	 * @param x
	 *            x coordinate
	 */
	public Vec1D(final double x) {
		this.x = x;
	}

	/**
	 * Creates a vector by cloning the give one
	 * 
	 * @param v
	 *            vector to be cloned
	 */
	public Vec1D(final Vec1D v) {
		x = v.x;
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
	 * Returns the result of vector addition of the given vector
	 * 
	 * @param v
	 *            vector to add
	 * @return new Vec1D instance
	 */
	public Vec1D add(final Vec1D v) {
		return new Vec1D(x + v.x);
	}


	/**
	 * Returns the result of vector subtraction of the given vector
	 * 
	 * @param v
	 *            vector to subtract
	 * @return new Vec1D instance
	 */
	public Vec1D sub(final Vec1D v) {
		return new Vec1D(x - v.x);
	}
	
	/**
	 * Returns the result of scalar multiplication
	 * 
	 * @param d
	 *            scalar value of type double
	 * @return new Vec1D instance
	 */
	public Vec1D mul(final double d) {
		return new Vec1D(x * d);
	}

	/**
	 * Returns the vector opposite to this vector
	 * 
	 * @return new Vec1D instance
	 */
	public Vec1D opposite() {
		return new Vec1D(-x);
	}

	/**
     * Returns a hash code value for the object. 
     * 
     * @return  a hash code value for this object.
     */
    @Override
	public int hashCode(){
		return Objects.hash(this.getX());
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
		return (this == obj) || (obj != null) && (obj instanceof Vec1D) && (new Double(((Vec1D) obj).getX()).equals(getX()));
	}

	/**
	 * Compares this Vec1D against the specified Vec1D with epsilon.
	 * 
	 * @param vec
	 *            the vector to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both numbers are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec1D vec, double epsilon) {
		return (this == vec) || (vec != null) 
				&& Compare.eEquals(getX(), vec.getX(), epsilon);
	}

	/**
	 * Compares this Vec1D against the specified Vec1D with epsilon.
	 * 
	 * @param vec
	 *            the vector to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec1D vec) {
		return eEquals(vec, Compare.EPSILON);
	}

	/**
	 * Returns String representation of this vector
	 * 
	 * @return floating-point value in parentheses
	 */
	@Override
	public String toString() {
		return toString("%4.1f");
	}
	
	/**
	 * Returns String representation of this vector with coordinate formated
	 * according to the given format, see
	 * {@link java.lang.String#format(String, Object...)}
	 * 
	 * @param format
	 *            String format applied to the coordinate
	 * @return floating-point value in parentheses, useful in constructor
	 */
	public String toString(String format) {
		return String.format(Locale.US, "("+format+")",x);
	}
}
