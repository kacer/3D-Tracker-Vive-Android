package cz.marw.threed_tracker_vive.rendering;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cz.marw.threed_tracker_vive.rendering.entities.Rectangle;
import cz.marw.threed_tracker_vive.rendering.entities.Triangle;
import cz.marw.threed_tracker_vive.rendering.transforms.Col;


public class GLRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private int width, height;
    private Rectangle rectangle;
    private Triangle triangle;

    public GLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        rectangle = new Rectangle(context);
        triangle = new Triangle(context, new Col(0.949, 0.475, 0.286));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GLES31.glBindFrameBuffer(GLES31.GL_FRAMEBUFFER, 0);
        //GLES31.glViewport(0, 0, width, height);
        GLES31.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        rectangle.draw(null, null, null);
        triangle.draw(null, null, null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES31.glViewport(0, 0, width, height);
    }

    public void close() {
        rectangle.cleanUp();
        triangle.cleanUp();
    }

    private void checkGLErrors() {
        int error;
        while((error = GLES31.glGetError()) != GLES31.GL_NO_ERROR) {
            System.out.println("GL Error: " + error);
        }
    }

}
