package in.net.kccollege.student.services;

import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Sahil on 13-08-2017.
 */

public class NotificationService extends FirebaseMessagingService {

	private static final String TAG = "NotificationService";
	private String RSS_URL;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
//		super.onMessageReceived(remoteMessage);
		Log.d(TAG, "onMessageReceived: Recieved data");
		Map<String, String> data = remoteMessage.getData();
		switch (data.get("type")) {
			case "rss":
				Log.d(TAG, "onMessageReceived: Data rss");

				GcmNetworkManager networkManager = GcmNetworkManager.getInstance(getApplicationContext());

				OneoffTask task = new OneoffTask.Builder()
						.setService(CheckRssService.class)
						.setTag("KCNotifService")
						.setExecutionWindow(0, 5)
						.setRequiredNetwork(Task.NETWORK_STATE_ANY)
						.build();

				networkManager.schedule(task);

				break;
		}

	}


}
