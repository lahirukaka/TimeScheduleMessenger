package msg.madfox.first;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class ViewListAdapter extends ResourceCursorAdapter {

	private long timelong;
	private ViewSchedules view_frag;
	
	public ViewListAdapter(Context context, int layout, Cursor c,
			int flags,ViewSchedules view_frag) {
		super(context, layout, c, flags);
		this.view_frag=view_frag;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		/*init views*/
		TextView number=(TextView)view.findViewById(R.id.view_list_number);
		TextView time=(TextView)view.findViewById(R.id.view_list_time);
		TextView msg=(TextView)view.findViewById(R.id.view_list_msg);
		TextView delete=(TextView)view.findViewById(R.id.view_list_delete);
		/*setting values*/
		number.setText(cursor.getString(cursor.getColumnIndex(ContentProviderContract.Schedules.COLUMN_NAME_NUMBER)));
		timelong=Long.parseLong(cursor.getString(cursor.getColumnIndex(ContentProviderContract.Schedules.COLUMN_NAME_TIME)));
		time.setText(LogicsOfApp.getStringDate(timelong));
		msg.setText(cursor.getString(cursor.getColumnIndex(ContentProviderContract.Schedules.COLUMN_NAME_MSG)));
		delete.setTag(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
		delete.setOnClickListener(view_frag);
	}
}
