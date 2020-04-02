package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Identity;

public class Rectangle extends Entity {

    private int matLoc;

    public Rectangle(Context context) {
        super(context, "test");
        matLoc = GLES31.glGetUniformLocation(shaderProgram, "matMVP");
    }

    @Override
    public void draw(Mat4 model, Mat4 view, Mat4 projection) {
        GLES31.glUseProgram(shaderProgram);
        GLES31.glUniformMatrix4fv(matLoc, 1, false, new Mat4Identity().floatArray(), 0);
        glBuffers.draw(GLES31.GL_TRIANGLES, shaderProgram);
        GLES31.glUseProgram(0);
    }

    @Override
    protected GLBuffers createGeometry() {
        float[] vertices = new float[] {
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                 0.5f,  0.5f, 0.0f,
                 0.5f, -0.5f, 0.0f
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 1, 3
        };

        GLBuffers.Attributes[] attributes = new GLBuffers.Attributes[] {
                new GLBuffers.Attributes("position", 3)
        };

        return new GLBuffers(vertices, indices, attributes);

    }

}
