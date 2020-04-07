package cz.marw.threed_tracker_vive.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cz.marw.threed_tracker_vive.rendering.transforms.Mat3;
import cz.marw.threed_tracker_vive.rendering.transforms.Vec3D;
import cz.marw.threed_tracker_vive.util.ByteUtils;

public class BaseStation {

    private byte number;

    private Vector3D origin;

    private Matrix3 rotationMatrix;


    public BaseStation(byte number, Vector3D origin, Matrix3 rotationMatrix) {
        this.number = number;
        this.origin = origin;
        this.rotationMatrix = rotationMatrix;
    }

    public BaseStation() {
        number = (byte) 0;
        origin = new Vector3D();
        rotationMatrix = new Matrix3();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (this == obj) || (obj instanceof BaseStation)
                && ((BaseStation) obj).number == number
                && ((BaseStation) obj).origin.equals(origin)
                && ((BaseStation) obj).rotationMatrix.equals(rotationMatrix);
    }

    @NonNull
    @Override
    public String toString() {
        String str = "";
        str += "[b" + number + " ";
        str += "origin " + origin + " ";
        str += "matrix " + rotationMatrix + "]";

        return str;
    }

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public Vector3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3D origin) {
        this.origin = origin;
    }

    public Matrix3 getRotationMatrix() {
        return rotationMatrix;
    }

    public void setRotationMatrix(Matrix3 rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    public static class Vector3D {

        private float x, y, z;

        public Vector3D(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3D(float[] xyz) {
            x = xyz[0];
            y = xyz[1];
            z = xyz[2];
        }

        /**
         *
         * @param xyz float bytes
         */
        public Vector3D(byte[] xyz) {
            this(ByteUtils.bytesToFloatsLE(xyz));
        }

        public Vector3D() {
            x = y = z = 0;
        }

        public float[] toArray() {
            return new float[]{x, y, z};
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(this == obj) {
                return true;
            }

            if(obj instanceof Vector3D) {
                Vector3D vec = (Vector3D) obj;

                return x == vec.x && y == vec.y && z == vec.z;
            }

            return false;
        }

        @NonNull
        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }

        public Vec3D getAsVec3D() {
            return new Vec3D(x, y, z);
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }
    }

    public static class Matrix3 {

        private float[][] mat = new float[3][3];

        public Matrix3(Vector3D row1, Vector3D row2, Vector3D row3) {
            mat[0][0] = row1.getX();
            mat[0][1] = row1.getY();
            mat[0][2] = row1.getZ();
            mat[1][0] = row2.getX();
            mat[1][1] = row2.getY();
            mat[1][2] = row2.getZ();
            mat[2][0] = row3.getX();
            mat[2][1] = row3.getY();
            mat[2][2] = row3.getZ();
        }

        public Matrix3() {
            for(int row = 0; row < 3; row++) {
                for(int col = 0; col < 3; col++) {
                    mat[row][col] = 0;
                }
            }
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return (this == obj) || (obj instanceof Matrix3)
                    && ((Matrix3) obj).getRow(0).equals(getRow(0))
                    && ((Matrix3) obj).getRow(1).equals(getRow(1))
                    && ((Matrix3) obj).getRow(2).equals(getRow(2));
        }

        @NonNull
        @Override
        public String toString() {
            return getRow(0) + " " + getRow(1) + " " + getRow(2);
        }

        public Vector3D getRow(int row) {
            return new Vector3D(mat[row][0], mat[row][1], mat[row][2]);
        }

        public void setRow(int row, Vector3D rowValues) {
            mat[row][0] = rowValues.getX();
            mat[row][1] = rowValues.getY();
            mat[row][2] = rowValues.getZ();
        }

        public Mat3 getAsMat3() {
            return new Mat3(getRow(0).getAsVec3D(), getRow(1).getAsVec3D(), getRow(2).getAsVec3D());
        }

    }

}
