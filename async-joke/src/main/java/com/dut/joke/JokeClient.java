package com.dut.joke;

// ref: https://condor.depaul.edu/elliott/435/hw/programs/program-joke.html
// hope it is still avaliable :P

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

interface JokeConsts {
    int SERVER_CLIENT_PORT = 4545;
    int SERVER_CLIENT_PORT_SECONDARY = 4546;
    int ADMIN_CLIENT_PORT = 5050;
    int ADMIN_CLIENT_PORT_SECONDARY = 5051;
    String CLIENT_NAME = "client";
    String CLIENT_ADMIN_NAME = "admin";
    int SOCKET_BLOCK_QUEUE_LEN = 6;
}

class AddressInfo {
    private String host;
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}

class Monitor {
    private final Map<String, List<AddressInfo>> serverIpMap = new ConcurrentHashMap<>();

    public Monitor() {
        serverIpMap.put(JokeConsts.CLIENT_NAME, new ArrayList<>());
        serverIpMap.put(JokeConsts.CLIENT_ADMIN_NAME, new ArrayList<>());
    }

    public AddressInfo getServerInfo(String clientName) {
        AddressInfo ret = new AddressInfo();
        List<AddressInfo> serverNameList = serverIpMap.get(clientName);

        if (serverNameList.isEmpty()) {
            ret.setHost("localhost");
            ret.setPort(JokeConsts.SERVER_CLIENT_PORT);
            return ret;
        }
        Random random = new Random();
        int i = random.nextInt() % serverNameList.size();
        AddressInfo info = serverNameList.get(i);
        ret.setHost(info.getHost());
        ret.setPort(info.getPort());
        return ret;

    }

    public void setServer(String name, List<String> servers) {
        if (servers.size() > 1) {
            AddressInfo info = new AddressInfo();
            info.setHost(servers.get(1));
            if (JokeConsts.CLIENT_NAME.equals(name)) {
                info.setPort(JokeConsts.SERVER_CLIENT_PORT_SECONDARY);
            } else {
                info.setPort(JokeConsts.ADMIN_CLIENT_PORT_SECONDARY);
            }
            serverIpMap.get(name).add(info);
        } else if (servers.size() > 0) {
            AddressInfo info = new AddressInfo();
            info.setHost(servers.get(0));
            if (JokeConsts.CLIENT_NAME.equals(name)) {
                info.setPort(JokeConsts.SERVER_CLIENT_PORT);
            } else {
                info.setPort(JokeConsts.ADMIN_CLIENT_PORT);
            }
            serverIpMap.get(name).add(info);
        }
    }
}

/**
 * @author algorithm
 */
public class JokeClient {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        monitor.setServer(JokeConsts.CLIENT_NAME, Arrays.asList(args));

        AddressInfo addressInfo = monitor.getServerInfo(JokeConsts.CLIENT_NAME);

        System.out.println("Santiweide's Joke Client, 0.1.\n");
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

    static String toText(byte ip[]) { /* Make portable for 128 bit format */
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < ip.length; ++i) {
            if (i > 0) {
                result.append(".");
            }
            result.append(0xff & ip[i]);
        }
        return result.toString();
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
