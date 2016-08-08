package ua.eform.androidsmsparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SendContactButtonListener implements OnClickListener {

	public static final String TAG = "SendContactButtonListener";
	
	public static final String DEFAULT_FILE_NAME = "contactlist.txt";
	
	private MainActivity activity;

	public SendContactButtonListener(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		clickButton();
	}

	private void clickButton() {
		String result = saveContacts();
		
		File file = Utils.getDefaultFileName(DEFAULT_FILE_NAME);
		
		PrintStream ps;
		try {
			ps = new PrintStream(file);
			ps.println(result);
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Utils.sendFileByEmail(file, activity);
	}

	private String saveContacts() {
		String[] projection = new String[]
			    {
				Contacts._ID,
	            Contacts.LOOKUP_KEY,
	            Contacts.DISPLAY_NAME_PRIMARY
			    };
		
		Cursor cursor =
		        activity.getContentResolver().query(
		                Contacts.CONTENT_URI,
		                projection ,
		                null,
		                null,
		                null);
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (cursor.moveToFirst()) {
			
			do {
				String contactId =
				        cursor.getString(cursor.getColumnIndex(Contacts._ID));
				String phone = queryPhone(contactId);
				if (phone.equals("[]")) {
					continue;
				}
				
				sb.append("[");
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					sb.append(cursor.getColumnName(i));
					sb.append(":");
					sb.append(cursor.getString(i));
					sb.append(",");
					Log.d(TAG, cursor.getColumnName(i)+" : "+cursor.getString(i));
				}
				sb.append(phone);
				sb.append("],");
			} while (cursor.moveToNext());
		} else {
			Log.d(TAG, "No data");
		}
		sb.append("]");
		cursor.close();
		return sb.toString();
	}

	private String queryPhone(String contactId) {
		
		StringBuilder sb = new StringBuilder();
		
		Cursor phones = 
				activity.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " =  " + contactId , null, null);
		
		sb.append("[");
		while (phones.moveToNext()) {
			sb.append("[");
	        String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
	        int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
	        sb.append(Phone.NUMBER);
	        sb.append(":");
	        sb.append(number);
	        sb.append(",");
	        sb.append(Phone.TYPE);
	        sb.append(":");
	        sb.append(type);
	        sb.append("]");
	        Log.d(TAG, "Phone number: " + number + " : " + type);
	    }
		sb.append("]");
		phones.close();
		return sb.toString();
	}
}
