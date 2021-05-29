package com.dut.server;

import com.dut.BlockChain;
import com.dut.Ports;
import com.dut.model.BlockRecord;
import com.dut.utils.CryptUtils;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Each process then, one by one, pops the unverified blocks from the priority queue,
 * attempts to solve the puzzle and verify the block in competition with the other processes.
 *
 * @author algorithm
 */
public class UnverifiedBlockConsumer implements Runnable {
    PriorityBlockingQueue<BlockRecord> queue;
    int PID;

    public UnverifiedBlockConsumer(PriorityBlockingQueue<BlockRecord> queue, int pid) {
        this.queue = queue;
        PID = pid;
    }

    @Override
    public void run() {

        BlockRecord tempRec;
        PrintStream toBlockChainServer;
        Socket BlockChainSock;
        String newblockchain;
        String fakeVerifiedBlock;
        Random r = new Random();

        System.out.println("Starting the Unverified Block Priority Queue Consumer thread.\n");
        try {
            while (true) {
                tempRec = queue.take();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tempRec.getLname()).append(tempRec.getFname())
                        .append(tempRec.getSSNum()).append(tempRec.getDOB()).append(tempRec.getDiag()).append(tempRec.getTreat());
                String data = stringBuilder.toString();
                // System.out.println("Consumer got unverified: " + data);

	/* Ordindarily we would do real work here to verify the UVB. Also, we would periodically want
	   to check to see whether some other proc has already verified this UVB. If so, stop the work and start
	   again on the next UVB in the queue. */

                CryptUtils.workB(data);


	/* With duplicate blocks that have already been verified by different procs and placed in the blockchain,
	   ordinarily we would keep only the one with the lowest verification timestamp. For the exmple we use a
	   crude filter, which also may let some dups through */
                // if(bc.blockchain.indexOf(data.substring(1, 9)) > 0){System.out.println("Duplicate: " + data);}

                if (BlockChain.blockchain.indexOf(data.substring(1, 9)) < 0) { // Crude, but excludes most duplicates.
                    fakeVerifiedBlock = "[" + data + " verified by P" + BlockChain.PID + " at time "
                            + Integer.toString(ThreadLocalRandom.current().nextInt(100, 1000)) + "]\n";
                    // System.out.print("Fake verified block: " + fakeVerifiedBlock);
                    String tempblockchain = fakeVerifiedBlock + BlockChain.blockchain;

                    for (int i = 0; i < BlockChain.numProcesses; i++) {
                        BlockChainSock = new Socket(BlockChain.serverName, Ports.BlockchainServerPortBase + (i * 1000));
                        toBlockChainServer = new PrintStream(BlockChainSock.getOutputStream());
                        toBlockChainServer.println(tempblockchain);
                        toBlockChainServer.flush();
                        BlockChainSock.close();
                    }
                }
                Thread.sleep(1500); // For the example, wait for our blockchain to be updated before processing a new block
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
