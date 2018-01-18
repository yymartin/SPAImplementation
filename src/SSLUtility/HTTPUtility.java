package SSLUtility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author yoanmartin
 *	Class which implements a method to execute an HTTP POST request
 */
public class HTTPUtility {
	/**
	 * Function which execute an HTTP POST request
	 * @param url The URL of the website
	 * @param data The data to send to the website
	 * @return A string containing the response of the server
	 */
	public static String executePost(String url, Map<String, String> data) {	

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> elem : data.entrySet()) {
			params.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String responseString = null;
		try {
			responseString = new BasicResponseHandler().handleResponse(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}
}
