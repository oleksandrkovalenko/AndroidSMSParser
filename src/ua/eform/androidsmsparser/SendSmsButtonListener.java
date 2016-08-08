package ua.eform.androidsmsparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SendSmsButtonListener implements OnClickListener {

	public static final String TAG = "SendSmsButtonListener";
	
	public static final String DEFAULT_FILE_NAME = "smslist.txt";
	
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
		Utils.logColumnsNames(cursor);
		
		File file = Utils.getDefaultFileName(DEFAULT_FILE_NAME);
		
		serializeCursorToFile(cursor, file);
		
		//Sending file as e-mail
		Utils.sendFileByEmail(file, activity);
	}

	private void serializeCursorToFile(Cursor cursor, File file) {
		cursor.moveToFirst();
		try {
			proceedCursorToFile(cursor, file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
