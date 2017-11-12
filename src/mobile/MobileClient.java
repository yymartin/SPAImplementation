package mobile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class MobileClient{
	
	public static void executeRegistration() {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(new MobileClientRegistrationThread());
	}
}
