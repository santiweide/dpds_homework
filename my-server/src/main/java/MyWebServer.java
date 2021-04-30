
import utils.MyConsts;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Worker extends Thread {
    Socket sock;

    public Worker(Socket s) {
        sock = s;
    }

    @Override
    public void run() {

    }
}

/**
 * todo MyWebServer compiles as one file. "javac *.java" in one directory compiles all code.
 *
 * @author algorithm
 */
public class MyWebServer {
    public static void main(String args[]) throws IOException {

        ServerSocket serverSocket = new ServerSocket(MyConsts.DEFAULT_IP_PORT, MyConsts.QUEUE_LENGTH);
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                new Worker(socket).start();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
