package cz.marw.threed_tracker_vive.rendering.entities;

import cz.marw.threed_tracker_vive.model.PositionTracker;
import cz.marw.threed_tracker_vive.rendering.transforms.Col;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Scale;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4Transl;

public class Tracker3D {

    private PositionTracker tracker;
    private Col color;

    public Tracker3D(PositionTracker tracker, Col color) {
        this.tracker = tracker;
        this.color = color;
    }

    public boolean isReadyToDraw() {
        return tracker.isConnected() &&
                tracker.getState().equals(PositionTracker.State.RECEIVING_COORDINATES);
    }

    public void bind(Cube cube) {
        cube.setColor(color);
        cube.clearModelMatrices();
        cube.pushModelMatrix(new Mat4Transl(tracker.getCoordinates()));
        cube.pushModelMatrix(new Mat4Scale(0.062, 0.005, 0.047)); // dimens of 3D tracker in meters, Y is a little bit boost
    }

    public PositionTracker getTracker() {
        return tracker;
    }

    public void setTracker(PositionTracker tracker) {
        this.tracker = tracker;
    }

}
