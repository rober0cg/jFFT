package jFFT;

//import java.util.Objects;
//import java.io.Serializable;


public class Complex {
//implements Serializable {
//	static final long serialVersionUID = 20211224L ;
    private double r;   // the real part
    private double i;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag) {
        r = real;
        i = imag;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        return String.format( "%+7.2fr, %+7.2fi", r, i);
    }

    // return abs/modulus/magnitude
    public double amplitude() {
        return Math.hypot( r, i );
    }

    // return angle/phase/argument, normalized to be between -pi and pi
    public double phase() {
        return Math.atan2( i, r );
    }

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        return new Complex( r + b.r, i + b.i );
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        return new Complex( r - b.r, i - b.i );
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        return new Complex(
            r * b.r - i * b.i,
            r * b.i + i * b.r
        );
    }

    // return a new object whose value is (this * alpha)
    public Complex scale(double a) {
        return new Complex( r * a, i * a );
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {
        return new Complex( r, -i );
    }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        double scale = r*r + i*i;
        return new Complex( r/scale, -i/scale );
    }

    // return the real or imaginary part
    public double real() { return r; }
    public double imag() { return i; }

    // return a / b
    public Complex divides(Complex b) {
//        Complex a = this;
        return this.times( b.reciprocal() );
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(
            Math.exp(r) * Math.cos(i), 
            Math.exp(r) * Math.sin(i)
        );
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(
            Math.sin(r) * Math.cosh(i), 
            Math.cos(r) * Math.sinh(i)
        );
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(
            Math.cos(r) * Math.cosh(i), 
            -Math.sin(r) * Math.sinh(i)
        );
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }


    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        return new Complex (
            a.r + b.r,
            a.i + b.i
        );
    }

// See Section 3.3.
//    public boolean equals(Object x) {
//        if (x == null) return false;
//        if (this.getClass() != x.getClass()) return false;
//        Complex that = (Complex) x;
//        return (this.r == that.r) && (this.i == that.i);
//    }

// See Section 3.3.
//    public int hashCode() {
//        return Objects.hash( r, i );
//    }

    // para reutilizar
    public void setReal ( double real ) {
        r = real;
    }
    public void setImag ( double imag ) {
        i = imag;
    }


    // sample client for testing
    public static void main(String[] args) {
        Complex a = new Complex(5.0, 6.0);
        Complex b = new Complex(-3.0, 4.0);

        System.out.println("a            = " + a);
        System.out.println("b            = " + b);
        System.out.println("Re(a)        = " + a.real());
        System.out.println("Im(a)        = " + a.imag());
        System.out.println("b + a        = " + b.plus(a));
        System.out.println("a - b        = " + a.minus(b));
        System.out.println("a * b        = " + a.times(b));
        System.out.println("b * a        = " + b.times(a));
        System.out.println("a / b        = " + a.divides(b));
        System.out.println("(a / b) * b  = " + a.divides(b).times(b));
        System.out.println("conj(a)      = " + a.conjugate());
        System.out.println("|a|          = " + a.amplitude());
        System.out.println("phase(a)     = " + a.phase());
        System.out.println("tan(a)       = " + a.tan());
    }

}
