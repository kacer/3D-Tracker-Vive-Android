package cz.marw.threed_tracker_vive.geometry;

import android.content.Context;

import java.util.List;

import cz.marw.threed_tracker_vive.geometry.io.GeometryDAO;
import cz.marw.threed_tracker_vive.model.BaseStation;
import java9.util.Comparators;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

public class Geometry {

    private static List<BaseStation> stationGeometries;

    public static void init(Context context) {
        GeometryDAO dao = new GeometryDAO(context);
        List<BaseStation> geometry = dao.load();
        stationGeometries = StreamSupport.stream(geometry)
                .sorted(Comparators.comparingInt(BaseStation::getNumber))
                .collect(Collectors.toList());
    }

    public static synchronized boolean isGeometrySet() {
        return isValid(stationGeometries);
    }

    public static boolean isValid(List<BaseStation> geometries) {
        if(geometries.isEmpty() || geometries.size() != 2) {
            return false;
        }

        BaseStation emptyStation = new BaseStation();
        for(BaseStation bs : geometries) {
            if(bs.equals(emptyStation)) {
                return false;
            }
        }

        return true;
    }

    public static synchronized List<BaseStation> getGeometry() {
        return stationGeometries;
    }

    public static synchronized void setGeometry(List<BaseStation> stations) {
        stationGeometries = stations;
    }

}
