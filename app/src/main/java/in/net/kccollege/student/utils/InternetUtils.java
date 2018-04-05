package in.net.kccollege.student.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import java.util.Map;

/**
 * Created by Sahil on 11-07-2017.
 */

public class InternetUtils {


	public static void getData(String url, String tag, JSONObjectRequestListener listener) {
		AndroidNetworking.get(url)
				.setTag(tag)
				.getResponseOnlyFromNetwork()
				.doNotCacheResponse()
				.build()
				.getAsJSONObject(listener);
	}

	public static void postData(String url, String tag, Map<String, String> body, JSONObjectRequestListener listener) {
		AndroidNetworking.post(url)
				.setTag(tag)
				.addBodyParameter(body)
				.getResponseOnlyFromNetwork()
				.doNotCacheResponse()
				.build()
				.getAsJSONObject(listener);
	}

}
