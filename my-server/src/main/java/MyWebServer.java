
import utils.MyConsts;
import utils.ServerHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

class Worker extends Thread {
    Socket sock;

    public Worker(Socket s) {
        sock = s;
    }

    @Override
    public void run() {
        String wwwhome = "D:\\coding\\distribute_system\\hw3\\my-server\\src\\main\\resources";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            OutputStream out = new BufferedOutputStream(sock.getOutputStream());
            PrintStream pout = new PrintStream(out);

            // read first line of request (ignore the rest)
            String request = in.readLine();
            if (request == null) {
                return;
            }
            ServerHelper.log(sock, request);
            while (true) {
                String misc = in.readLine();
                if (misc == null || misc.length() == 0) {
                    break;
                }
            }
            // parse the line
            boolean GETRequest = request.startsWith("GET");
            boolean isHttp1 = request.endsWith("HTTP/1.0") || request.endsWith("HTTP/1.1");
            if (!GETRequest || request.length() < 14 || !isHttp1) {
                // bad request
                ServerHelper.errorReport(pout, sock, "400", "Bad Request",
                        "Your browser sent a request that " +
                                "this server could not understand.");
            } else {
                String req = request.substring(4, request.length() - 9).trim();
                if (req.contains("..") ||
                        req.contains("/.ht") || req.endsWith("~")) {
                    // evil hacker trying to read non-wwwhome or secret file
                    ServerHelper.errorReport(pout, sock, "403", "Forbidden",
                            "You don't have permission to access the requested URL.");
                } else {
                    if ("/quit".equals(req)) {
                        out.flush();
                        MyWebServer.stop = true;
                        return;
                    }
                    String path = wwwhome + "\\" + req;
                    File f = new File(path);
                    if (f.isDirectory() && !path.endsWith("/")) {
                        // redirect browser if referring to directory without final '/'
                        pout.print("HTTP/1.0 301 Moved Permanently\r\n" +
                                "Location: http://" +
                                sock.getLocalAddress().getHostAddress() + ":" +
                                sock.getLocalPort() + "/" + req + "/\r\n\r\n");
                        ServerHelper.log(sock, "301 Moved Permanently");
                    } else {
                        if (f.isDirectory()) {
                            // if directory, implicitly add 'index.html'
                            path = path + "index.html";
                            f = new File(path);
                        }
                        try {
                            // send file
                            InputStream file = new FileInputStream(f);
                            pout.print("HTTP/1.0 200 OK\r\n" +
                                    "Content-Type: " + ServerHelper.guessContentType(path) + "\r\n" +
                                    "Date: " + new Date() + "\r\n" +
                                    "Server: MyWebServer 1.0\r\n\r\n");
                            // send raw file
                            ServerHelper.sendFile(file, out);
                            ServerHelper.log(sock, "200 OK");
                        } catch (FileNotFoundException e) {
                            // file not found
                            ServerHelper.errorReport(pout, sock, "404", "Not Found",
                                    "The requested URL was not found on this server.");
                        }
                    }
                }
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * todo MyWebServer compiles as one file. "javac *.java" in one directory compiles all code.
 *
 * @author algorithm
 */
public class MyWebServer {
    public static boolean stop = false;

    public static void main(String args[]) throws IOException {
        Socket socket = null;
        ServerSocket serverSocket = new ServerSocket(MyConsts.DEFAULT_IP_PORT, MyConsts.QUEUE_LENGTH);
        while (!stop) {
            try {
                socket = serverSocket.accept();
                new Worker(socket).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            socket.close();
        }
    }
}
