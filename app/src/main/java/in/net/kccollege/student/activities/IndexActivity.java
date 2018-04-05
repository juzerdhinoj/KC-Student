package in.net.kccollege.student.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.BuildConfig;
import in.net.kccollege.student.R;
import in.net.kccollege.student.db.DatabaseHandler;
import in.net.kccollege.student.fragments.NoticesFragment;
import in.net.kccollege.student.fragments.TimetableFragment;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.KEY_NOTICES;


public class IndexActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {

	private static final String TAG = "IndexActivity";
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	private SharedPreferences sp;
	private String[] fraglist = new String[]{"Notices", "Results", "Timetable", "App Feedback", "About us", "Contact us"};
	private long current = 0;
	private Drawer drawer;
	private Context context;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		ButterKnife.bind(this);
		context = this;

		setSupportActionBar(toolbar);

		sp = getSp();


//		String url = "http://kccollege.net.in/marks/dev.html";
//		String encrypted = EncryptionUtils.decrypt("title","summary", url);
//		System.out.println(encrypted);
//		String decrypted = EncryptionUtils.decrypt("title", "summary", encrypted);
//		System.out.println(decrypted);


		if (sp.getBoolean("notif", false)) {
//			new AlarmReceiver().setAlarm(Index.this);
			FirebaseMessaging.getInstance().subscribeToTopic(sp.getBoolean("guest", false) ? "guest" : "users");
			Log.d("Subscribing", sp.getBoolean("guest", false) ? "guest" : "users");
		}

		updatePreferences();
		checkRun();

		PrimaryDrawerItem notices = new PrimaryDrawerItem()
				.withIdentifier(0)
				.withName("Notices")
				.withIcon(R.drawable.ic_assignment_blue_36dp);
		PrimaryDrawerItem results = new PrimaryDrawerItem()
				.withIdentifier(1)
				.withName("Results")
				.withSelectable(false)
				.withIcon(R.drawable.ic_assessment_blue_36dp);
		PrimaryDrawerItem timetable = new PrimaryDrawerItem()
				.withIdentifier(2)
				.withName("Timetable")
				.withIcon(R.drawable.ic_access_time_blue_36dp);
		SecondaryDrawerItem feedback = new SecondaryDrawerItem()
				.withIdentifier(3)
				.withName("App Feedback")
				.withSelectable(false)
				.withIcon(R.drawable.ic_feedback_blue_36dp);
		SecondaryDrawerItem about = new SecondaryDrawerItem()
				.withIdentifier(4)
				.withName("About us")
				.withSelectable(false)
				.withIcon(R.drawable.ic_info_outline_blue_36dp);
		SecondaryDrawerItem contact = new SecondaryDrawerItem()
				.withIdentifier(5)
				.withName("Contact us")
				.withSelectable(false)
				.withIcon(R.drawable.ic_message_blue_36dp);

		if (sp.getBoolean("guest", false)) {
			results.withEnabled(false);
			timetable.withEnabled(false);
		}


		drawer = new DrawerBuilder()
				.withActivity(this)
				.withToolbar(toolbar)
				.withHeader(R.layout.header)
				.addDrawerItems(
						notices,
						results,
						timetable,
						new DividerDrawerItem(),
						about,
						feedback,
						contact
				)
				.withOnDrawerItemClickListener(this)
				.build();

		NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();

		if (savedInstanceState == null) {
			loadFragment();
		}
	}


	void loadFragment() {
		getSupportActionBar().setTitle(fraglist[(int) current]);

		Fragment fragment = getSelectedFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out);
		fragmentTransaction.replace(R.id.frameLayout, fragment, fraglist[(int) current]);
		fragmentTransaction.commitAllowingStateLoss();
	}

	Fragment getSelectedFragment() {
		switch ((int) current) {
			case 0:
				return NoticesFragment.getInstance();
			case 2:
				return TimetableFragment.getInstance();
			default:
				return NoticesFragment.getInstance();
		}
	}

	@Override
	public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
		if (current != drawerItem.getIdentifier()) {
			switch ((int) drawerItem.getIdentifier()) {
				case 0://notices
				case 2://timetable
					current = drawerItem.getIdentifier();
					loadFragment();
					break;
				case 1://results
					openResult();
					break;
				case 3://feedback


					Intent i2 = new Intent(IndexActivity.this, FeedbackActivity.class);
					startActivity(i2);
					break;
				case 4://about
					showAbout();
					break;
				case 5://contact us
					openContactURL();
					break;
			}
		}

		return false;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (sp.getBoolean("guest", false)) {
			inflater.inflate(R.menu.index_guest, menu);
		} else {
			inflater.inflate(R.menu.index, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.settings:
				Intent i1 = new Intent(IndexActivity.this, SettingsActivity.class);
				startActivity(i1);
				return true;

			case R.id.changepass:
				Intent i3 = new Intent(IndexActivity.this, ChangePasswordActivity.class);
				startActivity(i3);
				return true;

			case R.id.logout:
				Intent i4 = new Intent(context, LoginActivity.class);
				DatabaseHandler.logoutUser(context);
				startActivity(i4);
				finish();
				Toast.makeText(context, getString(R.string.loggedout), Toast.LENGTH_SHORT).show();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	void showAbout() {
		TextView msg = new TextView(context);
		msg.setText(getResources().getString(R.string.about)
				+ "\n\nVersion : "
				+ BuildConfig.VERSION_NAME + "("
				+ BuildConfig.VERSION_CODE
				+ ")");
		msg.setGravity(Gravity.CENTER);
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("About us").setView(msg)
				.setPositiveButton("OK", null);
		AlertDialog alert = adb.create();
		alert.show();
	}


	void updatePreferences() {
//		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.extra_prefs, false);

		if (sp.getInt("version", 0) == 0) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("version", BuildConfig.VERSION_CODE);
			editor.apply();
		} else if (sp.getInt("version", 0) < BuildConfig.VERSION_CODE) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("run", 1);
			editor.apply();
		}
	}

	void checkRun() {

		if (sp.contains("run")) {
			int j = sp.getInt("run", -1);
			if (j == 5 || j % 25 == 0) {
				final AlertDialog.Builder adb = new AlertDialog.Builder(context);
				adb.setTitle("Rate this app")
						.setCancelable(false)
						.setMessage("If you enjoy this app, please take a moment to rate this app on Play Store.\nThank you for your support")
						.setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.net.kccollege.student")));
								sp.edit().putInt("run", -9).apply();
							}
						})
						.setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sp.edit().putInt("run", -9).apply();
							}
						})
						.setNeutralButton("Remind later", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sp.edit().putInt("run", sp.getInt("run", -1) + 1).apply();
							}
						}).create().show();
			} else if (j != -9) {
				sp.edit().putInt("run", j + 1).apply();
			}
		} else {
			sp.edit().putInt("run", 1).apply();
		}
	}

	void openContactURL() {
		String url = getString(R.string.contacturl);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	void openResult() {
		Intent myIntent = new Intent(context, BrowserActivity.class);
		Bundle bundle = new Bundle();

		bundle.putString("type", "link");
		bundle.putString("url", getString(R.string.result));

		myIntent.putExtras(bundle);
		startActivity(myIntent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AndroidNetworking.cancel(KEY_NOTICES);
	}
}
