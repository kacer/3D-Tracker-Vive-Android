package cz.marw.threed_tracker_vive.rendering.transforms;

/**
 * A 4x4 identity matrix
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4Identity extends Mat4 {

	/**
	 * Creates an identity 4x4 matrix
	 */
	public Mat4Identity() {
		for (int i = 0; i < 4; i++)
			mat[i][i] = 1.0f;
	}
}
