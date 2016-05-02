package ubicomp.cs.uml.edu.smartfridge;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


/**
 * Created by Vignesh Dhamodaran on 4/21/16.
 */

public class UploadService extends Service {

    String ba1;
    public static String SERVER_POST_URL= "http://192.168.1.111:3000/ImageUpload";
    String mCurrentPhotoPath;

    // Upload image to server
    public void upload() {

        try {
            URL url = new URL(SERVER_POST_URL);
            //Setting up the Connection
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoInput(true);
            //Infroming the server about the POST
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            //Connecting to the server
            c.connect();

            //Converting Image again to bitmap to reduce the size
            Bitmap bm = BitmapFactory.decodeFile(createImageFile().getAbsolutePath());
            OutputStream output = c.getOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, output);

            output.close();
            //Sending to the server
            Scanner result = new Scanner(c.getInputStream());
            String response = result.nextLine();
            Log.e("ImageUploader", "Error uploading image: " + response);

            result.close();
            //Deleting the File after Upload
            new File(mCurrentPhotoPath).delete();

        } catch (IOException e) {
            Log.e("ImageUploader", "Error uploading image", e);
        }
    }

    //Creating Image file based on the timestamp
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SmartFridge" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir              );

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Starting the service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
}