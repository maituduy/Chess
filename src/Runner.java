import java.io.IOException;

public class Runner {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Board board = new Board();
                board.setVisible(true);
                try {
                    board.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Board board = new Board();
                board.setVisible(true);
                try {
                    board.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
