import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coordinate implements Serializable {
    private int x;
    private int y;

    public Coordinate(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[" + this.getX() + ", " + this.getY() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
        Coordinate coor = (Coordinate) obj;
        return coor.getX() == this.getX() && coor.getY() == this.getY();
    }

    public static List<Coordinate> toList(int[] x, int[] y) {
        List<Coordinate> res = new ArrayList<>();
        for (int i = 0; i < x.length; i++)
            res.add(new Coordinate(x[i], y[i]));
        return res;
    }

    public static double norm(Coordinate c1, Coordinate c2) {
        return Math.sqrt(Math.pow(c1.getX()-c2.getX(), 2) + Math.pow(c1.getY()-c2.getY(), 2));
    }
}
