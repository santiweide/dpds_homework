package com.dut.joke;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class WorkerData {
    private List<Integer> list = Arrays.asList(0, 1, 2, 3);
    private Integer pos = 0;
    private boolean first = true;
    private Map<Integer, Character> _map=  new ConcurrentHashMap<>();
    WorkerData(){
        for(int i = 0;i < 4;i ++){
            _map.put(i, (char)(i+'A'));
        }
    }
    private void randomize() {
        Collections.shuffle(list);
    }

    public Character getNextChar(String name) {
        Character ret;
        if(pos == 0){
            if(first){
                first = false;
            } else {
                System.out.println( name + "'s "  + (JokeServer.serverMode?"PROVERB":"JOKE")+ " CYCLE COMPLETED");
            }
        }
        ret = _map.get(list.get(pos));
        pos = (pos + 1) % list.size();
        if (pos == 0) {
            randomize();
        }
        return ret;
    }

}

/**
 * 一个Worker Handle一个 port
 */
class Worker extends Thread {
    Socket sock;

    Worker(Socket s) {
        sock = s;
    }

    @Override
    public void run() {
        PrintStream out;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(sock.getOutputStream());
            try {
                String clientName = in.readLine();
                if(!JokeServer.socketDataMap.containsKey(clientName)){
                    JokeServer.socketDataMap.put(clientName, new WorkerData());
                }
                serveData(clientName, out);
            } catch (IOException x) {
                System.out.println("Server read client name error");
                x.printStackTrace();
            }
            sock.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void serveData(String name, PrintStream out) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JokeServer.serverMode ? "P" : "J");
        stringBuilder.append(JokeServer.socketDataMap.get(name).getNextChar(name));
        stringBuilder.append(" ").append(name);
        out.println(stringBuilder.toString());
        System.out.println("Data: " + stringBuilder.toString());

    }
}

class ModWorker extends Thread {
    Socket sock;

    ModWorker(Socket s) {
        sock = s;
    }

    @Override
    public void run() {
        System.out.println("Changing JokeServer Mode from " + JokeServer.serverMode + " to " + !JokeServer.serverMode);
        JokeServer.serverMode = !JokeServer.serverMode;
    }

}

class AdminLooper implements Runnable {
    public static boolean adminControlSwitch = true;

    @Override
    public void run() {
        Socket sock;
        try {
            ServerSocket servsock = new ServerSocket(JokeConsts.ADMIN_CLIENT_PORT, JokeConsts.SOCKET_BLOCK_QUEUE_LEN);
            while (adminControlSwitch) {
                // wait for the next ADMIN client connection:
                sock = servsock.accept();
                new ModWorker(sock).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}

/**
 * @author algorithm
 */
public class JokeServer {
    /**
     * 0: Joke
     * 1: Proverb
     */
    public static boolean serverMode = false;
    public static Map<String, WorkerData> socketDataMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        new Thread(new AdminLooper()).start();
        Socket sock;
        int port;
        if (args.length > 0 && "secondary".equals(args[0])) {
            port = JokeConsts.SERVER_CLIENT_PORT_SECONDARY;
        } else {
            port = JokeConsts.SERVER_CLIENT_PORT;
        }
        ServerSocket serverSocket = new ServerSocket(port, JokeConsts.SOCKET_BLOCK_QUEUE_LEN);
        System.out.println("Santiweide's Joke server 0.1 starting up, listening at port " + port + ".\n");
        while (true) {
            sock = serverSocket.accept();
            new Worker(sock).start();
        }
    }
}
