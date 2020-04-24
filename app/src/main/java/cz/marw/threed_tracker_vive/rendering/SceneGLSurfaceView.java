package cz.marw.threed_tracker_vive.rendering;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class SceneGLSurfaceView extends GLSurfaceView {

    private static final int INVALID_POINTER_ID = -1;
    private static final double ANGLE_MULTIPLIER = 1.35;
    private double previousX;
    private double previousY;
    private boolean scaleStarted;
    private int activePointerId = INVALID_POINTER_ID;

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

        final int action = e.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = e.getActionIndex();
                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                previousX = x;
                previousY = y;

                activePointerId = e.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = e.findPointerIndex(activePointerId);

                double newX = e.getX(pointerIndex);
                double newY = e.getY(pointerIndex);

                double dx = newX - previousX;
                double dy = newY - previousY;

                if(!scaleStarted)
                    renderer.computeAngles(dx, dy);

                previousX = newX;
                previousY = newY;
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);

                if(pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    previousX = e.getX(newPointerIndex);
                    previousY = e.getY(newPointerIndex);
                    activePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            renderer.zoom(detector.getScaleFactor());

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            scaleStarted = true;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaleStarted = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderer.close();
    }
}
