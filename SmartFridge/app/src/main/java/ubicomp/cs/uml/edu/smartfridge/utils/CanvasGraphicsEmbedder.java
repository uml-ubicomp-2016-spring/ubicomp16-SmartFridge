package ubicomp.cs.uml.edu.smartfridge.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vignesh on 4/18/16.
 */
public class CanvasGraphicsEmbedder extends View {

    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int facing = CameraSource.CAMERA_FACING_FRONT;
    private Set<Graphic> graphics = new HashSet<>();
    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;

    //Inner Class for Graphic
    public static abstract class Graphic {
        private CanvasGraphicsEmbedder overlay;

        public Graphic(CanvasGraphicsEmbedder overlay) {
            this.overlay = overlay;
        }
        public abstract void drawFace(Canvas canvas);
        public float scaleX(float horizontal) {
            return horizontal * overlay.widthScaleFactor;
        }
        public float scaleY(float vertical) {
            return vertical * overlay.heightScaleFactor;
        }

        //Transalate the X values
        public float translateX(float x) {
            if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                return overlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }
        //Translate the Y values
        public float translateY(float y) {
            return scaleY(y);
        }

        //Post invalidate the current Overlay on update
        public void postInvalidate() {

            overlay.postInvalidate();
        }
    }

    //Call the super class to set the context and attributes
    public CanvasGraphicsEmbedder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //Clear the graphics
    public void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    //Overide the onDraw to fraw the graphics on the canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight != 0)) {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphics) {
                graphic.drawFace(canvas);
            }
        }
    }

    //Setting the camera size and all the other features from the camera source
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (lock) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.facing = facing;
        }
        postInvalidate();
    }

    //Add graphic with a lock
    public void add(Graphic graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
        postInvalidate();
    }

    //release graphic with a lock
    public void remove(Graphic graphic) {
        synchronized (lock) {
            graphics.remove(graphic);
        }
        postInvalidate();
    }

}
