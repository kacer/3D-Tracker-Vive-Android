package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 3x3 matrix of right-handed rotation about z-axis
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */public class Mat3Rot2D extends Mat3Identity {

	/**
	 * Creates a 3x3 transformation matrix equivalent to right-handed rotation
	 * about z-axis
	 * 
	 * @param alpha
	 *            rotation angle in radians
	 */
	public Mat3Rot2D(final double alpha) {
		mat[0][0] = Math.cos(alpha);
		mat[1][1] = Math.cos(alpha);
		mat[1][0] = -Math.sin(alpha);
		mat[0][1] = Math.sin(alpha);
	}
}