package com.dut.joke;

import java.io.IOException;
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
        System.out.println("Now communicating with: " + addressInfo.getHost() + ", port: " + addressInfo.getPort());
        try {
            Socket sock = new Socket(addressInfo.getHost(), JokeConsts.ADMIN_CLIENT_PORT);

            sock.close();
        } catch (IOException x) {
            System.out.println("Socket error.");
            x.printStackTrace();
        }

    }
}
