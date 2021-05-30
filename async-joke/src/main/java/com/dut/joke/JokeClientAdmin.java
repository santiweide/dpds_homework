package com.dut.joke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author algorithm
 */
public class JokeClientAdmin {
    public static void main(String[] args) {
        Monitor.setServer(JokeConsts.CLIENT_ADMIN_NAME, Arrays.asList(args));

        AddressInfo addressInfo = Monitor.getServerInfo(JokeConsts.CLIENT_ADMIN_NAME);

        System.out.println("Santiweide's Joke Client Admin, 0.1.\n");
        System.out.println("Using server: " + addressInfo.getHost() + ", Port: " + addressInfo.getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name;
            for (; ; ) {
                System.out.print("Enter a hostname or an IP address, (quit) to end: ");
                System.out.flush();
                name = in.readLine();
                if (!name.contains("quit")) {
                    getRemoteAddress(name, addressInfo.getHost());
                } else {
                    break;
                }
            }
            System.out.println("Cancelled by user request.");
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    static void getRemoteAddress(String name, String serverName) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
        try {
            sock = new Socket(serverName, JokeConsts.SERVER_CLIENT_PORT);
            fromServer =
                    new BufferedReader(new InputStreamReader(sock.getInputStream()));
            toServer = new PrintStream(sock.getOutputStream());
            toServer.println(name);
            toServer.flush();
            for (int i = 1; i <= 3; i++) {
                textFromServer = fromServer.readLine();
                if (textFromServer != null) {
                    System.out.println(textFromServer);
                }
            }
            sock.close();
        } catch (IOException x) {
            System.out.println("Socket error.");
            x.printStackTrace();
        }
    }
}
