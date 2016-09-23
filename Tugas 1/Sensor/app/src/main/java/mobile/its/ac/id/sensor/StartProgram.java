package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StartProgram extends AppCompatActivity implements SensorEventListener {
    private TextView existlight,valuelight,record;
    private Sensor lightSensor,accelerometer;
    private float myvaluelight = -1;
    private boolean isrecord = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_program);
        existlight = (TextView)findViewById(R.id.textlight);
        valuelight = (TextView)findViewById(R.id.valuelight);
        record = (TextView)findViewById(R.id.record);
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(lightSensor!=null){
            existlight.setText("Available Light Sensor");
        }
        else{
            existlight.setText("Unavailable Light Sensor");
        }
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            valuelight.setText(""+event.values[0]);
            myvaluelight = event.values[0];
        }
        record.setText("Not Record Data");
        if(myvaluelight==0 && !isrecord) {
            record.setText("Recording Data");
            isrecord = true;
        }
        if(myvaluelight>0 && isrecord){
            record.setText("Record Data Done");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
