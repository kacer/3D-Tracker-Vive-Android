package cz.marw.threed_tracker_vive;

import android.app.Application;

import cz.marw.threed_tracker_vive.geometry.Geometry;
import cz.marw.threed_tracker_vive.util.PreferenceManager;

public class TrackerViveApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager.init(androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));
        Geometry.init(this);
    }
}
