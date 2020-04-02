package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Arrays;
import java.util.Locale;
import java9.util.Optional;

/**
 * A 4x4 matrix with common operations, immutable 
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */

public class Mat4 {
	protected final double mat[][] = new double[4][4];

	/**
	 * Creates a zero 4x4 matrix
	 */
	public Mat4() {
		this(0.0);
	}

	/**
	 * Creates a 4x4 matrix of value
	 * 
	 * @param value
	 *            value of all elements of matrix
	 */
	public Mat4(final double value) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				mat[i][j] = value;
	}

	/**
	 * Creates a 4x4 matrix from row vectors
	 * 
	 * @param p1
	 *            row 0 vector (M00, M01, M02, M03)
	 * @param p2
	 *            row 1 vector (M10, M11, M12, M13)
	 * @param p3
	 *            row 2 vector (M20, M21, M22, M23)
	 * @param p4
	 *            row 3 vector (M30, M31, M32, M33)
	 */
	public Mat4(final Point3D p1, final Point3D p2, final Point3D p3, final Point3D p4) {
		mat[0][0] = p1.getX();
		mat[0][1] = p1.getY();
		mat[0][2] = p1.getZ();
		mat[0][3] = p1.getW();
		mat[1][0] = p2.getX();
		mat[1][1] = p2.getY();
		mat[1][2] = p2.getZ();
		mat[1][3] = p2.getW();
		mat[2][0] = p3.getX();
		mat[2][1] = p3.getY();
		mat[2][2] = p3.getZ();
		mat[2][3] = p3.getW();
		mat[3][0] = p4.getX();
		mat[3][1] = p4.getY();
		mat[3][2] = p4.getZ();
		mat[3][3] = p4.getW();
	}

	/**
	 * Creates a 4x4 matrix as a clone of the given 4x4 matrix
	 * 
	 * @param m
	 *            4x4 matrix to be cloned
	 */
	public Mat4(final Mat4 m) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				mat[i][j] = m.mat[i][j];
	}

	/**
	 * Creates a 4x4 matrix by filling a 3x3 submatrix of an identity 4x4 matrix
	 * from the given 3x3 matrix
	 * 
	 * @param m
	 *            3x3 matrix to be copied to submatrix
	 */
	public Mat4(final Mat3 m) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3 ; j++)
				mat[i][j] = m.mat[i][j];
		for (int i = 0; i < 3; i++){
			mat[i][3] = 0;
			mat[3][i] = 0;
		}
		mat[3][3] = 1;
	}


	/**
	 * Creates a 4x4 matrix row-wise from a 16-element array of doubles
	 * 
	 * @param m
	 *            double array of length 16 (asserted)
	 */
	public Mat4(final double[] m) {
		assert(m.length >= 16);
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				mat[i][j] = m[i * 4 + j];
	}

	/**
	 * Creates a 4x4 matrix from a 4x4 array of doubles
	 * 
	 * @param m
	 *            2D double array of length 4x4 (asserted)
	 */
	public Mat4(final double[][] m) {
		assert(m.length >= 4);
		for (int i = 0; i < 4; i++){
			assert(m[i].length >= 4);
			for (int j = 0; j < 4; j++)
				mat[i][j] = m[i][j];
		}
	}

	/**
	 * Returns the result of element-wise summation with the given 4x4 matrix
	 * 
	 * @param m
	 *            4x4 matrix
	 * @return new Mat4 instance
	 */
	public Mat4 add(final Mat4 m) {
		final Mat4 result = new Mat4();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				result.mat[i][j] = mat[i][j] + m.mat[i][j];
		return result;
	}

	/**
	 * Returns the result of element-wise multiplication by the given scalar value
	 * 
	 * @param d
	 *            scalar value of type double
	 * @return new Mat4 instance
	 */
	public Mat4 mul(final double d) {
		final Mat4 result = new Mat4();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				result.mat[i][j] = mat[i][j] * d;
		return result;
	}

	/**
	 * Returns the result of matrix multiplication by the given 4x4 matrix
	 * 
	 * @param m
	 *            4x4 matrix
	 * @return new Mat4 instance
	 */
	public Mat4 mul(final Mat4 m) {
		final Mat4 result = new Mat4();
		double sum;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				sum = 0.0;
				for (int k = 0; k < 4; k++)
					sum += mat[i][k] * m.mat[k][j];
				result.mat[i][j] = sum;
			}
		return result;
	}

	/**
	 * Returns a clone of this matrix with the given element replaced by the
	 * given value
	 * 
	 * @param row
	 *            0-based row index of the element to change
	 * @param column
	 *            0-based column index of the element to change
	 * @param element
	 *            new element value
	 * @return new Mat4 instance
	 */
	public Mat4 withElement(final int row, final int column, final double element) {
		assert(row >= 0 && row < 4 && column >= 0 && column < 4);
		final Mat4 result = new Mat4(this);
		result.mat[row][column] = element;
		return result;
	}

	/**
	 * Returns a clone of this matrix with the given row replaced by the given
	 * vector
	 * 
	 * @param index
	 *            0-based row index
	 * @param row
	 *            new row vector
	 * @return new Mat4 instance
	 */
	public Mat4 withRow(final int index, final Point3D row) {
		assert(index >= 0 && index < 4);
		final Mat4 result = new Mat4(this);
		result.mat[index][0] = row.getX();
		result.mat[index][1] = row.getY();
		result.mat[index][2] = row.getZ();
		result.mat[index][3] = row.getW();
		return result;
	}

	/**
	 * Returns a clone of this matrix with the given column replaced by the
	 * given vector
	 * 
	 * @param index
	 *            0-based column index
	 * @param column
	 *            new column vector
	 * @return new Mat4 instance
	 */
	public Mat4 withColumn(final int index, final Point3D column) {
		assert(index >= 0 && index < 4);
		final Mat4 result = new Mat4(this);
		result.mat[0][index] = column.getX();
		result.mat[1][index] = column.getY();
		result.mat[2][index] = column.getZ();
		result.mat[3][index] = column.getW();
		return result;
	}

	/**
	 * Returns a matrix element
	 * 
	 * @param row
	 *            0-based row index of the element
	 * @param column
	 *            0-based column index of the element
	 * @return element value
	 */
	public double get(final int row, final int column) {
		assert(row >= 0 && row < 4 && column >= 0 && column < 4);
		return mat[row][column];
	}

	/**
	 * Returns a row vector at the given index
	 * 
	 * @param row
	 *            0-based row index
	 * @return matrix row as a new Point3D instance
	 */
	public Point3D getRow(final int row) {
		assert(row >= 0 && row < 4);
		return new Point3D(mat[row][0], mat[row][1], mat[row][2], mat[row][3]);
	}

	/**
	 * Returns a column vector at the given index
	 * 
	 * @param column
	 *            0-based column index
	 * @return matrix column as a new Point3D instance
	 */
	public Point3D getColumn(final int column) {
		assert(column >= 0 && column < 4);
		return new Point3D(mat[0][column], mat[1][column], mat[2][column], mat[3][column]);
	}

	/**
	 * Returns the transposition of this matrix
	 * 
	 * @return new Mat3 instance
	 */
	public Mat4 transpose() {
		final Mat4 result = new Mat4();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				result.mat[i][j] = mat[j][i];
			}
		return result;
	}

	/**
	 * Returns the determinant of this matrix
	 * 
	 * @return determinant value of type double
	 */
	public double det() {
	    final double s0 = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];
	    final double s1 = mat[0][0] * mat[1][2] - mat[1][0] * mat[0][2];
	    final double s2 = mat[0][0] * mat[1][3] - mat[1][0] * mat[0][3];
	    final double s3 = mat[0][1] * mat[1][2] - mat[1][1] * mat[0][2];
	    final double s4 = mat[0][1] * mat[1][3] - mat[1][1] * mat[0][3];
	    final double s5 = mat[0][2] * mat[1][3] - mat[1][2] * mat[0][3];

	    final double c5 = mat[2][2] * mat[3][3] - mat[3][2] * mat[2][3];
	    final double c4 = mat[2][1] * mat[3][3] - mat[3][1] * mat[2][3];
	    final double c3 = mat[2][1] * mat[3][2] - mat[3][1] * mat[2][2];
	    final double c2 = mat[2][0] * mat[3][3] - mat[3][0] * mat[2][3];
	    final double c1 = mat[2][0] * mat[3][2] - mat[3][0] * mat[2][2];
	    final double c0 = mat[2][0] * mat[3][1] - mat[3][0] * mat[2][1];
	    return s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0;
	}

	/**
	 * Returns the inverse of this matrix if it exists or an empty Optional
	 * 
	 * @return new Optional<Mat4> instance
	 */
	public Optional<Mat4> inverse() {
	    final double s0 = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];
	    final double s1 = mat[0][0] * mat[1][2] - mat[1][0] * mat[0][2];
	    final double s2 = mat[0][0] * mat[1][3] - mat[1][0] * mat[0][3];
	    final double s3 = mat[0][1] * mat[1][2] - mat[1][1] * mat[0][2];
	    final double s4 = mat[0][1] * mat[1][3] - mat[1][1] * mat[0][3];
	    final double s5 = mat[0][2] * mat[1][3] - mat[1][2] * mat[0][3];

	    final double c5 = mat[2][2] * mat[3][3] - mat[3][2] * mat[2][3];
	    final double c4 = mat[2][1] * mat[3][3] - mat[3][1] * mat[2][3];
	    final double c3 = mat[2][1] * mat[3][2] - mat[3][1] * mat[2][2];
	    final double c2 = mat[2][0] * mat[3][3] - mat[3][0] * mat[2][3];
	    final double c1 = mat[2][0] * mat[3][2] - mat[3][0] * mat[2][2];
	    final double c0 = mat[2][0] * mat[3][1] - mat[3][0] * mat[2][1];
	    final double det = s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0;

	    if (det == 0)
			return Optional.empty();

	    final double iDet = 1 / det;
		final Mat4 res = new Mat4();
	    res.mat[0][0] = ( mat[1][1] * c5 - mat[1][2] * c4 + mat[1][3] * c3) * iDet;
	    res.mat[0][1] = (-mat[0][1] * c5 + mat[0][2] * c4 - mat[0][3] * c3) * iDet;
	    res.mat[0][2] = ( mat[3][1] * s5 - mat[3][2] * s4 + mat[3][3] * s3) * iDet;
	    res.mat[0][3] = (-mat[2][1] * s5 + mat[2][2] * s4 - mat[2][3] * s3) * iDet;

	    res.mat[1][0] = (-mat[1][0] * c5 + mat[1][2] * c2 - mat[1][3] * c1) * iDet;
	    res.mat[1][1] = ( mat[0][0] * c5 - mat[0][2] * c2 + mat[0][3] * c1) * iDet;
	    res.mat[1][2] = (-mat[3][0] * s5 + mat[3][2] * s2 - mat[3][3] * s1) * iDet;
	    res.mat[1][3] = ( mat[2][0] * s5 - mat[2][2] * s2 + mat[2][3] * s1) * iDet;

	    res.mat[2][0] = ( mat[1][0] * c4 - mat[1][1] * c2 + mat[1][3] * c0) * iDet;
	    res.mat[2][1] = (-mat[0][0] * c4 + mat[0][1] * c2 - mat[0][3] * c0) * iDet;
	    res.mat[2][2] = ( mat[3][0] * s4 - mat[3][1] * s2 + mat[3][3] * s0) * iDet;
	    res.mat[2][3] = (-mat[2][0] * s4 + mat[2][1] * s2 - mat[2][3] * s0) * iDet;

	    res.mat[3][0] = (-mat[1][0] * c3 + mat[1][1] * c1 - mat[1][2] * c0) * iDet;
	    res.mat[3][1] = ( mat[0][0] * c3 - mat[0][1] * c1 + mat[0][2] * c0) * iDet;
	    res.mat[3][2] = (-mat[3][0] * s3 + mat[3][1] * s1 - mat[3][2] * s0) * iDet;
	    res.mat[3][3] = ( mat[2][0] * s3 - mat[2][1] * s1 + mat[2][2] * s0) * iDet;
		return Optional.of(res);
	}
	

	/**
	 * Returns this matrix stored row-wise in a float array
	 * 
	 * @return new float array
	 */
	public float[] floatArray() {
		final float[] result = new float[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				result[i * 4 + j] = ((float) mat[i][j]);
		return result;
	}
	
	/**
     * Compares this object against the specified object.  
     * @param   obj   the object to compare with.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null) && (obj instanceof Mat4) 
			&& (((Mat4) obj).getRow(0)).equals(getRow(0))
			&& (((Mat4) obj).getRow(1)).equals(getRow(1))
			&& (((Mat4) obj).getRow(2)).equals(getRow(2))
			&& (((Mat4) obj).getRow(3)).equals(getRow(3));
	}

	/**
     * Returns a hash code value for the object. 
     * 
     * @return  a hash code value for this object.
     */
    @Override
	public int hashCode(){
		return Arrays.hashCode(floatArray());
	}

	/**
	 * Compares this Mat4 against the specified Mat4.
	 * 
	 * @param mat
	 *            the mat to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both mats are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Mat4 mat, double epsilon) {
		return (this == mat) || (mat != null) 
				&& getRow(0).eEquals( mat.getRow(0), epsilon) 
				&& getRow(1).eEquals( mat.getRow(1), epsilon)
				&& getRow(2).eEquals( mat.getRow(2), epsilon) 
				&& getRow(3).eEquals( mat.getRow(3), epsilon);
	}

	/**
	 * Compares this Mat4 against the specified Mat4.
	 * 
	 * @param mat
	 *            the mat to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Mat4 mat) {
		return eEquals(mat, Compare.EPSILON);
	}
	
	/**
	 * Returns String representation of this matrix
	 * 
	 * @return comma separated floating-point values per row, rows comma
	 *         separated in curly brackets
	 */
	@Override
	public String toString() {
		return toString("%4.1f");
	}
	
	/**
	 * Returns String representation of this matrix with elements formated
	 * according to the given format, see
	 * {@link java.lang.String#format(String, Object...)}
	 * 
	 * @param format
	 *            String format applied to each element
	 * @return comma separated floating-point values per row, rows comma
	 *         separated in curly brackets
	 */
	public String toString(final String format) {
		return String.format(Locale.US, "{{"+format+","+format+","+format+","+format+"},\n"+
				" {"+format+","+format+","+format+","+format+"},\n"+
				" {"+format+","+format+","+format+","+format+"},\n"+
				" {"+format+","+format+","+format+","+format+"}}",
				mat[0][0], mat[0][1], mat[0][2], mat[0][3],
				mat[1][0], mat[1][1], mat[1][2], mat[1][3],
				mat[2][0], mat[2][1], mat[2][2], mat[2][3],
				mat[3][0], mat[3][1], mat[3][2], mat[3][3]);
	}
	
}
