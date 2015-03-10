package msg.madfox.first;

import java.util.Calendar;

import madfox.colhh.saleslib.Packages;
import madfox.colhh.saleslib.adsdisplayer.AppPager;
import msg.madfox.first.ListDialogBox.ListDialogListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public final class AddSchedules extends Fragment implements OnTouchListener,
		OnDateSetListener,OnTimeSetListener,ListDialogListener {

	private final int PICK_CONTACT=1;
	
	private DatePickerDialog dp;
	private TimePickerDialog tp;
	private Calendar cal;

	/* Views */
	private EditText txt_msg;
	private EditText txt_tel;
	private Button btn_pb;
	private Button btn_set_dt;
	private TextView txt_schedule;

	/*vars*/
	private String s_date=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.add_schedules, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/* init views */
		View v=getView();
		txt_msg = (EditText) v.findViewById(R.id.add_msg);
		txt_tel = (EditText) v.findViewById(R.id.add_txt_tel);
		btn_pb = (Button) v.findViewById(R.id.add_btn_pb);
		btn_set_dt = (Button) v.findViewById(R.id.add_btn_pick_date);
		txt_schedule=(TextView) v.findViewById(R.id.add_txt_schedule);
		btn_pb.setOnTouchListener(this);
		btn_set_dt.setOnTouchListener(this);
		txt_msg.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				MainActivity.SCHEDULED_MSG=s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
			}
		});
		txt_tel.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				MainActivity.SCHEDULED_NUM=s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
			}
		});
		if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
		{
			getFragmentManager().beginTransaction()
				.replace(R.id.add_layout_adz, new AppPager(0,
						Packages.BD_WISHES_SENDER,
						Packages.JOKES_SENDER,
						Packages.QUOTES_SENDER,
						Packages.XMAS_SENDER,
						Packages.SEC_CON
						).setRotateTime(3000))
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
		}
	}
	
	/*Setters*/
	public void setPhoneNumber(String number)
	{
		this.txt_tel.setText(number);
	}
	
	/*clean all*/
	public void cleanAll()
	{
		try{
		txt_msg.setText("");	txt_schedule.setText(R.string.add_schedule_txt);
		txt_tel.setText("");
		}catch(NullPointerException er){}
		MainActivity.SCHEDULED_MSG="";	MainActivity.SCHEDULED_NUM="";
		MainActivity.SCHEDULED_TIME=0;
	}
	
	/* Touch Listener */
	@Override
	@SuppressLint("ClickableViewAccessibility") public boolean onTouch(View v, MotionEvent event) {
		Button view = (Button) v;
		//view.performClick();
		if (view == btn_pb && MotionEvent.ACTION_UP == event.getAction()) {
			/*Let user pick from contacts*/
			Intent phonebook=new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(phonebook, PICK_CONTACT);
			return true;
		} else if (view == btn_set_dt && MotionEvent.ACTION_UP == event.getAction()) {
			cal = Calendar.getInstance();

			this.dp = new DatePickerDialog(getActivity(),
					android.R.style.Theme_Holo_Light_Dialog, AddSchedules.this,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DATE));
			dp.setTitle("Set the Date");
			dp.show();
			return true;
		} else {
			return false;
		}
	}

	/* Date picker dialog listener */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if(view.isShown()){
			s_date=dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
			tp=new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, 
					AddSchedules.this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
			tp.setTitle("Set the Time");
			tp.show();
			//Log.w("DatePickerListener", "opens time picker");
		}
	}

	/*Time picker dialog listener*/
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(view.isShown())
		{
			s_date+="-"+hourOfDay+":"+minute;
			MainActivity.SCHEDULED_TIME=LogicsOfApp.getLongDate(s_date);
			txt_schedule.setText(LogicsOfApp.getStringDate(MainActivity.SCHEDULED_TIME));
			//Log.w("TimePickerListener", "setting date");
		}
	}
	
	/*On contact pick*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==PICK_CONTACT && resultCode==Activity.RESULT_OK)
		{
			LogicsOfApp.pickContact(getActivity(), data,this);
		}
	}

	@Override
	public void onListItemClick(View view, int position, String text) {
		if(text!=null)
		{
			this.txt_tel.setText(text);
		}
	}
}
