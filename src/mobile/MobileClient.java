package mobile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;
/**
 * @author yoanmartin
 * Object which instantiates the client connection with a mobile application
 */
public class MobileClient{
	
	/**
	 * Function which creates a connection with a compatible mobile application
	 * @param K The SecretKey to send
	 * @param password The password used to encrypt K
	 */
	public static void executeRegistration(SecretKey K, String password) {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(new MobileClientRegistrationThread(K, password));
	}
}
