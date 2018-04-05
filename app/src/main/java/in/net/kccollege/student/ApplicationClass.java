package in.net.kccollege.student;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Sahil on 11-07-2017.
 */

public class ApplicationClass extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "ApplicationClass";
	private static FirebaseAnalytics mFirebaseAnalytics;
	private static SharedPreferences sp;

	public static SharedPreferences getSp() {
		return sp;
	}

	public static FirebaseAnalytics getmFirebaseAnalytics() {
		return mFirebaseAnalytics;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		AndroidNetworking.initialize(this);
		Fabric.with(this, new Crashlytics(), new Answers());
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		sp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("notif")) {

			if (sharedPreferences.getBoolean("notif", false)) {
				FirebaseMessaging.getInstance().subscribeToTopic(sp.getBoolean("guest", false) ? "guest" : "users");
				Log.d(TAG, "onSharedPreferenceChanged: Subbed " + (sp.getBoolean("guest", false) ? "guest" : "users"));

			} else {
				FirebaseMessaging.getInstance().unsubscribeFromTopic(sp.getBoolean("guest", false) ? "guest" : "users");
				Log.d(TAG, "onSharedPreferenceChanged: UnSubbed " + (sp.getBoolean("guest", false) ? "guest" : "users"));
			}
		}
	}
}
