package cz.marw.threed_tracker_vive.rendering;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class SceneGLSurfaceView extends GLSurfaceView {

    private GLRenderer renderer;

    public SceneGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    // this constructor is needed when inflating view from XML layout
    public SceneGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(3);
        renderer = new GLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderer.close();
    }
}
