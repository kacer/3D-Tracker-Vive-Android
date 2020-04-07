package cz.marw.threed_tracker_vive.geometry.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cz.marw.threed_tracker_vive.model.BaseStation;

public interface GeometryParser {

    List<BaseStation> parse(InputStream src) throws IOException, InvalidDataException;

}
