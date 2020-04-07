package cz.marw.threed_tracker_vive.connectivity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.ParcelUuid;

public class TrackerServices {

    public static final String STR_UUID_SERVICE_COORDINATES = "3d000000-a000-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_COORDINATES = "3d000000-a001-cafe-0404-00451f003d3d";

    public static final String STR_UUID_SERVICE_STATION_GEOM = "3d000000-b000-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_STATION_INDEX = "3d000000-b001-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_STATION_ORIGIN = "3d000000-b002-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_STATION_ROT_ROW_1 = "3d000000-b003-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_STATION_ROT_ROW_2 = "3d000000-b004-cafe-0404-00451f003d3d";
    public static final String STR_UUID_CHAR_STATION_ROT_ROW_3 = "3d000000-b005-cafe-0404-00451f003d3d";


    /**
     * Service that contains one characteristic {@link #CHARACTERISTIC_COORDINATES_UUID} for providing
     * coordinates in 3D space.
     */
    public static final ParcelUuid SERVICE_COORDINATES_UUID = ParcelUuid.fromString(STR_UUID_SERVICE_COORDINATES);

    /**
     * Characteristic provides coordinates of 3D tracker in space relative to calibrated origin. Coordinates are in
     * right-handed oriented system where y is up. This system uses meter units (Vive coordinates system).
     * <br/>
     * <br/>
     * Characteristic properties  - Read, Notify
     */
    public static final ParcelUuid CHARACTERISTIC_COORDINATES_UUID = ParcelUuid.fromString(STR_UUID_CHAR_COORDINATES);

    public static final ParcelUuid SERVICE_BASE_STATION_GEOMETRY_UUID = ParcelUuid.fromString(STR_UUID_SERVICE_STATION_GEOM);
    public static final ParcelUuid CHARACTERISTIC_BASE_STATION_INDEX_UUID = ParcelUuid.fromString(STR_UUID_CHAR_STATION_INDEX);
    public static final ParcelUuid CHARACTERISTIC_BASE_STATION_ORIGIN_UUID = ParcelUuid.fromString(STR_UUID_CHAR_STATION_ORIGIN);
    public static final ParcelUuid CHARACTERISTIC_BASE_STATION_ROT_ROW_1_UUID = ParcelUuid.fromString(STR_UUID_CHAR_STATION_ROT_ROW_1);
    public static final ParcelUuid CHARACTERISTIC_BASE_STATION_ROT_ROW_2_UUID = ParcelUuid.fromString(STR_UUID_CHAR_STATION_ROT_ROW_2);
    public static final ParcelUuid CHARACTERISTIC_BASE_STATION_ROT_ROW_3_UUID = ParcelUuid.fromString(STR_UUID_CHAR_STATION_ROT_ROW_3);


    public static BluetoothGattCharacteristic getCharacteristic(BluetoothGatt gattDevice, ParcelUuid uuid) {
        switch(String.valueOf(uuid.toString().charAt(9))) {
            case "a":
                return gattDevice.getService(SERVICE_COORDINATES_UUID.getUuid()).getCharacteristic(uuid.getUuid());
            case "b":
                return gattDevice.getService(SERVICE_BASE_STATION_GEOMETRY_UUID.getUuid()).getCharacteristic(uuid.getUuid());
        }

        return null;
    }

}
