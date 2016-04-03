package ubicomp.cs.uml.edu.smartfridge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    //Variable Declaration
    private SensorManager sensorManager = null;

    //Accelerometer Values
    private TextView accX;
    private TextView accY;
    private TextView accZ;

    //Orientation Values
    private TextView magX;
    private TextView magY;
    private TextView magZ;

    //Sensors
    private Sensor AccSensor;
    private Sensor MagSensor;

    //Value arrays
    private float[] lastAcclerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean hasAccelerometerSet = false;

    private float[] magField = new float[9];
    private float[] MagOrientation = new float[3];

    //Low Pass Filter Values
    static final float ALPHA = 0.2f;

    //Time Stamp
    private String timeStamp;
    private String outputFile;

    //log Data to a File
    private StringBuilder writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            //Text Views for Showing Outputs
            accX = (TextView) findViewById(R.id.textView01);
            accY = (TextView) findViewById(R.id.textView02);
            accZ = (TextView) findViewById(R.id.textView03);

            magX = (TextView) findViewById(R.id.textView04);
            magY = (TextView) findViewById(R.id.textView05);
            magZ = (TextView) findViewById(R.id.textView06);


            accX.setText("initial test string");

            timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            outputFile =Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+timeStamp+".csv";
            writer = new StringBuilder();

        }

        catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("OnRESUME", "called");
        hasAccelerometerSet = false;
        AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        MagSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, MagSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("OnSTOP", "called");
        hasAccelerometerSet = false;
        sensorManager.unregisterListener(this, AccSensor);
        sensorManager.unregisterListener(this, MagSensor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do Nothing

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            try{
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);

                //Getting the Accelerometer Values
                if (event.sensor == AccSensor){
                    lastAcclerometer = lowpass(event.values,lastAcclerometer);

                    accX.setText("x: "+ nf.format(lastAcclerometer[0]));
                    accY.setText("y: "+ nf.format(lastAcclerometer[1]));
                    accZ.setText("z: " + nf.format(lastAcclerometer[2]));
                    hasAccelerometerSet = true;
                    writeToFile(writer.toString());

                }

                //Getting the Magnetometer Values
                else if (event.sensor == MagSensor) {
                    lastMagnetometer = lowpass(event.values,lastMagnetometer);
                    if (hasAccelerometerSet) {
                        SensorManager.getRotationMatrix(magField, null, lastAcclerometer, lastMagnetometer);
                        SensorManager.getOrientation(magField, MagOrientation);
                        magX.setText("x: " + nf.format(Math.toDegrees(MagOrientation[0])));
                        magY.setText("y: "+nf.format(Math.toDegrees(MagOrientation[1])));
                        magZ.setText("z: "+nf.format(Math.toDegrees(MagOrientation[2])));
                        writer.append(nf.format(lastAcclerometer[0]) + "," + nf.format(lastAcclerometer[1]) + "," + nf.format(lastAcclerometer[1]) + ","+nf.format(Math.toDegrees(MagOrientation[0])) + "," + nf.format(Math.toDegrees(MagOrientation[1])) + "," + nf.format(Math.toDegrees(MagOrientation[2])) + "\n");
                    }
                }
                //Error Management
                else {
                    Log.d("sensor", "got other sensor event " + event.sensor.getType());
                }

            }
            catch (Throwable th){
                th.printStackTrace();
            }
        }
    }


    // Implementation of Low Pass to reduce noise of Sensor Data
    private float[] lowpass(float[] sensorInput, float[] sensorOutput) {
        if(sensorOutput == null){
            return sensorInput;
        }
        for(int i=0; i < sensorInput.length; i++){
            sensorOutput[i] = sensorOutput[i] + ALPHA * (sensorInput[i] - sensorOutput[i]);
        }
        return sensorOutput;
    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fout = new FileOutputStream (new File(outputFile).getAbsolutePath(),true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fout);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
