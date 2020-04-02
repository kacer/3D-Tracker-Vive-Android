package cz.marw.threed_tracker_vive.rendering.glutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Buffers {

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * Sizeof.FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(data);
        buffer.flip();  // prepare buffer for reading

        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = ByteBuffer.allocateDirect(data.length * Sizeof.INT)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data);
        buffer.flip();  // prepare buffer for reading

        return buffer;
    }

}
