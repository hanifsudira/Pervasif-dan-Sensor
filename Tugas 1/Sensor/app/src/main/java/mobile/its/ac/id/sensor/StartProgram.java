package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StartProgram extends AppCompatActivity{
    private TextView existlight,valuelight,record,x,y,z;;
    private Sensor lightSensor,accelerometer;
    private float myvaluelight = -1;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private boolean isrecord = false;
    private String datefile = "" , nowtime;
    private List<String[]> data = new ArrayList<String[]>();

    public void writeTocsv() throws IOException{
        String basedir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filepath = basedir+File.separator+datefile+".csv";
        File file = new File(filepath);
        CSVWriter writer;
        if(file.exists() && !file.isDirectory()){
            FileWriter fileWriter = new FileWriter(filepath);
            writer = new CSVWriter(fileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filepath));
        }
        writer.writeAll(data);
        writer.close();
    }

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

        String Mytemp = (lightSensor!=null) ? "Available Light Sensor" : "Unavailable Light Sensor";
        existlight.setText(Mytemp);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                    valuelight.setText(""+event.values[0]);
                    myvaluelight = event.values[0];
                }
                else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    if(myvaluelight==0) {
                        isrecord = false;
                        datefile = (datefile.isEmpty() && datefile!=null) ? new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) : datefile;
                        record.setText("Recording Data");
                        x.setText("x : "+event.values[0]);
                        y.setText("y : "+event.values[1]);
                        z.setText("z : "+event.values[2]);
                        nowtime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                        data.add(new String[]{
                                    nowtime,
                                    Float.toString(event.values[0]),
                                    Float.toString(event.values[1]),
                                    Float.toString(event.values[2])
                                });
                    }
                    else{
                        isrecord = true;
                        record.setText("Record Data Done");
                        try {
                            writeTocsv();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(isrecord){
                        datefile = "";
                        data.clear();
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
