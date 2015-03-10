package msg.madfox.first;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import msg.madfox.first.ContentProviderContract.Alarms;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentManager;

public final class LogicsOfApp {
	
	/*parse string date to long date*/
	@SuppressLint("SimpleDateFormat") public static long getLongDate(String date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy-H:m");
		Date dateo;
		try{
			dateo= sdf.parse(date);
		}catch (Exception e) {return -1;}
		return dateo.getTime();
	}
	
	/*parse long date to string date*/
	@SuppressLint("SimpleDateFormat") public static String getStringDate(long date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MMMM-yyyy H:m");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(date);
		return sdf.format(cal.getTime());
	}
	
	/*Select number from Contacts*/
	public static void pickContact(Activity activity,Intent intent,AddSchedules addfrag)
	{
		ArrayList<String> numbers=new ArrayList<String>();
		Uri contactData = intent.getData();
		ContentResolver cr=activity.getContentResolver();	
	    Cursor c =  cr.query(contactData, null, null, null, null);
	    
	    if (c.moveToFirst()) 
	    {
	    	String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)); //contact _ID
	        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); //are there phone numbers ?
	        	        
	        if (hasPhone.equalsIgnoreCase("1")) 
	        {
		          Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
	                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, null, null);
		         
		          /*Destroy*/
		          cr=null;	contactData=null;	c.close();	c=null;
		          
		          if(phones.getCount()<2)
		          {
		        	  phones.moveToNext();
		        	  MainActivity.SCHEDULED_NUM=phones.getString(phones.getColumnIndex(Phone.NUMBER));
		        	  addfrag.setPhoneNumber(MainActivity.SCHEDULED_NUM);
		        	  phones.close();
		          }else
		          {
		              while(phones.moveToNext())
		              {
		            	  numbers.add(phones.getString(phones.getColumnIndex(Phone.NUMBER)));
		              }
		              phones.close();
		              showListDialog(numbers,((MainActivity)activity).getSupportFragmentManager(),addfrag);
		              
		              /*destroy*/
		              phones=null;
		          }
	        }
		    
	    }
	}
	
	/*wait*/
	public static void waitThread(int mills)
	{
		try
		{
			Thread.sleep(mills);
		}catch(Exception er){}
	}
	/**
	 * Cancel Schedule
	 */
	public static void cancelSchedule(long id,Context context)
	{/*need to improve, currently only deleting saved intent, not cancelling alarm */
		Cursor cursor=context.getContentResolver().query(Uri.parse(SchedulesProvider.CONTENT_ALARM_URI + "/" + id), 
				new String[] {Alarms.COLUMN_NAME_URI}, null, null, null);
		if(cursor.moveToFirst())
		{
			AlarmManager alarm=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			try{
				Intent intent=Intent.parseUri(cursor.getString(cursor.getColumnIndex(Alarms.COLUMN_NAME_URI)), 0);
				PendingIntent pintent=PendingIntent.getActivity(context, (int)id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				alarm.cancel(pintent);
			}catch(URISyntaxException er){}
			finally
			{
				context.getContentResolver().delete(Uri.parse(SchedulesProvider.CONTENT_ALARM_URI + "/" + id), 
						null, null);
			}
		}
	}
	/*Show List Dialog*/
	public static void showListDialog(ArrayList<String> values,FragmentManager fm,AddSchedules addfrag)
	{
		ListDialogBox listdialog=new ListDialogBox(values,"Select Number");
		listdialog.show(fm, "select_number");
		listdialog.setListDialogListener(addfrag);
	}
}
