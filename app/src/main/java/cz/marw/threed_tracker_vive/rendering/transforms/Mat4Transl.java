package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of translation
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4Transl extends Mat4Identity {

	/**
	 * Creates a 4x4 transformation matrix equivalent to translation in 3D
	 * 
	 * @param x
	 *            translation along x-axis
	 * @param y
	 *            translation along y-axis
	 * @param z
	 *            translation along z-axis
	 */
	public Mat4Transl(final double x, final double y, final double z) {
		mat[3][0] = x;
		mat[3][1] = y;
		mat[3][2] = z;
	}
	/**
	 * Creates a 4x4 transformation matrix equivalent to translation in 3D
	 * 
	 * @param v
	 *            translation vector
	 */
	public Mat4Transl(final Vec3D v) {
		this(v.getX(), v.getY(), v.getZ());
	}

}