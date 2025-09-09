package geometry;


import androidx.annotation.NonNull;

import java.util.Objects;

public class Vector {
    public final double x, y;
    public Vector(double x, double y) { this.x=x;this.y=y; }
    public Vector(){ x=0; y=0;}
    public double getX(){ return x; }
    public double getY(){ return y; }
    public double length() { return Math.sqrt(x * x + y * y); }
    public Vector reflectX(){ return new Vector(-x, y); }
    public Vector reflectY(){ return new Vector(x, -y); }
    public Vector scale(double scale){ return new Vector(x*scale, y*scale); }
    public Vector scaleX(double scale){ return new Vector(x*scale, y); }
    public Vector scaleY(double scale){return new Vector(x, y*scale); }
    public Vector translate(double deltaX, double deltaY) {return new Vector(x+deltaX, y+deltaY); }
    public Vector translate(Vector v){ return translate(v.getX(), v.getY());}
    public Vector rotate(double angle) {
        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = x * Math.sin(angle) + y * Math.cos(angle);
        return new Vector(newX, newY);
    }
    public Vector add(Vector v) { return new Vector(x + v.getX(), y + v.getY());}
    public Vector subtract(Vector v) { return new Vector(x - v.getX(), y - v.getY()); }
    public Vector copy(){return new Vector(x, y);}

    public Vector normalize() {
        double length = length();
        if (length == 0) return new Vector(0, 0); // Prevent division by zero
        return new Vector(x / length, y / length);
    }

    @NonNull
    @Override
    public String toString() {
        return "Vector {x=" + x + ", y=" + y + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector point = (Vector) o;
        return Math.abs(point.x - x) < 1e-4 && Math.abs(point.y - y) < 1e-4;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}

