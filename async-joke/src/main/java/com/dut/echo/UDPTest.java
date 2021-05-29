package com.dut.echo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author algorithm
 */
public class UDPTest {
    EchoClient client;

    @Before
    public void setup(){
        new EchoServer().start();
        client = new EchoClient();
    }

    @Test
    public void whenCanSendAndReceivePacketThenCorrect() {
        String echo = null;
        try {
            echo = client.sendEcho("hello server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("hello server", echo);
        try {
            echo = client.sendEcho("server is working");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotEquals("hello server", echo);
    }

    @After
    public void tearDown() {
        try {
            client.sendEcho("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.close();
    }
}