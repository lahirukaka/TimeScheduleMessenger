package msg.madfox.first;

import java.net.URISyntaxException;

import msg.madfox.first.ContentProviderContract.Alarms;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class BootEventReceiver extends BroadcastReceiver {

	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		try
		{
			scheduleOnBoot();
		}catch (Exception e) {
			Log.w("onBoot",e.getCause().getMessage()+"");
		}
	}

	private void scheduleOnBoot() throws Exception
	{
		new AsyncTask<Void, Void, Void>() {
			ContentResolver cr=context.getContentResolver();
			AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent;
			@Override
			protected Void doInBackground(Void... params) {

				Cursor cursor=cr.query(SchedulesProvider.CONTENT_ALARM_URI, new String[] {"*"}, null, null, null);
				while(cursor.moveToNext())
				{
					try{
					intent=Intent.parseUri(cursor.getString(cursor.getColumnIndex(Alarms.COLUMN_NAME_URI)), 0);
					PendingIntent p_alarm_intent=PendingIntent.getBroadcast(context, 
							(int)cursor.getLong(cursor.getColumnIndex(Alarms.COLUMN_NAME_ID)), 
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
					alarm.set(AlarmManager.RTC_WAKEUP, intent.getLongExtra("time", 0), p_alarm_intent);
					}catch(URISyntaxException er){
						Log.w("Uri Systax", er.getCause());
					}
				}
				
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}