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

    public void handleCall(SensorActivity sensorActivity, Intent intent) {
        srActivity = sensorActivity;
        Log.i("Out ", String.valueOf(status));
        if (!isHold()) {
            Log.i("In ", String.valueOf(status));
            createIntent(sensorActivity, intent);
        } else {
            synchronized (intent) {
                status = releaseHold();
            }
        }
    }

    private boolean releaseHold() {

        if (status == true) {
            return false;
        }

        return true;

    }


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


    private boolean isHold() {
        return status;
    }


}





