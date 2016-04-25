package ubicomp.cs.uml.edu.smartfridge;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

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

    private TextView Banner;
    private TextView Status;

    //Accelerometer Values
    private TextView accX;
    private TextView accY;
    private TextView accZ;

    //Orientation Values
    private TextView magX;
    private TextView magY;
    private TextView magZ;

    //Intent Handlers
    private IntentManager intentManager;

    private int Orientation;

    //yaw pitch and roll
    private double rmYaw;
    private double rmPitch;
    private double rmRoll;

    //Sensors
    private Sensor AccSensor;
    private Sensor MagSensor;

    //Value arrays
    private float[] lastAcclerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean hasAccelerometerSet = false;

    private float[] mRotMat = new float[9];
    private float[] MagOrientation = new float[3];

    //Low Pass Filter Values
    static final float ALPHA = 0.2f;

    //Open Event
    private float doorVel;
    private float doorVelCurrent;
    private float doorVelLast;

    //Time S
    private String timeStamp;
    private String outputFile;

    //log Data to a File
    private StringBuilder writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try {

            intentManager = new IntentManager();
            //Text Views for Showing Outputs
            accX = (TextView) findViewById(R.id.textView01);
            accY = (TextView) findViewById(R.id.textView02);
            accZ = (TextView) findViewById(R.id.textView03);

            magX = (TextView) findViewById(R.id.textView04);
            magY = (TextView) findViewById(R.id.textView05);
            magZ = (TextView) findViewById(R.id.textView06);

            Banner = (TextView) findViewById(R.id.textView2);
            Status = (TextView) findViewById(R.id.textView);

            accX.setText("initial test string");

            timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            outputFile =Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+timeStamp+".csv";
            writer = new StringBuilder();

            //initialising Door Open Close Calculators
            doorVel = 0.00f;
            doorVelCurrent = 0.01f;
            doorVelLast = 0.01f;

            Thread.sleep(1000);

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
        //Registering Sensors
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
        //Un-Registering Sensors
        sensorManager.unregisterListener(this, AccSensor);
        sensorManager.unregisterListener(this, MagSensor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //No Settings Menu As of Now
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(SensorActivity.this, FaceTrackerActivity.class);
            SensorActivity.this.startActivity(myIntent);
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
//         synchronized (this) {
            try{
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                Orientation = getResources().getConfiguration().orientation;
                //Getting the Accelerometer Values
                if (event.sensor == AccSensor){

                    lastAcclerometer[0]=(lastAcclerometer[0]*2+event.values[0])*0.33334f;
                    lastAcclerometer[1]=(lastAcclerometer[1]*2+event.values[1])*0.33334f;
                    lastAcclerometer[2]=(lastAcclerometer[2]*2+event.values[2])*0.33334f;

                    accX.setText("x: "+ nf.format(lastAcclerometer[0]));
                    accY.setText("y: "+ nf.format(lastAcclerometer[1]));
                    accZ.setText("z: " + nf.format(lastAcclerometer[2]));

                    hasAccelerometerSet = true;
                }

                //Getting the Magnetometer Values
                else if (event.sensor == MagSensor) {

                    if (hasAccelerometerSet) {
                        SensorManager.getRotationMatrix(mRotMat, null, lastAcclerometer, lastMagnetometer);
                        SensorManager.getOrientation(mRotMat, lastMagnetometer);
                        lastMagnetometer[0]=(lastMagnetometer[0]*1+event.values[0])*0.5f;
                        lastMagnetometer[1]=(lastMagnetometer[1]*1+event.values[1])*0.5f;
                        lastMagnetometer[2]=(lastMagnetometer[2]*1+event.values[2])*0.5f;
                        lastMagnetometer = lowpass(event.values,lastMagnetometer);

                    }
                }
                //Detect the door status
                if((event.sensor == MagSensor) || (event.sensor == AccSensor))
                {
                    float newMat[]=new float[16];
                    SensorManager.getRotationMatrix(newMat, null, lastAcclerometer, lastMagnetometer);


                        SensorManager.remapCoordinateSystem(newMat,SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,newMat);


                    //Calcuting Yaw, Pitch and Roll using Eulers Angles Equation https://en.wikipedia.org/wiki/Euler_angles
                    rmYaw = Math.toDegrees(Math.atan2(newMat[4], newMat[0]));
                    rmPitch = Math.toDegrees(Math.acos(-newMat[8]));
                    rmRoll = Math.toDegrees(Math.atan2(newMat[9], newMat[10]));

                    magX.setText("yaw: " + (int) Math.round(rmYaw));
                    magY.setText("pitch: " + (int) Math.round(rmPitch));
                    magZ.setText("roll: " + (int) Math.round(rmRoll));


                    writer.append(nf.format(lastAcclerometer[0]) + "," + nf.format(lastAcclerometer[1]) + "," + nf.format(lastAcclerometer[1]) + ","+nf.format(rmYaw) + "," + nf.format(rmPitch) + "," + nf.format(rmRoll) + "\n");

                    //Logging into External Csv File
                    //writeToFile(writer.toString());

                    //Calculating the Door Open Event
                    doorVelLast = doorVelCurrent;
                    doorVelCurrent = (float) Math.sqrt(rmYaw*rmYaw+rmPitch*rmPitch+rmRoll*rmRoll - SensorManager.GRAVITY_EARTH);
                    float d = doorVelCurrent - doorVelLast;
                    doorVel = doorVel * 0.09f + (d*d) * 0.01f;

                    //Toast.makeText(getApplicationContext(), String.valueOf(doorVel), Toast.LENGTH_SHORT).show();

                    Status.setText(String.valueOf(Math.round(doorVel) ));

                    //Call to Video Capture Methodology
                    if (((rmRoll >= -85.0)&&(rmPitch  <= 100.0)) && (doorVel >= 6.0))
                    {
                        Banner.setText(R.string.doorOpen);
                        Intent intent = new Intent(SensorActivity.this,FaceTrackerActivity.class);

                        synchronized (intent) {
                            Toast.makeText(getApplicationContext(), String.valueOf("called"), Toast.LENGTH_SHORT).show();
                            intentManager.handleCall(this,intent);

                            //Toast.makeText(getApplicationContext(), String.valueOf("intent calling"), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Banner.setText(R.string.doorClosed);
                        synchronized (this) {
                            //Destroy The intent
                        }
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
//    }


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


    //Implementation to Write the Sensor Data into External Storage.
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
