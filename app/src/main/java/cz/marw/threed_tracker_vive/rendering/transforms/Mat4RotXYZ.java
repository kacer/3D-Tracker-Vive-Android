package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of sequential right-handed rotation about x, y and z axes
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4RotXYZ extends Mat4 {

	/**
	 * Creates a 4x4 transformation matrix equivalent to right-handed rotations
	 * about x, y and z axes chained in sequence in this order
	 * 
	 * @param alpha
	 *            rotation angle about x-axis, in radians
	 * @param beta
	 *            rotation angle about y-axis, in radians
	 * @param gamma
	 *            rotation angle about z-axis, in radians
	 */
	public Mat4RotXYZ(final double alpha, final double beta, final double gamma) {
		super(new Mat4RotX(alpha).mul(new Mat4RotY(beta)).mul(
				new Mat4RotZ(gamma)));
	}
}