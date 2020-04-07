package cz.marw.threed_tracker_vive.connectivity.characteristicoperation;

import android.bluetooth.BluetoothGatt;

public interface CharacteristicOperation {

    boolean execute(BluetoothGatt gatt);

}
