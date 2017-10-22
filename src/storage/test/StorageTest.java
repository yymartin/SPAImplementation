package storage.test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;
import storage.client.Client;

public class StorageTest {
	public static void main(String[] args) {
		BigInteger id = BigInteger.valueOf(123);
		String password = "password";
	
		KeyPair keys = MyKeyGenerator.generateAsymmetricKey();
		PrivateKey ssk = keys.getPrivate();
		PublicKey svk = keys.getPublic();
		
		KeyPair blindKeys = MyKeyGenerator.generateAsymmetricKey();
		RSAPrivateKey bsk = (RSAPrivateKey) blindKeys.getPrivate();
		PublicKey bvk = blindKeys.getPublic();
		
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(bsk.getModulus());
		BigInteger passwordBlinded = AsymmetricEncryption.blind(new BigInteger(password.getBytes()), r, (RSAPublicKey) svk); 
		BigInteger sig = AsymmetricEncryption.sign(passwordBlinded, bsk);
		
		BigInteger hash = Hash.generateSHA256Hash(sig);
		
		byte[] ctext = ssk.getEncoded();
	}

}
