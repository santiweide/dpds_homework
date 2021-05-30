package com.dut.joke;

import java.io.*;
import java.net.*;

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
        PrintStream out = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader
                    (new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(sock.getOutputStream());
            try {
                String name;
                name = in.readLine();
                System.out.println("Looking up " + name);
                printRemoteAddress(name, out);
            } catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace();
            }
            sock.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    static void printRemoteAddress(String name, PrintStream out) {
        try {
            out.println("Looking up " + name + "...");
            InetAddress machine = InetAddress.getByName(name);
            out.println("Host name : " + machine.getHostName());
            out.println("Host IP : " + toText(machine.getAddress()));
        } catch (UnknownHostException ex) {
            out.println("Failed in atempt to look up " + name);
        }
    }

    static String toText(byte ip[]) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ip.length; ++i) {
            if (i > 0) {
                result.append(".");
            }
            result.append(0xff & ip[i]);
        }
        return result.toString();
    }
}

/**
 * @author algorithm
 */
public class JokeServer {

    public static void main(String[] args) throws IOException {
        Socket sock;
        int port;
        if(args.length > 0 && "secondary".equals(args[0])){
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
