package com.google.android.gms.samples.vision.barcodereader.cryptography;

        import java.math.BigInteger;
        import java.security.InvalidKeyException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.security.spec.InvalidKeySpecException;
        import java.security.spec.KeySpec;
        import java.util.Random;

        import javax.crypto.BadPaddingException;
        import javax.crypto.Cipher;
        import javax.crypto.IllegalBlockSizeException;
        import javax.crypto.Mac;
        import javax.crypto.NoSuchPaddingException;
        import javax.crypto.SecretKey;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.PBEKeySpec;
        import javax.crypto.spec.SecretKeySpec;

/**
 * @author yoanmartin
 * Library containing cryptographic functions concerning hash
 */
public class Hash {
    /**
     * Function which generate a SHA256 hash
     * @param number The BigInteger to has
     * @return A hash of the number as a BigInteger
     */
    public static BigInteger generateSHA256Hash(byte[] number) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] hash = digest.digest(number);
        return new BigInteger(hash);
    }

    /**
     * Function which sign a message using HMacSHA256
     * @param message The message to be signed
     * @param key The HMacSHA256 key
     * @return The signed message as a byte array
     */
    public static BigInteger generateHMac(BigInteger message, SecretKey key) {
        byte[] finalHmac = null;
        try {
            Mac generator = Mac.getInstance("HmacSHA256");
            generator.init(key);
            finalHmac = generator.doFinal(message.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new BigInteger(finalHmac);
    }

    /**
     * Function which decrypt a message using one time padding encryption
     * @param cipherText The message encrypted as a byte array
     * @param key The key as a byte array
     * @return The decrypted message as a BigInteger
     */
    public static byte[] decryptOneTimePadding(byte[] cipherText, byte[] key) {
        byte[] decrypted = new byte[cipherText.length];

        for(int i = 0; i < decrypted.length; i++) {
            decrypted[i] = (byte) (cipherText[i] ^ key[i]);
        }

        return decrypted;
    }

    /**
     * Function which generates an AES key from a given password. The password should be hashed, so it should be a BigInteger
     * @param password The hashed password
     * @return A SecretKey for AES encryption
     */
    public static SecretKey generateHMacKeyFromChallenge(BigInteger password) {
        String passwordAsString = String.valueOf(password);
        SecretKeyFactory f = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        KeySpec spec = new PBEKeySpec(passwordAsString.toCharArray(), "predefinedsalt".getBytes(), 10, 128);
        SecretKey s = null;
        try {
            s = f.generateSecret(spec);
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new SecretKeySpec(s.getEncoded(), "HMacSHA256");
    }
}

