package cz.marw.threed_tracker_vive.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cz.marw.threed_tracker_vive.connectivity.characteristicoperation.CharacteristicOperation;
import cz.marw.threed_tracker_vive.connectivity.characteristicoperation.ReadOperation;
import cz.marw.threed_tracker_vive.connectivity.characteristicoperation.WriteOperation;
import cz.marw.threed_tracker_vive.geometry.Geometry;
import cz.marw.threed_tracker_vive.model.BaseStation;
import cz.marw.threed_tracker_vive.model.PositionTracker;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec3D;
import cz.marw.threed_tracker_vive.util.ByteUtils;
import cz.marw.threed_tracker_vive.util.ColorAllocator;
import cz.marw.threed_tracker_vive.util.PreferenceManager;
import java9.lang.Iterables;
import java9.util.stream.StreamSupport;


public class DevicesScanner {

    private static final String PACKAGE = "cz.marw.threed_tracker_vive";

    public static final String ACTION_SCANNING_STATE = PACKAGE + ".ACTION_SCANNING_STATE";
    public static final String ACTION_CONNECTION_STATE = PACKAGE + ".ACTION_CONNECTION_STATE";
    public static final String ACTION_DEVICE_DISCOVERED = PACKAGE + ".ACTION_DEVICE_DISCOVERED";
    public static final String ACTION_COORDINATES_RECEIVED = PACKAGE + ".ACTION_COORDINATES_RECEIVED";
    public static final String ACTION_DEVICE_STATE_CHANGED = PACKAGE + ".ACTION_DEVICE_STATE_CHANGED";
    public static final String ACTION_DEBUG_BLE_CONNECTION = PACKAGE + ".ACTION_DEBUG_BLE_CONNECTION";

    public static final String KEY_DEVICE_POSITION_TRACKER = "key_device";
    public static final String KEY_SCANNING_STATE_BOOLEAN = "key_scanning_state";
    public static final String KEY_DEBUG_BLE_CONN_STATUS = "key_debug_ble_conn_status";
    public static final String KEY_DEBUG_BLE_CONN_NEW_STATE = "key_debug_ble_conn_new_state";

    public static final String SUBSCRIBE_NOTIFICATION_DESC_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private static final int SCAN_DURATION = 20000;     // 20s

    private List<PositionTracker> devices = new ArrayList<>();
    private List<BluetoothGatt> activeConnections = new ArrayList<>();

    private List<ScanFilter> scanFilters = new ArrayList<>();
    private ScanSettings scanSettings;
    private LeScanCallback scanCallback = new LeScanCallback();
    private BluetoothGattCallback gattCallback = new LeGattCallback();
    private Handler handler = new Handler();
    private boolean scanning;

    private Queue<CharacteristicOperation> characteristicQueue = new LinkedList<>();
    private int stationGeometryIndexR = 0;
    private int stationGeometryIndexW = 0;
    private List<BaseStation> stationsGeometryFromTracker;
    private boolean readingGeometry = false;

    private ColorAllocator colorAllocator = new ColorAllocator();

    private LocalBroadcastManager broadcastManager;

    private static DevicesScanner instance;

    private DevicesScanner(Context context) {
        broadcastManager = LocalBroadcastManager.getInstance(context);
        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setReportDelay(0)
                .build();
        scanFilters.add(new ScanFilter.Builder()
                .setServiceUuid(TrackerServices.SERVICE_COORDINATES_UUID)
                .build());
    }

    public static void init(Context context) {
        if(instance == null) {
            instance = new DevicesScanner(context);
        }
    }

    public static void destroy() {
        instance.close();
        instance = null;
    }

    public static DevicesScanner getInstance() {
        return instance;
    }

