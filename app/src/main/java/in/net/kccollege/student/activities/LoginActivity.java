package in.net.kccollege.student.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import static in.net.kccollege.student.utils.Constants.KEY_LOGIN;
import static in.net.kccollege.student.utils.Constants.KEY_MESSAGE;
import static in.net.kccollege.student.utils.Constants.KEY_NAME;
import static in.net.kccollege.student.utils.Constants.KEY_PASS;
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
import static in.net.kccollege.student.utils.GeneralUtils.validateEmail;
import static in.net.kccollege.student.utils.GeneralUtils.validatePassword;


public class LoginActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.layout_email)
	TextInputLayout inputLayoutEmail;

	@BindView(R.id.layout_password)
	TextInputLayout inputLayoutPassword;

	@BindView(R.id.email)
	TextInputEditText email;

	@BindView(R.id.password)
	TextInputEditText password;

	@BindView(R.id.btnlogin)
	Button btnLogin;

	@BindView(R.id.btnregister)
	Button btnRegister;

	@BindView(R.id.passres)
	TextView forgot;

	@BindView(R.id.btnguest)
	Button btnGuest;

	@BindView(R.id.lblLogin)
	TextView lblLogin;

	private SharedPreferences sp;
	private ProgressD pd;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);


		context = this;
		sp = getSp();


		email.setText(sp.getString(getString(R.string.pref_email), ""));

		email.addTextChangedListener(new EditTextWatcher(email, inputLayoutEmail, KEY_EMAIL, context));
		password.addTextChangedListener(new EditTextWatcher(password, inputLayoutPassword, KEY_PASS, context));
		forgot.setText(Html.fromHtml(getString(R.string.html_forgotpass)));


		if (getIntent().hasExtra("close")) {

			if (getIntent().getBooleanExtra("close", false)) {

				AlertDialog.Builder adb = new AlertDialog.Builder(context);
				adb.setMessage(R.string.error_logout_user)
						.setPositiveButton("OK", null)
						.setNeutralButton("CONTACT US", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(LoginActivity.this, FeedbackActivity.class));
							}
						})
						.create()
						.show();
			}
		}

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateEmail(context, email, inputLayoutEmail) && validatePassword(context, password, inputLayoutPassword)) {
					processLogin();
				}
			}
		});

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, RegisterActivity.class);
				startActivityForResult(intent, 0);

			}
		});

		forgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
				startActivity(intent);
			}
		});

		btnGuest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("guest", true);
				editor.apply();

				Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
				startActivity(intent);

				finish();
			}
		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	private void processLogin() {

		Map<String, String> request = new HashMap<>();
		request.put(KEY_TAG, KEY_LOGIN);
		request.put(KEY_EMAIL, email.getText().toString());
		request.put(KEY_PASS, password.getText().toString());


		pd = new ProgressD(context);
		pd.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel(KEY_LOGIN);
			}
		});
		pd.show();

		InternetUtils.postData(getString(R.string.api_url),
				KEY_LOGIN,
				request,
				new JSONObjectRequestListener() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("Success", "onResponse: " + response.toString());

						switch (response.optInt(KEY_STATUS, -9)) {
							case 1://login success
								JSONObject user = response.optJSONObject(KEY_USER);

								DatabaseHandler.logoutUser(context);
								UserDetails ud = new UserDetails(user.optString(KEY_NAME),
										user.optString(KEY_UNIQUE), user.optString(KEY_EMAIL),
										user.optString(KEY_DET), user.optString(KEY_CREATED));
								DatabaseHandler db = new DatabaseHandler(context);
								db.addUser(ud);

								sp.edit().putString(getString(R.string.pref_email), ud.getEmail()).apply();

								Intent intent = new Intent(context, IndexActivity.class);
								startActivity(intent);
								finish();

								break;
							case -1://email not found
								Toast.makeText(context, response.optString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
								break;
							case -9://unknown error
								toastUnknownError(context);
								break;
						}
						pd.dismiss();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {

			if (data.getBooleanExtra("registered", false)) {
				email.setText(sp.getString(getString(R.string.pref_email), ""));
			}
		}
	}
}
