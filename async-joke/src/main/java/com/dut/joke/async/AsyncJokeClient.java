package com.dut.joke.async;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author algorithm
 */
public class AsyncJokeClient {
    public static void main(String[] args) {

        try {
            DatagramSocket socket = new DatagramSocket(0);

            System.out.println("ASync Santiweide's Joke Client, 0.1.\n");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press Return for joke or proverb: ");
            System.out.flush();

            String str = scanner.nextLine();
            Integer port = socket.getLocalPort();
            System.out.println("'Client port: "+ port);
            byte[] buf = port.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 4945);
            socket.send(packet);

            System.out.println("Getting a joke or proverb from server... ");
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Get from server: " + received);
            System.out.flush();

            System.out.println("Enter numbers to sum: ");
            Integer a = scanner.nextInt();
            Integer b = scanner.nextInt();
            System.out.println("Your Sum is: " + (a + b));
            System.out.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
