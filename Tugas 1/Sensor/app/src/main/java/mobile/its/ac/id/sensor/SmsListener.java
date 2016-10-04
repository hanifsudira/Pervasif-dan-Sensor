package mobile.its.ac.id.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by PEKANBARU on 10/4/2016.
 */

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String message_from;
        if(bundle!=null){
            try{
                Object[] pdus = (Object[]) bundle.get("pdus");
                messages = new SmsMessage[pdus.length];
                for(int i=0;i<messages.length;i++){
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    message_from = messages[i].getOriginatingAddress();
                    String messageBody = messages[i].getMessageBody();
                }
            }
            catch (Exception e){
                Log.d("Exception caught",e.getMessage());
            }
        }
    }
}