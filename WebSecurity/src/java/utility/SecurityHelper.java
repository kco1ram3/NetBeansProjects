package utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityHelper {

    public static String getSalt() {
        /*Random rand = new Random((new Date()).getTime());
         byte[] salt = new byte[16];
         rand.nextBytes(salt);
         return new String(salt);*/
        /*
         String regEx = "[!-z]{16}";
         Xeger gen = new Xeger(regEx);
         return gen.generate();
         */
        SecureRandom random = new SecureRandom();
        return new String(random.generateSeed(16));
    }

    public static String MD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    public static String encrypt(String salt, String message) {
        try {
            return hash256((salt + message));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean checkPasswordStrength(String password) {
        int strengthPercentage = 0;
        String[] partialRegexChecks = {
            ".*[a-z]+.*", // lower
            ".*[A-Z]+.*", // upper
            ".*[0-9]+.*", // digits
            ".*[!@#$%^&*?_~]+.*" // symbols
        };
        if (password.matches(partialRegexChecks[0])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[1])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[2])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[3])) {
            strengthPercentage += 25;
        }
        return strengthPercentage >= 100;
    }
    
    public static String generateSessionKey(int length) {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int n = alphabet.length();
        String result = new String();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            result = result + alphabet.charAt(r.nextInt(n));
        }
        return result;
    }
}
