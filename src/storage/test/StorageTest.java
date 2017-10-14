package storage.test;

import java.math.BigInteger;

import storage.client.Client;

public class StorageTest {
	public static void main(String[] args) {
		//Be sure that the server is running
		BigInteger result = Client.getValuesFromId(BigInteger.valueOf(123456));
		System.out.println(result);
	}

}
