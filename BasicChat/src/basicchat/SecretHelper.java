package basicchat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class SecretHelper {
    
    private static final String algorithm = "AES";
    private static final String transformation = "AES/ECB/PKCS5Padding";
    
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(algorithm).generateKey();
    }
    
    public static SecretKey regenerateKey(byte[] encodedKey, int length) {
        return new SecretKeySpec(encodedKey,0 , length, algorithm);
    }

    public static byte[] EncryptString(String data, SecretKey key) {
        return EncryptBytes(data.getBytes(), key);
    }

    public static byte[] EncryptBytes(byte[] data, SecretKey key) {
        
        byte[] encryptedMsg = null;
        
        Cipher encryptCipher;
        
        try {
            encryptCipher = Cipher.getInstance(transformation);
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryptCipher)) {
                cipherOutputStream.write(data);
                cipherOutputStream.flush();
            }

            encryptedMsg = outputStream.toByteArray();
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException ex) {
            Logger.getLogger(SecretHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return encryptedMsg;
    }
    
    public static String DecryptString(byte[] data, int length, SecretKey key) {
        return new String(DecryptBytes(data, length, key));
    }
    
    public static byte[] DecryptBytes(byte[] data, int length, SecretKey key) {

        byte[] decryptedBytes = null;
        Cipher decryptCipher;
        
        try {
            decryptCipher = Cipher.getInstance(transformation);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data, 0, length);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, decryptCipher);
            
            byte[] buffer = new byte[4096];
            int byteRead;
            while ((byteRead = cipherInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, byteRead);
            }

            decryptedBytes = outputStream.toByteArray();
        } 
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException ex) {
            Logger.getLogger(SecretHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return decryptedBytes;
    }
    
    public static String BytesToStringBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public static byte[] StringBase64ToBytes(String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
    
    public static byte[] MD5Hash(byte[] data) {
        byte[] hashvalue = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hashvalue = md.digest(data);
        } 
        catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecretHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hashvalue;        
    }
    
    public static byte[] MD5HashToBytes(String data) {
        byte[] hashvalue = MD5Hash(data.getBytes());        
        return hashvalue;
    }

    public static String MD5HashToString(String data) {
        byte[] bytevalue = MD5HashToBytes(data);
        String hashvalue = new BigInteger(1, bytevalue).toString(16);
        return hashvalue;
    }
    
    public static byte[] MD5HashToBytes(InputStream inputStream) {
        byte[] hashvalue = null;
        
        try {
            byte[] buffer = new byte[4096];

            MessageDigest md = MessageDigest.getInstance("MD5");
            int numRead;

            while ((numRead = inputStream.read(buffer)) >= 0) {
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            }

            hashvalue = md.digest();
        }
        catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(SecretHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hashvalue;
    }

    public static String MD5HashToString(InputStream inputStream) {
        byte[] bytevalue = MD5HashToBytes(inputStream);
        String hashvalue = new BigInteger(1, bytevalue).toString(16);
        return hashvalue;
    }
}
