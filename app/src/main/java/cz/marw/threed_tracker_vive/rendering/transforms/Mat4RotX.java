package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of right-handed rotation about x-axis
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4RotX extends Mat4Identity {

	/**
	 * Creates a 4x4 transformation matrix equivalent to right-handed rotation
	 * about x-axis
	 * 
	 * @param alpha
	 *            rotation angle in radians
	 */
	public Mat4RotX(final double alpha) {
		mat[1][1] = Math.cos(alpha);
		mat[2][2] = Math.cos(alpha);
		mat[2][1] = -Math.sin(alpha);
		mat[1][2] = Math.sin(alpha);
	}
}