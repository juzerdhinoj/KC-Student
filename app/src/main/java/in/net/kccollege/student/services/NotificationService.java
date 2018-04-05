package in.net.kccollege.student.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static in.net.kccollege.student.utils.Constants.JOB_ID;

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
				JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
				ComponentName jobService = new ComponentName(getPackageName(), CheckRssService.class.getName());
				JobInfo jobInfo = new JobInfo.Builder(JOB_ID, jobService)
						.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
						.build();

				int jobId = jobScheduler.schedule(jobInfo);
				if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
					Log.d(TAG, "onMessageReceived: JOB SCHEDULED");

				} else {
					Log.d(TAG, "onMessageReceived: JOB FAILED");
				}
				break;
		}

	}


}
