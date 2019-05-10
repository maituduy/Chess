import java.io.Serializable;

class Move implements Serializable {
    private int piece;
    private Coordinate from;
    private Coordinate to;

    public Move(int piece, Coordinate from, Coordinate to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    public int getPiece() {
        return this.piece;
    }

    public Coordinate getFrom() {
        return this.from;
    }

    @Override
    public String toString() {
        return this.piece + ": " + from.toString() + " -> " + to.toString();
    }

    public Coordinate getTo() {
        return this.to;
    }
}