package cz.marw.threed_tracker_vive.rendering.entities;

import android.content.Context;
import android.opengl.GLES31;

import cz.marw.threed_tracker_vive.rendering.glutils.GLBuffers;
import cz.marw.threed_tracker_vive.rendering.glutils.ShaderLoader;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;

public abstract class Entity {

    protected GLBuffers glBuffers;

    protected int shaderProgram;

    public Entity(Context context) {}

    public Entity(Context context, String shaderFilename) {
        glBuffers = createGeometry();
        shaderProgram = ShaderLoader.loadProgram(shaderFilename, context);
    }

    protected abstract GLBuffers createGeometry();

    public abstract void draw(final Mat4 model, final Mat4 view, final Mat4 projection);

    public void cleanUp() {
        glBuffers.cleanUp();
        GLES31.glDeleteProgram(shaderProgram);
    }

}
