package com.dut.utils;


import com.dut.model.BlockRecord;

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
                /* CDE For the timestamp in the block entry: */
                try {
                    Thread.sleep(1001);
                } catch (InterruptedException e) {
                }
                String T1 = String.format("%1$s %2$tF.%2$tT", "", new Date());
                // No timestamp collisions!
                String TimeStampString = T1 + "." + processId;
                System.out.println("Timestamp: " + TimeStampString);
                // Will be able to priority sort by TimeStamp
                BlockRecord BR = new BlockRecord();
                BR.setTimeStamp(TimeStampString);
                BR.setBlockID(UUID.randomUUID().toString());
                BR.setBlockIdVersion(0);
                BR.setCreatingProcessId(processId);
                /* CDE put the file data into the block record: */
                tokens = InputLineStr.split(" +");
                BR.setFname(tokens[iFNAME]);
                BR.setLname(tokens[iLNAME]);
                BR.setSSNum(tokens[iSSNUM]);
                BR.setDOB(tokens[iDOB]);
                BR.setDiag(tokens[iDIAG]);
                BR.setTreat(tokens[iTREAT]);
                BR.setRx(tokens[iRX]);
                // todo and optionally an SHA-256 hash of the input data is placed in the DataHash field for auditing purposes—see below under DataHash. Not required!].

                recordList.add(BR);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return recordList;
    }
}