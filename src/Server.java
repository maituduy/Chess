import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static List<Move> moveHistory;
    static final int PORT = 9090;
    static int step = 0;
    public static void main(String[] args) {
        moveHistory = new ArrayList<>();

        try {
            ServerSocket server = new ServerSocket(PORT);
            Socket white = server.accept();
            System.out.println(white);
            Socket black = server.accept();
            System.out.println(black);

            Player p1 = new Player(white, 0);
            Player p2 = new Player(black, 1);

            p1.setRival(p2);
            p2.setRival(p1);

            p1.start();
            p2.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Player extends Thread {
        Player rival;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        Socket socket;
        int turn;

        public Player(Socket socket, int turn) {
            this.socket = socket;
            this.turn = turn;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            }
            catch(Exception e) {}
        }

        public void setRival(Player rival) {
            this.rival = rival;
        }


        @Override
        public void run() {
            try {
                int beginCode = (step % 2 == turn) ? -8 : 8;
                oos.writeObject(beginCode);
                while (true) {
                    Object object = this.ois.readObject();
                    Move move = null;
                    List<Move> castling = null;
                    try {
                        move = (Move) object;
                    }
                    catch (ClassCastException e) {
                        castling = (List) object;
                    }
//                    Move move = (Move) this.ois.readObject();
                    if (castling != null) {
                        oos.writeObject(castling);
                        rival.oos.writeObject(castling);
                        step++;
                    }
                    else {
                        if (move.getFrom().equals(move.getTo())) {
                            oos.writeObject(move);
                            rival.oos.writeObject(move);
                        }

                        if (step % 2 == turn) {
                            step++;
                            oos.writeObject(move);
                            rival.oos.writeObject(move);
                        }
                    }

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
