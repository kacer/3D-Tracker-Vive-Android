package cz.marw.threed_tracker_vive.connectivity.characteristicoperation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class ReadOperation implements CharacteristicOperation {

    private BluetoothGattCharacteristic characteristic;

    public ReadOperation(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public boolean execute(BluetoothGatt gatt) {
        return gatt.readCharacteristic(characteristic);
    }
}
