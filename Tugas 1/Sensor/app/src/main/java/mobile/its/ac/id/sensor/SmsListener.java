package mobile.its.ac.id.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;

import weka.core.Instance;
import weka.core.SparseInstance;

/**
 * Created by PEKANBARU on 10/4/2016.
 */

public class SmsListener extends BroadcastReceiver implements SensorEventListener{
    private SensorManager sensorManager;
    private LocationManager locationManager;

    private StartProgram startProgram;

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private int speed;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String messageFrom = "", messageBody = "";
        if(action.equals(SMS_RECEIVED)){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i=0;i<pdus.length;i++){
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    messageFrom = messages[i].getOriginatingAddress();
                    messageBody = messages[i].getMessageBody();
                }
                //debug
                Log.d("Pesan", messageBody);
                if(startProgram.getStatus().equals("Sedang Naik Motor")){
                    String message = "Maaf saya sedang dalam berkendara, harap hubungi nanti";
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(messageFrom, null, message, null, null);
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
