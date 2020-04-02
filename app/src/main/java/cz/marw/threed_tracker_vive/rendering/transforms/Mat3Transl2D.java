package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 3x3 matrix of 2D translation
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat3Transl2D extends Mat3Identity {

	/**
	 * Creates a 3x3 transformation matrix equivalent to translation in 2D
	 * 
	 * @param x
	 *            translation along x-axis
	 * @param y
	 *            translation along y-axis
	 */
	public Mat3Transl2D(final double x, final double y) {
		mat[2][0] = x;
		mat[2][1] = y;
	}
	
	/**
	 * Creates a 3x3 transformation matrix equivalent to translation in 2D
	 * 
	 * @param v
	 *            translation vector
	 */
	public Mat3Transl2D(final Vec2D v) {
		this(v.getX(), v.getY());
	}

}