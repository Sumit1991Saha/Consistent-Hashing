package Hashing;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
public class MD5HashFunction implements HashFunction {
    public Long getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            BigInteger modulo = new BigInteger("100000000");
            return (number.mod(modulo)).longValue();
            /*String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;*/
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
 
    /*public static void main(String[] args) throws NoSuchAlgorithmException {
        MD5HashFunction hashing = new MD5HashFunction();
    	System.out.println(hashing.getHash("hello"));
    }*/
}
