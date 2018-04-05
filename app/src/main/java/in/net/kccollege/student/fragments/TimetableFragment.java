package in.net.kccollege.student.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.R;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CONNECTION_ERROR;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.OnBackPressListener;
import static in.net.kccollege.student.utils.GeneralUtils.ProgressD;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;


public class TimetableFragment extends Fragment {

	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	ProgressD mProgressDialog;

	@BindView(R.id.dwd)
	Button download;

	@BindView(R.id.stream)
	RadioGroup str;

	@BindView(R.id.clas)
	RadioGroup clas;
	private Context context;
	private SharedPreferences sp;

	public static TimetableFragment getInstance() {
		return new TimetableFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity();
		sp = getSp();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_timetable, container, false);
		ButterKnife.bind(this, view);

		mProgressDialog = new ProgressD(context);
		mProgressDialog.setMessage("Downloading file...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnBackPressListener(new OnBackPressListener() {
			@Override
			public void onBackPressed() {
				AndroidNetworking.cancel("timetable");
			}
		});


		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if (str.getCheckedRadioButtonId() == -1) {
					Toast.makeText(context, "Please select a Stream.", Toast.LENGTH_LONG).show();
				} else if (clas.getCheckedRadioButtonId() == -1) {

					Toast.makeText(context, "Please select a Class.", Toast.LENGTH_LONG).show();
				} else if (permission != PackageManager.PERMISSION_GRANTED) {
					if (shouldAskPermission()) {
						requestPermissions(
								PERMISSIONS_STORAGE,
								REQUEST_EXTERNAL_STORAGE
						);
					} else {
						String fileName =
								((RadioButton) (view.findViewById(clas.getCheckedRadioButtonId()))).getText().toString() +
										((RadioButton) (view.findViewById(str.getCheckedRadioButtonId()))).getText().toString();
						downloadFile(fileName);

					}
				} else {
					String fileName =
							((RadioButton) (view.findViewById(clas.getCheckedRadioButtonId()))).getText().toString() +
									((RadioButton) (view.findViewById(str.getCheckedRadioButtonId()))).getText().toString();
					downloadFile(fileName);
				}

			}
		});

		return view;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);


		if (ContextCompat.checkSelfPermission(context, permissions[1]) == PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(context, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

			String fileName =
					((RadioButton) (getView().findViewById(clas.getCheckedRadioButtonId()))).getText().toString() +
							((RadioButton) (getView().findViewById(str.getCheckedRadioButtonId()))).getText().toString();

			downloadFile(fileName);
		} else {
			Toast.makeText(context, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
		}

	}

	private boolean shouldAskPermission() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	private void downloadFile(final String fileName) {

		mProgressDialog.show();

		String URL = String.format("%s%s.pdf", getString(R.string.downloadurl), fileName);
		System.out.println(String.format("%s/%s/", Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS) + fileName + ".pdf");
		AndroidNetworking.download(URL,
				String.format("%s/%s/", Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS), fileName + ".pdf")
				.setTag("timetable")
				.setPriority(Priority.MEDIUM)
				.addHeaders("Accept-Encoding", "identity")
				.build()
				.setDownloadProgressListener(new DownloadProgressListener() {
					@Override
					public void onProgress(long bytesDownloaded, long totalBytes) {
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setProgressNumberFormat("%1d KB/%2d KB");
						mProgressDialog.setMax(Math.round(totalBytes / (1024)));
						mProgressDialog.setProgress(Math.round(bytesDownloaded / 1024));

					}
				})
				.startDownload(new DownloadListener() {

					@Override
					public void onDownloadComplete() {
						mProgressDialog.dismiss();

						File file = new File(String.format("%s/%s/%s.pdf", Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS, fileName));


						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file), "application/" + (file.getName().split("\\."))[1]);
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

						try {
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(context, "No application found to open PDF files", Toast.LENGTH_LONG).show();
						}


					}

					@Override
					public void onError(ANError anError) {
						mProgressDialog.dismiss();
						anError.printStackTrace();
						switch (anError.getErrorDetail()) {
							case CONNECTION_ERROR:
							case RESPONSE_FROM_SERVER_ERROR:
								if (anError.getErrorCode() == 404) {
									Toast.makeText(context, String.format("Timetable is yet to be released for %s, %s", fileName.substring(0, 4), fileName.substring(4)), Toast.LENGTH_SHORT).show();
								} else {
									toastInternetError(context);
								}
								break;

							case PARSE_ERROR:
								toastUnknownError(context);
								break;
						}
					}
				});


	}

}
