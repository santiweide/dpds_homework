package com.bc.utils;


import com.bc.Ports;
import com.bc.model.BlockRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class BlockInputG {

    private static String FILENAME;

    Queue<BlockRecord> ourPriorityQueue = new PriorityQueue<>(4, BlockTSComparator);


    /* Token indexes for input: */
    private static final int iFNAME = 0;
    private static final int iLNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAG = 4;
    private static final int iTREAT = 5;
    private static final int iRX = 6;

    public static void main(String argv[]) {
        BlockInputG s = new BlockInputG(argv);
        s.run(argv);
    }

    public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>() {
        @Override
        public int compare(BlockRecord b1, BlockRecord b2) {
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {
                return 0;
            }
            if (s1 == null) {
                return -1;
            }
            if (s2 == null) {
                return 1;
            }
            return s1.compareTo(s2);
        }
    };


    public BlockInputG(String argv[]) {
        System.out.println("In the constructor...");
    }

    public void run(String argv[]) {

        System.out.println("Running now\n");
        try {
            getBlockRecordFromFile(0);
        } catch (Exception x) {
        }
        ;
    }

    public static List<BlockRecord> getBlockRecordFromFile(int processId) throws Exception {

        List<BlockRecord> recordList = new LinkedList<>();
        switch (processId) {
            case 1:
                FILENAME = "BlockInput1.txt";
                break;
            case 2:
                FILENAME = "BlockInput2.txt";
                break;
            default:
                FILENAME = "BlockInput0.txt";
                break;
        }

        System.out.println("Using input file: " + FILENAME);

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String[] tokens;
            String InputLineStr;
            String suuid;

            while ((InputLineStr = br.readLine()) != null) {

                BlockRecord BR = new BlockRecord();

                /* CDE For the timestamp in the block entry: */
                try {
                    Thread.sleep(1001);
                } catch (InterruptedException e) {
                }
                Date date = new Date();
                // String T1 = String.format("%1$s %2$tF.%2$tT", "Timestamp:", date);
                String T1 = String.format("%1$s %2$tF.%2$tT", "", date);
                // No timestamp collisions!
                String TimeStampString = T1 + "." + processId;
                System.out.println("Timestamp: " + TimeStampString);
                // Will be able to priority sort by TimeStamp
                BR.setTimeStamp(TimeStampString);

                /* CDE: Generate a unique blockID. This would also be signed by creating process: */
                suuid = UUID.randomUUID().toString();
                BR.setBlockID(suuid);
                /* CDE put the file data into the block record: */
                tokens = InputLineStr.split(" +");
                BR.setFname(tokens[iFNAME]);
                BR.setLname(tokens[iLNAME]);
                BR.setSSNum(tokens[iSSNUM]);
                BR.setDOB(tokens[iDOB]);
                BR.setDiag(tokens[iDIAG]);
                BR.setTreat(tokens[iTREAT]);
                BR.setRx(tokens[iRX]);

                recordList.add(BR);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return recordList;
    }
}