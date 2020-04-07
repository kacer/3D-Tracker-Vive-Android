package cz.marw.threed_tracker_vive.util;

import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String KEY_IS_USER_ADMIN = "is_user_admin";

    private static SharedPreferences sharedPreferences;

    private PreferenceManager() {}

    public static void init(SharedPreferences sp) {
        sharedPreferences = sp;
    }

    public static boolean isUserAdmin() {
        return sharedPreferences.getBoolean(KEY_IS_USER_ADMIN, false);
    }

    public static void setUserAdmin(boolean isUserAdmin) {
        sharedPreferences.edit().putBoolean(KEY_IS_USER_ADMIN, isUserAdmin).apply();
    }

}
