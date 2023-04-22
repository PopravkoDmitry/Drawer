package by.bsuir.popravko.paint.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PaintView2 extends View {
    //drawing path
    private Path drawPath;
   // private Path path;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmaap
    private Bitmap canvasBitmap;

    private float x0;
    private float y0;
    private int status;


    void setupDrawing() {
        drawPath = new Path();
        //path = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    private Canvas imageCanvas;
    private Bitmap selectedImage;
    private Bitmap imageBitmap;
    private boolean loadFile = false;
    private int w;
    private int h;

    public void loadFile(Bitmap selectedImage) {

        this.selectedImage = selectedImage;
        loadFile = true;

        onSizeChanged(w,h,w,h);
        invalidate();

    }

    public void recreate() {
        this.selectedImage = null;
        loadFile = false;

        onSizeChanged(w,h,w,h);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        this.w = w;
        this.h = h;

        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        if (loadFile) {
            imageBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            imageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

        imageCanvas = new Canvas(imageBitmap);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(imageBitmap, 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        canvas.drawPath(drawPath, drawPaint);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
//        canvas.drawBitmap(imageBitmap, 0, 0, canvasPaint);
//        //canvas.drawPath(path, drawPaint);
//        canvas.drawPath(drawPath, drawPaint);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (status) {
            case 1: drawCircle(event.getX(), event.getY(), event);
            break;
            case 2: drawRectangle(event.getX(), event.getY(), event);
            break;
            case 3: drawTriangle(event.getX(), event.getY(), event);
            break;
            case 4: drawline(event.getX(), event.getY(), event);
            break;
        }


        invalidate();
        return true;
    }

    public PaintView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public void drawline(float touchX, float touchY, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
               // path.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
               // path.lineTo(touchX,touchY);

                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(/*path*/drawPath, drawPaint);
                imageCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
               // path.reset();
                break;
            default:
        }
    }

    public void drawRectangle(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.reset();
               // path.reset();


                drawPath.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                //path.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);
                //path.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);

                drawPath.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
               // path.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);
               // path.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();

                break;
        }
    }

    public void drawCircle(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:

                drawPath.reset();
                //path.reset();


                if (Math.abs(touchY - y0) > Math.abs(touchX - x0)) {
                    if (y0 < touchY) {
                        drawPath.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                       // path.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                      //  path.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                    }
                } else {
                    if (x0 < touchX) {
                        drawPath.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                     //   path.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                      //  path.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                    }
                }


                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);

                drawPath.reset();

                break;
        }
    }

    public void drawTriangle(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:

                drawPath.reset();
               // path.reset();


                if (x0 < touchX) {
                    drawPath.moveTo(x0, y0);
                  //  path.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);
                  //  path.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                  //  path.moveTo(x0, y0);
                    drawPath.lineTo((x0 - Math.abs(x0 - touchX)), touchY);
                   // path.lineTo((x0 - Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);
                   // path.lineTo(touchX, touchY);
                } else {

                    drawPath.moveTo(x0, y0);
                   // path.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);
                  //  path.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                  //  path.moveTo(x0, y0);
                    drawPath.lineTo((x0 + Math.abs(x0 - touchX)), touchY);
                   // path.lineTo((x0 + Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);
                    //path.lineTo(touchX, touchY);

                }


                break;

            case MotionEvent.ACTION_UP:

                drawCanvas.drawPath(drawPath, drawPaint);

                drawPath.reset();

                break;
        }
    }



    public void clear() {
        imageBitmap.eraseColor(Color.WHITE);
        canvasBitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void saveImage(){
        String filename = "Paint" + System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");

        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);

            canvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            try {
                outputStream.flush();
                outputStream.close();

                Toast message = Toast.makeText(getContext(), "Image Saved" , Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            } catch (IOException e) {
                Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            }
        } catch (FileNotFoundException e) {
            Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }

        /*ContextWrapper cw = new ContextWrapper(getContext());
        String filename = "Paint" + System.currentTimeMillis();
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, filename + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            canvasBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e){
            Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        } finally {
            try {
                fos.flush();
                fos.close();
                Log.d("Image:", directory.getAbsolutePath());
                Toast message = Toast.makeText(getContext(), "Image Saved" + directory.getAbsolutePath(), Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            }catch (Exception e){
                Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            }
        }*/
    }

    public int getDrawingColor() {
        return drawPaint.getColor();
    }

    public void setDrawingColor(int argb) {
        drawPaint.setColor(argb);
    }

    public void setLineWidth(int progress) {
        drawPaint.setStrokeWidth(progress);
    }

    public void setStatus(int value) {
        status = value;
    }
}
