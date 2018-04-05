package in.net.kccollege.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.activities.IndexActivity;
import in.net.kccollege.student.activities.LoginActivity;
import in.net.kccollege.student.db.DatabaseHandler;

import static in.net.kccollege.student.ApplicationClass.getSp;

public class Splash extends AppCompatActivity {

	@BindView(R.id.image)
	ImageView imageView;
	@BindView(R.id.ver)
	TextView ver;

	CountDownTimer timer;
	Intent intent;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ButterKnife.bind(this);

		Glide.with(this)
				.load(R.drawable.splash)
				.into(imageView);

		ver.setText(String.format("Ver %s", BuildConfig.VERSION_NAME));

		DatabaseHandler db = new DatabaseHandler(Splash.this);
		SharedPreferences sp = getSp();

		if (db.getRowCount() != 0 || sp.getBoolean("guest", false)) {
			intent = new Intent(Splash.this, IndexActivity.class);
		} else {
			intent = new Intent(Splash.this, LoginActivity.class);
		}

		timer = new CountDownTimer(5000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {

				startActivity(intent);

				finish();
			}


		};

		timer.start();
	}

	@Override
	public void onBackPressed() {
		timer.cancel();
		super.onBackPressed();
	}
}
