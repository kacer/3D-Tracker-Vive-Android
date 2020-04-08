package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.ShaderLoader;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Identity;

public abstract class Entity {

    protected GLBuffers glBuffers;
    protected Deque<Mat4> modelMatricesStack = new ConcurrentLinkedDeque<>();

    protected int shaderProgram;
    protected int locMatM, locMatV, locMatP;

    public Entity(Context context, String shaderFilename) {
        glBuffers = createGeometry(context);
        shaderProgram = ShaderLoader.loadProgram(shaderFilename, context);
        locMatM = GLES31.glGetUniformLocation(shaderProgram, "matM");
        locMatV = GLES31.glGetUniformLocation(shaderProgram, "matV");
        locMatP = GLES31.glGetUniformLocation(shaderProgram, "matP");
    }

    protected abstract GLBuffers createGeometry(Context context);

    public abstract void draw(final Mat4 view, final Mat4 projection);

    public Mat4 getModelMat() {
        Mat4 model = new Mat4Identity();
        for(Mat4 m : modelMatricesStack) {
            model = model.mul(m);
        }

        return model;
    }

    /**
     * Through this method can be pushed into LIFO several model matrix.
     * Last pushed matrix will be applied as first before render in draw method.
     *
     * @param model - model matrix
     */
    public void pushModelMatrix(Mat4 model) {
        modelMatricesStack.push(model);
    }

    /**
     * This method serve to pop last pushed model matrix into LIFO.
     *
     * @return last pushed model matrix or identity matrix when LIFO is empty
     */
    public Mat4 popModelMatrix() {
        if(!modelMatricesStack.isEmpty())
            return modelMatricesStack.pop();

        return new Mat4Identity();
    }

    /**
     * After call this method LIFO with model matrices will be erased.
     */
    public void clearModelMatrices() {
        modelMatricesStack.clear();
    }

    public void cleanUp() {
        glBuffers.cleanUp();
        GLES31.glDeleteProgram(shaderProgram);
    }

}
