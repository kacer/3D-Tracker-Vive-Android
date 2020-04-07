package cz.marw.threed_tracker_vive.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;


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
    private @DrawableRes int color;


    public PositionTracker(BluetoothDevice device) {
        this.device = device;
    }

    private PositionTracker(Parcel in) {
        device = BluetoothDevice.CREATOR.createFromParcel(in);
        connected = in.readByte() == 1;
        state = State.values()[in.readInt()];
        color = in.readInt();
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
        dest.writeInt(color);

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
    public int getColor() {
        return color;
    }

    public void setColor(@DrawableRes int color) {
        this.color = color;
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
