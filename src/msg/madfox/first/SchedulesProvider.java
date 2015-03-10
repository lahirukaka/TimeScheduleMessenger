package msg.madfox.first;

import msg.madfox.first.ContentProviderContract.Alarms;
import msg.madfox.first.ContentProviderContract.Schedules;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class SchedulesProvider extends ContentProvider {

	private DatabaseHelper dbhelper;
	private SQLiteDatabase db;
	
	/*Provider*/
	private static final String AUTHORITY="msg.madfox.first.provider";
	private static final String PATH=ContentProviderContract.Schedules.TABLE_NAME;
	private static final String PATH_ALARM=ContentProviderContract.Alarms.TABLE_NAME;
	private static final String URL_ALARM="content://" + AUTHORITY + "/" + PATH_ALARM;
	private static final String URL="content://" + AUTHORITY + "/" + PATH;
	public static final Uri CONTENT_URI=Uri.parse(URL); //URI_schedules
	public static final Uri CONTENT_ALARM_URI=Uri.parse(URL_ALARM); //URI_Alarm
	
	private static final int SCHEDULES=100;
	private static final int SCHEDULES_ID=101;
	private static final int ALARMS=200;
	private static final int ALARMS_ID=201;
	
	private static UriMatcher urimatcher;
	static {
		urimatcher=new UriMatcher(UriMatcher.NO_MATCH);
		urimatcher.addURI(AUTHORITY, PATH, SCHEDULES);
		urimatcher.addURI(AUTHORITY, PATH + "/#", SCHEDULES_ID);
		urimatcher.addURI(AUTHORITY, PATH_ALARM, ALARMS);
		urimatcher.addURI(AUTHORITY, PATH_ALARM + "/#", ALARMS_ID);
	}
	
	public SchedulesProvider() {
	}
	
	@Override
	public boolean onCreate() {
		dbhelper=new DatabaseHelper(getContext());
		return true;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		/*get DB*/
		db=dbhelper.getWritableDatabase();
		long id=-1;
		/*match uri*/
		if(urimatcher.match(uri)==SCHEDULES)
		{
			id=db.insertWithOnConflict(PATH, null, values,SQLiteDatabase.CONFLICT_REPLACE);
			/*trigger listener*/
			scheduleProviderEvents(id,values,DaemonActions.ADD);
		}else if(urimatcher.match(uri)==ALARMS)
		{
			db.insertWithOnConflict(PATH_ALARM, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		}else
		{
			throw new IllegalArgumentException("Unknown URI  : " + uri);
		}
		db.close();
		/*Notify client*/
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(URL + "/" + id); //return newly added uri
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionargs) {
		/*get DB*/
		db=dbhelper.getWritableDatabase();
		int del_count=0;
		int urimatch=urimatcher.match(uri);
		/*match uri*/
		if(urimatch==SCHEDULES_ID)
		{
			del_count=db.delete(PATH, BaseColumns._ID + "=" + uri.getLastPathSegment(), null);
			scheduleProviderEvents(Long.parseLong(uri.getLastPathSegment()),null,DaemonActions.DELETE);
		}else if(urimatch==SCHEDULES)
		{
			del_count=db.delete(PATH, "1", null);
			scheduleProviderEvents(0, null, DaemonActions.DELETE_ALL);
		}else if(urimatch==ALARMS_ID)
		{
			del_count=db.delete(PATH_ALARM, Alarms.COLUMN_NAME_ID + "=" + uri.getLastPathSegment(), null);
		}else if(urimatch==ALARMS)
		{
			del_count=db.delete(PATH_ALARM, "1", null);
		}else
		{
			throw new IllegalArgumentException("Unknown URI : " + uri);
		}
		db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return del_count;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionargs,
			String sortorder) {
		db=dbhelper.getWritableDatabase();
		int urimatch=urimatcher.match(uri);
		Cursor crs=null;
		/*match uri*/
		if(urimatch==SCHEDULES)
		{
			crs=db.query(PATH, projection, selection, selectionargs, null, null, Schedules.COLUMN_NAME_TIME + " ASC");
		}else if(urimatch==ALARMS)
		{
			crs=db.query(PATH_ALARM, projection, selection, selectionargs, null, null, null);
		}else if(urimatch==SCHEDULES_ID)
		{
			crs=db.query(PATH, projection, BaseColumns._ID + "=" + uri.getLastPathSegment(), null, null, null, 
					Schedules.COLUMN_NAME_TIME + " ASC", null);
		}else if(urimatch==ALARMS_ID)
		{
			crs=db.query(PATH_ALARM, projection, Alarms.COLUMN_NAME_ID + "=" + uri.getLastPathSegment(), null, null, null, 
					null, null);
		}else
		{
			throw new IllegalArgumentException("Unknown URI : " + uri);
		}
		//db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return crs;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionargs) {
		db=dbhelper.getWritableDatabase();
		int rows=0;
		int urimatch=urimatcher.match(uri);
		/*match uri*/
		if(urimatch==SCHEDULES_ID)
		{
			rows=db.update(PATH, values, BaseColumns._ID + "=" + uri.getLastPathSegment(), null);
			scheduleProviderEvents(Long.parseLong(uri.getLastPathSegment()),values,DaemonActions.UPDATE);
		}else
		{
			throw new IllegalArgumentException("Unknown URI : " + uri);
		}
		db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return rows;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	/**
	 *Wake-up Service BR
	 */
	private Intent servisewakepu;
	
	private void scheduleProviderEvents(long id,ContentValues values,DaemonActions actions)
	{
		switch(actions)
		{
			case ADD:
				servisewakepu=new Intent(DaemonActions.ADD.getAction())
						.putExtra("id", id)
						.putExtra("time", values.getAsLong(Schedules.COLUMN_NAME_TIME));
				getContext().sendBroadcast(servisewakepu);
				break;
			case DELETE_ALL:
				delete(CONTENT_ALARM_URI, null, null);
				break;
			case DELETE:
				break;
		}
	}

}
