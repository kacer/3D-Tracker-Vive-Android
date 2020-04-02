package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of right-handed orthogonal visibility volume to normalized
 * clipping volume transformation
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */
public class Mat4OrthoRH extends Mat4Identity {

	/**
	 * Creates a 4x4 transformation matrix equivalent to the mapping of an
	 * orthogonal visibility volume (an axis-aligned cuboid symmetrical about xz
	 * and yz planes) of given dimensions to the normalized clipping volume
	 * ([-1,1]x[-1,1]x[0,1])
	 * 
	 * @param w
	 *            visibility cuboid width
	 * @param h
	 *            visibility cuboid height
	 * @param zn
	 *            distance to the near clipping plane along z-axis
	 * @param zf
	 *            distance to the far clipping plane along z-axis
	 */
	public Mat4OrthoRH(final double w, final double h, final double zn,
			final double zf) {
		mat[0][0] = 2.0 / w;
		mat[1][1] = 2.0 / h;
		mat[2][2] = 1.0 / (zn - zf);
		mat[3][2] = zn / (zn - zf);
	}
}
