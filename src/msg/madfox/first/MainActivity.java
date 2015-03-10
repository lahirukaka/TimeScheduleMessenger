package msg.madfox.first;

import madfox.colhh.saleslib.feedback.AppRatings;
import msg.madfox.first.UserNoticeDialogs.NoticeDialogListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ironsource.mobilcore.CallbackResponse;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

public final class MainActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

	public static long SCHEDULED_TIME=0;
	public static String SCHEDULED_MSG=null;
	public static String SCHEDULED_NUM=null;
	public static boolean LIST_EMPTY=true;
	
	//public static String ACTION_UPDATE_LIST="msg.madfox.first.ACTION_UPDATE_LIST";
	public static String MC_DEV_CODE="5RKJ8UBJW6X8MDNDSPE6F8JIBSGDP";
	
	ViewPager pager;
	TabAdapter TA;
	
	/*ActionBar*/
	private Button btn_add;
	Button btn_discard;
	private Button btn_view;
	private Button btn_refresh;
	
	private UserNoticeDialogs UND;
	
	public MainActivity() {
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null)
        {
        	new AppRatings(this).increaseViews();
        }
        setContentView(R.layout.activity_main);
        /*MC*/
        MobileCore.init(this,MC_DEV_CODE, LOG_TYPE.DEBUG,AD_UNITS.INTERSTITIAL); 
        /*object init*/
        pager=(ViewPager)findViewById(R.id.main_pager);
        pager.setAdapter(TA=new TabAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.custom_actionbar, null);
        getActionBar().setCustomView(v);
        
        btn_add=(Button)v.findViewById(R.id.action_add);
        btn_discard=(Button)v.findViewById(R.id.action_delete_all);
        btn_view=(Button)v.findViewById(R.id.action_view);
        btn_refresh=(Button)v.findViewById(R.id.action_refresh);
        btn_add.setOnClickListener(this);
        btn_discard.setOnClickListener(this);
        btn_view.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return false;
    }


	@Override
	public void onPageScrollStateChanged(int position) {	
		//Log.w("onPageScrollStateChanged", position+"_");
	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		//Log.w("onPageScrolled", arg0+"-"+arg1+"-"+arg2);
	}


	@Override
	public void onPageSelected(int position) {
		if(position==2)
		{
			/*ActionBar*/
			try
			{
				btn_view.setVisibility(View.GONE);
				btn_refresh.setVisibility(View.GONE);
			}catch (Exception e) {}
			
			if(SCHEDULED_MSG==null || SCHEDULED_MSG.length()<=0 || 
					SCHEDULED_NUM==null || SCHEDULED_NUM.length()<=0 || 
					SCHEDULED_TIME<=0)
			{
				/*move to add schedule fragment to gather needed params*/
				UND=new UserNoticeDialogs("Incomplete Schedule", R.drawable.error_dark, 
						"Message , Phone number and Time must be set");
				UND.setButtons(true, false, new String[] {"OK"}, null);
				UND.setNoticeDialogListener(new NoticeDialogListener() {
					@Override
					public void onNoticeDismiss() {
						pager.setCurrentItem(1,true);
						UND=null;
					}
				});
				UND.show(getSupportFragmentManager().beginTransaction(), "errorincomplete");
			}else
			{
				new AsyncTask<Void, Void, Integer>() {
					@Override
					protected Integer doInBackground(Void... params) {
						ContentValues values=new ContentValues();
						values.put(ContentProviderContract.Schedules.COLUMN_NAME_MSG, SCHEDULED_MSG);
						values.put(ContentProviderContract.Schedules.COLUMN_NAME_NUMBER, SCHEDULED_NUM);
						values.put(ContentProviderContract.Schedules.COLUMN_NAME_TIME, SCHEDULED_TIME);
						int result=(getContentResolver().insert(SchedulesProvider.CONTENT_URI, values)
								.getLastPathSegment().toString()!="-1")?1:0;
						return result;
					}
					
					@Override
					protected void onPostExecute(Integer result) {
						String msg=null;
						if(result==1)msg="Successfully Scheduled!";
						else if(result==0)msg="Failed to Schedule!";
						((AddSchedules)TA.add).cleanAll();
						Toast toast=Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						LogicsOfApp.waitThread(1000);
						pager.setCurrentItem(0, true);
						MobileCore.showInterstitial(MainActivity.this, null);
					}
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
			}
		}else if(position==0)
		{
			try{
			((ViewSchedules)TA.view).updateCursor();
			if(!LIST_EMPTY)btn_discard.setVisibility(View.VISIBLE);
			else btn_discard.setVisibility(View.GONE);
			}catch (NullPointerException e) {
			}finally
			{
				btn_add.setVisibility(View.VISIBLE);
				btn_view.setVisibility(View.GONE);
				btn_refresh.setVisibility(View.VISIBLE);
			}
		}else if(position==1)
		{
			try{
				btn_add.setVisibility(View.GONE);
				btn_discard.setVisibility(View.GONE);
				btn_view.setVisibility(View.VISIBLE);
				btn_refresh.setVisibility(View.GONE);
			}catch(NullPointerException er){}
		}
	}

	@Override
	public void onClick(View view) {
        switch(view.getId())
        {
        	case R.id.action_add:
        		pager.setCurrentItem(1, true);
        		break;
        	case R.id.action_delete_all:
        		UND=new UserNoticeDialogs("Delete All ?", R.drawable.question, "All schedules will be deleted.");
        		UND.setButtons(true, true, new String[] {"Delete","Cancel"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(DialogInterface.BUTTON_POSITIVE==which)
						{
							int del=getContentResolver().delete(SchedulesProvider.CONTENT_URI, null, null);
							Toast toast=Toast.makeText(MainActivity.this, "Canceled " + del +  " schedules.", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				});
        		UND.show(getSupportFragmentManager(),"deleteall");
        		break;
        	case R.id.action_view:
        		pager.setCurrentItem(0,true);
        		break;
        	case R.id.action_refresh:
        		try{
        			((ViewSchedules)TA.view).updateCursor();
        		}catch(NullPointerException er){}
        		break;
        }
	}
	
	@Override
	public void onBackPressed() {
		MobileCore.showInterstitial(this, new CallbackResponse() {
			@Override
			public void onConfirmation(TYPE type) {
				MainActivity.this.finish();
			}
		});
			
	}
}