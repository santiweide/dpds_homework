package com.bc.server;

import com.bc.Ports;
import com.bc.model.BlockRecord;
import com.bc.utils.MyConsts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;

/**
 * @author algorithm
 */
public class UnverifiedBlockServer implements Runnable {
    private BlockingQueue<BlockRecord> queue;

    public UnverifiedBlockServer(BlockingQueue<BlockRecord> queue) {
        this.queue = queue;
    }
    public static Comparator<BlockRecord> BlockTSComparator = (b1, b2) -> {
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

      /* Inner class to share priority queue. We are going to place the unverified blocks (UVBs) into this queue in the order
       we get them, but they will be retrieved by a consumer process sorted by TimeStamp of when created. */

    class UnverifiedBlockWorker extends Thread {
        Socket sock;

        UnverifiedBlockWorker(Socket s) {
            sock = s;
        }

        BlockRecord BR = new BlockRecord();

        // Receive a UVB and put it into the shared priority queue.
        @Override
        public void run() {
            try {
                ObjectInputStream unverifiedIn = new ObjectInputStream(sock.getInputStream());
                // 反序列化JSON
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                BR = gson.fromJson((String)unverifiedIn.readObject(), BlockRecord.class);
                System.out.println("Received UVB: " + BR.getTimeStamp() + " " + BR.getFname() + " " + BR.getLname());
                queue.put(BR);
                // Note: make sure you have a large enough blocking priority queue to accept all the puts
                sock.close();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Socket sock;
        System.out.println("Starting the Unverified Block Server input thread using " + Ports.UnverifiedBlockServerPort);
        try {
            ServerSocket UVBServer = new ServerSocket(Ports.UnverifiedBlockServerPort, MyConsts.SOCKET_BLOCKINGQUEUE_LEN);
            while (true) {
                // Got a new unverified block
                sock = UVBServer.accept();
                new UnverifiedBlockWorker(sock).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
