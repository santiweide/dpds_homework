package com.dut.model;


import java.io.Serializable;


public class BlockRecord implements Serializable{
    /* Examples of block fields. You should pick, and justify, your own set: */
    Integer BlockIdVersion;
    Integer CreatingProcessId;

    String BlockID;
    String TimeStamp;
    String VerificationProcessID;
    String PreviousHash; // We'll copy from previous block
    String Fname;
    String Lname;
    String SSNum;
    String DOB;
    String RandomSeed; // Our guess. Ultimately our winning guess.
    String WinningHash;
    String Diag;
    String Treat;
    String Rx;

    public Integer getBlockIdVersion() {
        return BlockIdVersion;
    }

    public void setBlockIdVersion(Integer blockIdVersion) {
        this.BlockIdVersion = blockIdVersion;
    }

    public Integer getCreatingProcessId() {
        return CreatingProcessId;
    }

    public void setCreatingProcessId(Integer creatingProcessId) {
        CreatingProcessId = creatingProcessId;
    }
    /* Examples of accessors for the BlockRecord fields: */
    public String getBlockID() {
        return BlockID;
    }

    public void setBlockID(String BID) {
        this.BlockID = BID;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String TS) {
        this.TimeStamp = TS;
    }

    public String getVerificationProcessID() {
        return VerificationProcessID;
    }

    public void setVerificationProcessID(String VID) {
        this.VerificationProcessID = VID;
    }

    public String getPreviousHash() {
        return this.PreviousHash;
    }

    public void setPreviousHash(String PH) {
        this.PreviousHash = PH;
    }


    public String getLname() {
        return Lname;
    }

    public void setLname(String LN) {
        this.Lname = LN;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String FN) {
        this.Fname = FN;
    }

    public String getSSNum() {
        return SSNum;
    }

    public void setSSNum(String SS) {
        this.SSNum = SS;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String RS) {
        this.DOB = RS;
    }

    public String getDiag() {
        return Diag;
    }

    public void setDiag(String D) {
        this.Diag = D;
    }

    public String getTreat() {
        return Treat;
    }

    public void setTreat(String Tr) {
        this.Treat = Tr;
    }

    public String getRx() {
        return Rx;
    }

    public void setRx(String Rx) {
        this.Rx = Rx;
    }

    public String getRandomSeed() {
        return RandomSeed;
    }

    public void setRandomSeed(String RS) {
        this.RandomSeed = RS;
    }

    public String getWinningHash() {
        return WinningHash;
    }

    public void setWinningHash(String WH) {
        this.WinningHash = WH;
    }

}

