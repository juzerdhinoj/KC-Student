package in.net.kccollege.student.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import in.net.kccollege.student.R;

import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_FEEDBACK;
import static in.net.kccollege.student.utils.Constants.KEY_NAME;
import static in.net.kccollege.student.utils.Constants.KEY_PASS;
import static in.net.kccollege.student.utils.Constants.KEY_UNIQUE;

/**
 * Created by Sahil on 11-07-2017.
 */

public class GeneralUtils {


	public static boolean isValidEmail(String email) {
		return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	/**
	 * Check if device is connected to the Internet
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean isDeviceOnline(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		boolean isOnline = (networkInfo != null && networkInfo.isConnected());
		if (!isOnline)
			Toast.makeText(context, " No internet Connection ", Toast.LENGTH_SHORT).show();

		return isOnline;
	}

	public static boolean validateEmail(Context context, TextInputEditText email, TextInputLayout inputLayoutEmail) {
		String stremail = email.getText().toString().trim();

		if (stremail.isEmpty()) {
			inputLayoutEmail.setError(context.getString(R.string.error_blank_email));
			requestFocus(context, email);
			return false;
		} else if (!isValidEmail(stremail)) {
			inputLayoutEmail.setError(context.getString(R.string.error_email_invalid));
			requestFocus(context, email);
			return false;
		} else {
			inputLayoutEmail.setErrorEnabled(false);
		}

		return true;
	}

	public static boolean validatePassword(Context context, TextInputEditText password, TextInputLayout inputLayoutPassword) {
		if (password.getText().toString().trim().length() < 5) {
			inputLayoutPassword.setError(context.getString(R.string.error_blank_pass));
			requestFocus(context, password);
			return false;
		} else {
			inputLayoutPassword.setErrorEnabled(false);
		}

		return true;
	}

	public static boolean validateName(Context context, TextInputEditText name, TextInputLayout inputLayoutName) {
		String strname = name.getText().toString().trim();

		if (strname.isEmpty()) {
			inputLayoutName.setError(context.getString(R.string.error_blank_name));
			requestFocus(context, name);
			return false;
		} else {
			inputLayoutName.setErrorEnabled(false);
		}
		return true;
	}

	public static boolean validateFeedback(Context context, TextInputEditText feedback, TextInputLayout inputLayoutFeedback) {
		String strfeedback = feedback.getText().toString().trim();

		if (strfeedback.isEmpty()) {
			inputLayoutFeedback.setError(context.getString(R.string.error_blank_feedback));
			requestFocus(context, feedback);
			return false;
		} else {
			inputLayoutFeedback.setErrorEnabled(false);
		}
		return true;
	}


	public static boolean validateUnique(Context context, TextInputEditText unique, TextInputLayout lay_unique) {
		String strunique = unique.getText().toString().trim();
		if (strunique.length() != 6) {
			lay_unique.setError(context.getString(R.string.error_unique_chars));
			requestFocus(context, unique);
			return false;
		} else {
			lay_unique.setErrorEnabled(false);
		}
		return true;
	}


	public static void requestFocus(Context context, View view) {
		if (view.requestFocus()) {
			((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	public static void toastInternetError(Context context) {
		Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
	}

	public static void toastUnknownError(Context context) {
		Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
	}

	public static boolean validateConfPassword(Context context, TextInputEditText pass, TextInputEditText confpass, TextInputLayout lay_confpass) {

		if (!pass.getText().toString().equals(confpass.getText().toString())) {
			lay_confpass.setError(context.getString(R.string.error_password_match));
			requestFocus(context, confpass);
			return false;
		} else {
			lay_confpass.setErrorEnabled(false);
		}

		return true;
	}

	public interface OnBackPressListener {
		void onBackPressed();
	}

	public static class ProgressD extends ProgressDialog {

		private String title;
		private String message;
		private OnBackPressListener onBackPressListener;

		public ProgressD(Context context) {
			this(context, context.getString(R.string.processing), context.getString(R.string.contactingservers));
		}

		public ProgressD(Context context, String title, String message) {
			super(context);

			setTitle(title);
			setMessage(message);
			setIndeterminate(true);
			setCancelable(true);
		}

		public void setOnBackPressListener(OnBackPressListener listener) {
			onBackPressListener = listener;
		}

		@Override
		public void onBackPressed() {
			onBackPressListener.onBackPressed();
		}

		public void dismiss() {
			super.dismiss();
		}

	}

	public static class EditTextWatcher implements TextWatcher {

		private TextInputEditText editText;
		private TextInputLayout inputLayout;
		private Context context;
		private String type;
		private TextInputEditText confPass;

		public EditTextWatcher(TextInputEditText editText, TextInputLayout inputLayout, String type, Context context) {
			this.editText = editText;
			this.inputLayout = inputLayout;
			this.type = type;
			this.context = context;
		}

		public EditTextWatcher(TextInputEditText pass, TextInputEditText confPass, TextInputLayout inputLayout, String type, Context context) {
			this(pass, inputLayout, type, context);
			this.confPass = confPass;
		}


		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			switch (type) {
				case KEY_PASS:
					validatePassword(context, editText, inputLayout);
					break;
				case KEY_EMAIL:
					validateEmail(context, editText, inputLayout);
					break;
				case "confpass":
					validateConfPassword(context, editText, confPass, inputLayout);
					break;
				case KEY_UNIQUE:
					validateUnique(context, editText, inputLayout);
					break;
				case KEY_NAME:
					validateName(context, editText, inputLayout);
					break;
				case KEY_FEEDBACK:
					validateFeedback(context, editText, inputLayout);
					break;

			}
		}
	}


}
