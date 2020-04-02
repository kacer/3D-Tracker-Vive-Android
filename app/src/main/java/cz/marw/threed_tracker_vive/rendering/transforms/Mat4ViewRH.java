package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 matrix of right-handed view transformation
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4ViewRH extends Mat4Identity {

	/**
	 * Creates a 4x4 transition matrix from the current frame (coordinate
	 * system) to the observer (camera) frame described with respect to the
	 * current frame by origin position, view vector and up vector. The inherent
	 * observer frame is constructed orthonormal, specifically in {o, {x, y, z}}
	 * notation it is 
	 * {e, {normalize(u cross -v), normalize(-v cross (u cross -v)), normalize(-v)}} 
	 * where cross is the cross-product and normalize returns a collinear unit vector 
	 * (by dividing all vector components by vector length)
	 * 
	 * @param e
	 *            eye, position of the observer frame origin with respect to the
	 *            current frame
	 * @param v
	 *            view vector, the direction of the observer frame -z axis with
	 *            respect to the current frame
	 * @param u
	 *            up vector, together with eye and view vector defines with
	 *            respect to the current frame the plane perpendicular to the
	 *            observer frame x axis (i.e. the plane in which lies the
	 *            observer frame y axis)
	 */
	public Mat4ViewRH(Vec3D e, Vec3D v, Vec3D u) {
		Vec3D x, y, z;
		z = v.mul(-1.0).normalized().orElse(new Vec3D(1, 0, 0));
		x = u.cross(z).normalized().orElse(new Vec3D(1, 0, 0));
		y = z.cross(x);
		mat[0][0] = x.getX();
		mat[1][0] = x.getY();
		mat[2][0] = x.getZ();
		mat[3][0] = -e.dot(x);
		mat[0][1] = y.getX();
		mat[1][1] = y.getY();
		mat[2][1] = y.getZ();
		mat[3][1] = -e.dot(y);
		mat[0][2] = z.getX();
		mat[1][2] = z.getY();
		mat[2][2] = z.getZ();
		mat[3][2] = -e.dot(z);

	}
}
