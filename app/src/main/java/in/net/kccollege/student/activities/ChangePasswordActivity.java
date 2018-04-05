package in.net.kccollege.student.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import in.net.kccollege.student.db.DatabaseHandler;
import in.net.kccollege.student.model.UserDetails;
import in.net.kccollege.student.utils.InternetUtils;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CONNECTION_ERROR;
import static in.net.kccollege.student.utils.Constants.KEY_CHANGE_PASS;
import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_NEW_PASS;
import static in.net.kccollege.student.utils.Constants.KEY_PASS;
import static in.net.kccollege.student.utils.Constants.KEY_STATUS;
import static in.net.kccollege.student.utils.Constants.KEY_TAG;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.EditTextWatcher;
import static in.net.kccollege.student.utils.GeneralUtils.OnBackPressListener;
import static in.net.kccollege.student.utils.GeneralUtils.ProgressD;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;

public class ChangePasswordActivity extends AppCompatActivity {

	@BindView(R.id.newpass)
	TextInputEditText newpass;

	@BindView(R.id.lay_pass)
	TextInputLayout lay_pass;

	@BindView(R.id.btchangepass)
	Button changepass;

	@BindView(R.id.btcancel)
	Button cancel;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private SharedPreferences sp;
	private ProgressD pd;
	private Context context;
	private UserDetails ud;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		context = this;
		sp = getSp();

		ud = new DatabaseHandler(context).getUserDetails();

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		newpass.addTextChangedListener(new EditTextWatcher(newpass, lay_pass, KEY_PASS, context));

		changepass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processChangePassword();
			}
		});

	}

	private void processChangePassword() {


		Map<String, String> request = new HashMap<>();
		request.put(KEY_TAG, KEY_CHANGE_PASS);
		request.put(KEY_NEW_PASS, newpass.getText().toString());
		request.put(KEY_EMAIL, ud.getEmail());


		pd = new ProgressD(context);
		pd.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel(KEY_CHANGE_PASS);
			}
		});
		pd.show();

		InternetUtils.postData(getString(R.string.api_url),
				KEY_CHANGE_PASS,
				request,
				new JSONObjectRequestListener() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("Success", "onResponse: " + response.toString());
						switch (Integer.parseInt(response.optString(KEY_STATUS))) {
							case 1:
								pd.dismiss();
								Toast.makeText(context, "Your Password is successfully changed.", Toast.LENGTH_LONG).show();
								break;
							case -9:
								pd.dismiss();
								Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
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
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
}
