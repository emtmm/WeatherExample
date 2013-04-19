package com.emtmm.weatherexample.data;

import java.util.ArrayList;
import java.util.List;

import com.emtmm.weatherexample.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {

	public static final String DB_NAME = "weather_example";
	public static final String DB_TABLE = "w_locations";
	public static final int DB_VERSION = 2;

	private static final String CLASSNAME = DBHelper.class.getSimpleName();
	private static final String[] COLS = new String[] { "_id", "zip", "city",
			"region", "woeid" };

	private SQLiteDatabase db;
	private final DBOpenHelper dbOpenHelper;

	public static class Location {

		public long id;
		public String woeid;
		public String zip;
		public String city;
		public String region;

		public Location() {
		}

		public Location(final long id, final String zip, final String city,
				final String region, final String woeid) {
			this.id = id;
			this.zip = zip;
			this.city = city;
			this.region = region;
			this.woeid = woeid;
		}

		@Override
		public String toString() {
			return this.zip + " " + this.city + ", " + this.region + ", "
					+ this.woeid;
		}
	}

	private static class DBOpenHelper extends SQLiteOpenHelper {

		private static final String DB_CREATE = "CREATE TABLE "
				+ DBHelper.DB_TABLE
				+ " (_id INTEGER PRIMARY KEY, zip TEXT UNIQUE NOT NULL, city TEXT, region TEXT, woeid INTEGER);";

		public DBOpenHelper(final Context context) {
			super(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			try {
				db.execSQL(DBOpenHelper.DB_CREATE);
			} catch (SQLException e) {
				Log.e(Constants.LOGTAG, DBHelper.CLASSNAME, e);
			}
		}

		@Override
		public void onOpen(final SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_TABLE);
			onCreate(db);
		}
	}

	public DBHelper(final Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		startDb();
	}

	private void startDb() {
		if (this.db == null) {
			this.db = this.dbOpenHelper.getWritableDatabase();
		}
	}

	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	public void insert(final Location location) {
		ContentValues values = new ContentValues();
		values.put("zip", location.zip);
		values.put("city", location.city);
		values.put("region", location.region);
		values.put("woeid", location.woeid);
		this.db.insert(DBHelper.DB_TABLE, null, values);
	}

	public void update(final Location location) {
		ContentValues values = new ContentValues();
		values.put("zip", location.zip);
		values.put("city", location.city);
		values.put("region", location.region);
		values.put("woeid", location.woeid);
		this.db.update(DBHelper.DB_TABLE, values, "_id=" + location.id, null);
	}

	public void delete(final long id) {
		this.db.delete(DBHelper.DB_TABLE, "_id=" + id, null);
	}

	public void delete(final String zip) {
		this.db.delete(DBHelper.DB_TABLE, "zip='" + zip + "'", null);
	}

	public Location get(final String zip) {
		Cursor c = null;
		Location location = null;
		try {
			c = this.db.query(true, DBHelper.DB_TABLE, DBHelper.COLS, "zip = '"
					+ zip + "'", null, null, null, null, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				location = new Location();
				location.id = c.getLong(0);
				location.zip = c.getString(1);
				location.city = c.getString(2);
				location.region = c.getString(3);
				location.woeid = c.getString(4);
			}
		} catch (SQLException e) {
			Log.v(Constants.LOGTAG, DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return location;
	}

	public List<Location> getAll() {
		ArrayList<Location> ret = new ArrayList<Location>();
		Cursor c = null;
		try {
			c = db.query(DBHelper.DB_TABLE, DBHelper.COLS, null, null, null,
					null, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Location location = new Location();
				location.id = c.getLong(0);
				location.zip = c.getString(1);
				location.city = c.getString(2);
				location.region = c.getString(3);
				location.woeid = c.getString(4);

				ret.add(location);

				c.moveToNext();
			}
		} catch (SQLException e) {
			Log.v(Constants.LOGTAG, DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return ret;
	}
}
