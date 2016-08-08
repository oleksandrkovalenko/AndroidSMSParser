package ua.eform.androidsmsparser.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Storage extends SQLiteOpenHelper {

	public static final String TAG = Storage.class.getSimpleName();
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_NAME = "Database.db";
	
	public Storage(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
	}

}
