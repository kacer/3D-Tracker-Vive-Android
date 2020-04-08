package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.ToFloatArray;
import cz.marw.threed_tracker_vive.rendering.transforms.Col;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Identity;

public class Triangle extends Entity {

    private int matLoc;
    private int colorLoc;
    private Col color = new Col();

    public Triangle(Context context, Col color) {
        super(context, "test");
        this.color = color;
        matLoc = GLES31.glGetUniformLocation(shaderProgram, "matMVP");
        colorLoc = GLES31.glGetUniformLocation(shaderProgram, "color");
    }

    @Override
    public void draw(Mat4 view, Mat4 projection) {
        GLES31.glUseProgram(shaderProgram);
        GLES31.glUniformMatrix4fv(matLoc, 1, false, getModelMat().floatArray(), 0);
        GLES31.glProgramUniform4fv(shaderProgram, colorLoc, 1, ToFloatArray.convert(color), 0);
        glBuffers.draw(GLES31.GL_TRIANGLES, shaderProgram);
        GLES31.glUseProgram(0);
    }

    @Override
    protected GLBuffers createGeometry(Context context) {
        float[] vertices = new float[] {
                -0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        GLBuffers.Attributes[] attributes = new GLBuffers.Attributes[] {
                new GLBuffers.Attributes("position", 3)
        };

        GLBuffers glBuffers = new GLBuffers();
        glBuffers.addVertexBuffer(vertices, attributes);

        return glBuffers;
    }

}
