import java.util.ArrayList;
import java.util.List;

public class Route {
    final static int[] knightX = {-2, -1, -2, -1, 1, 2, 2, 1};
    final static int[] knightY = {-1, -2, 1, 2, -2, -1, 1, 2};

    final static int[] bishopX = {1,2,3,4,5,6,7,-1,-2,-3,-4,-5,-6,-7,-1,-2,-3,-4,-5,-6,-7,1,2,3,4,5,6,7};
    final static int[] bishopY = {1,2,3,4,5,6,7,-1,-2,-3,-4,-5,-6,-7,1,2,3,4,5,6,7,-1,-2,-3,-4,-5,-6,-7};

    final static int[] kingX = {-1,-1,-1,0,0,1,1,1};
    final static int[] kingY = {-1,0,1,-1,1,-1,0,1};

    final static int[] rookX = {0,0,0,0,0,0,0,1,2,3,4,5,6,7,0,0,0,0,0,0,0,-1,-2,-3,-4,-5,-6,-7};
    final static int[] rookY = {1,2,3,4,5,6,7,0,0,0,0,0,0,0,-1,-2,-3,-4,-5,-6,-7,0,0,0,0,0,0,0};

    final static int[] pawnWX = {-2,-1,-1,-1};
    final static int[] pawnWY = {0,-1,1,0};

    final static int[] pawnBX = {2,1,1,1};
    final static int[] pawnBY = {0,-1,1,0};

    final static int[] queenX = concat(bishopX, rookX);
    final static int[] queenY = concat(bishopY, rookY);

    static List<Move> moveHistory;
    public static int[] concat(int[] x, int[] y) {
        int[] res = new int[x.length+y.length];
        int k = 0;
        for (int i = 0; i < x.length; i++)
            res[k++] = x[i];
        for (int i = 0; i < y.length; i++)
            res[k++] = y[i];

        return res;
    }

    public static List<Coordinate> getRoute(int x, int y, int[][] matrix, List<Move> moveHistory) {
        Route.moveHistory = moveHistory;
        List<Coordinate> res = new ArrayList<>();
        List<Coordinate> route;
        switch (matrix[x][y]) {
            case Piece.BLACK_BISHOP:
            case Piece.WHITE_BISHOP:
                route = Coordinate.toList(bishopX, bishopY);
                res = new Route().getRoute(route, matrix, x, y, 1);
                break;


            case Piece.BLACK_ROOK:
            case Piece.WHITE_ROOK:
                route = Coordinate.toList(rookX, rookY);
                res = new Route().getRoute(route, matrix, x, y, 1);
                break;


            case Piece.BLACK_QUEEN:
            case Piece.WHITE_QUEEN:
                route = Coordinate.toList(queenX, queenY);
                res = new Route().getRoute(route, matrix, x, y, 1);
                break;

            case Piece.BLACK_KNIGHT:
            case Piece.WHITE_KNIGHT:
                route = Coordinate.toList(knightX, knightY);
                res = new Route().getRoute(route, matrix, x, y, 2);
                break;


            case Piece.BLACK_KING:
            case Piece.WHITE_KING:
                route = Coordinate.toList(kingX, kingY);
                res = new Route().getRoute(route, matrix, x, y, 2);
                break;

            case Piece.BLACK_PAWN:
                route = Coordinate.toList(pawnBX, pawnBY);
                res = new Route().getRoute(route, matrix, x, y, 3);
                break;
            case Piece.WHITE_PAWN:
                route = Coordinate.toList(pawnWX, pawnWY);
                res = new Route().getRoute(route, matrix, x, y, 3);
                break;
        }


        return res;

    }