    private BluetoothLeScanner getLeScanner() {
        return BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    public void registerBroadcastReceiver(BroadcastReceiver receiver, String... actions) {
        for(String action : actions) {
            broadcastManager.registerReceiver(receiver, new IntentFilter(action));
        }
    }

    public void unregisterBroadcastReceiver(BroadcastReceiver receiver) {
        broadcastManager.unregisterReceiver(receiver);
    }

    private class LeScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            PositionTracker tracker = new PositionTracker(result.getDevice());
            if(!devices.contains(tracker)) {
                devices.add(tracker);
                colorAllocator.allocate(tracker);

                // broadcast new device discovered
                Intent intent = new Intent(ACTION_DEVICE_DISCOVERED);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);
            }
        }
    }

    private class LeGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Intent intent = new Intent(ACTION_DEBUG_BLE_CONNECTION);
                intent.putExtra(KEY_DEBUG_BLE_CONN_STATUS, status);
                intent.putExtra(KEY_DEBUG_BLE_CONN_NEW_STATE, newState);
                if(PreferenceManager.isUserAdmin())
                    broadcastManager.sendBroadcast(intent);
                System.out.println("*****DEBUG_BLE***** -> Status: " + status + " NewState: " + newState);
            }

            if(newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                activeConnections.add(gatt);
                gatt.discoverServices();
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                activeConnections.remove(gatt);
            }

            PositionTracker tracker = getPositionTracker(gatt.getDevice());
            if(tracker != null) {
                tracker.setConnected(newState == BluetoothProfile.STATE_CONNECTED);
                if(!tracker.isConnected())
                    tracker.setState(PositionTracker.State.UNKNOWN);

                // broadcast connection state change
                Intent intent = new Intent(ACTION_CONNECTION_STATE);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status != BluetoothGatt.GATT_SUCCESS)
                return;

            System.out.println("********ON SERVICE DISCOVERED******");
            if(gatt.getServices().isEmpty()) {
                System.out.println("No services were not discovered!");
                gatt.disconnect();
                return;
            } else {
                for(BluetoothGattService service : gatt.getServices()) {
                    System.out.println(service.getUuid());
                }
            }
            readGeometryFromTracker(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            float x = ByteBuffer.wrap(characteristic.getValue(), 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float y = ByteBuffer.wrap(characteristic.getValue(), 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float z = ByteBuffer.wrap(characteristic.getValue(), 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            PositionTracker tracker = getPositionTracker(gatt.getDevice());
            if(tracker == null) {
                return;
            }

            PositionTracker.State state = tracker.getState();
            if(!(state.equals(PositionTracker.State.CONNECTING) ||
                    state.equals(PositionTracker.State.DISCONNECTING) ||
                    state.equals(PositionTracker.State.READING_GEOMETRY) ||
                    state.equals(PositionTracker.State.SENDING_GEOMETRY) ||
                    state.equals(PositionTracker.State.RECEIVING_COORDINATES))) {

                tracker.setState(PositionTracker.State.RECEIVING_COORDINATES);
                Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);
            }

            tracker.setCoordinates(new Vec3D(x, y, z));
            Intent intent = new Intent(ACTION_COORDINATES_RECEIVED);
            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
            broadcastManager.sendBroadcast(intent);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID subscribeNotificationDescUuid = UUID.fromString(SUBSCRIBE_NOTIFICATION_DESC_UUID);
            if(subscribeNotificationDescUuid.equals(descriptor.getUuid())) {
                PositionTracker tracker = getPositionTracker(gatt.getDevice());
                if(tracker == null) {
                    return;
                }

                if(status == BluetoothGatt.GATT_SUCCESS) {
                    tracker.setState(PositionTracker.State.RECEIVING_COORDINATES);
                } else {
                    tracker.setState(PositionTracker.State.NOT_RECEIVING_COORDINATES);
                }

                Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("Characteristic write: " + characteristic.getUuid() + " status: " + status);

            switch(characteristic.getUuid().toString()) {
                case TrackerServices.STR_UUID_CHAR_STATION_ROT_ROW_3:
                    PositionTracker tracker = getPositionTracker(gatt.getDevice());
                    if(tracker == null)
                        return;

                    Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        if(++stationGeometryIndexW == 2) { // the geometry was successfully set/write to tracker

                            // is need to subscribe characteristic for receiving coordinates
                            subscribeNotifications(
                                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_COORDINATES_UUID),
                                    gatt
                            );

                            tracker.setState(PositionTracker.State.GEOMETRY_IS_SET);
                            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                            broadcastManager.sendBroadcast(intent);
                        }
                    } else {
                        if(tracker.getState().equals(PositionTracker.State.SENDING_GEOMETRY)) {
                            tracker.setState(PositionTracker.State.GEOMETRY_NOT_SET);
                            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                            broadcastManager.sendBroadcast(intent);
                        } else {
                            System.err.println("Geometry was not successfully read!");
                            characteristicQueue.clear();
                            readingGeometry = false;
                            disconnectFromDevice(gatt.getDevice());
                        }
                    }
                    break;
            }
            fetchNextOperation(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if(!readingGeometry) {
                    return;
                }
                BaseStation actualReadingGeometry = stationsGeometryFromTracker.get(stationGeometryIndexR);
                actualReadingGeometry.setNumber((byte) stationGeometryIndexR);
                BaseStation.Matrix3 mat = actualReadingGeometry.getRotationMatrix();

                switch(characteristic.getUuid().toString()) {
                    case TrackerServices.STR_UUID_CHAR_STATION_ORIGIN:
                        System.out.println("Read origin: " + new BaseStation.Vector3D(characteristic.getValue()) + " at R index: " + stationGeometryIndexR);
                        actualReadingGeometry.setOrigin(new BaseStation.Vector3D(characteristic.getValue()));
                        break;
                    case TrackerServices.STR_UUID_CHAR_STATION_ROT_ROW_1:
                        mat.setRow(0, new BaseStation.Vector3D(characteristic.getValue()));
                        actualReadingGeometry.setRotationMatrix(mat);
                        break;
                    case TrackerServices.STR_UUID_CHAR_STATION_ROT_ROW_2:
                        mat.setRow(1, new BaseStation.Vector3D(characteristic.getValue()));
                        actualReadingGeometry.setRotationMatrix(mat);
                        break;
                    case TrackerServices.STR_UUID_CHAR_STATION_ROT_ROW_3:
                        mat.setRow(2, new BaseStation.Vector3D(characteristic.getValue()));
                        actualReadingGeometry.setRotationMatrix(mat);

                        if(++stationGeometryIndexR == 2) {
                            // end
                            readingGeometry = false;
                            PositionTracker tracker = getPositionTracker(gatt.getDevice());
                            if(tracker == null) {
                                return;
                            }

                            System.out.println("Received geometry from Tracker");
                            for(BaseStation bs : stationsGeometryFromTracker)
                                System.out.println(bs);
                            if(Geometry.isValid(stationsGeometryFromTracker)) {
                                // Android 9 and 10 returns characteristic value wrongly sometimes
                                // (origins of the base stations are equal), but in the 3D tracker are
                                // set correctly, so in this case try to read geometry again
                                if(stationsGeometryFromTracker.get(0).getOrigin()
                                        .equals(stationsGeometryFromTracker.get(1).getOrigin())) {
                                    readGeometryFromTracker(gatt);

                                    return;
                                }

                                // Everything is valid, set new geometry
                                Geometry.setGeometry(stationsGeometryFromTracker);
                                tracker.setState(PositionTracker.State.GEOMETRY_IS_SET);
                                subscribeNotifications(
                                        TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_COORDINATES_UUID),
                                        gatt
                                );
                            } else {
                                tracker.setState(PositionTracker.State.GEOMETRY_NOT_SET);
                            }

                            // broadcast a new state of device
                            Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                            broadcastManager.sendBroadcast(intent);
                        }
                        break;
                }
            } else {
                System.err.println("Geometry was not successfully read!");
                readingGeometry = false;
                characteristicQueue.clear();
                disconnectFromDevice(gatt.getDevice());
            }
            fetchNextOperation(gatt);
        }
    }

    private void readGeometryFromTracker(BluetoothGatt gatt) {
        // TODO: only one geometry can be read at time -> after quick connection to next tracker
        // TODO: could cause that the geometry for new connection will not be read
        if(readingGeometry) {
            return;
        }
        readingGeometry = true;
        stationGeometryIndexR = 0;
        stationsGeometryFromTracker = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            stationsGeometryFromTracker.add(new BaseStation());
        }

        for(byte i = 0; i < 2; i++) {
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_INDEX_UUID),
                    new byte[]{i}
            ));
            characteristicQueue.add(new ReadOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ORIGIN_UUID)
            ));
            characteristicQueue.add(new ReadOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_1_UUID)
            ));
            characteristicQueue.add(new ReadOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_2_UUID)
            ));
            characteristicQueue.add(new ReadOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_3_UUID)
            ));
        }
        fetchNextOperation(gatt);

        PositionTracker tracker = getPositionTracker(gatt.getDevice());
        if(tracker != null) {
            tracker.setState(PositionTracker.State.READING_GEOMETRY);
            Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private boolean writeGeometryToTracker(BluetoothGatt gatt, List<BaseStation> geometry) {
        if(geometry.size() != 2) {
            return false;
        }
        stationGeometryIndexW = 0;

        for(byte i = 0; i < 2; i++) {
            BaseStation bs = geometry.get(i);
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_INDEX_UUID),
                    new byte[]{i}
            ));
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ORIGIN_UUID),
                    ByteUtils.floatsToBytesLE(bs.getOrigin().toArray())
            ));
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_1_UUID),
                    ByteUtils.floatsToBytesLE(bs.getRotationMatrix().getRow(0).toArray())
            ));
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_2_UUID),
                    ByteUtils.floatsToBytesLE(bs.getRotationMatrix().getRow(1).toArray())
            ));
            characteristicQueue.add(new WriteOperation(
                    TrackerServices.getCharacteristic(gatt, TrackerServices.CHARACTERISTIC_BASE_STATION_ROT_ROW_3_UUID),
                    ByteUtils.floatsToBytesLE(bs.getRotationMatrix().getRow(2).toArray())
            ));
        }
        fetchNextOperation(gatt);

        return true;
    }

    private void fetchNextOperation(BluetoothGatt gatt) {
        if(characteristicQueue.isEmpty()) {
            return;
        }

        CharacteristicOperation nextChar = characteristicQueue.remove();
        nextChar.execute(gatt);
    }

    public void sendGeometryToTracker(BluetoothDevice device) {
        if(PreferenceManager.isUserAdmin()) {
            BluetoothGatt gatt = getGattFromDevice(device);
            if(gatt == null)
                return;

            boolean success = writeGeometryToTracker(gatt, Geometry.getGeometry());

            PositionTracker tracker = getPositionTracker(device);
            if(tracker == null)
                return;

            if(success) {
                tracker.setState(PositionTracker.State.SENDING_GEOMETRY);
            } else {
                tracker.setState(PositionTracker.State.GEOMETRY_NOT_SET);
            }
            Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
            intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
            broadcastManager.sendBroadcast(intent);
        }
    }

    public void startScanning() {
        if(scanning) {
            stopScanning();
        }
        // remove old devices records if the device is not connected
        Iterables.removeIf(devices, (e) -> {
            if(!e.isConnected()) {
                colorAllocator.free(e);

                return true;
            }

            return false;
        });

        getLeScanner().startScan(scanFilters, scanSettings, scanCallback);
        handler.postDelayed(this::stopScanning, SCAN_DURATION);
        scanning = true;

        // broadcast start scanning state
        Intent intent = new Intent(ACTION_SCANNING_STATE);
        intent.putExtra(KEY_SCANNING_STATE_BOOLEAN, true);
        broadcastManager.sendBroadcast(intent);
    }

    public void stopScanning() {
        if(getLeScanner() == null) {
            return;
        }

        handler.removeCallbacksAndMessages(null);
        scanning = false;
        getLeScanner().stopScan(scanCallback);

        // broadcast stop scanning state
        Intent intent = new Intent(ACTION_SCANNING_STATE);
        intent.putExtra(KEY_SCANNING_STATE_BOOLEAN, false);
        broadcastManager.sendBroadcast(intent);
    }

    public boolean connectToDevice(BluetoothDevice device, Context context) {
        if(device == null)
            return false;

        BluetoothGatt gatt = device.connectGatt(context, false, gattCallback);

        if(gatt != null) {
            if(!gatt.connect()) {
                return false;
            }

            PositionTracker tracker = getPositionTracker(device);
            if(tracker != null) {
                tracker.setState(PositionTracker.State.CONNECTING);
                Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);
            }

            return true;
        }

        return false;
    }

    public void disconnectFromDevice(BluetoothDevice device) {
        for(BluetoothGatt gatt : activeConnections) {
            if(gatt.getDevice().equals(device)) {
                gatt.disconnect();

                PositionTracker tracker = getPositionTracker(device);
                tracker.setState(PositionTracker.State.DISCONNECTING);
                Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
                intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
                broadcastManager.sendBroadcast(intent);

                return;
            }
        }
    }

    /**
     * In order that application could subscribe characteristic notifications
     * from BLE peripheral (3D tracker) the specific value in characteristic descriptor
     * has to be written. If this operation was successful then application
     * can receive coordinates from peripheral.
     */
    private void subscribeNotifications(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        gatt.setCharacteristicNotification(characteristic, true);
        UUID uuid = UUID.fromString(SUBSCRIBE_NOTIFICATION_DESC_UUID);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean success = gatt.writeDescriptor(descriptor);

        PositionTracker tracker = getPositionTracker(gatt.getDevice());
        if(success) {
            tracker.setState(PositionTracker.State.SUBSCRIBING_NOTIFICATION);
        } else {
            tracker.setState(PositionTracker.State.NOT_RECEIVING_COORDINATES);
        }

        Intent intent = new Intent(ACTION_DEVICE_STATE_CHANGED);
        intent.putExtra(KEY_DEVICE_POSITION_TRACKER, tracker);
        broadcastManager.sendBroadcast(intent);
    }

    private PositionTracker getPositionTracker(BluetoothDevice device) {
        for(PositionTracker tracker : devices) {
            if(tracker.getDevice().equals(device)) {
                return tracker;
            }
        }

        return null;
    }

    private BluetoothGatt getGattFromDevice(BluetoothDevice device) {
        for(BluetoothGatt gatt : activeConnections) {
            if(gatt.getDevice().equals(device)) {
                return gatt;
            }
        }

        return null;
    }

    private void close() {
        if(activeConnections == null || activeConnections.isEmpty()) {
            return;
        }

        StreamSupport.stream(activeConnections).forEach(e -> e.close());
        activeConnections.clear();
        devices.clear();
    }

    public List<PositionTracker> getDevices() {
        return new ArrayList<>(devices);
    }

}
