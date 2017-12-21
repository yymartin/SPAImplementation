package mobile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;
public class MobileClient{
	
	public static void executeRegistration(SecretKey K, String password) {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(new MobileClientRegistrationThread(K, password));
	}
}
