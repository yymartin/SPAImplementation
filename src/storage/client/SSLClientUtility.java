package storage.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
//https://github.com/gpotter2/SSLKeystoreFactories
public class SSLClientUtility {	
	public static SSLSocket getSocketWithCert(InetAddress ip, int port, InputStream pathToCert, String passwordFromCert) {
		X509TrustManager[] trustManager;
		KeyStore keyStore;
		SSLSocket socket = null;
		try {
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(pathToCert, passwordFromCert.toCharArray());
			trustManager = getTrustManager(keyStore);
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, trustManager, null);
			SSLSocketFactory SocketFactory = context.getSocketFactory();
			socket = (SSLSocket) SocketFactory.createSocket();
			socket.connect(new InetSocketAddress(ip, port), 5000);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}
	
	private static X509TrustManager[] getTrustManager(KeyStore keystore) {
		TrustManagerFactory trustManagerFactory;
		TrustManager trustManagers[];
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keystore);
			trustManagers = trustManagerFactory.getTrustManagers();
			for (TrustManager trustManager : trustManagers) {
				if (trustManager instanceof X509TrustManager) {
					X509TrustManager[] trustManagerArray = new X509TrustManager[1];
					trustManagerArray[0] = (X509TrustManager) trustManager;
					return trustManagerArray;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
