package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListSensor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensor);

        ListView listView = (ListView)findViewById(R.id.listview);
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        ArrayList<String> listSensor = new ArrayList<String>();
        for (Sensor sensor : sensors){
            listSensor.add(sensor.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_list_view,listSensor);
        listView.setAdapter(adapter);
    }
}
