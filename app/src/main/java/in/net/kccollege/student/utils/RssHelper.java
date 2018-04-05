package in.net.kccollege.student.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.net.kccollege.student.BuildConfig;
import in.net.kccollege.student.R;
import in.net.kccollege.student.model.RssEntry;

import static in.net.kccollege.student.utils.Constants.KEY_DATE;
import static in.net.kccollege.student.utils.Constants.KEY_DESC;
import static in.net.kccollege.student.utils.Constants.KEY_ENTRIES;
import static in.net.kccollege.student.utils.Constants.KEY_TITLE;
import static in.net.kccollege.student.utils.Constants.KEY_VER;

/**
 * Created by Sahil on 16-07-2017.
 */

public class RssHelper {

	private static SimpleDateFormat fromformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat toformat = new SimpleDateFormat("EEE, MMM d ''yy");

	public static void saveOfflineRssFile(Context context, String data) {

		String enstri = EncryptionUtils.encrypt(context.getString(R.string.tag_title), context.getString(R.string.tag_summary), data);
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;

		try {
			File savef = new File(context.getExternalFilesDir(null) + "/" + context.getString(R.string.rss_file_name));
			savef.createNewFile();

			fOut = new FileOutputStream(savef);
			myOutWriter =
					new OutputStreamWriter(fOut);
			myOutWriter.write(enstri);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				myOutWriter.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static JSONObject getOfflineRssFile(Context context) {
		FileInputStream fIn = null;
		BufferedReader myReader = null;
		try {

			File myFile = new File(context.getExternalFilesDir(null) + "/" + context.getString(R.string.rss_file_name));
			fIn = new FileInputStream(myFile);
			myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String receiveString;
			StringBuilder stringBuilder = new StringBuilder();

			while ((receiveString = myReader.readLine()) != null) {
				stringBuilder.append(receiveString);
			}

			String destri = EncryptionUtils.decrypt(context.getString(R.string.tag_title), context.getString(R.string.tag_summary), stringBuilder.toString());
			myReader.close();
			fIn.close();

			JSONObject json = new JSONObject(destri);

			return json;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}

	public static ArrayList<RssEntry> getListFromJson(JSONObject rssData) {
		ArrayList<RssEntry> list = new ArrayList<RssEntry>();
		JSONArray feed = rssData.optJSONArray(KEY_ENTRIES);

		for (int i = 0; i < feed.length(); i++) {//add items to list
			JSONObject ent = feed.optJSONObject(i);

			if (ent.has("ver")) {
				if (!ent.optString(KEY_VER).contains(BuildConfig.VERSION_NAME)) {
					continue;
				}
			}

			RssEntry entry = new RssEntry();

			entry.setTitle(ent.optString(KEY_TITLE));
			entry.setDesc(ent.optString(KEY_DESC));

			try {
				Date date = fromformat.parse(ent.optString(KEY_DATE));
				entry.setDate(toformat.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}


			list.add(entry);
		}
		return list;
	}

}
