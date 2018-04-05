package in.net.kccollege.student.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.R;
import in.net.kccollege.student.activities.BrowserActivity;
import in.net.kccollege.student.adapters.NoticesAdapter;
import in.net.kccollege.student.model.RssEntry;
import in.net.kccollege.student.utils.InternetUtils;
import in.net.kccollege.student.utils.RssHelper;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.CONNECTION_ERROR;
import static in.net.kccollege.student.utils.Constants.KEY_DATE;
import static in.net.kccollege.student.utils.Constants.KEY_DESC;
import static in.net.kccollege.student.utils.Constants.KEY_NOTICES;
import static in.net.kccollege.student.utils.Constants.KEY_TITLE;
import static in.net.kccollege.student.utils.Constants.PARSE_ERROR;
import static in.net.kccollege.student.utils.Constants.RESPONSE_FROM_SERVER_ERROR;
import static in.net.kccollege.student.utils.GeneralUtils.toastInternetError;
import static in.net.kccollege.student.utils.GeneralUtils.toastUnknownError;

public class NoticesFragment extends Fragment {

	@BindView(R.id.status)
	TextView status;
	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeView;
	@BindView(R.id.recyler_view)
	RecyclerView recyclerView;
	private Context context;
	private SharedPreferences sp;
	private String RSS_URL;
	private ArrayList<RssEntry> entryArrayList = new ArrayList<RssEntry>();
	private LinearLayoutManager linearLayoutManager;

	public static NoticesFragment getInstance() {
		return new NoticesFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity().getBaseContext();
		sp = getSp();
		RSS_URL = getString(sp.getBoolean("guest", false) ? R.string.link_guest : R.string.link);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notices, container, false);
		ButterKnife.bind(this, view);

		swipeView.setColorSchemeColors(getResources().getColor(R.color.gblue));
		swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeView.setRefreshing(true);
				(new Handler()).postDelayed(new Runnable() {
					@Override
					public void run() {
						processRss(false);
					}
				}, 3000);
			}
		});

		linearLayoutManager = new LinearLayoutManager(context);

//		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);

		recyclerView.setLayoutManager(linearLayoutManager);
//		recyclerView.addItemDecoration(dividerItemDecoration);


		NoticesAdapter adapter = new NoticesAdapter(entryArrayList);
		adapter.setOnClickListener(new NoticesAdapter.OnItemClickListener() {
			@Override
			public void onClick(RssEntry entry) {
//				Toast.makeText(context, entry.getTitle(), Toast.LENGTH_SHORT).show();


				Intent intent = new Intent(getContext(), BrowserActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("type", "rss");
				bundle.putString(KEY_DESC, entry.getDesc());
				bundle.putString(KEY_TITLE, entry.getTitle());
				bundle.putString(KEY_DATE, entry.getDate());

				intent.putExtras(bundle);

				startActivity(intent);

			}
		});
		recyclerView.setAdapter(adapter);

		swipeView.post(new Runnable() {
			@Override
			public void run() {
				swipeView.setRefreshing(true);
				processRss(false);
			}
		});

		if (!sp.contains("notif")) {
			createNotifDialogue();
		}

//		processRss(false);
		return view;
	}

	void createNotifDialogue() {
		AlertDialog.Builder adb = new AlertDialog.Builder(getContext());

		adb.setMessage(getString(R.string.notif)).setTitle("Notices");
		adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("notif", false);
				editor.apply();
			}
		}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("notif", true);
				editor.apply();

			}
		});
		adb.create().show();
	}

	private void processRss(boolean offline) {
		status.setText(R.string.status_refreshing);
		swipeView.setRefreshing(true);

		if (offline) {
			JSONObject offlineData = RssHelper.getOfflineRssFile(context);
			if (offlineData != null) {
				status.setText(String.format(getString(R.string.offline_notices_messsage), sp.getString("rssdate", null)));
				new ConvertJson(null, 2).execute();

			} else {
				status.setText(R.string.error_file_notfound);
			}

			swipeView.setRefreshing(false);
		} else {
			InternetUtils.getData(RSS_URL,
					KEY_NOTICES,
					new JSONObjectRequestListener() {
						@Override
						public void onResponse(JSONObject response) {

							new ConvertJson(response, 1).execute();

							Calendar rightNow = Calendar.getInstance();
							DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
							status.setText(String.format("%s%s", getString(R.string.lastupdate), formatter.format(rightNow.getTime())));
							swipeView.setRefreshing(false);
						}

						@Override
						public void onError(ANError anError) {
							swipeView.setRefreshing(false);
							anError.printStackTrace();
							switch (anError.getErrorDetail()) {
								case CONNECTION_ERROR:
								case RESPONSE_FROM_SERVER_ERROR:
									toastInternetError(context);
									status.setText(R.string.error_connection);
									createNetErrorDialog();
									break;

								case PARSE_ERROR:
									toastUnknownError(context);
									status.setText(R.string.error_unknown);
									break;
							}

						}
					});
		}
	}

	private void updateList() {
		((NoticesAdapter) recyclerView.getAdapter()).swapDataset(entryArrayList);
		recyclerView.invalidate();
	}

	protected void createNetErrorDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(getResources().getString(R.string.errorbox))
				.setTitle("Unable to connect")
				.setCancelable(false)
				.setNeutralButton("Offline",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								processRss(true);
							}
						}
				)
				.setPositiveButton("Refresh",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								processRss(false);
							}
						})
				.setNegativeButton("Cancel",
						null
				)
		;
		AlertDialog alert = builder.create();
		alert.show();
	}

	private class ConvertJson extends AsyncTask<Void, Void, Void> {

		JSONObject data = new JSONObject();
		Integer command;


		/**
		 * @param data    can be null if jsondata is offline
		 * @param command 1 : save jsondata and update list
		 *                2 : load jsondata and update list
		 *                3 : just update list
		 */
		public ConvertJson(JSONObject data, Integer command) {
			this.data = data;
			this.command = command;
		}

		@Override
		protected Void doInBackground(Void... voids) {

			switch (command) {
				case 1:
					RssHelper.saveOfflineRssFile(context, data.toString());
					Calendar rightNow = Calendar.getInstance();
					DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
					sp.edit().putString("rssdate", formatter.format(rightNow.getTime())).apply();
					break;
				case 2:
					data = RssHelper.getOfflineRssFile(context);
					break;
				case 3:
				default:
					break;
			}
			entryArrayList = RssHelper.getListFromJson(data);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			updateList();
			super.onPostExecute(aVoid);
		}
	}

}
