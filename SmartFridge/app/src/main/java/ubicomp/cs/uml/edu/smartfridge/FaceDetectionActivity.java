package ubicomp.cs.uml.edu.smartfridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import ubicomp.cs.uml.edu.smartfridge.utils.CanvasGraphicsEmbedder;
import ubicomp.cs.uml.edu.smartfridge.utils.PreviewCamera;

/**
 * Created by Vignesh on 4/18/16.
 */
public class FaceDetectionActivity extends Activity {


    private CameraSource cameraSource = null;

    private boolean called = false;

    private PreviewCamera previewCamera;

    private LinearLayout layout;

    private CanvasGraphicsEmbedder graphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        called=true;
        //Setting the camera preview
        previewCamera = (PreviewCamera) findViewById(R.id.preview);
        //Setting the graphic overlay
        graphicOverlay = (CanvasGraphicsEmbedder) findViewById(R.id.faceOverlay);

        //Getting Permission for Using Camera
        int rc = ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            //if Granted Permission Create Camera Source
            createCamSource();
        } else {
            //if not ask permission
            requestCamPerm();
        }

        //Starting the upload service
        uploadThread.start();

        //Starting return intent Capture
        returnThread.start();

    }


    //Requesting permission for Camera Access
    private void requestCamPerm() {

        //Getting permissions from manifest File
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    //Creating Camera Cam Source to get Video
    private void createCamSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        cameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Starting Camera Source to show the video and overlay
        startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        previewCamera.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    //Sacing the permissions for current session
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCamSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //Starting Camera Source with the google vision Api and Starting the camera source
    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                previewCamera.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }


    //Adding the Face with the graphocs overlay
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(graphicOverlay);
        }
    }

    //Graphics Face tracker
    private class GraphicFaceTracker extends Tracker<Face> {
        private CanvasGraphicsEmbedder cOverlay;
        private CanvasGraphics canvasGraphics;

        GraphicFaceTracker(CanvasGraphicsEmbedder overlay) {
            cOverlay = overlay;
            canvasGraphics = new CanvasGraphics(overlay);
        }


        @Override
        public void onNewItem(int faceId, Face item) {
            canvasGraphics.setId(faceId);
        }


        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            cOverlay.add(canvasGraphics);
            canvasGraphics.updateFace(face);
        }


        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            cOverlay.remove(canvasGraphics);
        }


        @Override
        public void onDone() {
            cOverlay.remove(canvasGraphics);
        }
    }

    Thread uploadThread = new Thread() {
        public void run() {
            try {
                Thread.sleep(10000);
                byte[] bytes = previewCamera.getApplicationWindowToken().toString().getBytes();
                onPictureTaken(bytes);
                UploadService up = new UploadService();
                up.upload();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Thread returnThread = new Thread() {
        public void run() {
            try {
                Thread.sleep(10000);
                IntentManager im  = new IntentManager();
                im.releaseHold();
                Intent intent = new Intent(FaceDetectionActivity.this,SensorActivity.class);
                FaceDetectionActivity.this.startActivity(intent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    public void onPictureTaken(byte[] bytes) {
        try {
            File imageFile;

            // convert byte array into bitmap
            Bitmap loadedImage = null;
            Bitmap rotatedBitmap = null;
            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-90);
            rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                    loadedImage.getWidth(), loadedImage.getHeight(),
                    rotateMatrix, false);

            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);


            boolean success = true;
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }
            if (success) {
                java.util.Date date = new java.util.Date();
                imageFile = new File(storageDir.getAbsolutePath()
                        + File.separator
                        + new Timestamp(date.getTime()).toString()
                        + "Image.jpg");

                imageFile.createNewFile();
            } else {
                Toast.makeText(getBaseContext(), "Image Not saved",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();

            // save image into gallery
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

            FileOutputStream fout = new FileOutputStream(imageFile);
            fout.write(ostream.toByteArray());
            fout.close();
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN,
                    System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA,
                    imageFile.getAbsolutePath());

            FaceDetectionActivity.this.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //saveToInternalStorage(loadedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
