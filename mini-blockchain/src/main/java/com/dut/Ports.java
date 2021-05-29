package com.dut;

/**
 * @author algorithm
 */
public class Ports {
    /**
     * Port 4710+process number receives public keys (4710, 4711, 4712)
     */
    public static int KeyServerPortBase = 4710;
    /**
     * Port 4820+process number receives unverified blocks (4820, 4821, 4822)
     */
    public static int UnverifiedBlockServerPortBase = 4820;
    /**
     * Port 4930+process number receives updated blockchains (4930, 4931, 4932)
     */
    public static int BlockchainServerPortBase = 4930;

    public static int KeyServerPort;
    public static int UnverifiedBlockServerPort;
    public static int BlockchainServerPort;

    public void setPorts() {
        KeyServerPort = KeyServerPortBase + BlockChain.PID;
        UnverifiedBlockServerPort = UnverifiedBlockServerPortBase + BlockChain.PID;
        BlockchainServerPort = BlockchainServerPortBase + BlockChain.PID;
    }
}