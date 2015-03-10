package msg.madfox.first;

import java.util.ArrayList;

import msg.madfox.first.ContentProviderContract.Schedules;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSender extends BroadcastReceiver {
	
	private final String SENT="msg.madfox.first.SMS_SENT";

	private SmsManager sms_manager;
	
	private long id;
	private long time;
	private String msg;
	private String number;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Bundle bundle = intent.getExtras();
		id = bundle.getLong("id");
		time = bundle.getLong("time");
		
		/* Handle intent */
		handleSending();
	}

	private void handleSending() {
		Cursor cursor;
		if (time <= System.currentTimeMillis()) {
			cursor = context.getContentResolver().query(Uri.parse(SchedulesProvider.CONTENT_URI + "/" + id),
					new String[] {Schedules.COLUMN_NAME_MSG,Schedules.COLUMN_NAME_NUMBER}, null, null, null);
			if(cursor.moveToFirst())
			{
				msg=cursor.getString(cursor.getColumnIndex(Schedules.COLUMN_NAME_MSG));
				number=cursor.getString(cursor.getColumnIndex(Schedules.COLUMN_NAME_NUMBER));
				
				sms_manager=SmsManager.getDefault();
				
				ArrayList<String> msgs = sms_manager.divideMessage(msg);
				ArrayList<PendingIntent> pi_send=new ArrayList<PendingIntent>();
				
				for(int i=0;i<msgs.size();i++)
				{
					pi_send.add(PendingIntent.getBroadcast(context, (int)id, new Intent(SENT), 0));
				}
				
				/*send*/
				try
				{
					sms_manager.sendMultipartTextMessage(number, null, msgs, pi_send, null);
					
					new AsyncTask<Void, Void, Boolean>() {
						@Override
						protected Boolean doInBackground(Void... params) {
							context.getContentResolver().delete(Uri.parse(SchedulesProvider.CONTENT_ALARM_URI + "/" + id), 
									null, null);
							context.getContentResolver().delete(Uri.parse(SchedulesProvider.CONTENT_URI + "/" + id), 
									null, null);
							return true;
						}
					}.execute();
					
				}catch(IllegalArgumentException er)
				{
					Toast.makeText(context, "Schedule Messenger : Failed to send message...", Toast.LENGTH_LONG).show();
					//listener
				}
			}
		}
	}
}