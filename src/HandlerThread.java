import java.io.*;
import java.net.Socket;

public class HandlerThread extends Thread {

    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public HandlerThread (Socket socket) {
        this.socket = socket;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                Move move = (Move) ois.readObject();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
