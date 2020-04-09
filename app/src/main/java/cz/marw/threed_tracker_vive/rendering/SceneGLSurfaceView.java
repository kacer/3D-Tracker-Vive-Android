package cz.marw.threed_tracker_vive.rendering;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class SceneGLSurfaceView extends GLSurfaceView {

    private static final double ANGLE_MULTIPLIER = 1.35;
    private double previousX;
    private double previousY;

    private GLRenderer renderer;
    private Context context;
    private ScaleGestureDetector scaleDetector;

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
        this.context = context;
        setEGLContextClientVersion(3);
        renderer = new GLRenderer(context);
        setRenderer(renderer);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void changeBackground() {
        renderer.changeBackground();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleDetector.onTouchEvent(e);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = e.getX();
                previousY = e.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                double newX = e.getX();
                double newY = e.getY();

                double dx = newX - previousX;
                double dy = newY - previousY;

                renderer.computeAngles(dx * ANGLE_MULTIPLIER, dy * ANGLE_MULTIPLIER);

                previousX = newX;
                previousY = newY;
                break;
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            renderer.zoom(detector.getScaleFactor());

            return true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderer.close();
    }
}
