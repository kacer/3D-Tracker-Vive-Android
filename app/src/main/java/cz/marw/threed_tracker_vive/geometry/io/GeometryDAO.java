package cz.marw.threed_tracker_vive.geometry.io;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.marw.threed_tracker_vive.model.BaseStation;

public class GeometryDAO {

    private static final String GEOMETRY_FILE_NAME = "geometry.json";

    private Context context;

    public GeometryDAO(Context context) {
        this.context = context;
    }

    public boolean save(List<BaseStation> stations) {
        try {
            FileWriter fw = new FileWriter(new File(context.getFilesDir(), GEOMETRY_FILE_NAME));
            Gson gson = new Gson();
            gson.toJson(stations, fw);
            fw.flush();
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<BaseStation> load() {
        List<BaseStation> stations = new ArrayList<>();
        try {
            FileReader fr = new FileReader(new File(context.getFilesDir(), GEOMETRY_FILE_NAME));
            Gson gson = new Gson();
            Type type = TypeToken.getParameterized(List.class, BaseStation.class).getType();
            stations = gson.fromJson(fr, type);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stations;
    }

}