    private List<Coordinate> getRoute(List<Coordinate> route, int[][] matrix, int x, int y, int group) {
        List<Coordinate> res = new ArrayList<>();
        if (group == 1) {
            for (Coordinate coordinate: route) {
                int x_new = coordinate.getX()+x;
                int y_new = coordinate.getY()+y;
                if (checkMove(route, matrix, x, y, x_new, y_new))
                    res.add(new Coordinate(x_new,y_new));
            }

        }
        else if (group == 2) {
            for (Coordinate coordinate : route) {
                int x_new = coordinate.getX() + x;
                int y_new = coordinate.getY() + y;
                try {
                    if (matrix[x_new][y_new] * matrix[x][y] <= 0)
                        res.add(new Coordinate(x_new, y_new));
                } catch (ArrayIndexOutOfBoundsException e) {}
            }

            if (matrix[x][y] == 5 || matrix[x][y] == -5) {
                int val = matrix[x][y];
                Coordinate king_coor = (val < 0) ? new Coordinate(7,4): new Coordinate(0,4);
                Coordinate rook0 = (val < 0) ? new Coordinate(7,0): new Coordinate(0,0);
                Coordinate rook7 = (val < 0) ? new Coordinate(7,7): new Coordinate(0,7);

                List<Coordinate> from_list = new ArrayList<>();
                for (Move move: Route.moveHistory)
                    from_list.add(move.getFrom());
                if (!from_list.contains(king_coor)) {
                    if (!from_list.contains(rook0) && checkCastling(matrix, rook0))
                        res.add(new Coordinate(x,2));
                    if (!from_list.contains(rook7) && checkCastling(matrix, rook7))
                        res.add(new Coordinate(x,6));
                }
            }
        }
        else {
            for (int i = 0; i < route.size(); i++) {
                int x_new = route.get(i).getX() + x;
                int y_new = route.get(i).getY() + y;
                try {
                    if (i==0)
                        if ((x == 1 || x == 6) && matrix[route.get(i).getX()/2 + x][route.get(i).getY() + y] == 0)
                        {
                            try {
                                if (matrix[x_new][y_new] != 0)
                                    continue;
                                res.add(new Coordinate(x_new, y_new));
                            } catch (IndexOutOfBoundsException e) {}
                        }

                    if (i==1 || i==2)
                        if (matrix[x_new][y_new] * matrix[x][y] < 0)
                            res.add(new Coordinate(x_new, y_new));
                    if (i==3 && matrix[x_new][y_new] * matrix[x][y] == 0)
                            res.add(new Coordinate(x_new, y_new));

                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        }
        return res;

    }
    public static boolean checkMove(List<Coordinate> coordinates, int[][] matrix, int x, int y, int xCheck, int yCheck) {
        try {
            if (matrix[x][y] * matrix[xCheck][yCheck] > 0)
                return false;

        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        Coordinate rootCoor = new Coordinate(x,y);
        Coordinate checkCoor = new Coordinate(xCheck,yCheck);

        for (Coordinate coordinate: coordinates) {
            int t_x = coordinate.getX() + x;
            int t_y = coordinate.getY() + y;
            Coordinate midCoor = new Coordinate(t_x, t_y);
            try {
                if (!midCoor.equals(rootCoor) && !midCoor.equals(checkCoor) && matrix[midCoor.getX()][midCoor.getY()] != 0) {
                    if (checkOnLine(rootCoor, midCoor, checkCoor))
                        return false;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {}
        }

        return true;
    }

    public static boolean checkOnLine(Coordinate c1, Coordinate midC, Coordinate c2) {
//        return c1.getY() + ((c2.getY() - c1.getY()) / (c2.getX() - c1.getX())) * (cCheck.getX() - c1.getX()) - cCheck.getY() == 0;
        return Math.abs(Coordinate.norm(c1, midC) + Coordinate.norm(midC, c2) - Coordinate.norm(c1, c2)) < 0.000001;
    }

    public static boolean checkCastling(int[][] matrix, Coordinate rook) {
        int row = rook.getX();
        if (rook.getY() == 0) {
            for (int col = 1; col<4; col++)
                if (matrix[row][col] != 0)
                    return false;
        }


        if (rook.getY() == 7)
            for (int col2 = 5; col2<7; col2++)
                if (matrix[row][col2] != 0)
                    return false;
        return true;
    }


}
