package ubicomp.cs.uml.edu.smartfridge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    //Variable Declaration
    private SensorManager sensorManager = null;

    //Acclerometer Values
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

    private float[] lastAcclerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean hasAccelerometerSet = false;

    private float[] magField = new float[9];
    private float[] MagOrientation = new float[3];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Text Views for Showing Outputs
        accX = (TextView) findViewById(R.id.textView01);
        accY = (TextView) findViewById(R.id.textView02);
        accZ = (TextView) findViewById(R.id.textView03);

        magX = (TextView) findViewById(R.id.textView04);
        magY = (TextView) findViewById(R.id.textView05);
        magZ = (TextView) findViewById(R.id.textView06);


        accX.setText("initial test string");


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
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);

            //Getting the Acclerometer Values
            if (event.sensor == AccSensor){
                System.arraycopy(event.values, 0, lastAcclerometer, 0, event.values.length);
                accX.setText("x: "+ nf.format(lastAcclerometer[0]));
                accY.setText("y: "+ nf.format(lastAcclerometer[1]));
                accZ.setText("z: "+nf.format(lastAcclerometer[2]));
                hasAccelerometerSet = true;
            }

            //Getting the Magnetometer Values
            else if (event.sensor == MagSensor) {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
                if (hasAccelerometerSet) {
                    SensorManager.getRotationMatrix(magField, null, lastAcclerometer, lastMagnetometer);
                    SensorManager.getOrientation(magField, MagOrientation);
                    magX.setText("x: "+nf.format(Math.toDegrees(MagOrientation[0])));
                    magY.setText("y: "+nf.format(Math.toDegrees(MagOrientation[1])));
                    magZ.setText("z: "+nf.format(Math.toDegrees(MagOrientation[2])));
                }
            }
            //Error Management
            else {
                Log.d("sensor", "got other sensor event " + event.sensor.getType());
            }
        }
    }
}
