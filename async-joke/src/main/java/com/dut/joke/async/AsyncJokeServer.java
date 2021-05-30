package com.dut.joke.async;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class WorkerData {
    private List<Integer> list = Arrays.asList(0, 1, 2, 3);
    private Integer pos = 0;
    private boolean first = true;
    private Map<Integer, Character> _map = new ConcurrentHashMap<>();

    WorkerData() {
        for (int i = 0; i < 4; i++) {
            _map.put(i, (char) (i + 'A'));
        }
    }

    private void randomize() {
        Collections.shuffle(list);
    }

    public Character getNextChar() {
        Character ret;
        if (pos == 0) {
            if (first) {
                first = false;
            } else {
                System.out.println((AsyncJokeServer.serverMode ? "PROVERB" : "JOKE") + " CYCLE COMPLETED");
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
    @Override
    public void run() {
        try {
            DatagramSocket sock = null;
            while (AsyncJokeServer.running) {

                sock = new DatagramSocket(4945);
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                sock.receive(packet);
                String clientUDPServerPortStr = new String(packet.getData(), 0, packet.getLength());
                Integer clientPort = Integer.valueOf(clientUDPServerPortStr);
                System.out.println("'Server receive client port: "+ clientPort);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(AsyncJokeServer.serverMode ? "P" : "J");
                stringBuilder.append(AsyncJokeServer.socketDataMap.get(" ").getNextChar());
                stringBuilder.append(" ");
                String msg = stringBuilder.toString();
                System.out.println("Giving back "+ msg);
                buf = msg.getBytes();
                packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), clientPort);
                Thread.sleep(1000);
                sock.send(packet);
            }
            if(sock != null){
                sock.close();
            }
        } catch (IOException | InterruptedException x) {
            System.out.println("Server read client name error");
            x.printStackTrace();
        }
    }
}

/**
 * @author algorithm
 */
public class AsyncJokeServer {
    /**
     * 0: Joke
     * 1: Proverb
     */
    public static boolean running = true;
    public static boolean serverMode = false;
    public static Map<String, WorkerData> socketDataMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        socketDataMap.put(" ", new WorkerData());
        System.out.println("Starting Santiweide Aysnc Joke Server");
        new Thread(new Worker()).start();

    }
}
