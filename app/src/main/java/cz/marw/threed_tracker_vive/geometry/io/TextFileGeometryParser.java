package cz.marw.threed_tracker_vive.geometry.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.marw.threed_tracker_vive.model.BaseStation;

public class TextFileGeometryParser implements GeometryParser {

    @Override
    public List<BaseStation> parse(InputStream src) throws IOException, InvalidDataException {
        List<BaseStation> stations = new ArrayList<>(2);

        BufferedReader bf = new BufferedReader(new InputStreamReader(src));
        String line;
        while((line = bf.readLine()) != null) {
            String[] splits = line.split(" ");
            if(splits.length != 15) {
                throw new InvalidDataException();
            }

            try {
                if(stations.size() == 2) return stations;

                byte number = Byte.parseByte(splits[0].substring(1));

                BaseStation.Vector3D position = new BaseStation.Vector3D(
                        Float.parseFloat(splits[2]),
                        Float.parseFloat(splits[3]),
                        Float.parseFloat(splits[4])
                );

                BaseStation.Vector3D firstRow = new BaseStation.Vector3D(
                        Float.parseFloat(splits[6]),
                        Float.parseFloat(splits[7]),
                        Float.parseFloat(splits[8])
                );
                BaseStation.Vector3D secondRow = new BaseStation.Vector3D(
                        Float.parseFloat(splits[9]),
                        Float.parseFloat(splits[10]),
                        Float.parseFloat(splits[11])
                );
                BaseStation.Vector3D thirdRow = new BaseStation.Vector3D(
                        Float.parseFloat(splits[12]),
                        Float.parseFloat(splits[13]),
                        Float.parseFloat(splits[14])
                );
                BaseStation.Matrix3 rotation = new BaseStation.Matrix3(
                        firstRow,
                        secondRow,
                        thirdRow
                );

                stations.add(new BaseStation(number, position, rotation));

            } catch(NumberFormatException e) {
                throw new InvalidDataException();
            }
        }

        return stations;
    }
}
