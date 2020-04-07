package cz.marw.threed_tracker_vive.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ByteUtils {

    /**
     * Convert float array into byte array with Little Endian order.
     * @param floats to be converted to raw bytes
     * @return byte representation of floats in given float array
     */
    public static byte[] floatsToBytesLE(float[] floats) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * floats.length).order(ByteOrder.nativeOrder());
        for(float f : floats) {
            buffer.putFloat(f);
        }

        return buffer.array();
    }

    /**
     * Convert raw bytes in Little Endian order into float array.
     * @param bytes raw bytes
     * @return floats
     */
    public static float[] bytesToFloatsLE(byte[] bytes) {
        FloatBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] floats = new float[buffer.limit()];
        buffer.get(floats);

        return floats;
    }

}
