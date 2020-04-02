package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 3x3 identity matrix
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat3Identity extends Mat3 {

	/**
	 * Creates an identity 3x3 matrix
	 */
	public Mat3Identity() {
		for (int i = 0; i < 3; i++)
			mat[i][i] = 1.0f;
	}
}
