package com.bc.server;

import com.bc.model.BlockRecord;

import java.util.concurrent.PriorityBlockingQueue;

/**
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

    }
}
