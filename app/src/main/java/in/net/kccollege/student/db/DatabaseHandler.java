package in.net.kccollege.student.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.messaging.FirebaseMessaging;

import in.net.kccollege.student.model.UserDetails;

import static in.net.kccollege.student.ApplicationClass.getSp;
import static in.net.kccollege.student.utils.Constants.KEY_CREATED;
import static in.net.kccollege.student.utils.Constants.KEY_DET;
import static in.net.kccollege.student.utils.Constants.KEY_EMAIL;
import static in.net.kccollege.student.utils.Constants.KEY_NAME;
import static in.net.kccollege.student.utils.Constants.KEY_UNIQUE;


public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "users";

	// Login table name
	private static final String TABLE_LOGIN = "login";
	// Login Table Columns names


	String CREATE_LOGIN_TABLE = "CREATE TABLE login (" +
			"id INTEGER PRIMARY KEY, " +
			"name TEXT, " +
			"`unique` TEXT, " +
			"email TEXT, " +
			"details TEXT, " +
			"created TEXT);";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Logout User
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		FirebaseMessaging.getInstance().unsubscribeFromTopic(getSp().getBoolean("guest", false) ? "guest" : "users");
		getSp().edit().putBoolean("guest", false).apply();
		return true;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_LOGIN_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 */

	public void addUser(UserDetails ud) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ud.getName()); // FirstName
		values.put("`" + KEY_UNIQUE + "`", ud.getUnique()); // UniqueID
		values.put(KEY_EMAIL, ud.getEmail()); // Email
		values.put(KEY_DET, ud.getDetails()); // Details
		values.put(KEY_CREATED, ud.getCreated_at()); // Created At

		// Inserting Row
		db.insert(TABLE_LOGIN, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Return current users details
	 *
	 * @return
	 */
	public UserDetails getUserDetails() {
		UserDetails ud;

		String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {

			ud = new UserDetails(cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5));
			cursor.close();
			db.close();
			return ud;
		}

		return null;
	}

	/**
	 * get row count of the table
	 *
	 * @return int - number of rows
	 */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	/**
	 * Re crate database
	 */
	private void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_LOGIN, null, null);
		db.close();
	}

}
