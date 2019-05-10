import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Board extends JFrame implements Serializable{

    private JButton[][] jButtons;
    private Map<Integer, ImageIcon> icons;
    private List<Coordinate> suggest;
    private List<Coordinate> logs;
    private int[][] matrix;
    private List<Move> moveHistory;
    private int color;
    
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public Board() {
        setSize(500, 500);
        setLayout(new GridLayout(8, 8));
        matrix = getInitMatrix();
        suggest = new ArrayList<>();
        logs = new ArrayList<>();
        moveHistory = new ArrayList<>();

        initImageIcon();
        initPieces();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public int[][] getInitMatrix() {
        int[][] res = {
                {1, 2, 3, 4, 5, 3, 2, 1},
                {6, 6, 6, 6, 6, 6, 6, 6},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {-6, -6, -6, -6, -6, -6, -6, -6},
                {-1, -2, -3, -4, -5, -3, -2, -1}

        };
        return res;
    }

    public void initPieces() {
        jButtons = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jButtons[i][j] = new JButton();
                if ((i+j) % 2 == 0)
                    jButtons[i][j].setBackground(Color.WHITE);
                else
                    jButtons[i][j].setBackground(Color.GRAY);
                if (matrix[i][j] != 0)
                    jButtons[i][j].setIcon(icons.get(matrix[i][j]));
                add(jButtons[i][j]);
            }
        }
    }

    public void initImageIcon() {
        icons = new HashMap<>();

        icons.put(Piece.BLACK_ROOK, new ImageIcon(new ImageIcon("./icons/black_rook.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.BLACK_KING, new ImageIcon(new ImageIcon("./icons/black_king.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.BLACK_BISHOP, new ImageIcon(new ImageIcon("./icons/black_bishop.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.BLACK_QUEEN, new ImageIcon(new ImageIcon("./icons/black_queen.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.BLACK_KNIGHT, new ImageIcon(new ImageIcon("./icons/black_knight.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.BLACK_PAWN, new ImageIcon(new ImageIcon("./icons/black_pawn.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        icons.put(Piece.WHITE_ROOK, new ImageIcon(new ImageIcon("./icons/white_rook.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.WHITE_KING, new ImageIcon(new ImageIcon("./icons/white_king.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.WHITE_BISHOP, new ImageIcon(new ImageIcon("./icons/white_bishop.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.WHITE_QUEEN, new ImageIcon(new ImageIcon("./icons/white_queen.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.WHITE_KNIGHT, new ImageIcon(new ImageIcon("./icons/white_knight.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        icons.put(Piece.WHITE_PAWN, new ImageIcon(new ImageIcon("./icons/white_pawn.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
    }

    public void setSuggestBackground() {
        resetBackground();
        for (Coordinate coordinate: suggest) {
            int x = coordinate.getX();
            int y = coordinate.getY();
            if (jButtons[x][y].getActionListeners().length > 0)
                for (int i = 0; i < jButtons[x][y].getActionListeners().length; i++)
                    jButtons[x][y].removeActionListener(jButtons[x][y].getActionListeners()[0]);

            jButtons[x][y].addActionListener(new AC(x,y));
            jButtons[x][y].setBackground(Color.red);
        }

    }

    public void resetBackground() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0)
                    jButtons[i][j].setBackground(Color.WHITE);
                else
                    jButtons[i][j].setBackground(Color.GRAY);
            }
        }
    }

    public void addAC() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (jButtons[i][j].getActionListeners().length != 0)
                    for (ActionListener ac: jButtons[i][j].getActionListeners())
                      jButtons[i][j].removeActionListener(ac);

                if (color == -8)
                    if (matrix[i][j]<0)
                        jButtons[i][j].addActionListener(new AC(i, j));

                if (color == 8)
                    if (matrix[i][j] > 0)
                        jButtons[i][j].addActionListener(new AC(i, j));

            }
        }
    }

    public void removeAC() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (jButtons[i][j].getActionListeners().length != 0)
                    for (ActionListener ac: jButtons[i][j].getActionListeners())
                        jButtons[i][j].removeActionListener(ac);

            }
        }
    }

    class AC implements ActionListener {
        int i;
        int j;

        AC(int i, int j) {
            this.i = i;
            this.j = j;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Coordinate buttonClicked = new Coordinate(i,j);

            if (!suggest.contains(buttonClicked)) {
                suggest = Route.getRoute(i,j,matrix, moveHistory);
                setSuggestBackground();
            }
            else {
                Coordinate prevButton = logs.get(logs.size()-1);
                try {
                    int piece = matrix[prevButton.getX()][prevButton.getY()];
                    int sign = (int) Math.signum(color);
                    if (piece ==  sign * 5) {

                        List<Integer> pieces= new ArrayList<>();
                        for (Move mh: moveHistory)
                            pieces.add(mh.getPiece());
                        if (!pieces.contains(piece)) {
                            List<Move> sendList =  new ArrayList<>();
                            if (buttonClicked.getY() == 6){
                                sendList.add(new Move(piece, prevButton, buttonClicked));
                                sendList.add(new Move(sign, new Coordinate(prevButton.getX(),7), new Coordinate(prevButton.getX(),5)));
                                oos.writeObject(sendList);
                            }
                            else if (buttonClicked.getY() == 2) {
                                sendList.add(new Move(piece, prevButton, buttonClicked));
                                sendList.add(new Move(sign, new Coordinate(prevButton.getX(),0), new Coordinate(prevButton.getX(),3)));
                                oos.writeObject(sendList);
                            }
                            else
                                oos.writeObject(new Move(piece, prevButton, buttonClicked));
                        }
                        else
                            oos.writeObject(new Move(piece, prevButton, buttonClicked));
                    }
                    else
                        oos.writeObject(new Move(piece, prevButton, buttonClicked));
                } catch (IOException ex) {}
            }
            logs.add(buttonClicked);
        }
    }

    public void checkDialog(int i, int j) {
        Integer choosed =  null;
        if ((i == 0 && matrix[i][j] == -6) || (i == 7 && matrix[i][j] == 6))
            choosed = chooseDialog(matrix[i][j]);


        if (choosed != null) {
            try {
                oos.writeObject(new Move(choosed, new Coordinate(i,j), new Coordinate(i,j)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Integer chooseDialog(int val) {
        JOptionPane optionPane = new JOptionPane();

        int[] pieces = {1,2,3,4};

        JButton[] buttons = new JButton[pieces.length];
        int i = 0;
        for (Integer x: pieces) {
            if (val<0)
                x = -x;

            buttons[i++] = getButtonDialog(optionPane, x, icons.get(x));
        }

        optionPane.setOptions(buttons);
        optionPane.setMessage(null);
        JDialog dialog = optionPane.createDialog(this, "Choose a piece");
        dialog.setVisible(true);

        Integer choosed = (Integer) optionPane.getValue();

        return choosed;
    }

    public JButton getButtonDialog(JOptionPane optionPane,int piece, Icon icon) {
        JButton button = new JButton(icon);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(piece);
            }
        });
        return button;
    }

    public void start() throws IOException {
        try {
            socket = new Socket("localhost", 9090);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            this.color = (Integer) ois.readObject();
            if (this.color < 0)
                addAC();

            while (true) {
                resetBackground();
                suggest.clear();

                Object object = this.ois.readObject();
                Move response = null;
                List<Move> castling = null;
                try {
                    response = (Move) object;
                }
                catch (ClassCastException e) {
                    castling = (List) object;
                }
//                Move response = (Move) ois.readObject();
                if (castling != null) {
                    for (Move m: castling) {
                        Coordinate from = m.getFrom();
                        Coordinate to = m.getTo();
                        int pieceMoved = m.getPiece();

                        matrix[to.getX()][to.getY()] = pieceMoved;
                        matrix[from.getX()][from.getY()] = 0;

                        jButtons[from.getX()][from.getY()].setIcon(null);
                        jButtons[to.getX()][to.getY()].setIcon(icons.get(pieceMoved));
                        moveHistory.add(m);
                    }

                    if (color * castling.get(0).getPiece() < 0)
                        addAC();
                    else
                        removeAC();

                }
                else {
                    Coordinate from = response.getFrom();
                    Coordinate to = response.getTo();
                    int pieceMoved = response.getPiece();

                    if (from.equals(to)) {
                        matrix[to.getX()][to.getY()] = pieceMoved;
                        jButtons[to.getX()][to.getY()].setIcon(icons.get(pieceMoved));
                        continue;
                    }
                    moveHistory.add(response);
                    int lost = matrix[to.getX()][to.getY()];

                    matrix[to.getX()][to.getY()] = pieceMoved;
                    matrix[from.getX()][from.getY()] = 0;

                    jButtons[from.getX()][from.getY()].setIcon(null);
                    jButtons[to.getX()][to.getY()].setIcon(icons.get(pieceMoved));

                    if (lost == 5 || lost == -5) {
                        String title = (lost * color > 0) ? "YOU LOSE" : "YOU WIN";
                        removeAC();
                        resetBackground();
                        JOptionPane.showMessageDialog(this, title);
                        break;
                    }
                    if (color * pieceMoved > 0) {
                        removeAC();
                        checkDialog(to.getX(), to.getY());
                    }

                    if (color * pieceMoved < 0)
                        addAC();
                }

            }
        }
        catch (IOException e) {}
        catch (ClassNotFoundException e) {}
        finally {
            socket.close();
        }
    }


}
