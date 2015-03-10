package msg.madfox.first;

import madfox.colhh.saleslib.Packages;
import madfox.colhh.saleslib.feedback.AppRatings;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ViewSchedules extends Fragment implements OnClickListener {

	private ListView list;
	private TextView empty;
	
	private UserNoticeDialogs UND;
	
	public Cursor cursor;
	public ViewListAdapter adapter;
	private final Uri CONTENT_URI=SchedulesProvider.CONTENT_URI;;
	
	@Override
	public void onResume() {
		super.onResume();
		try{
			updateCursor();
		}catch(NullPointerException er){}
	}
	
	public void updateCursor() throws NullPointerException
	{
		cursor=getActivity().getContentResolver().query(CONTENT_URI, 
				new String[] {"*"}, null, null, null);
		if(cursor.getCount()>0)
		{
			empty.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			adapter=new ViewListAdapter(getActivity(), R.layout.custom_list_row, cursor, 0,this);
			list.setAdapter(adapter);
			MainActivity.LIST_EMPTY=false;
			((MainActivity)getActivity()).btn_discard.setVisibility(View.VISIBLE);
			//adapter.notifyDataSetChanged();
		}else
		{
			//adapter.notifyDataSetChanged();
			MainActivity.LIST_EMPTY=true;
			((MainActivity)getActivity()).btn_discard.setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.view_schedule, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*views init*/
		list=(ListView)getView().findViewById(R.id.view_list);
		empty=(TextView)getView().findViewById(R.id.view_empty);
		
		if(savedInstanceState==null)
		{
			/*get ratings*/
			new AppRatings(getActivity()).checkAndAsk(Packages.SSP,getActivity());
		}
	}

	/*on item delete click*/
	@Override
	public void onClick(View v) {
		final View fv=v;
		UND=new UserNoticeDialogs("Schedule Deletion", R.drawable.question, "Schedule will be deleted. Are you sure ?");
		UND.setButtons(true, true, new String[] {"Yes","No"},new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(DialogInterface.BUTTON_POSITIVE==which)
				{
					long id=Long.parseLong(fv.getTag().toString());
					getActivity().getContentResolver().delete(Uri.parse(CONTENT_URI + "/" + id), null, null);
					LogicsOfApp.cancelSchedule(id,getActivity());
					updateCursor();
				} 
			}
		});
		UND.show(getActivity().getSupportFragmentManager(), null);
		
	}
}
