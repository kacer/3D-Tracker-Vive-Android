package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Locale;
import java.util.Objects;

/**
 * A quaternion with common operations, immutable 
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */

public class Quat {
	protected final double r, i, j, k;

	/**
	 * Creates a zero quaternion
	 */
	public Quat() {
		i = j = k = r = 0.0f;
	}

	/**
	 * Creates a quaternion with the given coordinates
	 * 
	 * @param r
	 *            r coordinate
	 * @param i
	 *            i coordinate
	 * @param j
	 *            j coordinate
	 * @param k
	 *            k coordinate
	 */
	public Quat(double r, double i, double j, double k) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.r = r;
	}

	/**
	 * Creates a quaternion by cloning the given one
	 * 
	 * @param q
	 *            quaternion to be cloned
	 */
	public Quat(Quat q) {
		i = q.i;
		j = q.j;
		k = q.k;
		r = q.r;
	}
	
	/**
	 * Returns the r coordinate
	 * 
	 * @return the r
	 */
	public double getR() {
		return r;
	}

	/**
	 * Returns the i coordinate
	 * 
	 * @return the i
	 */
	public double getI() {
		return i;
	}

	/**
	 * Returns the j coordinate
	 * 
	 * @return the j
	 */
	public double getJ() {
		return j;
	}

	/**
	 * Returns the k coordinate
	 * 
	 * @return the k
	 */
	public double getK() {
		return k;
	}

	/**
	 * Returns the result of quaternion addition of the given quaternion
	 * 
	 * @param q
	 *            quaternion to add
	 * @return new Quat instance
	 */
	public Quat add(Quat q) {
		return new Quat(r + q.r, i + q.i, j + q.j, k + q.k);
	}

	/**
	 * Returns the result of quaternion subtraction of the given quaternion
	 * 
	 * @param q
	 *            quaternion to subtract
	 * @return new Quat instance
	 */
	public Quat sub(Quat q) {
		return new Quat(r - q.r, i - q.i, j - q.j, k - q.k);
	}

	/**
	 * Returns the result of scalar multiplication
	 * 
	 * @param a
	 *            scalar value of type double
	 * @return new Quat instance
	 */
	public Quat mul(double a) {
		return new Quat(a * r, a * i, a * j, a * k);
	}

	/**
	 * Returns the result of right side quaternion multiplication by the given quaternion
	 * 
	 * @param q
	 *            quaternion
	 * @return new Quat instance
	 */
	public Quat mulR(Quat q) {
		return new Quat(this.r * q.r - this.i * q.i - this.j * q.j - this.k
				* q.k, this.r * q.i + this.i * q.r + this.j * q.k - this.k
				* q.j, this.r * q.j - this.i * q.k + this.j * q.r + this.k
				* q.i, this.r * q.k + this.i * q.j - this.j * q.i + this.k
				* q.r);
	}

	/**
	 * Returns the result of left side quaternion multiplication by the given quaternion
	 * 
	 * @param q
	 *            quaternion
	 * @return new Quat instance
	 */
	public Quat mulL(Quat q) {
		return new Quat(q.r * this.r - q.i * this.i - q.j * this.j - q.k
				* this.k, q.r * this.i + q.i * this.r + q.j * this.k - q.k
				* this.j, q.r * this.j + q.j * this.r + q.k * this.i - q.i
				* this.k, q.r * this.k + q.k * this.r + q.i * this.j - q.j
				* this.i);
	}

	/**
	 * Returns the result of right side quaternion multiplication by the given quaternion
	 * 
	 * @param q
	 *            quaternion
	 * @return new Quat instance
	 */
	public Quat mul(Quat q) {
		return mulR(q);
	}

	/**
	 * Returns the inverse of this quaternion if it exists or an empty quaternion
	 * 
	 * @return new Quat instance
	 */
	public Quat inverse() {
		double norm = this.norm();
		norm = norm * norm;
		if (norm > 0)
			return new Quat(this.r / norm, -this.i / norm, -this.j / norm,
					-this.k / norm);
		else
			return new Quat(0, 0, 0, 0);
	}

	/**
	 * Return logarithm functions of this quaternion
	 * 
	 * @return new Quat instance
	 */
	public Quat log() {
		if ((this.i == 0) && (this.i == 0) && (this.i == 0)) {
			if (r > 0)
				return new Quat(Math.log(r), 0, 0, 0);
			else if (r < 0)
				return new Quat(Math.log(-r), 1, 0, 0);
			else
				// error log(0)
				return new Quat();
		} else {
			double s = Math.sqrt(i * i + j * j + k * k);
			double a = Math.atan2(s, r) / s;
			return new Quat(Math.log(this.norm()), a * i, a * j, a * k);
		}
	}

	/**
	 * Return exponential functions of this quaternion
	 * 
	 * @return new Quat instance
	 */
	public Quat exp() {
		if ((this.i == 0) && (this.j == 0) && (this.k == 0))
			return new Quat(Math.exp(this.r), 0, 0, 0);
		else {
			double s = Math.sqrt(i * i + j * j + k * k);
			double cos = Math.cos(s);
			// double exp = Math.exp(r);
			s = Math.exp(r) * Math.sin(s) / s;
			return new Quat(Math.exp(r) * cos, s * i, s * j, s * k);
		}
	}

	/**
	 * Returns the quaternion opposite to this quaternion
	 * 
	 * @return new Quat instance
	 */
	public Quat opposite() {
		return new Quat(-this.r, -this.i, -this.j, -this.k);
	}

	/**
	 * Returns the norm of this quaternion
	 * 
	 * @return double-precision floating point value
	 */
	public double norm() {
		return Math.sqrt(r * r + i * i + j * j + k * k);
	}

	/**
	 * Returns the result of dot-product with the given quaternion
	 * 
	 * @param q
	 *            quaternion 
	 * @return double-precision floating point value
	 */
	public double dot(Quat q) {
		return this.i * q.i + this.j * q.j + this.k * q.k + this.r * q.r;
	}
	
	/**
	 * Returns a normalized quaternion if possible (nonzero norm), 
	 * empty quaternion otherwise
	 * 
	 * @return Quat instance
	 */
	public Quat normalized() {
		double norm = this.norm();
		if (norm > 0)
			return new Quat(r / norm, i / norm, j / norm, k / norm);
		return new Quat(0, 0, 0, 0);
	}

	/**
	 * Creates a 4x4 transformation matrix equivalent to rotation defined by quaternion
	 * 
	 * @return new Mat4 instance
	 */
	public Mat4 toRotationMatrix() {
		Mat4 res = new Mat4Identity();
		this.normalized();
		res.mat[0][0] = 1 - 2 * (j * j + k * k);
		res.mat[1][0] = 2 * (i * j - r * k);
		res.mat[2][0] = 2 * (r * j + i * k);

		res.mat[0][1] = 2 * (i * j + r * k);
		res.mat[1][1] = 1 - 2 * (i * i + k * k);
		res.mat[2][1] = 2 * (k * j - i * r);

		res.mat[0][2] = 2 * (i * k - r * j);
		res.mat[1][2] = 2 * (k * j + i * r);
		res.mat[2][2] = 1 - 2 * (i * i + j * j);
		return res;
	}

	/**
	 * Creates a new quaternion equivalent to the rotation given by 
	 * the 4x4 transformation matrix
	 * 
	 * @param m
	 *            4x4 matrix
	 * @return new Quat instance
	 */
	public static Quat fromRotationMatrix(Mat4 mat) {
		double r, i, j, k;
		double diagonal = mat.mat[0][0] + mat.mat[1][1] + mat.mat[2][2];

		if (diagonal > 0.0f) {
			r = (0.5f * Math.sqrt(diagonal + mat.mat[3][3]));
			i = (mat.mat[2][1] - mat.mat[1][2]) / (4 * r);
			j = (mat.mat[0][2] - mat.mat[2][0]) / (4 * r);
			k = (mat.mat[1][0] - mat.mat[0][1]) / (4 * r);
		} else {
			int[] indices = { 1, 2, 0 };
			int a = 0, b, c;

			if (mat.mat[1][1] > mat.mat[0][0])
				a = 1;
			if (mat.mat[2][2] > mat.mat[a][a])
				a = 2;

			b = indices[a];
			c = indices[b];

			diagonal = mat.mat[a][a] - mat.mat[b][b] - mat.mat[c][c]
					+ mat.mat[3][3];
			r = (0.5f * Math.sqrt(diagonal));
			i = (mat.mat[a][b] + mat.mat[b][a]) / (4 * r);
			j = (mat.mat[a][c] + mat.mat[c][a]) / (4 * r);
			k = (mat.mat[b][c] - mat.mat[c][b]) / (4 * r);
		}
		return new Quat(r, i, j, k);
	}

	/**
	 * Creates a new quaternion equivalent to the right-handed rotations given by 
	 * the Euler angles about x, y and z axes chained in sequence in this order 
	 * 
	 * @param alpha
	 *            rotation angle about x-axis, in radians
	 * @param beta
	 *            rotation angle about y-axis, in radians
	 * @param gamma
	 *            rotation angle about z-axis, in radians
	 * @return new Quat instance
	 */
	public static Quat fromEulerAngles(double alpha, double beta, double gamma) {
		/*
		 * Quat Qi = new Quat(Math.cos(a/2),Math.sin(a/2),0,0); Quat Qj = new
		 * Quat(Math.cos(b/2),0,Math.sin(b/2),0); Quat Qk = new
		 * Quat(Math.cos(c/2),0,0,Math.sin(c/2));
		 */
		Quat Qi = Quat.fromEulerAngle(alpha, 1, 0, 0);
		Quat Qj = Quat.fromEulerAngle(beta, 0, 1, 0);
		Quat Qk = Quat.fromEulerAngle(gamma, 0, 0, 1);

		return new Quat(Qk.mul(Qj).mul(Qi));

		// this.renorm();
	}

	/**
	 * Creates a new quaternion equivalent to the rotations about axis given by 
	 * vector x, y and z  
	 * 
	 * @param angle
	 *            rotation angle in radians
	 * 
	 * @param x
	 *            x vector coordinate
	 * @param y
	 *            y vector coordinate
	 * @param z
	 *            z vector coordinate
	 * @return new Quat instance
	 */
	public static Quat fromEulerAngle(double angle, double x, double y, double z) {
		return new Quat(Math.cos(angle / 2), Math.sin(angle / 2) * x,
				Math.sin(angle / 2) * y, Math.sin(angle / 2) * z);
	}

	/**
	 * Return the angle and axis rotation in format Point3D:(angle, x-axis, y-axis, z-axis)
	 * 
	 * @return new Poin3D instance 
	 */
	public Point3D toEulerAngle() {
		double angle = 2 * Math.acos(r);
		double x = this.i;
		double y = this.j;
		double z = this.k;

		double s = Math.sqrt(x * x + y * y + z * z);
		if (s < 0.0001d)
			return new Point3D(angle, 1, 0, 0);
		return new Point3D(angle, x / s, y / s, z / s);
	}

	/**
	 * Linear interpolation between two this and given quaternion Lerp(Q1,Q2,t)=(1-t)Q1+tQ2
	 * 
	 * @param q
	 *            quaternion
	 * @param t
	 *            interpolation parameter in interval <0;1>
	 * @return new Quat instance
	 */
	public Quat lerp(Quat q, double t) {
		if (t >= 1)
			return new Quat(q);
		else if (t <= 0)
			return new Quat(this);
		else
			return new Quat((this.mul(1 - t)).add(q.mul(t)));
	}

	/**
	 * Spherical interpolation between two this and given quaternion
	 * 
	 * @param q
	 *            quaternion
	 * @param t
	 *            interpolation parameter in interval <0;1>
	 * @return new Quat instance
	 */
	public Quat slerp(Quat q, double t) {
		double c = this.dot(q);
		if (c > 1.0)
			c = 1.0;
		else if (c < -1.0)
			c = -1.0;
		double uhel = Math.acos(c);
		if (Math.abs(uhel) < 1.0e-5)
			return new Quat(this);
		double s = 1 / Math.sin(uhel);
		if (t >= 1)
			return new Quat(this);
		else if (t <= 0)
			return new Quat(q);
		else
			return new Quat(this.normalized().mul(Math.sin((1 - t) * uhel) * s)
					.add(q.normalized().mul(Math.sin(t * uhel) * s))).normalized();
	}

	/**
	 * Cubic interpolation between two this and given quaternion
	 * 
	 * @param q
	 *            quaternion
	 * @param t
	 *            interpolation parameter in interval <0;1>
	 * @return new Quat instance
	 */
	public Quat squad(Quat q, Quat q1, Quat q2, double t) {
		return new Quat(this.slerp(q, t).slerp(q1.slerp(q2, t),
				(double) (2 * t * (1 - t))));
	}

	private Quat quadrangle(Quat q1, Quat q2) {
		Quat s1 = this.inverse().mul(q1);
		Quat s2 = this.inverse().mul(q2);
		return new Quat((s1.log().add(s2.log()).mul(-1 / 4)).exp());
	}

	public Quat squad2(Quat q1, Quat q2, Quat q3, double t) {

		Quat s1 = this.quadrangle(q1, q2);
		Quat s2 = q2.quadrangle(this, q3);
		return new Quat(this.slerp(q2, t).slerp(s1.slerp(s2, t),
				(double) (2 * t * (1 - t))));
	}

	/**
	 * Compares this object against the specified object.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof Vec3D) 
				&& (new Double(((Quat) obj).getR()).equals(getR()))
				&& (new Double(((Quat) obj).getI()).equals(getI()))
				&& (new Double(((Quat) obj).getJ()).equals(getJ()))
				&& (new Double(((Quat) obj).getK()).equals(getK()));
	}

	/**
     * Returns a hash code value for the object. 
     * 
     * @return  a hash code value for this object.
     */
    @Override
	public int hashCode(){
		return Objects.hash(this.getR(), this.getI(), this.getJ(), this.getK());
	}
    
	/**
	 * Compares this Quat against the specified Quat with epsilon.
	 * 
	 * @param quat
	 *            the vector to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both numbers are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Quat quat, double epsilon) {
		return (quat != null) 
				&& Compare.eEquals(getR(), quat.getR(), epsilon)
				&& Compare.eEquals(getI(), quat.getI(), epsilon)
				&& Compare.eEquals(getJ(), quat.getJ(), epsilon)
				&& Compare.eEquals(getK(), quat.getK(), epsilon);
	}

	/**
	 * Compares this Quat against the specified Quat with epsilon.
	 * 
	 * @param quat
	 *            the vector to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Quat quat) {
		return eEquals(quat, Compare.EPSILON);
	}
	
	/**
	 * Returns String representation of this quaternion
	 * 
	 * @return floating-point value in parentheses
	 */
	@Override
	public String toString() {
		return toString("%4.1f");
	}

	/**
	 * Returns String representation of this quaternion with coordinate formated
	 * according to the given format, see
	 * {@link java.lang.String#format(String, Object...)}
	 * 
	 * @param format
	 *            String format applied to the coordinate
	 * @return floating-point value in parentheses, useful in constructor
	 */
	public String toString(String format) {
		return String.format(Locale.US, "("+format+","+format+","+format+","+format+")",r,i,j,k);
	}
	
	
}
