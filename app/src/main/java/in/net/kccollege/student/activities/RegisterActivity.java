package in.net.kccollege.student.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import static in.net.kccollege.student.utils.Constants.KEY_CREATED;
import static in.net.kccollege.student.utils.Constants.KEY_DET;
import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_NAME;
import static in.net.kccollege.student.utils.Constants.KEY_PASS;
import static in.net.kccollege.student.utils.Constants.KEY_REGISTER;
import static in.net.kccollege.student.utils.Constants.KEY_STATUS;
import static in.net.kccollege.student.utils.Constants.KEY_TAG;
import static in.net.kccollege.student.utils.Constants.KEY_UNIQUE;
import static in.net.kccollege.student.utils.Constants.KEY_USER;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.EditTextWatcher;
import static in.net.kccollege.student.utils.GeneralUtils.OnBackPressListener;
import static in.net.kccollege.student.utils.GeneralUtils.ProgressD;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;
import static in.net.kccollege.student.utils.GeneralUtils.validateConfPassword;
import static in.net.kccollege.student.utils.GeneralUtils.validateEmail;
import static in.net.kccollege.student.utils.GeneralUtils.validatePassword;
import static in.net.kccollege.student.utils.GeneralUtils.validateUnique;

public class RegisterActivity extends AppCompatActivity {


	@BindView(R.id.name)
	TextInputEditText name;

	@BindView(R.id.email)
	TextInputEditText email;

	@BindView(R.id.unique)
	TextInputEditText unique;

	@BindView(R.id.password)
	TextInputEditText pass;

	@BindView(R.id.confpass)
	TextInputEditText confpass;

	@BindView(R.id.roll)
	TextInputEditText roll;

	@BindView(R.id.register)
	Button btnregister;

	@BindView(R.id.bktologin)
	Button btnback;

	@BindView(R.id.clas)
	Spinner clas;

	@BindView(R.id.stream)
	Spinner stream;

	@BindView(R.id.lay_name)
	TextInputLayout lay_name;

	@BindView(R.id.lay_email)
	TextInputLayout lay_email;

	@BindView(R.id.lay_unique)
	TextInputLayout lay_unique;

	@BindView(R.id.lay_pass)
	TextInputLayout lay_pass;

	@BindView(R.id.lay_confpass)
	TextInputLayout lay_confpass;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.accept_privacy)
	TextView lblPrivacy;

	private SharedPreferences sp;
	private ProgressD pd;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		ButterKnife.bind(this);

		context = this;
		sp = getSp();

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		email.addTextChangedListener(new EditTextWatcher(email, lay_email, KEY_EMAIL, context));
		pass.addTextChangedListener(new EditTextWatcher(pass, lay_pass, KEY_PASS, context));
		confpass.addTextChangedListener(new EditTextWatcher(pass, confpass, lay_confpass, "confpass", context));
		unique.addTextChangedListener(new EditTextWatcher(unique, lay_unique, KEY_UNIQUE, context));

		//DEBUG
//		name.setText("Developer");
//		email.setText("sahilnirkhe@gmail.com");
//		pass.setText("sss131");
//		confpass.setText("sss131");
//		unique.setText("debug1");
//

		btnback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnregister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name.getText().toString().trim().equals("") ||
						email.getText().toString().trim().equals("") ||
						unique.getText().toString().trim().equals("") ||
						pass.getText().toString().trim().equals("") ||
						confpass.getText().toString().trim().equals("") ||
						roll.getText().toString().trim().equals("") ||
						stream.getSelectedItemPosition() == 0 ||
						clas.getSelectedItemPosition() == 0) {
					Toast.makeText(context, R.string.error_field_required, Toast.LENGTH_SHORT).show();
				} else if (validateEmail(context, email, lay_email) &&
						validateUnique(context, unique, lay_unique) &&
						validatePassword(context, pass, lay_pass) &&
						validateConfPassword(context, pass, confpass, lay_confpass)) {
					processRegister();
				}
			}
		});

		lblPrivacy.setText(Html.fromHtml(getString(R.string.accept_privacy)));
		lblPrivacy.setMovementMethod(LinkMovementMethod.getInstance());


	}

	private void processRegister() {

		String detailsFormat = "%s-%s-%s";
		String details = String.format(detailsFormat, clas.getSelectedItem().toString().equals("XI") ? "X1" : "X2",
				stream.getSelectedItem().toString().substring(0, 2).toUpperCase(),
				roll.getText().toString().trim());

		Map<String, String> request = new HashMap<>();

		request.put(KEY_TAG, KEY_REGISTER);
		request.put(KEY_NAME, name.getText().toString().trim());
		request.put(KEY_UNIQUE, unique.getText().toString().trim());
		request.put(KEY_EMAIL, email.getText().toString().trim());
		request.put(KEY_DET, details);
		request.put(KEY_PASS, pass.getText().toString().trim());

		pd = new ProgressD(context);
		pd.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel(KEY_REGISTER);
			}
		});
		pd.show();

		InternetUtils.postData(getString(R.string.api_url),
				KEY_REGISTER,
				request,
				new JSONObjectRequestListener() {

					private static final String TAG = "Register";

					@Override
					public void onResponse(JSONObject response) {

						Log.d(TAG, "onResponse: " + response.toString());

						switch (response.optInt(KEY_STATUS)) {
							case 1://login success
								JSONObject user = response.optJSONObject(KEY_USER);

								DatabaseHandler.logoutUser(context);
								UserDetails ud = new UserDetails(user.optString(KEY_NAME),
										user.optString(KEY_UNIQUE), user.optString(KEY_EMAIL),
										user.optString(KEY_DET), user.optString(KEY_CREATED));
								DatabaseHandler db = new DatabaseHandler(context);
								db.addUser(ud);

								pd.dismiss();

								sp.edit().putString(getString(R.string.pref_email), ud.getEmail()).apply();

								Intent intent = new Intent(context, RegisteredActivity.class);
								startActivity(intent);

								Intent intent1 = new Intent();
								intent1.putExtra("registered", true);
								setResult(RESULT_OK, intent1);

								finish();

								break;
							case -1://email already exists
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_user_found), Toast.LENGTH_SHORT).show();
								break;
							case -2://unique is already taken
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_unique_taken), Toast.LENGTH_SHORT).show();
								break;
							case -3://unique not found
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_unique_not_found), Toast.LENGTH_SHORT).show();
								break;
							case -4://roll out of range
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_roll_taken), Toast.LENGTH_SHORT).show();
								break;
							case -9:
								pd.dismiss();
								toastUnknownError(context);
								break;

						}
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

