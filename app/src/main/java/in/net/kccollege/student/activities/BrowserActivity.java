package in.net.kccollege.student.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.R;
import in.net.kccollege.student.db.DatabaseHandler;
import in.net.kccollege.student.model.UserDetails;

public class BrowserActivity extends AppCompatActivity {

	@BindView(R.id.webview)
	WebView myWebView;

	@BindView(R.id.srl)
	SwipeRefreshLayout srl;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private Context context;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		ButterKnife.bind(this);

		context = this;

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		srl.setColorSchemeColors(getResources().getColor(R.color.gblue));


		final Bundle bundle = getIntent().getExtras();
		myWebView.getSettings().setBuiltInZoomControls(true);
		myWebView.getSettings().setDisplayZoomControls(false);

		switch (bundle.getString("type", "")) {

			case "rss":
				getSupportActionBar().setTitle("Notice");
				srl.setEnabled(false);
				StringBuilder message = new StringBuilder();
				message.append("<h3>" + bundle.getString("title") + "</h3>");
				message.append("<p><i>" + bundle.getString("date") + "</i></p>");
				message.append(bundle.getString("desc"));
				myWebView.loadData(message.toString(), "text/html", "utf-8");

				break;
			case "link":
				getSupportActionBar().setTitle("Results");
				myWebView.getSettings().setJavaScriptEnabled(true);


				srl.setRefreshing(true);
				DatabaseHandler db = new DatabaseHandler(context);
				UserDetails ud = db.getUserDetails();
				final String[] details = ud.getDetails().split("-");

//				boolean is_dev = ud.getUnique().contains("dev00");
//				String url = is_dev ? bundle.getString("url") :
				if (ud.getUnique().contains("dev00")) {
					myWebView.loadUrl(new String(Base64.decode("aHR0cDovL2tjY29sbGVnZS5uZXQuaW4vbWFya3MvZGV2Lmh0bWw=", Base64.DEFAULT)));
					srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						@Override
						public void onRefresh() {
							myWebView.loadUrl(new String(Base64.decode("aHR0cDovL2tjY29sbGVnZS5uZXQuaW4vbWFya3MvZGV2Lmh0bWw=", Base64.DEFAULT)));
						}
					});
				} else {
					try {
						myWebView.postUrl(bundle.getString("url"),
								(getString(R.string.post3) + URLEncoder.encode(details[2], "UTF-8") + "&" +
										getString(R.string.post2) + URLEncoder.encode(details[1], "UTF-8") + "&" +
										getString(R.string.post1) + URLEncoder.encode((details[0].equals("X1") ? "XI" : "XII"), "UTF-8")).getBytes()
						);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
//
					srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						@Override
						public void onRefresh() {
							try {
								myWebView.postUrl(bundle.getString("url"),
										(getString(R.string.post3) + URLEncoder.encode(details[2], "UTF-8") + "&" +
												getString(R.string.post2) + URLEncoder.encode(details[1], "UTF-8") + "&" +
												getString(R.string.post1) + URLEncoder.encode((details[0].equals("X1") ? "XI" : "XII"), "UTF-8")).getBytes()
								);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						}
					});
				}
//
				myWebView.setWebViewClient(new WebViewClient() {
					@Override
					public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
						myWebView.loadData("", "text/html", null);
						AlertDialog.Builder adb = new AlertDialog.Builder(BrowserActivity.this);
						adb.setTitle("Error");
						TextView msg = new TextView(BrowserActivity.this);
						msg.setGravity(Gravity.CENTER);
						msg.setText("No internet Connection");
						adb.setView(msg).setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								myWebView.reload();
							}
						}).setNegativeButton("Back", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								finish();
							}
						});
						AlertDialog ad = adb.create();
						ad.show();
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						Toast.makeText(BrowserActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
						srl.setRefreshing(false);
					}

				});

		}

	}


	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

}
