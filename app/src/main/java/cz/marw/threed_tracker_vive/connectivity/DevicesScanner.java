package cz.marw.threed_tracker_vive_android.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DevicesScanner {

    public DevicesScanner(BluetoothManager manager, Context context) {
        BluetoothLeScanner leScanner = manager.getAdapter().getBluetoothLeScanner();
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilterBuilder.setServiceUuid(ParcelUuid.fromString("3d000000-a000-cafe-0404-00451f003d3d"));
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setReportDelay(0);

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(scanFilterBuilder.build());
        leScanner.startScan(filters, scanSettingsBuilder.build(), new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                System.out.println(result);
                leScanner.stopScan(this);
                result.getDevice().connectGatt(context, true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if(newState == BluetoothProfile.STATE_CONNECTED) {
                            boolean state = gatt.discoverServices();
                            System.out.println("Does discovery process start? - " + state);
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString("3d000000-a000-cafe-0404-00451f003d3d")).getCharacteristic(UUID.fromString("3d000000-a001-cafe-0404-00451f003d3d"));
                        boolean state = gatt.setCharacteristicNotification(characteristic, true);
                        UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                        System.out.println("Set notification? - " + state);
                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        float x = ByteBuffer.wrap(characteristic.getValue(), 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        float y = ByteBuffer.wrap(characteristic.getValue(), 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        float z = ByteBuffer.wrap(characteristic.getValue(), 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        System.out.println(x + " " + y + " " + z);
                    }
                });
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                System.out.println("onBatchScanResults");
                for(ScanResult sc : results) {
                    System.out.println(sc);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                System.out.println("onScanFailed with error: " + errorCode);
            }
        });
    }

    private byte[] copyBytes(byte[] src, int offset, int length) {
        if(length > src.length || offset < 0 || offset >= length) {
            return null;
        }
        byte[] bytes = new byte[length - offset];
        for(int i = 0, j = offset; i < bytes.length && j < (offset + length); i++, j++) {
            bytes[i] = src[j];
        }

        return bytes;
    }

}
