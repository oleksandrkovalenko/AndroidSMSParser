package ua.eform.androidsmsparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SendSmsButtonListener implements OnClickListener {

	public static final String TAG = "SendSmsButtonListener";
	
	private MainActivity activity;
	
	public SendSmsButtonListener(MainActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) {
		clickButton();
	}

	private void clickButton() {
		Cursor cursor = createSmsCursor();
		
		cursor.moveToFirst();
		logColumnsNames(cursor);
		
		File file = getDefaultFileName();
		
		cursor.moveToFirst();
		try {
			proceedCursorToFile(cursor, file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Sending file as e-mail
		sendFileByEmail(file);
	}
	
	private Cursor createSmsCursor() {
		Uri uriSms = Uri.parse(Constants.CONTENT_SMS_INBOX);
		
		String selection = "address = ? or address = ? or address = ?";
		
		String[] selectionArg = new String[] {"+380630555678", "OTP Bank", "10901"};
		
		Cursor cursor = activity.getContentResolver().
				query(uriSms,
					  null, //columns to return. null return all columns
					  selection,
					  selectionArg,
					  null);
		return cursor;
	}
	
	private void proceedCursorToFile(Cursor cursor, File file) throws FileNotFoundException {
		PrintStream ps = new PrintStream(file);
		do {
			writeSmsRecordToStream(ps, cursor);
		} while (cursor.moveToNext());
		ps.close();
	}
	
	private void writeSmsRecordToStream(PrintStream ps, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex("body"));
		String sender = cursor.getString(cursor.getColumnIndex("address"));
		long date = cursor.getLong(cursor.getColumnIndex("date"));
		StringBuilder builder = new StringBuilder();
		builder.append(date)
			.append(";")
			.append(sender)
			.append(";")
			.append(body.replace("\n", ""));
		ps.println(builder.toString());
		Log.d(TAG, cursor.getString(cursor.getColumnIndex("body")));
	}

	private File getDefaultFileName() {
		return new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
				"smslist.txt");
	}

	private void logColumnsNames(Cursor cursor) {
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

	private void sendFileByEmail(File file) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"alekz@ukr.net"});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "file list");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));
		activity.startActivity(emailIntent);
	}
}
