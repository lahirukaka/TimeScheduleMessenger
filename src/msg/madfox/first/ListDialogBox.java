package msg.madfox.first;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class ListDialogBox extends DialogFragment {

	private ListView list;
	private ArrayList<String> numbers;
	private String title;
	
	public ListDialogBox() {}
	
	public ListDialogBox(ArrayList<String> numbers,String title)
	{
		this.numbers=numbers;
		this.title=title;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.list_dialog, container);
		list=(ListView)view;
		return view;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		list=null;
		numbers.clear();
		numbers=null;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		
		getDialog().setTitle(title);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, this.numbers);
		try{
			list.setAdapter(adapter);
		}catch(Exception er){}
		
		list.setOnItemClickListener(new OnItemClickListener() {
			TextView text;
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				text=(TextView)view.findViewById(android.R.id.text1);
				if(LDL!=null){LDL.onListItemClick(view, position, text.getText().toString());}
				dismiss();
			}
			
		});
	}
	
	/*interface stuff*/
	ListDialogListener LDL;
	interface ListDialogListener
	{
		public void onListItemClick(View view,int position,String text);
	}
	
	public void setListDialogListener(ListDialogListener LDL)
	{
		this.LDL=LDL;
	}
}
