package geometry;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

public class Pose extends Vector{
    public final double angle;

    public Pose(double x, double y, double angle) {
        super(x, y);
        this.angle = angle;
    }

    public Pose(Vector v, double angle){
        super(v.getX(), v.getY());
        this.angle = angle;
    }

    public double getAngle(){ return angle; }

    public Pose rotateAngle(double deltaAngle){ return new Pose(x, y, angle + deltaAngle); }
    public Pose reflectAngle(){ return new Pose(x, y, -angle); }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pose pose = (Pose) o;
        return Math.abs(pose.x - x) < 1e-4 && Math.abs(pose.y - y) < 1e-4 && Math.abs(pose.angle - angle) < 1e-4;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y, angle);
    }

    @NonNull
    @Override
    public String toString() {
        return "Pose {x=" + x + ", y=" + y + ", angle=" + angle + "}";
    }
}
