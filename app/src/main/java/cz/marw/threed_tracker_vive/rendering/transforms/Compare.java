package cz.marw.threed_tracker_vive.rendering.transforms;

public class Compare {
	protected static final double EPSILON = 1e-15;
	
	/**
	 * Compares two double values for equivalence with the maximum relative difference between them
	 * given as a fraction of the values.
	 * 
	 * @param value1
	 *            the first double to compare
	 * @param value2
	 *            the second double to compare
	 * @param epsilon
	 *            the maximum relative difference - a fraction of the values
	 *            in which the values can differ and still be considered equal.
	 *            E.g. with epsilon of 0.1 the difference must not exceed 10% of either value
	 * @return {@code true} if the values are considered equal; {@code false}
	 *         otherwise.
	 */
	public static boolean eEquals(double value1, double value2, double epsilon) {
		return (Math.abs(value1 - value2) <= epsilon * Math.abs(value1))
				&& (Math.abs(value1 - value2) <= epsilon * Math.abs(value2));
	}

	/**
	 * Compares two double values for equivalence with the default maximum relative difference between them.
	 *
	 * @param value1
	 *            the first double to compare
	 * @param value2
	 *            the second double to compare
	 * @return {@code true} if the values are considered equal; {@code false}
	 *         otherwise.
	 */
	public static boolean eEquals(double value1, double value2) {
		return eEquals(value1, value2, EPSILON);
	}


}
