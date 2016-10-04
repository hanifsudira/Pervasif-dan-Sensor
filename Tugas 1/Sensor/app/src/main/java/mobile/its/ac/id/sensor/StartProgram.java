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
    private TextView existlight,valuelight,record,x,y,z,gx,gy,gz;
    private Sensor lightSensor,accelerometer,gyrometer;
    private float myvaluelight = -1;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private boolean isrecord,isrecordGyro = false;
    private String datefile = "", datefilegyro = "", nowtime;
    private List<String[]> dataAcce = new ArrayList<String[]>();
    private List<String[]> dataGyro = new ArrayList<String[]>();

    public void writeTocsv() throws IOException{
        String basedir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filepath = basedir+File.separator+datefile+"_Acce.csv";
        File file = new File(filepath);
        CSVWriter writer;
        if(file.exists() && !file.isDirectory()){
            FileWriter fileWriter = new FileWriter(filepath);
            writer = new CSVWriter(fileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filepath));
        }
        writer.writeAll(dataAcce);
        writer.close();
    }

    public void writeTocsvGyro() throws  IOException{
        String basedir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filepath = basedir+File.separator+datefilegyro+"_Gyro.csv";
        File file = new File(filepath);
        CSVWriter writer;
        if(file.exists() && !file.isDirectory()){
            FileWriter fileWriter = new FileWriter(filepath);
            writer = new CSVWriter(fileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filepath));
        }
        writer.writeAll(dataGyro);
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
        gx = (TextView)findViewById(R.id.gx);
        gy = (TextView)findViewById(R.id.gy);
        gz = (TextView)findViewById(R.id.gz);
        record = (TextView)findViewById(R.id.record);

        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); //TYPE_LIGHT
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrometer = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        String Mytemp = (lightSensor!=null && gyrometer!=null) ? "Available Proximity & Gyro Sensor" : "Unavailable Proximity & Gyro Sensor";
        existlight.setText(Mytemp);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
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
                        dataAcce.add(new String[]{
                                    nowtime,
                                    Float.toString(event.values[0]),
                                    Float.toString(event.values[1]),
                                    Float.toString(event.values[2])
                                });
                    }
                    else{
                        isrecord = true;
                        record.setText("Not Record Data");
                        x.setText("X : -");
                        y.setText("Y : -");
                        z.setText("Z : -");
                    }

                    if(isrecord){
                        try {
                            writeTocsv();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        datefile = "";
                        dataAcce.clear();
                    }
                }
                else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
                    if(myvaluelight==0){
                        isrecordGyro = false;
                        datefilegyro = (datefilegyro.isEmpty() && datefilegyro!=null) ? new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) : datefilegyro;
                        gx.setText("x : "+event.values[0]);
                        gy.setText("y : "+event.values[1]);
                        gz.setText("z : "+event.values[2]);
                        nowtime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                        dataGyro.add(new String[]{
                                nowtime,
                                Float.toString(event.values[0]),
                                Float.toString(event.values[1]),
                                Float.toString(event.values[2])
                        });
                    }
                    else{
                        isrecordGyro = true;
                        gx.setText("X : -");
                        gy.setText("Y : -");
                        gz.setText("X : -");
                    }
                    if(isrecordGyro){
                        try{
                            writeTocsvGyro();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        datefilegyro = "";
                        dataGyro.clear();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,gyrometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
