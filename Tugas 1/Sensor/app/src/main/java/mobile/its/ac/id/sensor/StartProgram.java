package mobile.its.ac.id.sensor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

public class StartProgram extends AppCompatActivity {
    private TextView type, existlight, valuelight, record, x, y, z, speed;
    private Sensor lightSensor, accelerometer, linearacc;
    private float myvaluelight = -1;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private boolean isrecord = false;
    private String datefile = "", datefilegyro = "", nowtime;
    private List<String[]> dataAcce = new ArrayList<String[]>();
    private float[][] dataTraining = new float[200][4];
    private float[][] dataTest = new float[50][3];
    private int counter = 0;

    private Integer myTemp;
    private String tempOns;

    public String getStatus(){
        return tempOns;
    }

    public void writeTocsv() throws IOException {
        String basedir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filepath = basedir + File.separator + datefile + "_Acce.csv";
        File file = new File(filepath);
        CSVWriter writer;
        if (file.exists() && !file.isDirectory()) {
            FileWriter fileWriter = new FileWriter(filepath);
            writer = new CSVWriter(fileWriter);
        } else {
            writer = new CSVWriter(new FileWriter(filepath));
        }
        writer.writeAll(dataAcce);
        writer.close();
    }

    public void myKnn() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "datasensor");
        File file = new File(folder, "motor.csv");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            float[][] buffer = new float[50][3];
            int a = 0, b = 0;

            while ((line = bufferedReader.readLine()) != null && a < 5000) {
                String[] temp = line.replace("\"", "").split(",");
                buffer[a % 50][0] = Float.parseFloat(temp[0]);
                buffer[a % 50][1] = Float.parseFloat(temp[1]);
                buffer[a % 50][2] = Float.parseFloat(temp[2]);
                a++;

                if (a % 50 == 0) {
                    float[] temp2 = {0, 0, 0};
                    for (int i = 0; i < 50; i++) {
                        temp2[0] = buffer[i][0] / 50;
                        temp2[1] = buffer[i][1] / 50;
                        temp2[2] = buffer[i][2] / 50;
                    }
                    dataTraining[b][0] = temp2[0];
                    dataTraining[b][1] = temp2[1];
                    dataTraining[b][2] = temp2[2];
                    b++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file2 = new File(folder, "Jalan.csv");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
            String line;
            float[][] buffer = new float[50][3];
            int a = 0, b = 100;

            while ((line = bufferedReader.readLine()) != null && a < 5000) {
                String[] temp = line.replace("\"", "").split(",");
                buffer[a % 50][0] = Float.parseFloat(temp[0]);
                buffer[a % 50][1] = Float.parseFloat(temp[1]);
                buffer[a % 50][2] = Float.parseFloat(temp[2]);
                a++;

                if (a % 50 == 0) {
                    float[] temp2 = {0, 0, 0};
                    for (int i = 0; i < 50; i++) {
                        temp2[0] = buffer[i][0] / 50;
                        temp2[1] = buffer[i][1] / 50;
                        temp2[2] = buffer[i][2] / 50;
                    }
                    dataTraining[b][0] = temp2[0];
                    dataTraining[b][1] = temp2[1];
                    dataTraining[b][2] = temp2[2];
                    b++;
                }
            }
        } catch (FileNotFoundException e) {
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
        existlight = (TextView) findViewById(R.id.textlight);
        valuelight = (TextView) findViewById(R.id.valuelight);
        type = (TextView) findViewById(R.id.type);
        x = (TextView) findViewById(R.id.rx);
        y = (TextView) findViewById(R.id.ry);
        z = (TextView) findViewById(R.id.rz);
        speed = (TextView) findViewById(R.id.kecepatan);
        record = (TextView) findViewById(R.id.record);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                location.getLatitude();
                int temp = (int) (location.getSpeed() * 3600 / 100);
                myTemp = temp;
                speed.setText("Kecepatan : "+myTemp.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //sensor
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); //TYPE_LIGHT
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearacc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        String Mytemp = (lightSensor != null) ? "Available Proximity" : "Unavailable Proximity Sensor";
        existlight.setText(Mytemp);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    valuelight.setText("" + event.values[0]);
                    myvaluelight = event.values[0];
                } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    if (myvaluelight == 0) {
                        isrecord = false;
                        datefile = (datefile.isEmpty() && datefile != null) ? new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) : datefile;
                        record.setText("Recording Data");
                        x.setText("X : " + event.values[0]);
                        y.setText("Y : " + event.values[1]);
                        z.setText("Z : " + event.values[2]);
                        nowtime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                        dataAcce.add(new String[]{
                                nowtime,
                                Float.toString(event.values[0]),
                                Float.toString(event.values[1]),
                                Float.toString(event.values[2])
                        });
                        if (counter < 50) {
                            dataTest[counter][0] = event.values[0];
                            dataTest[counter][1] = event.values[1];
                            dataTest[counter][2] = event.values[2];
                            counter++;
                        }
                        if (counter == 50) {
                            float[] temp = {0, 0, 0};
                            for (int i = 0; i < 50; i++) {
                                temp[0] += dataTest[i][0] / 50;
                                temp[1] += dataTest[i][1] / 50;
                                temp[2] += dataTest[i][2] / 50;
                            }
                            float[][] distance = new float[200][2];
                            for (int i = 0; i < 200; i++) {
                                float tempX = temp[0] - dataTraining[i][0];
                                float tempY = temp[1] - dataTraining[i][1];
                                float tempZ = temp[2] - dataTraining[i][2];
                                distance[i][0] = (tempX * tempX) + (tempY * tempY) + (tempZ * tempZ);
                                distance[i][1] = dataTraining[i][3];
                            }
                            Arrays.sort(distance, new Comparator<float[]>() {
                                @Override
                                public int compare(float[] entry1, float[] entry2) {
                                    return Float.compare(entry1[0], entry2[0]);
                                }
                            });
                            int[] sum = {0, 0};
                            for (int i = 0; i < 10; i++) {
                                if (distance[i][0] == 0) {
                                    sum[0]++;
                                } else {
                                    sum[1]++;
                                }
                            }
                            String tempOn = (sum[0] < sum[1]) ? "Sedang Naik Motor" : "Tidak Naik Motor";
                            tempOns = tempOn.equals("Sedang Naik Motor") && myTemp > 20 ? "Sedang Naik Motor" : "Tidak Naik Motor";
                            type.setText(tempOns);
                            counter = 0;
                        }

                    } else {
                        isrecord = true;
                        record.setText("Not Record Data");
                        type.setText("---");
                        x.setText("X : -");
                        y.setText("Y : -");
                        z.setText("Z : -");
                    }

                    if (isrecord) {
                        /*try {
                            writeTocsv();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        datefile = "";
                        dataAcce.clear();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        //sensor
        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, linearacc, SensorManager.SENSOR_DELAY_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
}