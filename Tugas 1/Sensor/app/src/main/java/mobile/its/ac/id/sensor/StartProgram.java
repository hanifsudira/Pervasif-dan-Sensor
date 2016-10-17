package mobile.its.ac.id.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.opencsv.CSVWriter;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StartProgram extends AppCompatActivity{
    private TextView type,existlight,valuelight,record,x,y,z,gx,gy,gz;
    private Sensor lightSensor,accelerometer,gyrometer;
    private float myvaluelight = -1;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private boolean isrecord,isrecordGyro = false;
    private String datefile = "", datefilegyro = "", nowtime;
    private List<String[]> dataAcce = new ArrayList<String[]>();
    private List<String[]> dataGyro = new ArrayList<String[]>();
    private float[][] dataTraining = new float[200][4];
    private float[][] dataTest = new float[50][3];
    private int counter = 0;

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

    public void myKnn(){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"datasensor");
        File file = new File(folder,"motor.csv");
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            float[][] buffer = new float[50][3];
            int a=0,b=0;

            while((line = bufferedReader.readLine())!=null && a < 5000){
                String[] temp = line.replace("\"","").split(",");
                buffer[a%50][0] = Float.parseFloat(temp[0]);
                buffer[a%50][1] = Float.parseFloat(temp[1]);
                buffer[a%50][2] = Float.parseFloat(temp[2]);
                a++;

                if(a%50==0){
                    float[] temp2 = {0,0,0};
                    for(int i=0;i<50;i++){
                        temp2[0] = buffer[i][0]/50;
                        temp2[1] = buffer[i][1]/50;
                        temp2[2] = buffer[i][2]/50;
                    }
                    dataTraining[b][0] = temp2[0];
                    dataTraining[b][1] = temp2[1];
                    dataTraining[b][2] = temp2[2];
                    b++;
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file2 = new File(folder,"Jalan.csv");
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
            String line;
            float[][] buffer = new float[50][3];
            int a=0,b=100;

            while((line = bufferedReader.readLine())!=null && a < 5000){
                String[] temp = line.replace("\"","").split(",");
                buffer[a%50][0] = Float.parseFloat(temp[0]);
                buffer[a%50][1] = Float.parseFloat(temp[1]);
                buffer[a%50][2] = Float.parseFloat(temp[2]);
                a++;

                if(a%50==0){
                    float[] temp2 = {0,0,0};
                    for(int i=0;i<50;i++){
                        temp2[0] = buffer[i][0]/50;
                        temp2[1] = buffer[i][1]/50;
                        temp2[2] = buffer[i][2]/50;
                    }
                    dataTraining[b][0] = temp2[0];
                    dataTraining[b][1] = temp2[1];
                    dataTraining[b][2] = temp2[2];
                    b++;
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_program);
        myKnn();
        existlight = (TextView)findViewById(R.id.textlight);
        valuelight = (TextView)findViewById(R.id.valuelight);
        type = (TextView)findViewById(R.id.type);
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
                        /*dataAcce.add(new String[]{
                                    nowtime,
                                    Float.toString(event.values[0]),
                                    Float.toString(event.values[1]),
                                    Float.toString(event.values[2])
                                });*/
                        if(counter<50){
                            dataTest[counter][0] = event.values[0];
                            dataTest[counter][1] = event.values[1];
                            dataTest[counter][2] = event.values[2];
                            counter++;
                        }
                        if(counter==50){
                            float[] temp = {0,0,0};
                            for(int i=0;i<50;i++){
                                temp[0] += dataTest[i][0]/50;
                                temp[1] += dataTest[i][1]/50;
                                temp[2] += dataTest[i][2]/50;
                            }
                            float[][] distance = new float[200][2];
                            for(int i=0;i<200;i++){
                                float tempX = temp[0]-dataTraining[i][0];
                                float tempY = temp[1]-dataTraining[i][1];
                                float tempZ = temp[2]-dataTraining[i][2];
                                distance[i][0] = (tempX*tempX)+(tempY*tempY)+(tempZ*tempZ);
                                distance[i][1] = dataTraining[i][3];
                            }
                            Arrays.sort(distance,new Comparator<float[]>(){
                                @Override
                                public int compare(float[] entry1,float[] entry2){
                                   return Float.compare(entry1[0],entry2[0]);
                                }
                            });
                            int[] sum = {0,0};
                            for(int i=0;i<10;i++){
                                if(distance[i][0]==0){
                                    sum[0]++;
                                }
                                else {
                                    sum[1]++;
                                }
                            }
                            String tempOn = (sum[0]<sum[1]) ? "Sedang Naik Motor" : "Tidak Naik Motor" ;
                            type.setText(tempOn);
                            counter = 0;
                        }

                    }
                    else{
                        isrecord = true;
                        record.setText("Not Record Data");
                        x.setText("X : -");
                        y.setText("Y : -");
                        z.setText("Z : -");
                    }

                    if(isrecord){
                        /*try {
                            writeTocsv();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
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
                        /*dataGyro.add(new String[]{
                                nowtime,
                                Float.toString(event.values[0]),
                                Float.toString(event.values[1]),
                                Float.toString(event.values[2])
                        });*/
                    }
                    else{
                        isrecordGyro = true;
                        gx.setText("X : -");
                        gy.setText("Y : -");
                        gz.setText("X : -");
                    }
                    if(isrecordGyro){
                        /*try{
                            writeTocsvGyro();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }*/
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
        sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener,gyrometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
