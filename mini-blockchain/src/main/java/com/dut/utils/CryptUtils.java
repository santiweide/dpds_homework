package com.dut.utils;

import java.security.MessageDigest;

/**
 * @author algorithm
 */
public class CryptUtils {
    public static String byteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (int i = 0; i < ba.length; i++) {
            hex.append(String.format("%02X", ba[i]));
        }
        return hex.toString();
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 生成随机字符串RAND，CAT<-cat(RAND,data), 16bNum<-SHA-256(CAT)最左16位, 如果16bNum<5000就solve了。不然的话就重新生成RAND，直到成功为止。
     * （如果把5000换乘2500，就更难了哦哈哈哈）
     */
    public static void workB(String stringIn){
        String randString = randomAlphaNumeric(8);
        System.out.println("Our example random seed string is: " + randString + "\n");
        System.out.println("Concatenated with the \"data\": " + stringIn + randString + "\n");

        System.out.println("Number will be between 0000 (0) and FFFF (65535)\n");
        int workNumber;
        // Number will be between 0000 (0) and FFFF (65535), here's proof:
        workNumber = Integer.parseInt("0000", 16);
        // Lowest hex value
        System.out.println("0x0000 = " + workNumber);

        workNumber = Integer.parseInt("FFFF", 16);
        System.out.println("0xFFFF = " + workNumber + "\n");

        try {

            for (;;) {
                randString = randomAlphaNumeric(8);
                String concatString = stringIn + randString;
                MessageDigest MD = MessageDigest.getInstance("SHA-256");
                byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8"));

                // stringOut = DatatypeConverter.printHexBinary(bytesHash);
                String stringOut = byteArrayToString(bytesHash);
                System.out.println("Hash is: " + stringOut);
                // Between 0000 (0) and FFFF (65535)
                workNumber = Integer.parseInt(stringOut.substring(0, 4), 16);
                System.out.println("First 16 bits in Hex and Decimal: " + stringOut.substring(0, 4) + " and " + workNumber);
                if (!(workNumber < 20000)) {
                    System.out.format("%d is not less than 20,000 so we did not solve the puzzle\n\n", workNumber);
                }
                if (workNumber < 20000) {
                    System.out.format("%d IS less than 20,000 so puzzle solved!\n", workNumber);
                    System.out.println("The seed (puzzle answer) was: " + randString);
                    break;
                }
                // Here is where you would periodically check to see if the blockchain has been updated
                // ...if so, then abandon this verification effort and start over.
                // Here is where you will sleep if you want to extend the time up to a second or two.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
