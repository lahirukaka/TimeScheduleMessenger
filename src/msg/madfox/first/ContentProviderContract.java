package msg.madfox.first;

import android.provider.BaseColumns;

public final class ContentProviderContract {

	/* DB */
	public static final String DBNAME = "msgschedules.db";
	public static final int DB_VERSION = 1;

	public abstract static class Schedules implements BaseColumns {
		public static final String TABLE_NAME = "schedules";
		public static final String COLUMN_NAME_NUMBER = "number";
		public static final String COLUMN_NAME_MSG = "message";
		public static final String COLUMN_NAME_TIME = "time";

		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
				+ " ( " + _ID + " INTEGER PRIMARY KEY," + COLUMN_NAME_NUMBER
				+ " TEXT," + COLUMN_NAME_MSG + " TEXT," + COLUMN_NAME_TIME
				+ " INTEGER ); ";
	}

	public abstract static class Alarms {
		public static final String TABLE_NAME = "alarms";
		public static final String COLUMN_NAME_ID="_ID";
		public static final String COLUMN_NAME_URI = "uri";
		
		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
				+ " ( " + COLUMN_NAME_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME_URI + " TEXT ); ";
	}
}