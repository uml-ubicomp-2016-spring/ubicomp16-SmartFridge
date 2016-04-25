package ubicomp.cs.uml.edu.smartfridge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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

public class UploadService {

    String ba1;
    public static String SERVER_POST_URL= "http://localhost:8080/dev/getImage.php";
    String mCurrentPhotoPath;


    private void upload() {


        // Upload image to server
        try {

            URL url = new URL(SERVER_POST_URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoInput(true);
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            c.connect();

            Bitmap bm = BitmapFactory.decodeFile(createImageFile().getAbsolutePath());
            OutputStream output = c.getOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, output);

            output.close();

            Scanner result = new Scanner(c.getInputStream());
            String response = result.nextLine();
            Log.e("ImageUploader", "Error uploading image: " + response);

            result.close();
        } catch (IOException e) {
            Log.e("ImageUploader", "Error uploading image", e);
        }
    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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

}