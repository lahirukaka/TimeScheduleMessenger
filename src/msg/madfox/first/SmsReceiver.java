package msg.madfox.first;

import madfox.colhh.saleslib.feedback.AppRatings;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public final class SmsReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		switch (getResultCode())
        {

            case Activity.RESULT_OK:
            	Toast.makeText(context, "Schedule Messenger : Successfully Sent the message...", Toast.LENGTH_LONG).show();
            	new AppRatings(context).increasePositiveImpr();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                //send_status=("Generic failure");
                //break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            	//send_status=("No service");
                //break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
            	//send_status=("Null PDU");
                //break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            	//send_status=("Radio off");
            	Toast.makeText(context, "Schedule Messenger : Failed to Send message...", Toast.LENGTH_LONG).show();
                break;
        }
	}
}
