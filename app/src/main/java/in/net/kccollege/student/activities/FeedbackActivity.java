package in.net.kccollege.student.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.R;
import in.net.kccollege.student.db.DatabaseHandler;
import in.net.kccollege.student.model.UserDetails;
import in.net.kccollege.student.utils.InternetUtils;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CONNECTION_ERROR;
import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_FEEDBACK;
import static in.net.kccollege.student.utils.Constants.KEY_MESSAGE;
import static in.net.kccollege.student.utils.Constants.KEY_NAME;
import static in.net.kccollege.student.utils.Constants.KEY_TAG;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.EditTextWatcher;
import static in.net.kccollege.student.utils.GeneralUtils.OnBackPressListener;
import static in.net.kccollege.student.utils.GeneralUtils.ProgressD;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;
import static in.net.kccollege.student.utils.GeneralUtils.validateEmail;
import static in.net.kccollege.student.utils.GeneralUtils.validateFeedback;
import static in.net.kccollege.student.utils.GeneralUtils.validateName;


public class FeedbackActivity extends AppCompatActivity {

	@BindView(R.id.name)
	TextInputEditText name;

	@BindView(R.id.email)
	TextInputEditText email;

	@BindView(R.id.feedback)
	TextInputEditText feedback;

	@BindView(R.id.lay_name)
	TextInputLayout lay_name;

	@BindView(R.id.lay_email)
	TextInputLayout lay_email;

	@BindView(R.id.lay_feedback)
	TextInputLayout lay_feedback;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private SharedPreferences sp;
	private ProgressD pd;
	private Context context;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		context = this;
		sp = getSp();

		UserDetails ud = new DatabaseHandler(context).getUserDetails();
		email.setText(ud != null ? ud.getEmail() : "");

		email.addTextChangedListener(new EditTextWatcher(email, lay_email, KEY_EMAIL, context));
		name.addTextChangedListener(new EditTextWatcher(name, lay_name, KEY_NAME, context));
		feedback.addTextChangedListener(new EditTextWatcher(feedback, lay_feedback, KEY_FEEDBACK, context));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feedback, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.send:
				if (validateName(context, name, lay_name) &&
						validateEmail(context, email, lay_email) &&
						validateFeedback(context, feedback, lay_feedback)) {

					processFeedback();
					return true;
				}
		}
		return super.onOptionsItemSelected(item);
	}

	private void processFeedback() {


		Map<String, String> request = new HashMap<>();
		request.put(KEY_TAG, KEY_FEEDBACK);
		request.put(KEY_NAME, name.getText().toString());
		request.put(KEY_EMAIL, email.getText().toString());
		request.put(KEY_MESSAGE, feedback.getText().toString());


		pd = new ProgressD(context, "Sending feedback...", "Please wait...");
		pd.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel(KEY_FEEDBACK);
			}
		});
		pd.show();

		InternetUtils.postData(getString(R.string.feedback_url),
				KEY_FEEDBACK,
				request,
				new JSONObjectRequestListener() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("Success", "onResponse: " + response.toString());
						pd.dismiss();

						AlertDialog.Builder adb = new AlertDialog.Builder(context);

						adb.setTitle("Thank you!");
						adb.setMessage("Your feedback has been sent.");
						adb.setCancelable(false);
						adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								finish();
							}
						});
						adb.create().show();


					}

					@Override
					public void onError(ANError anError) {
						pd.dismiss();
						anError.printStackTrace();
						switch (anError.getErrorDetail()) {
							case CONNECTION_ERROR:
							case RESPONSE_FROM_SERVER_ERROR:
								toastInternetError(context);
								break;

							case PARSE_ERROR:
								toastUnknownError(context);
								break;
						}
					}
				});

	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
}


