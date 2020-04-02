package cz.marw.threed_tracker_vive.rendering.glutils;

import java.util.List;

import cz.marw.threed_tracker_vive.rendering.transforms.Col;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat3;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat4;
import cz.marw.threed_tracker_vive.rendering.transforms.Point3D;
import cz.marw.threed_tracker_vive.rendering.transforms.Quat;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec1D;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec2D;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec3D;

public class ToFloatArray {

    public static float[] convert(Vec1D vec) {
        float[] result = new float[1];
        result[0] = (float) vec.getX();
        return result;
    }

    public static float[] convert(Vec2D vec) {
        float[] result = new float[2];
        result[0] = (float) vec.getX();
        result[1] = (float) vec.getY();
        return result;
    }

    public static float[] convert(Vec3D vec) {
        float[] result = new float[3];
        result[0] = (float) vec.getX();
        result[1] = (float) vec.getY();
        result[2] = (float) vec.getZ();
        return result;
    }

    public static float[] convert(Point3D vec) {
        float[] result = new float[4];
        result[0] = (float) vec.getX();
        result[1] = (float) vec.getY();
        result[2] = (float) vec.getZ();
        result[3] = (float) vec.getW();
        return result;
    }

    public static float[] convert(Col vec) {
        float[] result = new float[4];
        result[0] = (float) vec.getR();
        result[1] = (float) vec.getG();
        result[2] = (float) vec.getB();
        result[3] = (float) vec.getA();
        return result;
    }

    public static float[] convert(Quat vec) {
        float[] result = new float[4];
        result[0] = (float) vec.getR();
        result[1] = (float) vec.getI();
        result[2] = (float) vec.getJ();
        result[3] = (float) vec.getK();
        return result;
    }

    public static float[] convert(Mat3 mat) {
        return mat.floatArray();
    }

    public static float[] convert(Mat4 mat) {
        return mat.floatArray();
    }

    public static float[] convert(Object vec) {
        if (vec instanceof Vec1D) return convert((Vec1D) vec);
        if (vec instanceof Vec2D) return convert((Vec2D) vec);
        if (vec instanceof Vec3D) return convert((Vec3D) vec);
        if (vec instanceof Point3D) return convert((Point3D) vec);
        if (vec instanceof Col) return convert((Col) vec);
        if (vec instanceof Quat) return convert((Quat) vec);
        if (vec instanceof Mat3) return convert((Mat3) vec);
        if (vec instanceof Mat4) return convert((Mat4) vec);
        if (vec instanceof float[]) return (float[]) vec;
        return null;
    }

    public static <Element> float[] convert(List<Element> list) {
        if (list.isEmpty())
            return null;
        int size = 0;
        for (Element element : list)
            size += floatSize(element);
        float[] result = new float[size];
        int index = 0;
        for (Element element : list) {
            float[] elementArray = convert(element);
            for (int i = 0; i < elementArray.length; i++)
                result[index++] = elementArray[i];
        }
        return result;
    }

    protected static int floatSize(Vec1D vec) { return 1; }
    protected static int floatSize(Vec2D vec) { return 2; }
    protected static int floatSize(Vec3D vec) { return 3; }
    protected static int floatSize(Point3D vec) { return 4; }
    protected static int floatSize(Col vec) { return 4; }
    protected static int floatSize(Quat vec) { return 4; }
    protected static int floatSize(Mat3 vec) { return 9; }
    protected static int floatSize(Mat4 vec) { return 16; }

    protected static int floatSize(Object vec) {
        if (vec instanceof Vec1D) return floatSize((Vec1D )vec);
        if (vec instanceof Vec2D) return floatSize((Vec2D) vec);
        if (vec instanceof Vec3D) return floatSize((Vec3D) vec);
        if (vec instanceof Point3D) return floatSize((Point3D) vec);
        if (vec instanceof Col) return floatSize((Col) vec);
        if (vec instanceof Quat) return floatSize((Quat) vec);
        if (vec instanceof Mat3) return floatSize((Mat3) vec);
        if (vec instanceof Mat4) return floatSize((Mat4) vec);
        if (vec instanceof float[]) return ((float[]) vec).length;
        return 0;
    }

}
