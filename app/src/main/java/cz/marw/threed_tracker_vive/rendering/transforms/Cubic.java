package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * Cubic aproximation curves in 3D, immutable
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */

public class Cubic {
	/**
	 * Bezier base matrix
	 */
	public static final Mat4 BEZIER = new Mat4(new double[] {
			-1, 3, -3, 1,
			3, -6, 3, 0,
			-3, 3, 0, 0,
			1, 0, 0, 0
		});

	/**
	 * Coons base matrix
	 */
	public static final Mat4 COONS = new Mat4(new double[] {
			-1, 3, -3, 1,
			3, -6, 3, 0,
			-3, 0, 3, 0,
			1, 4, 1, 0
		}).mul(1.0 / 6.0);
		
	/**
	 * Ferguson base matrix
	 */
	public static final Mat4 FERGUSON = new Mat4(new double[] {
			2, -2, 1, 1,
			-3, 3, -2, -1,
			0, 0, 1, 0,
			1, 0, 0, 0
		});

	/**
	 * Control polygon matrix (base matrix * control points matrix)
	 */
	private final Mat4 controlMat;
	

	/**
	 * Create cubic using 4 individual control points
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 *            control points
	 */
	public Cubic(final Mat4 baseMat, final Point3D p1, final Point3D p2, final Point3D p3, final Point3D p4) {
		controlMat = baseMat.mul(new Mat4(p1, p2, p3, p4));
	}

	/**
	 * Create cubic using an array of 4 control points
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param points
	 *            array of 4 control points
	 */
	public Cubic(final Mat4 baseMat, final Point3D[] points) {
		this(baseMat, points, 0);
	}

	/**
	 * Create cubic using an array of 4 control points with an offset to the
	 * first point
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param points
	 *            array of 4 control points
	 * @param startIndex
	 *            offset to the point to start at
	 */
	public Cubic(final Mat4 baseMat, final Point3D[] points, final int startIndex) {
		controlMat = baseMat.mul(new Mat4(points[startIndex], points[startIndex + 1],
				points[startIndex + 2], points[startIndex + 3]));
	}

	/**
	 * Compute the coordinates of a point on the cubic curve corresponding
	 * to the parameter from [0,1]
	 * 
	 * @param param
	 *            parameter from [0,1]
	 * @return new Point3D on the curve
	 */
	public Point3D compute(final double param) {
		final double t = param > 0 ? param < 1 ? param : 1 : 0; 
		final Point3D res = new Point3D(t * t * t, t * t, t, 1).mul(controlMat);
		return new Point3D(res.ignoreW());
	}

}
