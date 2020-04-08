package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Locale;

/**
 * The same virtual camera as {@link Camera} but with Y-axis up. The comments in this class
 * are copied from class {@link Camera} and thus they are not 100 % correct. Keep this in
 * mind.
 *
 * @author Martin Donát
 * @version 2020
 */
public class CameraYUp {

    private final double azimuth, radius, zenith;
    private final boolean firstPerson; // true -> 1st person, false -> 3rd person
    private final Vec3D eye, pos, viewVector;
    private final Mat4 view;

    /**
     * Creates 1st person camera with observer positioned in the origin and
     * looking along the x-axis in positive direction (zero azimuth and zenith)
     */
    public CameraYUp() {
        this(new Vec3D(0.0, 0.0, 0.0), 0.0, 0.0, 1.0, true);
    }

    /**
     * Creates a camera as a copy of another but with the given 1st/3rd person
     * mode flag
     *
     * @param cam
     *            camera to be copied
     * @param firstPerson
     *            boolean flag indicating 1st (true) / 3rd (false) person camera
     *            mode
     */
    public CameraYUp(final CameraYUp cam, final boolean firstPerson) {
        this(cam.getPosition(), cam.getAzimuth(), cam.getZenith(), cam.getRadius(), firstPerson);
    }

    /**
     * Creates a camera as a copy of another but with the given radius
     *
     * @param cam
     *            camera to be copied
     * @param radius
     *            distance between the eye (camera origin) and the observer
     *            position in the 3rd person camera mode
     */
    public CameraYUp(final CameraYUp cam, final double radius) {
        this(cam.getPosition(), cam.getAzimuth(), cam.getZenith(), radius, cam.getFirstPerson());
    }

    /**
     * Creates a camera as a copy of another but with the given azimuth and
     * zenith
     *
     * @param cam
     *            camera to be copied
     * @param azimuth
     *            angle (in radians) between the xz and uv planes where v is the
     *            view vector and u is the up vector, i.e. the vector considered
     *            vertical by observer, i.e. the vector of the y-axis of the
     *            camera coordinate set
     * @param zenith
     *            angle (in radians) between the view vector and z-axis
     */
    public CameraYUp(final CameraYUp cam, final double azimuth, final double zenith) {
        this(cam.getPosition(), azimuth, zenith, cam.getRadius(), cam.getFirstPerson());
    }

    /**
     * Create a camera as a copy of another but with the given observer
     * position
     *
     * @param cam
     *            camera to be copied
     * @param pos
     *            observer position
     */
    public CameraYUp(final CameraYUp cam, final Vec3D pos) {
        this(pos, cam.getAzimuth(), cam.getZenith(), cam.getRadius(), cam.getFirstPerson());
    }

    /**
     * Creates a camera with the given parameters
     *
     * @param pos
     *            observer position
     * @param azimuth
     *            angle (in radians) between the xz and uv planes where v is the
     *            view vector and u is the up vector, i.e. the vector considered
     *            vertical by observer, i.e. the vector of the y-axis of the
     *            camera coordinate set
     * @param zenith
     *            angle (in radians) between the view vector and z-axis
     * @param radius
     *            distance between the eye (camera origin) and the observer
     *            position in the 3rd person camera mode
     * @param firstPerson
     *            boolean flag indicating 1st (true) / 3rd (false) person camera
     *            mode
     */
    public CameraYUp(final Vec3D pos, final double azimuth, final double zenith,
                  final double radius, final boolean firstPerson) {
        this.pos = pos;
        this.azimuth = azimuth;
        this.zenith = zenith;
        this.radius = radius;
        this.firstPerson = firstPerson;
        viewVector = new Vec3D(
                Math.sin(azimuth) * Math.cos(zenith),
                Math.sin(zenith),
                Math.cos(azimuth) * Math.cos(zenith));
        final Vec3D upVector = new Vec3D(
                Math.sin(azimuth) * Math.cos(zenith + Math.PI / 2),
                Math.sin(zenith + Math.PI / 2),
                Math.cos(azimuth) * Math.cos(zenith + Math.PI / 2)
        );
        if (firstPerson) {
            eye = new Vec3D(pos);
            view = new Mat4ViewRH(pos, viewVector.mul(radius), upVector);
        } else {
            eye = pos.add(viewVector.mul(-radius));
            view = new Mat4ViewRH(eye, viewVector.mul(radius), upVector);
        }
    }

    /**
     * Returns a new camera with azimuth summed with the given value
     *
     * @param ang
     *            azimuth change in radians
     * @return new Camera instance
     */
    public CameraYUp addAzimuth(final double ang) {
        return new CameraYUp(this, azimuth + ang, zenith);
    }

    /**
     * Returns a new camera with radius summed with the given value. Radius is
     * kept >= 0.1
     *
     * @param dist
     *            radius change amount
     * @return new Camera instance
     */
    public CameraYUp addRadius(final double dist) {
        return new CameraYUp(this, Math.max(radius + dist, 0.1));
    }

    /**
     * Returns a new camera with zenith summed with the given value. Zenith is
     * kept in [-pi/2, pi/2]
     *
     * @param ang
     *            zenith change in radians
     * @return new Camera instance
     */
    public CameraYUp addZenith(final double ang) {
        return new CameraYUp(this, azimuth, Math.max(-Math.PI / 2, Math.min(zenith + ang, Math.PI / 2)));
    }

