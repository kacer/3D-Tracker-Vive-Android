package cz.marw.threed_tracker_vive.connectivity;

import android.os.ParcelUuid;

public class TrackerServices {

    /**
     * Service that contains one characteristic {@link #CHARACTERISTIC_COORDINATES_UUID} for providing
     * coordinates in 3D space.
     */
    public static final ParcelUuid SERVICE_COORDINATES_UUID = ParcelUuid.fromString("3d000000-a000-cafe-0404-00451f003d3d");

    /**
     * Characteristic provides coordinates of 3D tracker in space relative to calibrated origin. Coordinates are in
     * right-handed oriented system where y is up. This system uses meter units (Vive coordinates system).
     * <br/>
     * <br/>
     * Characteristic properties  - Read, Notify
     */
    public static final ParcelUuid CHARACTERISTIC_COORDINATES_UUID = ParcelUuid.fromString("3d000000-a001-cafe-0404-00451f003d3d");

}
