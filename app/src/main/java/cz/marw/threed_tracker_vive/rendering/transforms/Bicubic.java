package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * Bicubic approximation surfaces in 3D, immutable
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */

public class Bicubic {
	private final Cubic c1, c2, c3, c4;
	private final Mat4 baseMat;


	/**
	 * Create bicubic using 4x4 individual control points
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param p11
	 * @param p12
	 * @param p13
	 * @param p14
	 * @param p21
	 * @param p22
	 * @param p23
	 * @param p24
	 * @param p31
	 * @param p32
	 * @param p33
	 * @param p34
	 * @param p41
	 * @param p42
	 * @param p43
	 * @param p44
	 *            control points
	 */
	public Bicubic(final Mat4 baseMat, final Point3D p11, final Point3D p12, final Point3D p13, final Point3D p14,
			final Point3D p21, final Point3D p22, final Point3D p23, final Point3D p24, final Point3D p31,
			final Point3D p32, final Point3D p33, final Point3D p34, final Point3D p41, final Point3D p42,
			final Point3D p43, final Point3D p44) {
		this.baseMat = baseMat;
		c1 = new Cubic(baseMat, p11, p12, p13, p14);
		c2 = new Cubic(baseMat, p21, p22, p23, p24);
		c3 = new Cubic(baseMat, p31, p32, p33, p34);
		c4 = new Cubic(baseMat, p41, p42, p43, p44);
	}

	/**
	 * Create bicubic using an array of 4x4 control points
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param points
	 *            array of 16 control points
	 */
	public Bicubic(final Mat4 baseMat, final Point3D[] points) {
		this(baseMat, points, 0);
	}

	/**
	 * Create bicubic using an array of 4x4 control points with an offset to the
	 * first point
	 * 
	 * @param baseMat
	 *            base matrix, for instance Cubic.BEZIER
	 * @param points
	 *            array of 16 control points
	 * @param startIndex
	 *            offset to the point to start at
	 */
	public Bicubic(final Mat4 baseMat, final Point3D[] points, final int startIndex) {
		this.baseMat = baseMat;
		c1 = new Cubic(baseMat, points, startIndex);
		c2 = new Cubic(baseMat, points, startIndex + 4);
		c3 = new Cubic(baseMat, points, startIndex + 8);
		c4 = new Cubic(baseMat, points, startIndex + 12);
	}

	/**
	 * Compute the coordinates of a point on the bicubic surface corresponding
	 * to the u,v parameters from [0,1]
	 * 
	 * @param paramU
	 *            u parameter from [0,1]
	 * @param paramV
	 *            v parameter from [0,1]
	 * @return new Point3D on the surface
	 */
	public Point3D compute(final double paramU, final double paramV) {
		final double u = paramU > 0 ? paramU < 1 ? paramU : 1 : 0; 
		final double v = paramV > 0 ? paramV < 1 ? paramV : 1 : 0; 
		final Point3D u1, u2, u3, u4;
		u1 = c1.compute(u);
		u2 = c2.compute(u);
		u3 = c3.compute(u);
		u4 = c4.compute(u);
		return new Cubic(baseMat, u1, u2, u3, u4).compute(v);
	}
}
