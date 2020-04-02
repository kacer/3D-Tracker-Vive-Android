package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 3x3 matrix of right-handed rotation about x-axis
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat3RotX extends Mat3Identity {

	/**
	 * Creates a 3x3 transformation matrix equivalent to right-handed rotation
	 * about x-axis
	 * 
	 * @param alpha
	 *            rotation angle in radians
	 */
	public Mat3RotX(final double alpha) {
		mat[1][1] = Math.cos(alpha);
		mat[2][2] = Math.cos(alpha);
		mat[2][1] = -Math.sin(alpha);
		mat[1][2] = Math.sin(alpha);
	}
}