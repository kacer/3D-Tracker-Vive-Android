package cz.marw.threed_tracker_vive.rendering.glutils;

import android.opengl.GLES20;
import android.opengl.GLES31;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLBuffers {

    public static class Attributes {

        String name;
        int dimension;
        boolean normalize = false;
        int offset = -1;

        public Attributes(String name, int dimension) {
            this.name = name;
            this.dimension = dimension;
        }

        public Attributes(String name, int dimension, int offsetInFloats) {
            this.name = name;
            this.dimension = dimension;
            this.offset = Sizeof.FLOAT * offsetInFloats;
        }

        public Attributes(String name, int dimension, boolean normalize) {
            this.name = name;
            this.dimension = dimension;
            this.normalize = normalize;
        }

        public Attributes(String name, int dimension, boolean normalize, int offsetInFloats) {
            this.name = name;
            this.dimension = dimension;
            this.normalize = normalize;
            this.offset = Sizeof.FLOAT * offsetInFloats;
        }
    }

    protected class VertexBuffer {

        int id;
        int stride;
        Attributes[] attributes;

        public VertexBuffer(int id, int stride, Attributes[] attributes) {
            this.id = id;
            this.stride = stride;
            this.attributes = attributes;
        }
    }

    protected List<VertexBuffer> vertexBuffers = new ArrayList<>();
    protected List<Integer> attributesArrays = null;
    protected int[] indexBuffer;
    protected int indexCount = -1;
    protected int vertexCount = -1;

    public GLBuffers() {}

    public GLBuffers(float[] vertices, int[] indices, Attributes[] attributes) {
        addVertexBuffer(vertices, attributes);
        if(indices != null) {
            setIndexBuffer(indices);
        }
    }

    public GLBuffers(float[] vertices, int floatsPerVertex, int[] indices, Attributes[] attributes) {
        addVertexBuffer(vertices, floatsPerVertex, attributes);
        if(indices != null) {
            setIndexBuffer(indices);
        }
    }

    public void addVertexBuffer(float[] vertices, Attributes[] attributes) {
        if(attributes == null || attributes.length == 0) {
            return;
        }

        int floatsPerVertex = 0;
        for(int i = 0; i < attributes.length; i++) {
            floatsPerVertex += attributes[i].dimension;
        }

        addVertexBuffer(vertices, floatsPerVertex, attributes);
    }

    public void addVertexBuffer(float[] vertices, int floatsPerVertex, Attributes[] attributes) {
        int[] vboID = new int[1];
        FloatBuffer buffer = Buffers.storeDataInFloatBuffer(vertices);
        GLES31.glGenBuffers(1, vboID, 0);
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vboID[0]);
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, vertices.length * Sizeof.FLOAT, buffer, GLES31.GL_STATIC_DRAW);

        if(vertices.length % floatsPerVertex != 0) {
            throw new RuntimeException("The total number of floats is incongruent with the number of floats per vertex.");
        }

        if(vertexCount < 0) {
            vertexCount = vertices.length / floatsPerVertex;
        } else if(vertexCount != vertices.length / floatsPerVertex) {
            System.out.println("Warning: GLBuffers.addVertexBuffer: vertex count differs from the first one.");
        }

        vertexBuffers.add(new VertexBuffer(vboID[0], floatsPerVertex * Sizeof.FLOAT, attributes));
    }

    public void setIndexBuffer(int[] indices) {
        indexBuffer = new int[1];
        indexCount = indices.length;
        IntBuffer buffer = Buffers.storeDataInIntBuffer(indices);
        GLES31.glGenBuffers(1, indexBuffer, 0);
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
        GLES31.glBufferData(GLES31.GL_ELEMENT_ARRAY_BUFFER, indices.length * Sizeof.INT, buffer, GLES20.GL_STATIC_DRAW);
    }

    public void bind(int shaderProgram) {
        if(attributesArrays != null) {
            for(Integer attribute : attributesArrays) {
                GLES31.glDisableVertexAttribArray(attribute);
            }
        }

        attributesArrays = new ArrayList<>();
        for(VertexBuffer vb : vertexBuffers) {
            GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vb.id);
            int offset = 0;
            for(int j = 0; j < vb.attributes.length; j++) {
                Attributes attr = vb.attributes[j];

                int location = GLES31.glGetAttribLocation(shaderProgram, attr.name);
                if(location >= 0) {
                    attributesArrays.add(location);
                    GLES31.glEnableVertexAttribArray(location);
                    GLES31.glVertexAttribPointer(location, attr.dimension, GLES31.GL_FLOAT, attr.normalize,
                            vb.stride, (attr.offset < 0) ? offset : attr.offset);
                }

                offset += Sizeof.FLOAT * attr.dimension;
            }
        }

        if(indexBuffer != null) {
            GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
        }
    }

    public void unbind() {
        if(attributesArrays != null) {
            for(Integer attribute : attributesArrays) {
                GLES31.glDisableVertexAttribArray(attribute);
            }
            attributesArrays = null;
        }
    }

    public void draw(int topology, int shaderProgram) {
        bind(shaderProgram);
        if(indexBuffer == null) {
            GLES31.glDrawArrays(topology, 0, vertexCount);
        } else {
            GLES31.glDrawElements(topology, indexCount, GLES31.GL_UNSIGNED_INT, 0);
        }
        unbind();
    }

    public void draw(int topology, int shaderProgram, int count) {
        draw(topology, shaderProgram, count, 0);
    }

    public void draw(int topology, int shaderProgram, int count, int start) {
        bind(shaderProgram);
        if(indexBuffer == null) {
            GLES31.glDrawArrays(topology, start, count);
        } else {
            GLES31.glDrawElements(topology, count, GLES31.GL_UNSIGNED_INT, start * Sizeof.FLOAT);
        }
        unbind();
    }

    public void cleanUp() {
        if(indexBuffer != null) {
            GLES31.glDeleteBuffers(1, indexBuffer, 0);
        }
        int[] vboIDs = new int[vertexBuffers.size()];
        for(int i = 0; i < vertexBuffers.size(); i++) {
            vboIDs[i] = vertexBuffers.get(i).id;
        }
        GLES31.glDeleteBuffers(vboIDs.length, vboIDs, 0);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        cleanUp();
    }
}
