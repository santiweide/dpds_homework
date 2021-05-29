package com.dut;

import com.dut.model.BlockRecord;
import com.dut.server.BlockchainServer;
import com.dut.server.PublicKeyServer;
import com.dut.server.UnverifiedBlockConsumer;
import com.dut.server.UnverifiedBlockServer;
import com.dut.utils.BlockInputG;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author algorithm
 */
public class BlockChain {
    public static String serverName = "localhost";
    /**
     * initialized with a [dummy head]
     */
    public static String blockchain = "Previous Proof of Work";
    /**
     * 如果是批处理脚本启动n个进程，这里就被初始化为n。
     */
    public static int numProcesses = 3;
    /**
     * Our process ID
     */
    public static int PID = 0;
    List<BlockRecord> recordList = new LinkedList<>();
    /**
     * This queue of UVBs must be concurrent because it is shared by producer threads and the consumer thread
     */
    final PriorityBlockingQueue<BlockRecord> ourPriorityQueue = new PriorityBlockingQueue<>(100, BlockTSComparator);
    public static Comparator<BlockRecord> BlockTSComparator = (b1, b2) -> {
        //System.out.println("In comparator");
        String s1 = b1.getTimeStamp();
        String s2 = b2.getTimeStamp();
        if (s1.equals(s2)) {
            return 0;
        }
        if (s2 == null) {
            return 1;
        }
        return s1.compareTo(s2);
    };

    public void KeySend() {
        // Multicast our public key to the other processes
        Socket sock;
        PrintStream toServer;
        try {
            for (int i = 0; i < numProcesses; i++) {
                // Send our public key to all servers.
                System.out.println("connecting to " + Ports.KeyServerPortBase + i);
                sock = new Socket(serverName, Ports.KeyServerPortBase + i);
                toServer = new PrintStream(sock.getOutputStream());
                // 此处的 public key 位 FakeKeyProcess + PID
                toServer.println("FakeKeyProcess" + BlockChain.PID);
                toServer.flush();
                sock.close();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    /**
     * 根据文件数据创建UVB，使用JSON作为序列化格式，组播到其他process
     */
    public void unverifiedSend() {
        // Will be client connection to the Unverified Block Server for each other process.
        Socket UVBSocket;
        Random r = new Random();
        try {
            // Each process reads in a data file 每个进程只读取自己进程ID的那一个文件
            BlockRecord unverifiedBR = BlockInputG.getBlockRecordFromFile(PID).get(0);
            // The completed unverified block is marshaled as JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String unverifiedBRStr = gson.toJson(unverifiedBR);
            // multicast to all processes at the correct unverified block port, including to the creating process itself
            ObjectOutputStream toServerOOS;
            for (int i = 0; i < numProcesses; i++) {
                // 组播，Send some sample Unverified Blocks (UVBs) to each process,
                System.out.println("Sending UVBs to process " + i + "...");
                // Client connection. Triggers Unverified Block Worker in other process's UVB server:
                UVBSocket = new Socket(serverName, Ports.UnverifiedBlockServerPortBase + i);
                toServerOOS = new ObjectOutputStream(UVBSocket.getOutputStream());
                // Sleep up to a second to randominze when sent.
                Thread.sleep((r.nextInt(9) * 100));
                toServerOOS.writeObject(unverifiedBRStr);
                toServerOOS.flush();
                UVBSocket.close();

            }
            // Sleep up to a second to randomize when sent.
            Thread.sleep((r.nextInt(9) * 100));
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void main(String args[]) {
        BlockChain s = new BlockChain();
        // Break out of main to avoid static reference conflicts.
        s.run(args);
    }

    public void run(String args[]) {
        System.out.println("Running now\n");

        // Process ID is passed to the JVM
        PID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);
        System.out.println("Clark Elliott's Block Coordination Framework. Use Control-C to stop the process.\n");
        System.out.println("Using processID " + PID + "\n");
        // Establish OUR port number scheme, based on PID
        new Ports().setPorts();

        new Thread(new PublicKeyServer()).start();
        // 处理未验证的block，这些UVB来自文件，经过UnverifiedBlockServer push到队列里面
        new Thread(new UnverifiedBlockServer(ourPriorityQueue)).start();

        // New thread to process incoming new blockchains
        new Thread(new BlockchainServer()).start();
        try {
            // P0 P1 并不会出发KeySend，所以这里要Sleep一下，要P2启动之后才KeySend()
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Multicast public key
        KeySend();
        try {
            // 等待所有public key
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Multicast some new unverified blocks out to all servers as data
        new BlockChain().unverifiedSend();
        try {
            // Wait for multicast ready
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Start consuming the queued-up unverified blocks
        new Thread(new UnverifiedBlockConsumer(ourPriorityQueue, PID)).start();
    }
}
