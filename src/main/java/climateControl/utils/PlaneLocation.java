package climateControl.utils;

import java.util.Comparator;

import net.minecraft.util.ChunkCoordinates;

/**
 *
 * @author Zeno410
 */
public class PlaneLocation {

    private int x;
    private int z;

    public PlaneLocation(int _x, int _z) {
        x = _x;
        z = _z;
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    private void setX(int newX) {
        x = newX;
    }

    private void setZ(int newZ) {
        z = newZ;
    }

    public PlaneLocation(ChunkCoordinates coordinates) {
        this(coordinates.posX, coordinates.posZ);
    }

    public float distance(PlaneLocation location) {
        return ((float) (x - location.x)) * ((float) (x - location.x))
            + ((float) (z - location.z)) * ((float) (z - location.z));
    }

    public <Type extends Provider> Type closest(Iterable<Type> choices) {
        Type result = null;
        float bestDistance = Float.MAX_VALUE;
        float distance;

        for (Type tested : choices) {
            distance = this.distance(tested.planeLocation());
            if (distance < bestDistance) {
                result = tested;
            }
        }
        return result;
    }

    public abstract class Provider {

        abstract public PlaneLocation planeLocation();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PlaneLocation)) {
            return false;
        }
        final PlaneLocation other = (PlaneLocation) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + "," + z + ")";
    }

    /**
     * A subclass of PlaneLocation which allows the vars to be changed.
     * Intended for probing hash sets and maps
     */
    public static class Probe extends PlaneLocation {

        public Probe(int x, int z) {
            super(x, z);
        }

        public void setX(int newX) {
            super.setX(newX);
        }

        public void setZ(int newZ) {
            super.setZ(newZ);
        }
    }

    public static Comparator<PlaneLocation> topLefttoBottomRight() {
        return new Comparator<PlaneLocation>() {

            public int compare(PlaneLocation arg0, PlaneLocation arg1) {
                int result = arg0.x - arg1.x;
                if (result == 0) return arg0.z - arg1.z;
                return result;
            }

        };
    }
}
