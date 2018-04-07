package in.net.kccollege.student.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONArray;
import org.json.JSONObject;

import in.net.kccollege.student.R;
import in.net.kccollege.student.activities.IndexActivity;
import in.net.kccollege.student.utils.RssHelper;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;
import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CHANNEL_ID;
import static in.net.kccollege.student.utils.Constants.JOB_ID;
import static in.net.kccollege.student.utils.Constants.KEY_ENTRIES;
import static in.net.kccollege.student.utils.Constants.KEY_NOTICES;
import static in.net.kccollege.student.utils.Constants.KEY_TITLE;

public class CheckRssService extends GcmTaskService {

	private static final String TAG = "CheckRssServiceGCM";
	private String RSS_URL;

	@Override
	public int onRunTask(TaskParams taskParams) {

		int result = RESULT_SUCCESS;

		SharedPreferences sp = getSp();
		RSS_URL = getString(sp.getBoolean("guest", false) ? R.string.link_guest : R.string.link);

		Log.d(TAG, "onStartJob: JOB started");


		ANRequest request = AndroidNetworking.get(RSS_URL)
				.setTag(KEY_NOTICES)
				.getResponseOnlyFromNetwork()
				.doNotCacheResponse()
				.build();

		ANResponse<JSONObject> anResponse = request.executeForJSONObject();
		if (anResponse.isSuccess()) {

			JSONObject response = anResponse.getResult();

			Log.d(TAG, "onResponse: SERVER RESPONDED");

			JSONObject offlineData = RssHelper.getOfflineRssFile(CheckRssService.this);
			String onlineData = response.toString();
			Log.d("Offline data", offlineData.toString() + "\n\nLength:" + offlineData.toString().length());
			Log.d("Online data", onlineData + "\n\nLength:" + onlineData.length());
			if (offlineData.toString().length() != onlineData.length()) {
				Log.d("Data is", "different");

				JSONArray entries = response.optJSONArray(KEY_ENTRIES);
				JSONObject firstItem = entries.optJSONObject(0);
				sendNotification(firstItem.optString(KEY_TITLE));

				RssHelper.saveOfflineRssFile(CheckRssService.this, onlineData);

			} else {
				Log.d("JSONData is", "same");
			}

		} else {
			anResponse.getError().printStackTrace();
			result = RESULT_RESCHEDULE;
		}

		return result;
	}

	public void initChannels(Context context) {
		Log.d(TAG, "initChannels: init channels");
		if (Build.VERSION.SDK_INT < 26) {
			return;
		}
		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
				getString(R.string.channel_name),
				NotificationManager.IMPORTANCE_DEFAULT);
		channel.setDescription(getString(R.string.channel_description));
		notificationManager.createNotificationChannel(channel);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "sendNotification: sending notif");
		initChannels(CheckRssService.this);

		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, IndexActivity.class).putExtra("from_notif", true), 0);


		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this, CHANNEL_ID)
						.setSmallIcon(R.drawable.hdpi)
						.setContentTitle("KC College")
						.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(msg))
						.setContentText(msg)
						.setSound(uri);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(JOB_ID, mBuilder.build());
	}


	@Override
	public void onDestroy() {
		AndroidNetworking.cancel(KEY_NOTICES);
		super.onDestroy();
	}
}
