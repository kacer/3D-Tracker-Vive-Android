package cz.marw.threed_tracker_vive.rendering.transforms;

import java.util.Locale;
import java.util.Objects;

/**
 * Four-channel RGB with alpha double-precision color representation, immutable
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */

public class Col {

	private final double r, g, b, a;

	/**
	 * Creates black color with alpha set to 1.0
	 */
	public Col() {
		r = g = b = 0.0;
		a = 1.0;
	}

	/**
	 * Creates new color as a clone of the given one
	 * 
	 * @param c
	 *            color to be copied
	 */
	public Col(final Col c) {
		r = c.r;
		g = c.g;
		b = c.b;
		a = c.a;
	}

	/**
	 * Creates new color by reinterpreting given homogeneous point coordinates
	 * as RGBA channels
	 * 
	 * @param p
	 *            point as an instance of Point3D
	 */
	public Col(final Point3D p) {
		r = p.getX();
		g = p.getY();
		b = p.getZ();
		a = p.getW();
	}

	/**
	 * Creates new color by extracting RGB color channels packed as bytes of a
	 * single int value, B in the least significant byte (LSB), and setting
	 * alpha to 1.0
	 * 
	 * @param rgb
	 *            int-packed RGB, B in the LSB, MSB ignored
	 */
	public Col(final int rgb) {
		a = 1.0f;
		r = ((rgb >> 16) & 0xffL) / 255.0;
		g = ((rgb >> 8) & 0xffL) / 255.0;
		b = (rgb & 0xffL) / 255.0;
	}

	/**
	 * Creates new color by extracting RGB color channels packed as bytes of a
	 * single int value, B in the least significant byte (LSB), alpha in the MSB
	 * or alpha set to 1.0 and MSB ignored, based on isAlpha
	 * 
	 * @param argb
	 *            int-packed ARGB, B in the LSB, alpha in the MSB or MSB
	 *            ignored, based on isAlpha
	 * @param isAlpha
	 *            flag to indicate whether to interpret the MSB as alpha channel
	 *            or set alpha to 1.0 and ignore the MSB
	 */
	public Col(final int argb, final boolean isAlpha) {
		if (isAlpha)
			a = ((argb >> 24) & 0xffL) / 255.0;
		else
			a = 1.0;
		r = ((argb >> 16) & 0xffL) / 255.0;
		g = ((argb >> 8) & 0xffL) / 255.0;
		b = (argb & 0xffL) / 255.0;
	}

	/**
	 * Creates a new color with the given individual int RGB channels with
	 * values in [0,255] and alpha set to 1.0
	 * 
	 * @param r
	 *            red channel as an int in [0,255]
	 * @param g
	 *            green channel as an int in [0,255]
	 * @param b
	 *            blue channel as an int in [0,255]
	 */
	public Col(final int r, final int g, final int b) {
		a = 1.0;
		this.r = r / 255.0;
		this.g = g / 255.0;
		this.b = b / 255.0;
	}

	/**
	 * Creates a new color with the given individual int RGBA channels with
	 * values in [0,255]
	 * 
	 * @param r
	 *            red channel as an int in [0,255]
	 * @param g
	 *            green channel as an int in [0,255]
	 * @param b
	 *            blue channel as an int in [0,255]
	 * @param a
	 *            alpha channel as an int in [0,255]
	 */
	public Col(final int r, final int g, final int b, final int a) {
		this.a = a / 255.0;
		this.r = r / 255.0;
		this.g = g / 255.0;
		this.b = b / 255.0;
	}

