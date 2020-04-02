package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of 3D scaling
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4Scale extends Mat4Identity {

	/**
	 * Creates a 4x4 transformation matrix equivalent to scaling in 3D
	 * 
	 * @param x
	 *            x-axis scale factor
	 * @param y
	 *            y-axis scale factor
	 * @param z
	 *            z-axis scale factor
	 */
	public Mat4Scale(final double x, final double y, final double z) {
		mat[0][0] = x;
		mat[1][1] = y;
		mat[2][2] = z;
	}

	/**
	 * Creates a 4x4 transformation matrix equivalent to uniform scaling in 3D
	 * 
	 * @param scale
	 *            x,y,z -axis scale factor
	 */
	public Mat4Scale(final double scale) {
		this(scale, scale, scale);
	}

	/**
	 * Creates a 4x4 transformation matrix equivalent to scaling in 3D
	 * 
	 * @param v
	 *            vector scale factor
	 */
	public Mat4Scale(final Vec3D v) {
		this(v.getX(), v.getY(), v.getZ());
	}

}