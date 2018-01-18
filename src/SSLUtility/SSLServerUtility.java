package SSLUtility;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

//https://github.com/gpotter2/SSLKeystoreFactories
/**
 * @author yoanmartin
 * Utility class which creates an SSL connection with a client
 */
public class SSLServerUtility {
	/**
	 * Function which creates an SSL Socket to connect with a client
	 * @param port The port used by the client
	 * @param pathToCert An InputStream containing the certificate
	 * @param passwordFromCert The password of the certificate
	 * @return The SSL Socket
	 */
	public static SSLServerSocket getServerSocketWithCert(int port, InputStream pathToCert, String passwordFromCert) {
		TrustManager[] trustManagerArray = new TrustManager[1];
		KeyManager[] keyManagerArray = new KeyManager[1];
		KeyStore keyStore;
		SSLServerSocket socketServer = null;
		try {
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(pathToCert, passwordFromCert.toCharArray());
			trustManagerArray[0] = getTrustManager(keyStore);
			keyManagerArray[0] = getKeyManager(keyStore, passwordFromCert);
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(keyManagerArray, trustManagerArray, null);
			SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) context.getServerSocketFactory();
			socketServer = (SSLServerSocket) socketFactory.createServerSocket(port);
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
		return socketServer;
	}
	
	private static X509TrustManager getTrustManager(KeyStore keystore){
		TrustManagerFactory trustManagerFactory;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keystore);
			TrustManager trustManagers[] = trustManagerFactory.getTrustManagers();
			for (int i = 0; i < trustManagers.length; i++) {
				if (trustManagers[i] instanceof X509TrustManager) {
					return (X509TrustManager) trustManagers[i];
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
	};


	private static X509KeyManager getKeyManager(KeyStore keystore, String password){
		KeyManagerFactory keyManagerFactory;
		try {
			keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, password.toCharArray());
			KeyManager keyManagers[] = keyManagerFactory.getKeyManagers();
			for (int i = 0; i < keyManagers.length; i++) {
				if (keyManagers[i] instanceof X509KeyManager) {
					return (X509KeyManager) keyManagers[i];
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	};
}