	/**
	 * Creates a new color with the given individual double RGB channels with
	 * values in [0.0,1.0] and alpha set to 1.0
	 * 
	 * @param r
	 *            red channel as a double in [0.0,1.0]
	 * @param g
	 *            green channel as a double in [0.0,1.0]
	 * @param b
	 *            blue channel as a double in [0.0,1.0]
	 */
	public Col(final double r, final double g, final double b) {
		a = 1.0;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Creates a new color with the given individual double RGBA channels with
	 * values typically in [0.0,1.0]
	 * 
	 * @param r
	 *            red channel as a double in [0.0,1.0]
	 * @param g
	 *            green channel as a double in [0.0,1.0]
	 * @param b
	 *            blue channel as a double in [0.0,1.0]
	 * @param a
	 *            alpha channel as a double in [0.0,1.0]
	 */
	public Col(final double r, final double g, final double b, final double a) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Returns the red channel
	 * 
	 * @return double-precision floating point typically in [0.0,1.0]
	 */
	public double getR() {
		return r;
	}

	/**
	 * Returns the green channel
	 * 
	 * @return double-precision floating point typically in [0.0,1.0]
	 */
	public double getG() {
		return g;
	}

	/**
	 * Returns the blue channel
	 * 
	 * @return double-precision floating point typically in [0.0,1.0]
	 */
	public double getB() {
		return b;
	}

	/**
	 * Returns the aplha channel
	 * 
	 * @return double-precision floating point typically in [0.0,1.0]
	 */
	public double getA() {
		return a;
	}

	/**
	 * Returns a new color by summing individual RGB channels with the channels
	 * of the given color, no saturation (clamping channels to [0.0,1.0])
	 * performed, new alpha set to 1.0.
	 * 
	 * @param c
	 *            color to sum with
	 * @return new instance of Col
	 */
	public Col addna(final Col c) {
		return new Col(r + c.r, g + c.g, b + c.b);
	}

	/**
	 * Returns a new color by multiplying individual RGB channels by a scalar
	 * (one double-precision value), no saturation (clamping channels to
	 * [0.0,1.0]) performed, new alpha set to 1.0.
	 * 
	 * @param x
	 *            multiplication scalar coefficient (a double-precision floating
	 *            point)
	 * @return new instance of Col
	 */
	public Col mulna(final double x) {
		return new Col(r * x, g * x, b * x);
	}

	/**
	 * Returns a new color by summing individual RGBA channels with the channels
	 * of the given color, no saturation (clamping channels to [0.0,1.0])
	 * performed.
	 * 
	 * @param c
	 *            color to sum with
	 * @return new instance of Col
	 */
	public Col add(final Col c) {
		return new Col(r + c.r, g + c.g, b + c.b, a + c.a);
	}

	/**
	 * Returns a new color by multiplying individual RGBA channels by a scalar
	 * (one double-precision value), no saturation (clamping channels to
	 * [0.0,1.0]) performed.
	 * 
	 * @param x
	 *            multiplication scalar coefficient (a double-precision floating
	 *            point)
	 * @return new instance of Col
	 */
	public Col mul(final double x) {
		return new Col(r * x, g * x, b * x, a * x);
	}

	/**
	 * Returns a new color by multiplying individual RGBA channels by the
	 * channels of the given color, no saturation (clamping channels to
	 * [0.0,1.0]) performed.
	 * 
	 * @param c
	 *            color to per-channel multiply by
	 * @return new instance of Col
	 */
	public Col mul(final Col c) {
		return new Col(r * c.r, g * c.g, b * c.b, a * c.a);
	}

	/**
	 * Returns a new color with gamma-correction applied to RGB channels, alpha
	 * remains the same
	 * 
	 * @param gamma
	 *            power to which the RGB channels are raised
	 * @return new instance of Col
	 */
	public Col gamma(final double gamma) {
		return new Col(Math.pow(r, gamma), Math.pow(g, gamma), Math.pow(b,
				gamma), a);
	}

	/**
	 * Clamps all channels but alpha to [0.0,1.0]
	 * 
	 * @return new instance of Col
	 */
	public Col saturate() {
		return new Col(Math.max(0, Math.min(r, 1)),
				Math.max(0, Math.min(g, 1)), Math.max(0, Math.min(b, 1)), a);
	}

	/**
	 * Returns the RGB channels scaled to [0,255] and packed to individual bytes
	 * of a 32-bit integer value with B in the least significant byte and zero
	 * in the most significant byte. The channels are NOT clamped to [0.0,1.0]
	 * before the calculation.
	 * 
	 * @return RGB in lower 24 bits of a 32-bit integer
	 */
	public int getRGB() {
		return (((int) (r * 255.0)) << 16) | (((int) (g * 255.0)) << 8)
				| (int) (b * 255.0);
	}

	/**
	 * Returns the ARGB channels scaled to [0,255] and packed to individual bytes
	 * of a 32-bit integer value with B in the least significant byte and alpha
	 * in the most significant byte. The channels are NOT clamped to [0.0,1.0]
	 * before the calculation.
	 * 
	 * @return ARGB in a 32-bit integer
	 */
	public int getARGB() {
		return (((int) (a * 255.0)) << 24) | (((int) (r * 255.0)) << 16)
				| (((int) (g * 255.0)) << 8) | (int) (b * 255.0);
	}

	/**
	 * Compares this object against the specified object.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null) && (obj instanceof Col) 
				&& (new Double(((Col) obj).getR()).equals(getR()))
				&& (new Double(((Col) obj).getG()).equals(getG()))
				&& (new Double(((Col) obj).getB()).equals(getB()))
				&& (new Double(((Col) obj).getA()).equals(getA()));
	}

	/**
     * Returns a hash code value for the object. 
     * 
     * @return  a hash code value for this object.
     */
    @Override
	public int hashCode(){
		return Objects.hash(this.getR(), this.getG(), this.getB(), this.getA());
	}

	/**
	 * Compares this Col against the specified Col with epsilon.
	 * 
	 * @param col
	 *            the color to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both numbers are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Col col, double epsilon) {
		return (this == col) || (col != null) 
				&& Compare.eEquals(getR(), col.getR(), epsilon)
				&& Compare.eEquals(getG(), col.getG(), epsilon)
				&& Compare.eEquals(getB(), col.getB(), epsilon)
				&& Compare.eEquals(getA(), col.getA(), epsilon);
	}

	/**
	 * Compares this Col against the specified Col with epsilon.
	 * 
	 * @param col
	 *            the color to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Col col) {
		return eEquals(col, Compare.EPSILON);
	}
	
	/**
	 * Returns String representation of RGBA
	 * 
	 * @return comma separated floating-point values
	 */
	@Override
	public String toString() {
		return toString("%4.1f");
	}

	/**
	 * Returns String representation of RGBA with channels formated according to 
	 * the given format, see {@link java.lang.String#format(String, Object...)}
	 * 
	 * @param format
	 *            String format applied to each channel
	 * @return comma separated formated floating-point values
	 */
	public String toString(final String format) {
		return String.format(Locale.US, "(" + format + "," + format + ","
				+ format + "," + format + ")", r, g, b, a);
	}
}
