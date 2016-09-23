package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private TextView Myx,Myy,Myz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        Myx = (TextView)findViewById(R.id.myx);
        Myy = (TextView)findViewById(R.id.myy);
        Myz = (TextView)findViewById(R.id.myz);
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor accelermoter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelermoter,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Myx.setText("X: "+event.values[0]+"m/s\u00B2");
        Myy.setText("Y: "+event.values[1]+"m/s\u00B2");
        Myz.setText("Z: "+event.values[2]+"m/s\u00B2");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
