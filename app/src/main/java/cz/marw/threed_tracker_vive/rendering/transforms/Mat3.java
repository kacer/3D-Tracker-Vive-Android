package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Arrays;
import java.util.Locale;
import java9.util.Optional;

/**
 * A 3x3 matrix with common operations, immutable 
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat3 {
	protected final double mat[][] = new double[3][3];

	/**
	 * Creates a zero 3x3 matrix
	 */
	public Mat3() {
		this(0.0);
	}

	/**
	 * Creates a 3x3 matrix of value
	 * 
	 * @param value
	 *            value of all elements of matrix
	 */
	public Mat3(final double value) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mat[i][j] = value;
	}

	/**
	 * Creates a 3x3 matrix from row vectors
	 * 
	 * @param v1
	 *            row 0 vector (M00, M01, M02)
	 * @param v2
	 *            row 1 vector (M10, M11, M12)
	 * @param v3
	 *            row 2 vector (M20, M21, M22)
	 */
	public Mat3(final Vec3D v1, final Vec3D v2, final Vec3D v3) {
		mat[0][0] = v1.getX();
		mat[0][1] = v1.getY();
		mat[0][2] = v1.getZ();
		mat[1][0] = v2.getX();
		mat[1][1] = v2.getY();
		mat[1][2] = v2.getZ();
		mat[2][0] = v3.getX();
		mat[2][1] = v3.getY();
		mat[2][2] = v3.getZ();
	}


	/**
	 * Creates a 3x3 matrix as a clone of the given 3x3 matrix
	 * 
	 * @param m
	 *            3x3 matrix to be cloned
	 */
	public Mat3(final Mat3 m) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mat[i][j] = m.mat[i][j];
	}

	/**
	 * Creates a 3x3 matrix row-wise from a 9-element array of doubles
	 * 
	 * @param m
	 *            double array of length 9 (asserted)
	 */
	public Mat3(final double[] m) {
		assert(m.length >= 9);
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mat[i][j] = m[i * 3 + j];
	}

	/**
	 * Creates a 3x3 matrix from a 3x3 array of doubles
	 * 
	 * @param m
	 *            2D double array of length 3x3 (asserted)
	 */
	public Mat3(final double[][] m) {
		assert(m.length >= 3);
		for (int i = 0; i < 3; i++) {
			assert(m[i].length >= 3);
			for (int j = 0; j < 3; j++)
				mat[i][j] = m[i][j];
		}
	}
	
	/**
	 * Creates a 3x3 matrix from a submatrix of a 4x4 matrix
	 * 
	 * @param m
	 *            4x4 matrix
	 */
	public Mat3(final Mat4 m) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mat[i][j] = m.mat[i][j];
	}
	
	
	/**
	 * Returns the result of element-wise summation with the given 3x3 matrix
	 * 
	 * @param m
	 *            3x3 matrix
	 * @return new Mat3 instance
	 */
	public Mat3 add(final Mat3 m) {
		final Mat3 result = new Mat3();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				result.mat[i][j] = mat[i][j] + m.mat[i][j];
		return result;
	}

	/**
	 * Returns the result of element-wise multiplication by the given scalar value
	 * 
	 * @param d
	 *            scalar value of type double
	 * @return new Mat3 instance
	 */
	public Mat3 mul(final double d) {
		final Mat3 result = new Mat3();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				result.mat[i][j] = mat[i][j] * d;
		return result;
	}

	/**
	 * Returns the result of matrix multiplication by the given 3x3 matrix
	 * 
	 * @param m
	 *            3x3 matrix
	 * @return new Mat3 instance
	 */
	public Mat3 mul(final Mat3 m) {
		final Mat3 result = new Mat3();
		double sum;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				sum = 0.0;
				for (int k = 0; k < 3; k++)
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
	 * @return new Mat3 instance
	 */
	public Mat3 withElement(final int row, final int column, final double element) {
		assert(row >= 0 && row < 3 && column >= 0 && column < 3);
		final Mat3 result = new Mat3(this);
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
	 * @return new Mat3 instance
	 */
	public Mat3 withRow(final int index, final Vec3D row) {
		assert(index >= 0 && index < 3);
		final Mat3 result = new Mat3(this);
		result.mat[index][0] = row.getX();
		result.mat[index][1] = row.getY();
		result.mat[index][2] = row.getZ();
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
	 * @return new Mat3 instance
	 */
	public Mat3 withColumn(final int index, final Vec3D column) {
		assert(index >= 0 && index < 3);
		final Mat3 result = new Mat3(this);
		result.mat[0][index] = column.getX();
		result.mat[1][index] = column.getY();
		result.mat[2][index] = column.getZ();
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
		assert(row >= 0 && row < 3 && column >= 0 && column < 3);
		return mat[row][column];
	}

	/**
	 * Returns a row vector at the given index
	 * 
	 * @param row
	 *            0-based row index
	 * @return matrix row as a new Vec3D instance
	 */
	public Vec3D getRow(final int row) {
		assert(row >= 0 && row < 3);
		return new Vec3D(mat[row][0], mat[row][1], mat[row][2]);
	}

	/**
	 * Returns a column vector at the given index
	 * 
	 * @param column
	 *            0-based column index
	 * @return matrix column as a new Vec3D instance
	 */
	public Vec3D getColumn(final int column) {
		assert(column >= 0 && column < 3);
		return new Vec3D(mat[0][column], mat[1][column], mat[2][column]);
	}	

	/**
	 * Returns the transposition of this matrix
	 * 
	 * @return new Mat3 instance
	 */
	public Mat3 transpose() {
		final Mat3 result = new Mat3();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				result.mat[i][j] = mat[j][i];
		return result;
	}

	/**
	 * Returns the determinant of this matrix
	 * 
	 * @return determinant value of type double
	 */
	public double det() {
		return mat[0][0] * (mat[1][1] * mat[2][2] - mat[2][1] * mat[1][2])
				- mat[0][1] * (mat[1][0] * mat[2][2] - mat[2][0] * mat[1][2])
				+ mat[0][2] * (mat[1][0] * mat[2][1] - mat[2][0] * mat[1][1]);
	}

	/**
	 * Returns the inverse of this matrix if it exists or an empty Optional
	 * 
	 * @return new Optional<Mat3> instance
	 */
	public Optional<Mat3> inverse() {
		final double det = det();
		if (det == 0)
			return Optional.empty();
		final Mat3 res = new Mat3();

		res.mat[0][0] = (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1]) / det;
		res.mat[0][1] = (mat[0][2] * mat[2][1] - mat[0][1] * mat[2][2]) / det;
		res.mat[0][2] = (mat[0][1] * mat[1][2] - mat[0][2] * mat[1][1]) / det;

		res.mat[1][0] = (mat[1][2] * mat[2][0] - mat[1][0] * mat[2][2]) / det;
		res.mat[1][1] = (mat[0][0] * mat[2][2] - mat[0][2] * mat[2][0]) / det;
		res.mat[1][2] = (mat[0][2] * mat[1][0] - mat[0][0] * mat[1][2]) / det;

		res.mat[2][0] = (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]) / det;
		res.mat[2][1] = (mat[0][1] * mat[2][0] - mat[0][0] * mat[2][1]) / det;
		res.mat[2][2] = (mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]) / det;

		return Optional.of(res);
	}
	
	/**
	 * Returns this matrix stored row-wise in a float array
	 * 
	 * @return new float array
	 */
	public float[] floatArray() {
		final float[] result = new float[9];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				result[i * 3 + j] = ((float) mat[i][j]);
		return result;
	}

	/**
     * Compares this object against the specified object.  
     * @param   obj   
     * 				the object to compare with.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null) && (obj instanceof Mat3) 
			&& (((Mat3) obj).getRow(0)).equals(getRow(0))
			&& (((Mat3) obj).getRow(1)).equals(getRow(1))
			&& (((Mat3) obj).getRow(2)).equals(getRow(2));
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
	 * Compares this Mat3 against the specified Mat3.
	 * 
	 * @param mat
	 *            the mat to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both mats are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Mat3 mat, double epsilon) {
		return (this == mat) || (mat != null) 
				&& getRow(0).eEquals( mat.getRow(0), epsilon) 
				&& getRow(1).eEquals( mat.getRow(1), epsilon)
				&& getRow(2).eEquals( mat.getRow(2), epsilon);
	}

	/**
	 * Compares this Mat3 against the specified Mat3.
	 * 
	 * @param mat
	 *            the mat to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Mat3 mat) {
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
		return String.format(Locale.US, "{{"+format+","+format+","+format+"},"+
				"{"+format+","+format+","+format+"},\n"+
				"{"+format+","+format+","+format+"}\n",
				mat[0][0], mat[0][1], mat[0][2],
				mat[1][0], mat[1][1], mat[1][2],
				mat[2][0], mat[2][1], mat[2][2]);
	}
}