    /**
     * Returns a new camera moved in the opposite direction of the view vector
     * by the given distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp backward(final double speed) {
        return forward(-speed);
    }

    /**
     * Returns a new camera moved in the negative direction of z-axis by the
     * given distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp down(final double speed) {
        return up(-speed);
    }

    /**
     * Returns a new camera moved in the direction of the view vector by the
     * given distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp forward(final double speed) {
        return new CameraYUp(this, pos.add(viewVector.mul(speed)));
    }

    /**
     * Returns azimuth in radians
     *
     * @return azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Returns the eye (camera) position, depends on the value of the 1st/3rd
     * person camera mode flag
     *
     * @return eye position
     */
    public Vec3D getEye() {
        return eye;
    }

    /**
     * Returns the value of 1st/3rd person camera mode flag
     *
     * @return true -> 1st person mode, false -> 3rd person mode
     */
    public boolean getFirstPerson() {
        return firstPerson;
    }

    /**
     * Returns the observer position, can be different from the eye (camera)
     * position (depends on 1st/3rd person camera mode)
     *
     * @return eye observer position
     */
    public Vec3D getPosition() {
        return pos;
    }

    /**
     * Returns radius (the distance between the eye (camera) and the observer in
     * 3rd person camera mode)
     *
     * @return radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns the view matrix that transforms coordinates from the world
     * coordinate set to the camera coordinate set
     *
     * @return view matrix
     */
    public Mat4 getViewMatrix() {
        return view;
    }

    /**
     * Returns the view direction as specified by azimuth and zenith
     *
     * @return view vector
     */
    public Vec3D getViewVector() {
        return viewVector;
    }

    /**
     * Returns zenith in radians
     *
     * @return zenith
     */
    public double getZenith() {
        return zenith;
    }

    /**
     * Returns a new camera moved in the opposite direction of a cross product
     * of the view vector and the up vector, i.e. to the left from the
     * observer's perspective, by the given distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp left(final double speed) {
        return right(-speed);
    }

    /**
     * Returns a new camera moved by the given vector
     *
     * @param dir
     *            vector to move by
     * @return new Camera instance
     */
    public CameraYUp move(final Vec3D dir) {
        return new CameraYUp(this, pos.add(dir));
    }

    /**
     * Returns a new camera with radius multiplied by the given coefficient.
     * Radius is kept >= 0.1
     *
     * @param scale
     *            radius scale coefficient
     * @return new Camera instance
     */
    public CameraYUp mulRadius(final double scale) {
        return new CameraYUp(this, Math.max(radius * scale, 0.1));
    }

    /**
     * Returns a new camera moved in the direction of a cross product of the
     * view vector and the up vector, i.e. to the right from the observer's
     * perspective, by the given distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp right(final double speed) {
        return new CameraYUp(this, pos.add(new Vec3D((double) Math.cos(azimuth
                - Math.PI / 2), (double) Math.sin(azimuth - Math.PI / 2), 0.0)
                .mul(speed)));
    }

    /**
     * Returns a new camera moved in the direction of z-axis by the given
     * distance
     *
     * @param speed
     *            distance to move by
     * @return new Camera instance
     */
    public CameraYUp up(final double speed) {
        return new CameraYUp(this, pos.add(new Vec3D(0, 0, speed)));
    }

    /**
     * Returns a new camera with azimuth set to the given value
     *
     * @param ang
     *            new azimuth value
     * @return new Camera instance
     */
    public CameraYUp withAzimuth(final double ang) {
        return new CameraYUp(this, ang, zenith);
    }

    /**
     * Returns a new camera with 1st/3rd person camera mode flag set to the
     * given value
     *
     * @param firstPerson
     *            boolean flag indicating 1st (true) / 3rd (false) person camera
     *            mode
     * @return new Camera instance
     */
    public CameraYUp withFirstPerson(final boolean firstPerson) {
        return new CameraYUp(this, firstPerson);
    }

    /**
     * Returns a new camera with position set to the given vector
     *
     * @param pos
     *            new position
     * @return new Camera instance
     */
    public CameraYUp withPosition(final Vec3D pos) {
        return new CameraYUp(this, pos);
    }

    /**
     * Returns a new camera with radius (the distance between the eye (camera)
     * and the observer in 3rd person camera mode) set to the given value
     *
     * @param radius
     *            new radius value
     * @return new Camera instance
     */
    public CameraYUp withRadius(final double radius) {
        return new CameraYUp(this, radius);
    }

    /**
     * Returns a new camera with zenith set to the given value
     *
     * @param ang
     *            new zenith value
     * @return new Camera instance
     */
    public CameraYUp withZenith(final double ang) {
        return new CameraYUp(this, azimuth, ang);
    }

    /**
     * Returns String representation of this camera with floating point values formatted with the given format
     *
     * @return String
     */
    public String toString(final String format) {
        return String.format(Locale.US,
                "CameraYUp()\n" +
                        "	.withFirstPerson("+ getFirstPerson() + ")\n" +
                        "	.withPosition(new Vec3D"+ getPosition().toString(format) + ")\n" +
                        "	.withAzimuth("+ format + ")\n" +
                        "	.withZenith("+ format + ")\n" +
                        "	.withRadius("+ format + ")",
                getAzimuth(), getZenith(), getRadius()
        );


    }
    /**
     * Returns String representation of this camera
     *
     * @return String
     */
    @Override
    public String toString() {
        return toString("%4.2f");
    }
}
