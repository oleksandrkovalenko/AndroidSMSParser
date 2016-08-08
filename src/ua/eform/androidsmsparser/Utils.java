package ua.eform.androidsmsparser;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();

	public static void sendFileByEmail(File file, MainActivity activity) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"alekz@ukr.net"});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "file list");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));
		activity.startActivity(emailIntent);
	}
	
	public static File getDefaultFileName(String fileName) {
		return new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
				fileName);
	}
	
	public static void logColumnsNames(Cursor cursor) {
		for (String column : cursor.getColumnNames()) {
			int columnIndex = cursor.getColumnIndex(column);
			int typeInt = cursor.getType(columnIndex);
			switch (typeInt)
			{
			case Cursor.FIELD_TYPE_NULL: 
				Log.d(TAG, column + " NULL");
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				Log.d(TAG, column + " Integer " + cursor.getInt(columnIndex));
				break;
			case Cursor.FIELD_TYPE_FLOAT:
				Log.d(TAG, column + " float " + cursor.getFloat(columnIndex));
				break;
			case Cursor.FIELD_TYPE_STRING:
				Log.d(TAG, column + " string " + cursor.getString(columnIndex));
				break;
			default:
				Log.d(TAG, column + " " + typeInt);
				break;
			}
		}
		Log.d(TAG, String.valueOf(cursor.getCount()));
	}
}
