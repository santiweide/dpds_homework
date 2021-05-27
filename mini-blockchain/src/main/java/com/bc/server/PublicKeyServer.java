package com.bc.server;

import com.bc.Ports;
import com.bc.model.ProcessBlock;
import com.bc.model.PublicKey;
import com.bc.utils.MyConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 *  监听public key recv端口，如果有传来public key就保存一下
 * @author algorithm
 */
public class PublicKeyServer implements Runnable {
    /**
     *  Typical would be: One block to store info for each process.
     */
    public Map<Integer, ProcessBlock> processBlockPublicKeyMap = new ConcurrentHashMap<>();

    /**
     * Inner class to share processBlockPublicKeyMap
     * Worker thread to process incoming public keys
     */
    class PublicKeyWorker extends Thread {
        /**
         * one worker one socket
         */
        Socket keySock;

        PublicKeyWorker(Socket s) {
            keySock = s;
        }

        /**
         * 约定协议格式
         * 第一行pid
         * 第二行public key
         */
        @Override
        public void run() {
            try {
                // 读一个key
                BufferedReader in = new BufferedReader(new InputStreamReader(keySock.getInputStream()));
                Integer pid = Integer.parseInt(in.readLine());
                String data = in.readLine();
                System.out.println("Got key: " + data);
                // todo data 需要保存起来，用于以后的解密
                ProcessBlock pb = new ProcessBlock();
                PublicKey publicKey = new PublicKey();
                publicKey.setValue(data);
                pb.setPubKey(publicKey);
                processBlockPublicKeyMap.put(pid, pb);
                keySock.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Socket keySock;
        System.out.println("Starting Key Server input thread using " + Ports.KeyServerPort);
        try {
            ServerSocket servsock = new ServerSocket(Ports.KeyServerPort, MyConsts.SOCKET_BLOCKINGQUEUE_LEN);
            while (true) {
                keySock = servsock.accept();
                new PublicKeyWorker(keySock).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
