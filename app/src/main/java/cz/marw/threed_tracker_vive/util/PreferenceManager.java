package cz.marw.threed_tracker_vive.util;

import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String KEY_IS_USER_ADMIN = "is_user_admin";
    private static final String KEY_DARK_BACKGROUND_3D_SCENE = "dark_background_3d_scene";

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

    public static boolean isDarkBackground3DScene() {
        return sharedPreferences.getBoolean(KEY_DARK_BACKGROUND_3D_SCENE, true);
    }

    public static void setDarkBackground3DScene(boolean dark) {
        sharedPreferences.edit().putBoolean(KEY_DARK_BACKGROUND_3D_SCENE, dark).apply();
    }

}
