package cz.marw.threed_tracker_vive.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec3D;


public class PositionTracker implements Parcelable {

    private BluetoothDevice device;

    private boolean connected;

    private State state = State.UNKNOWN;

    public enum State {
        UNKNOWN, GEOMETRY_NOT_SET, GEOMETRY_IS_SET, RECEIVING_COORDINATES
    }

    /**
     * There is stored drawable gradient.
     */
    private @DrawableRes int colorDrawable;

    private @ColorRes int color;

    private Vec3D coordinates;


    public PositionTracker(BluetoothDevice device) {
        this.device = device;
    }

    private PositionTracker(Parcel in) {
        device = BluetoothDevice.CREATOR.createFromParcel(in);
        connected = in.readByte() == 1;
        state = State.values()[in.readInt()];
        colorDrawable = in.readInt();
    }

    public static final Parcelable.Creator<PositionTracker> CREATOR = new Creator<PositionTracker>() {
        @Override
        public PositionTracker createFromParcel(Parcel source) {
            return new PositionTracker(source);
        }

        @Override
        public PositionTracker[] newArray(int size) {
            return new PositionTracker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        device.writeToParcel(dest, flags);
        dest.writeByte((byte) (connected ? 1 : 0));
        dest.writeInt(state.ordinal());
        dest.writeInt(colorDrawable);

    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if(!connected) {
            setState(State.UNKNOWN);
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @DrawableRes
    public int getColorDrawable() {
        return colorDrawable;
    }

    public void setColorDrawable(@DrawableRes int colorDrawable) {
        this.colorDrawable = colorDrawable;
        switch(colorDrawable) {
            case R.drawable.item_3d_tracker_blue:
                color = R.color.colorTrackerBlueMiddle;
                break;
            case R.drawable.item_3d_tracker_cherry:
                color = R.color.colorTrackerCherryMiddle;
                break;
            case R.drawable.item_3d_tracker_green:
                color = R.color.colorTrackerGreenMiddle;
                break;
            case R.drawable.item_3d_tracker_orange:
                color = R.color.colorTrackerOrangeMiddle;
                break;
            case R.drawable.item_3d_tracker_pink:
                color = R.color.colorTrackerPinkMiddle;
                break;
            case R.drawable.item_3d_tracker_purple:
                color = R.color.colorTrackerPurpleMiddle;
                break;
            case R.drawable.item_3d_tracker_red:
                color = R.color.colorTrackerRedMiddle;
                break;
        }
    }

    public int getColor() {
        return color;
    }

    public Vec3D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Vec3D coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int hashCode() {
        return device.getAddress().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof PositionTracker) {
            return device.equals(((PositionTracker) obj).getDevice());
        }

        return false;
    }
}
