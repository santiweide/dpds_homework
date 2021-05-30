package com.dut.joke.async;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * @author algorithm
 */
public class AsyncJokeClient {
    public static void main(String[] args) {

        System.out.println("ASync Santiweide's Joke Client, 0.1.\n");
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("Press Return for joke or proverb: ");
            System.out.print("Getting a joke or proverb from server... ");
            System.out.flush();
            System.out.println(getData());
            System.out.flush();
            System.out.println("Enter numbers to sum: ");
            Integer a= scanner.nextInt();
            Integer b= scanner.nextInt();
            System.out.println("Your Sum is: " + (a + b));
            System.out.flush();
        }
    }


    static String getData() {
        String received = null;
        try {
            DatagramSocket socket = new DatagramSocket();;
            DatagramPacket packet;
            byte[] buf = new byte[255];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            received = new String(packet.getData(), 0, packet.getLength());
        } catch (IOException x) {
            System.out.println("Socket error.");
            x.printStackTrace();
        }
        return received;
    }
}
