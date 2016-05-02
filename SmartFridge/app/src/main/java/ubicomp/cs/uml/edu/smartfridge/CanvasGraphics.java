package ubicomp.cs.uml.edu.smartfridge;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

/**
 * Created by Vignesh on 4/18/16.
 */


public class CanvasGraphics extends ubicomp.cs.uml.edu.smartfridge.utils.CanvasGraphicsEmbedder.Graphic {


    //paint type declaration
    private Paint facePositionPaint;
    private Paint idPaint;
    private Paint boxPaint;


    //Size declaration for all the offsets
    private static final float id_Text_Size = 40.0f;
    private static final float id_Y_Offset = 50.0f;
    private static final float id_X_Offset = -50.0f;
    private static final float box_width = 5.0f;

    //Face and its Properties
    private volatile Face face;
    private int faceId;
    private float faceHappiness;


    //Color array for Faces
    private static final int Colors[] = {
            Color.BLUE,
            Color.RED
    };
    private static int currentColor = 0;

    //Constructor with the overlay
    public CanvasGraphics(ubicomp.cs.uml.edu.smartfridge.utils.CanvasGraphicsEmbedder overlay) {
        super(overlay);
        currentColor = (currentColor + 1) % Colors.length;
        final int selectedColor = Colors[currentColor];
        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);
        idPaint = new Paint();
        idPaint.setColor(selectedColor);
        idPaint.setTextSize(id_Text_Size);
        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(box_width);
    }


    //drawing the canvas with face positions
    @Override
    public void drawFace(Canvas canvas) {
        Face face = this.face;
        if (face == null) {
            return;
        }
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawText("Happiness-" + String.format("%.2f", face.getIsSmilingProbability()*100), x - id_X_Offset, y - id_Y_Offset, idPaint);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, boxPaint);
    }

    //Setting Face id
    void setId(int id) {

        faceId = id;
    }

    //Update Face as it moves
    void updateFace(Face face) {
        this.face = face;
        postInvalidate();
    }

}
