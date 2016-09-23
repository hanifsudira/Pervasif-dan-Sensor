package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StartProgram extends AppCompatActivity{
    private TextView existlight,valuelight,record,x,y,z;;
    private Sensor lightSensor,accelerometer;
    private float myvaluelight = -1;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_program);
        existlight = (TextView)findViewById(R.id.textlight);
        valuelight = (TextView)findViewById(R.id.valuelight);
        x = (TextView)findViewById(R.id.rx);
        y = (TextView)findViewById(R.id.ry);
        z = (TextView)findViewById(R.id.rz);
        record = (TextView)findViewById(R.id.record);

        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(lightSensor!=null){
            existlight.setText("Available Light Sensor");
        }
        else{
            existlight.setText("Unavailable Light Sensor");
        }
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                    valuelight.setText(""+event.values[0]);
                    myvaluelight = event.values[0];
                }
                else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    if(myvaluelight==0) {
                        record.setText("Recording Data");
                        x.setText("x : "+event.values[0]);
                        y.setText("y : "+event.values[1]);
                        z.setText("z : "+event.values[2]);

                    }
                    else{
                        record.setText("Record Data Done");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
