package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 3x3 matrix of 2D scaling
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat3Scale2D extends Mat3Identity {

	/**
	 * Creates a 3x3 transformation matrix equivalent to scaling in 2D
	 * 
	 * @param x
	 *            x-axis scale factor
	 * @param y
	 *            y-axis scale factor
	 */
	public Mat3Scale2D(final double x, final double y) {
		mat[0][0] = x;
		mat[1][1] = y;
	}

	/**
	 * Creates a 3x3 transformation matrix equivalent to uniform scaling in 2D
	 * 
	 * @param scale
	 *            x,y -axis scale factor
	 */
	public Mat3Scale2D(final double scale) {
		this(scale, scale);
	}

	/**
	 * Creates a 3x3 transformation matrix equivalent to scaling in 2D
	 * 
	 * @param v
	 *            vector scale factor
	 */
	public Mat3Scale2D(final Vec2D v) {
		this(v.getX(), v.getY());
	}

}
