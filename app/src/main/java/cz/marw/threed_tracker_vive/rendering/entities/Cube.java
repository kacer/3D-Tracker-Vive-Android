package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.ToFloatArray;
import cz.marw.threed_tracker_vive.rendering.transforms.Col;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Point3D;

public class Cube extends Entity {

    private int locColor, locLightPosition;
    private Col color;
    private Point3D light;

    public Cube(Context context, Col color, Point3D light) {
        super(context, "cube");
        this.color = color;
        this.light = light;
        locColor = GLES31.glGetUniformLocation(shaderProgram, "color");
        locLightPosition = GLES31.glGetUniformLocation(shaderProgram, "lightPosition");
    }

    @Override
    protected GLBuffers createGeometry(Context context) {
        float[] vertices = new float[] {
                // bottom (z-) face
                0.5f, -0.5f, -0.5f, 	0, 0, -1,
                -0.5f, -0.5f, -0.5f,	0, 0, -1,
                0.5f, 0.5f, -0.5f,  	0, 0, -1,
                -0.5f, 0.5f, -0.5f, 	0, 0, -1,
                // top (z+) face
                0.5f, -0.5f, 0.5f,	    0, 0, 1,
                -0.5f, -0.5f, 0.5f,	    0, 0, 1,
                0.5f, 0.5f, 0.5f,   	0, 0, 1,
                -0.5f, 0.5f, 0.5f,	    0, 0, 1,
                // x+ face
                0.5f, 0.5f, -0.5f,	    1, 0, 0,
                0.5f, -0.5f, -0.5f, 	1, 0, 0,
                0.5f, 0.5f, 0.5f,	    1, 0, 0,
                0.5f, -0.5f, 0.5f,	    1, 0, 0,
                // x- face
                -0.5f, 0.5f, -0.5f,	    -1, 0, 0,
                -0.5f, -0.5f, -0.5f,	-1, 0, 0,
                -0.5f, 0.5f, 0.5f,	    -1, 0, 0,
                -0.5f, -0.5f, 0.5f, 	-1, 0, 0,
                // y+ face
                0.5f, 0.5f, -0.5f,	    0, 1, 0,
                -0.5f, 0.5f, -0.5f,	    0, 1, 0,
                0.5f, 0.5f, 0.5f,	    0, 1, 0,
                -0.5f, 0.5f, 0.5f,	    0, 1, 0,
                // y- face
                0.5f, -0.5f, -0.5f,	    0, -1, 0,
                -0.5f, -0.5f, -0.5f,	0, -1, 0,
                0.5f, -0.5f, 0.5f,	    0, -1, 0,
                -0.5f, -0.5f, 0.5f,	    0, -1, 0
        };

        int[] indices = new int[36];
        for (int i = 0; i<6; i++){
            indices[i*6] = i*4;
            indices[i*6 + 1] = i*4 + 1;
            indices[i*6 + 2] = i*4 + 2;
            indices[i*6 + 3] = i*4 + 1;
            indices[i*6 + 4] = i*4 + 2;
            indices[i*6 + 5] = i*4 + 3;
        }

        GLBuffers.Attributes[] attributes = new GLBuffers.Attributes[] {
                new GLBuffers.Attributes("inPosition", 3),
                new GLBuffers.Attributes("inNormal", 3)
        };

        return new GLBuffers(vertices, indices, attributes);
    }

    @Override
    public void draw(Mat4 view, Mat4 projection) {
        GLES31.glUseProgram(shaderProgram);
        GLES31.glUniformMatrix4fv(locMatM, 1, false, getModelMat().floatArray(), 0);
        GLES31.glUniformMatrix4fv(locMatV, 1, false, view.floatArray(), 0);
        GLES31.glUniformMatrix4fv(locMatP, 1, false, projection.floatArray(), 0);
        GLES31.glUniform4fv(locColor, 1, ToFloatArray.convert(color), 0);
        GLES31.glUniform4fv(locLightPosition, 1, ToFloatArray.convert(light), 0);
        glBuffers.draw(GLES31.GL_TRIANGLES, shaderProgram);
        GLES31.glUseProgram(0);
    }

    public void setColor(Col color) {
        this.color = color;
    }
}
