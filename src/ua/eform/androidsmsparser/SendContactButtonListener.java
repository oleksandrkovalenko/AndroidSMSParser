package ua.eform.androidsmsparser;

import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SendContactButtonListener implements OnClickListener {

	public static final String TAG = "SendContactButtonListener";
	
	private MainActivity activity;

	public SendContactButtonListener(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		clickButton();
	}

	private void clickButton() {
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
		
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					Log.d(TAG, cursor.getColumnName(i)+" : "+cursor.getString(i));
				}
				
				String contactId =
				        cursor.getString(cursor.getColumnIndex(Contacts._ID));
				queryPhone(contactId);
				
			} while (cursor.moveToNext());
		} else {
			Log.d(TAG, "No data");
		}
		cursor.close();
	}

	private void queryPhone(String contactId) {
		
		Cursor phones = 
				activity.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " =  " + contactId , null, null);
		
		while (phones.moveToNext()) {
	        String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
	        int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
	        switch (type) {
	            case Phone.TYPE_HOME:
	                // do something with the Home number here...
	                break;
	            case Phone.TYPE_MOBILE:
	                // do something with the Mobile number here...
	                break;
	            case Phone.TYPE_WORK:
	                // do something with the Work number here...
	                break;
	        }
	        Log.d(TAG, "Phone number: " + number + " : " + type);
	    }
		phones.close();
	}
}
