package mobile;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class MobileClient{
	
	public static String executeRegistration() {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		Future<String> address = ex.submit(new MobileClientRegistrationThread());
		String addressValue = null;
		try {
			addressValue = address.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return addressValue;
	}
}
