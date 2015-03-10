package msg.madfox.first;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public final class TabAdapter extends FragmentPagerAdapter {

	Fragment view;
	Fragment add;
	Fragment dos;
	
	public TabAdapter(FragmentManager fm) {
		super(fm);
		view=new ViewSchedules();
		add=new AddSchedules();
		dos=new DoSchedule();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment frag=null;
		switch(position)
		{
			case 0:
				frag=view;
				break;
			case 1:
				frag=add;
				break;
			case 2:
				frag=dos;
				break;
		}
		return frag;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		CharSequence title=null;
		switch(position)
		{
			case 0:
				title="View Schedules";
				break;
			case 1:
				title="Add New Schedule";
				break;
			case 2:
				title="Confirm Schedule";
				break;
		}
		return title;
	}
}
