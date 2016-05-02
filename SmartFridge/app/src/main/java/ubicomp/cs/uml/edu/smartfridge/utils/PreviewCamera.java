package ubicomp.cs.uml.edu.smartfridge.utils;

/**
 * Created by Vignesh on 4/18/16.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class PreviewCamera extends ViewGroup {

    private boolean startRequested;
    private boolean surfaceAvailable;
    private Context context;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private CanvasGraphicsEmbedder overlay;

    //Constructor to set attributes and get the surface view
    public PreviewCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        startRequested = false;
        surfaceAvailable = false;
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(surfaceView);
    }

    //Start the camera with the camera source
    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        this.cameraSource = cameraSource;

        if (this.cameraSource != null) {
            startRequested = true;
            startIfReady();
        }
    }

    //Starting the camera if all the holders are set
    private void startIfReady() throws IOException {
        if (startRequested && surfaceAvailable) {
            cameraSource.start(surfaceView.getHolder());
            if (overlay != null) {
                Size size = cameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                //drawing the face if its in portrait mode
                if (isModePotrait()) {
                    overlay.setCameraInfo(min, max, cameraSource.getCameraFacing());

                } else {
                    overlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
                }
                //clear overlay if done
                overlay.clear();
            }
            startRequested = false;
        }
    }

    //Changing the layout image width and layout for more efficient and faster access
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 320;
        int height = 240;
        if (cameraSource != null) {
            Size size = cameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        //changing direction if you are in
        if (isModePotrait()) {
            int temp = width;
            width = height;
            height = temp;
        }

        final int lwidth = right - left;
        final int lheight = bottom - top;

        int cWidth = lwidth;
        int cHeight = (int)(((float) lwidth / (float) width) * height);

        if (cHeight > lheight) {
            cHeight = lheight;
            cWidth = (int)(((float) lheight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, cWidth, cHeight);
        }

        try {
            startIfReady();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Calling Surface Callback
    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            surfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            surfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    //Checking the orientation that it is potrait or landscape
    private boolean isModePotrait() {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        return false;
    }

    //Start the camera
    public void start(CameraSource cameraSource, CanvasGraphicsEmbedder overlay) throws IOException {
        this.overlay = overlay;
        start(cameraSource);
    }

    //release the camera
    public void release() {
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    //Stop the camera
    public void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }


}
