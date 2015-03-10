package msg.madfox.first;

import msg.madfox.first.ContentProviderContract.Alarms;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class CPEventReceiver extends BroadcastReceiver
{
	private Uri CONTENT_URI=SchedulesProvider.CONTENT_ALARM_URI;
	private AlarmManager alarm_manager;
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		String action=intent.getAction();
		if(action.toString().equals(DaemonActions.ADD.getAction()))
		{

			addAlarm(intent);
		}
	}
	
	private void addAlarm(Intent intent)
	{
		long id=intent.getLongExtra("id", 0);
		long time=intent.getLongExtra("time", 0);
		if(id>=0)
		{
			/*Set Alarm*/
			alarm_manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent alarm_intent=new Intent(context,SMSSender.class)
					.putExtra("id", id)
					.putExtra("time", time);
			PendingIntent p_alarm_intent=PendingIntent.getBroadcast(context, (int)id, alarm_intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm_manager.set(AlarmManager.RTC_WAKEUP, time, p_alarm_intent);
			/*prepare Intent to be stored*/
			ContentValues values=new ContentValues();
			values.put(Alarms.COLUMN_NAME_ID, id);
			values.put(Alarms.COLUMN_NAME_URI, intent.toUri(0));
			/*save*/
			context.getContentResolver().insert(CONTENT_URI, values);
		}
	}
}

