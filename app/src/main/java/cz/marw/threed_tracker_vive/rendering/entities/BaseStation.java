package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;;

import java.io.IOException;
import java.io.InputStream;

import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.GLTexture2D;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

public class BaseStation extends Entity {

    private int texture0ID;

    public BaseStation(Context context) {
        super(context, "objRenderer");
    }

    @Override
    protected GLBuffers createGeometry(Context context) {
        texture0ID = GLTexture2D.loadTexture(context, R.raw.lh_basestation_vive);

        Obj obj;
        try {
            InputStream in = context.getAssets().open("models/lh_basestation_vive/lh_basestation_vive.obj");
            obj = ObjUtils.convertToRenderable(ObjReader.read(in));
        } catch(IOException e) {
            System.err.println("Could not open OBJ file - BaseStation");

            return new GLBuffers(); // return empty buffer
        }

        float[] vertices = ObjData.getVerticesArray(obj);
        int[] indices = ObjData.getFaceVertexIndicesArray(obj);
        float[] texCoords = ObjData.getTexCoordsArray(obj, 2);
        float[] normals = ObjData.getNormalsArray(obj);

        float[] data = new float[vertices.length + normals.length + texCoords.length];
        int vertexIndex = 0;
        int normalIndex = 0;
        int texCoordsIndex = 0;
        for(int i = 0; i < data.length; i += 8) {
            data[i    ] = vertices[vertexIndex++];
            data[i + 1] = vertices[vertexIndex++];
            data[i + 2] = vertices[vertexIndex++];
            data[i + 3] = normals[normalIndex++];
            data[i + 4] = normals[normalIndex++];
            data[i + 5] = normals[normalIndex++];
            data[i + 6] = texCoords[texCoordsIndex++];
            data[i + 7] = texCoords[texCoordsIndex++];
        }

        /*System.out.println("Vertices count: " + vertices.length + " Normals count: " +
                normals.length + " Texture coordinates count: " + texCoords.length);

        for(int i = 0; i < 10; i++) {
            System.out.println("v " + vertices[i] + " vn " + normals[i] + " vt " + texCoords[i] +
                    " f " + indices[i]);
        }*/

        GLBuffers.Attributes[] attributes = new GLBuffers.Attributes[] {
                new GLBuffers.Attributes("inPosition", 3),
                new GLBuffers.Attributes("inNormal", 3),
                new GLBuffers.Attributes("inTexCoords", 2)
        };

        return new GLBuffers(data, indices, attributes);
    }

    @Override
    public void draw(Mat4 view, Mat4 projection) {
        GLES31.glUseProgram(shaderProgram);
        GLES31.glUniformMatrix4fv(locMatM, 1, false, getModelMat().floatArray(), 0);
        GLES31.glUniformMatrix4fv(locMatV, 1, false, view.floatArray(), 0);
        GLES31.glUniformMatrix4fv(locMatP, 1, false, projection.floatArray(), 0);
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, texture0ID);
        glBuffers.draw(GLES31.GL_TRIANGLES, shaderProgram);
        GLES31.glUseProgram(0);
    }
}
