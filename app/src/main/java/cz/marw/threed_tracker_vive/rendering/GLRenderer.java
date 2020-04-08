package cz.marw.threed_tracker_vive.rendering;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cz.marw.threed_tracker_vive.connectivity.DevicesScanner;
import cz.marw.threed_tracker_vive.geometry.Geometry;
import cz.marw.threed_tracker_vive.model.PositionTracker;
import cz.marw.threed_tracker_vive.rendering.entities.Axis;
import cz.marw.threed_tracker_vive.rendering.entities.BaseStation;
import cz.marw.threed_tracker_vive.rendering.entities.Cube;
import cz.marw.threed_tracker_vive.rendering.entities.Entity;
import cz.marw.threed_tracker_vive.rendering.entities.Tracker3D;
import cz.marw.threed_tracker_vive.rendering.transforms.CameraYUp;
import cz.marw.threed_tracker_vive.rendering.transforms.Col;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat3;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4PerspRH;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Scale;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Transl;
import cz.marw.threed_tracker_vive.rendering.transforms.Point3D;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec3D;


public class GLRenderer implements GLSurfaceView.Renderer {

    private static final double MAX_CAMERA_ZOOM = 20;
    private static final double MIN_CAMERA_ZOOM = 0.5;

    private Context context;
    private int width, height;
    private BaseStation baseStationTest;
    private Entity baseStation0;
    private Entity baseStation1;
    private Cube tracker;
    private Axis axis;
    private Mat4 proj;
    private CameraYUp cam;
    private Point3D lightPosition;

    private DevicesBroadcastReceiver broadcastReceiver = new DevicesBroadcastReceiver();
    private List<Tracker3D> trackers = Collections.synchronizedList(new ArrayList<>());

    public GLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        DevicesScanner.getInstance()
                .registerBroadcastReceiver(broadcastReceiver, DevicesScanner.ACTION_COORDINATES_RECEIVED,
                        DevicesScanner.ACTION_CONNECTION_STATE, DevicesScanner.ACTION_DEVICE_STATE_CHANGED);
        cam = new CameraYUp(new Vec3D(0, 0, 0), 0 - Math.PI, 0 - Math.PI / 4, 8, false);
        lightPosition = new Point3D(0, 5, 15);
        baseStationTest = new BaseStation(context);
        axis = new Axis(context);
        tracker = new Cube(context, new Col(), lightPosition);

        if(Geometry.isGeometrySet()) {
            Mat4 stationsScale = new Mat4Scale(0.06, 0.083, 0.083);

            Mat4 rotation = new Mat4(new Mat3(
                    Geometry.getGeometry().get(0).getRotationMatrix().getRow(0).getAsVec3D(),
                    Geometry.getGeometry().get(0).getRotationMatrix().getRow(1).getAsVec3D(),
                    Geometry.getGeometry().get(0).getRotationMatrix().getRow(2).getAsVec3D()
            ));
            baseStation0 = new Cube(context, new Col(0.5, 0.35, 0.8), lightPosition);
            baseStation0.pushModelMatrix(new Mat4Transl(Geometry.getGeometry().get(0).getOrigin().getAsVec3D()));
            //baseStation0.pushModelMatrix(new Mat4Transl(0, 0, 0.15));
            baseStation0.pushModelMatrix(rotation);
            baseStation0.pushModelMatrix(stationsScale);

            rotation = new Mat4(new Mat3(
                    Geometry.getGeometry().get(1).getRotationMatrix().getRow(0).getAsVec3D(),
                    Geometry.getGeometry().get(1).getRotationMatrix().getRow(1).getAsVec3D(),
                    Geometry.getGeometry().get(1).getRotationMatrix().getRow(2).getAsVec3D()
            ));
            baseStation1 = new Cube(context, new Col(0, 0, 0), lightPosition);
            baseStation1.pushModelMatrix(new Mat4Transl(Geometry.getGeometry().get(1).getOrigin().getAsVec3D()));
            baseStation1.pushModelMatrix(rotation);
            baseStation1.pushModelMatrix(stationsScale);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GLES31.glBindFrameBuffer(GLES31.GL_FRAMEBUFFER, 0);
        //GLES31.glViewport(0, 0, width, height);
        GLES31.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);

        GLES31.glEnable(GLES31.GL_DEPTH_TEST);

        baseStationTest.draw(cam.getViewMatrix(), proj);
        axis.draw(cam.getViewMatrix(), proj);
        baseStation0.draw(cam.getViewMatrix(), proj);
        baseStation1.draw(cam.getViewMatrix(), proj);

        for(Tracker3D t : trackers) {
            if(t.isReadyToDraw()) {
                t.bind(tracker);
                tracker.draw(cam.getViewMatrix(), proj);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES31.glViewport(0, 0, width, height);
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.1, 50);
    }

    public void close() {
        //TODO: cleanUp entities
        baseStationTest.cleanUp();
        baseStation0.cleanUp();
        baseStation1.cleanUp();
        tracker.cleanUp();
        axis.cleanUp();
        DevicesScanner.getInstance()
                .unregisterBroadcastReceiver(broadcastReceiver);
    }

    public void computeAngles(double dx, double dy) {
        double zenith = cam.getZenith() + Math.PI * (dy / height);
        double azimuth = cam.getAzimuth() + Math.PI * (dx / width);

        if(zenith > Math.PI / 2) zenith = Math.PI / 2;
        if(zenith < -Math.PI / 2) zenith = -Math.PI / 2;
        azimuth = azimuth % (2 * Math.PI);

        cam = cam.withAzimuth(azimuth).withZenith(zenith);
    }

    public void zoom(float scale) {
        double scaleFactor = cam.getRadius() / scale;
        scaleFactor = Math.max(MIN_CAMERA_ZOOM, Math.min(scaleFactor, MAX_CAMERA_ZOOM));
        cam = cam.withRadius(scaleFactor);
    }

    class DevicesBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(DevicesScanner.ACTION_COORDINATES_RECEIVED.equals(action)) {
                handleCoordinatesReceived(intent);

            } else if(DevicesScanner.ACTION_CONNECTION_STATE.equals(action)) {
                handleConnectionState(intent);

            } else if(DevicesScanner.ACTION_DEVICE_STATE_CHANGED.equals(action)) {
                handleDevicesStateChange(intent);
            }
        }
    }

    private void handleConnectionState(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);
        if(!tracker.isConnected()) {
            Tracker3D tracker3D = getTracker3D(tracker);
            trackers.remove(tracker3D);
        }
    }

    private void handleDevicesStateChange(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);
        updateTracker3D(tracker);
    }

    private void handleCoordinatesReceived(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);
        Tracker3D tracker3D = getTracker3D(tracker);
        if(tracker3D == null) {
            int color;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = context.getColor(tracker.getColor());
            } else {
                color = context.getResources().getColor(tracker.getColor());
            }

            trackers.add(new Tracker3D(tracker, new Col(color)));
        } else {
            updateTracker3D(tracker);
        }
    }

    private Tracker3D getTracker3D(PositionTracker tracker) {
        for(Tracker3D t : trackers) {
            if(t.getTracker().equals(tracker)) {
                return t;
            }
        }

        return null;
    }

    private void updateTracker3D(PositionTracker tracker) {
        Tracker3D tracker3D = getTracker3D(tracker);
        if(tracker3D != null) {
            tracker3D.setTracker(tracker);
        }
    }

    private void checkGLErrors() {
        int error;

        while((error = GLES31.glGetError()) != GLES31.GL_NO_ERROR) {
            System.out.println("GL Error: " + error);
        }
    }

}
