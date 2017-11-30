package SSLUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class HTTPUtility {
	public static String executePost(String url, Map<String, String> data) throws ClientProtocolException, IOException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> elem : data.entrySet()) {
			params.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);

		String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
	}
}
