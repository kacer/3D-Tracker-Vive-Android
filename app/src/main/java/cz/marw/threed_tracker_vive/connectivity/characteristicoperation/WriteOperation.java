package cz.marw.threed_tracker_vive.connectivity.characteristicoperation;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class WriteOperation implements CharacteristicOperation {

    private BluetoothGattCharacteristic characteristic;
    private byte[] valueInBytes;

    public WriteOperation(BluetoothGattCharacteristic characteristic,
                          byte[] valueInBytes) {
        this.characteristic = characteristic;
        this.valueInBytes = valueInBytes;
    }

    @Override
    public boolean execute(BluetoothGatt gatt) {
        characteristic.setValue(valueInBytes);
        return gatt.writeCharacteristic(characteristic);
    }
}
