package in.net.kccollege.student.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import in.net.kccollege.student.R;
import in.net.kccollege.student.activities.IndexActivity;
import in.net.kccollege.student.utils.InternetUtils;
import in.net.kccollege.student.utils.RssHelper;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CHANNEL_ID;
import static in.net.kccollege.student.utils.Constants.JOB_ID;
import static in.net.kccollege.student.utils.Constants.KEY_ENTRIES;
import static in.net.kccollege.student.utils.Constants.KEY_NOTICES;
import static in.net.kccollege.student.utils.Constants.KEY_TITLE;

/**
 * Created by Sahil on 14-03-2018.
 */

public class CheckRssService extends JobService {

	private static final String TAG = "CheckRssService";
	private String RSS_URL;

	@Override
	public boolean onStartJob(final JobParameters params) {
		SharedPreferences sp = getSp();
		RSS_URL = getString(sp.getBoolean("guest", false) ? R.string.link_guest : R.string.link);

		Log.d(TAG, "onStartJob: JOB started");

		InternetUtils.getData(RSS_URL,
				KEY_NOTICES,
				new JSONObjectRequestListener() {
					@Override
					public void onResponse(JSONObject response) {

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

						jobFinished(params, false);
					}

					@Override
					public void onError(ANError anError) {
						anError.printStackTrace();
						jobFinished(params, true);
					}
				});


		return true;
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
	public boolean onStopJob(JobParameters params) {
		AndroidNetworking.cancel(KEY_NOTICES);
		return false;
	}
}
