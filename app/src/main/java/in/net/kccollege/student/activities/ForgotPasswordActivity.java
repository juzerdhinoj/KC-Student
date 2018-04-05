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
import android.view.View;
import android.widget.Button;
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
import in.net.kccollege.student.utils.InternetUtils;

import static in.net.kccollege.student.utils.Constants.CONNECTION_ERROR;
import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_FORGOT_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_FORGOT_TAG;
import static in.net.kccollege.student.utils.Constants.KEY_STATUS;
import static in.net.kccollege.student.utils.Constants.KEY_TAG;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.EditTextWatcher;
import static in.net.kccollege.student.utils.GeneralUtils.OnBackPressListener;
import static in.net.kccollege.student.utils.GeneralUtils.ProgressD;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;

public class ForgotPasswordActivity extends AppCompatActivity {

	@BindView(R.id.email)
	TextInputEditText email;

	@BindView(R.id.respass)
	Button resetpass;

	@BindView(R.id.cancel)
	Button cancel;

	@BindView(R.id.lay_email)
	TextInputLayout lay_email;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private Context context;
	private SharedPreferences sp;
	private ProgressD pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		email.addTextChangedListener(new EditTextWatcher(email, lay_email, KEY_EMAIL, context));

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		resetpass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processForgotPassword();
			}
		});

	}

	private void processForgotPassword() {

		Map<String, String> request = new HashMap<>();
		request.put(KEY_TAG, KEY_FORGOT_TAG);
		request.put(KEY_FORGOT_EMAIL, email.getText().toString());

		pd = new ProgressD(context);
		pd.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel(KEY_FORGOT_TAG);
			}
		});
		pd.show();

		InternetUtils.postData(getString(R.string.api_url),
				KEY_FORGOT_TAG,
				request,
				new JSONObjectRequestListener() {
					@Override
					public void onResponse(JSONObject response) {
						switch (Integer.parseInt(response.optString(KEY_STATUS))) {
							case 1://login success
								pd.dismiss();
								AlertDialog.Builder adb = new AlertDialog.Builder(context);
								adb.setMessage("A recovery email is sent to you, see it for more details.\nCheck the junk/spam folder.")
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										}).create().show();


								break;
							case -1://email not found
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
								break;
							case -9:
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
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
