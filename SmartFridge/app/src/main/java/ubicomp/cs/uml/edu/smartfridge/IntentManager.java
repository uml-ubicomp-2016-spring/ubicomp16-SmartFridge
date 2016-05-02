package ubicomp.cs.uml.edu.smartfridge;

import android.content.Intent;
import android.util.Log;

import java.util.Timer;

/**
 * Created by Vignesh Dhamodaran on 4/18/16.
 */
public class IntentManager {

    private volatile boolean status = false;
    private SensorActivity srActivity;
    Timer timer = new Timer();

    //Handling the door open or close requests based on the sensor change
    public void handleCall(SensorActivity sensorActivity, Intent intent) {
        srActivity = sensorActivity;
        Log.i("Out ", String.valueOf(status));
        if (!isHold()) {
            Log.i("In ", String.valueOf(status));
            //Calling create intent to forward the activity
            createIntent(sensorActivity, intent);
        } else {
            synchronized (intent) {
                //Releasing the synchronised hold if the door is closed
                status = releaseHold();
            }
        }
    }

    //Releasing the hold if the door is closed to make it go back to the sensor activity
    public boolean releaseHold() {

        if (status == true) {
            return false;
        }

        return true;

    }

    //Creating Intent to the face detection Activity
    private void createIntent(SensorActivity sensorActivity, Intent toIntent) {
        try {
            status = true;
            Log.i("Create", "Called");
            synchronized (this) {
                toIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sensorActivity.startActivity(toIntent);
            }
        }
        catch (Exception e){

        }
    }

    //Checking the hold status
    private boolean isHold() {
        return status;
    }



}





