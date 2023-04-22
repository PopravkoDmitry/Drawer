package by.bsuir.popravko.paint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class PaintView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(7);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       /* int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStated(event.getX(actionIndex), event.getY(actionIndex), event. getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else{
            touchMoved(event);
        }
*/
        drawCircle(event.getX(), event.getY(), event);
        invalidate();

        return true;
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newX - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }


    public void clear() {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId);
        bitmapCanvas.drawPath(path, paintLine);
        path.reset();
    }

    private void touchStated(float x, float y, int pointerId) {
        Path path;
        Point point;

        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        } else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    float x0;
    float y0;

    Path drawPath;
    Path path;

    Canvas drawCanvas;
    Canvas imageCanvas;

    public void drawRectangle(float touchX, float touchY, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = touchX;
                y0 = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.reset();
                path.reset();

                drawPath.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);

                drawPath.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
                drawPath.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);

                path.addRect(new RectF(x0, y0, touchX, touchY), Path.Direction.CW);
                path.addRect(new RectF(x0, touchY, touchX, y0), Path.Direction.CW);

                path.addRect(new RectF(touchX, y0, x0, touchY), Path.Direction.CW);
                path.addRect(new RectF(touchX, touchY, x0, y0), Path.Direction.CW);

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, paintScreen);
                imageCanvas.drawPath(path, paintScreen);
                drawPath.reset();
                path.reset();
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
                path.reset();

                if (Math.abs(touchY - y0) > Math.abs(touchX - x0)) {
                    if (y0 < touchY) {
                        drawPath.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                    }
                } else {
                    if (x0 < touchX) {
                        drawPath.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                    } else {
                        drawPath.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                    }
                }

                if (Math.abs(touchY - y0) > Math.abs(touchX - x0)) {
                    if (y0 < touchY) {
                        path.addCircle(x0, y0, touchY - y0, Path.Direction.CW);
                    } else {
                        path.addCircle(x0, y0, y0 - touchY, Path.Direction.CW);
                    }
                } else {
                    if (x0 < touchX) {
                        path.addCircle(x0, y0, touchX - x0, Path.Direction.CW);
                    } else {
                        path.addCircle(x0, y0, x0 - touchX, Path.Direction.CW);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, paintScreen);
                imageCanvas.drawPath(path, paintScreen);
                drawPath.reset();
                path.reset();
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
                path.reset();

                if (x0 < touchX) {
                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo((x0 - Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);
                } else {

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo(touchX, touchY);

                    drawPath.moveTo(x0, y0);
                    drawPath.lineTo((x0 + Math.abs(x0 - touchX)), touchY);

                    drawPath.lineTo(touchX, touchY);

                }

                if (x0 < touchX) {
                    path.moveTo(x0, y0);
                    path.lineTo(touchX, touchY);

                    path.moveTo(x0, y0);
                    path.lineTo((x0 - Math.abs(x0 - touchX)), touchY);

                    path.lineTo(touchX, touchY);
                } else {

                    path.moveTo(x0, y0);
                    path.lineTo(touchX, touchY);

                    path.moveTo(x0, y0);
                    path.lineTo((x0 + Math.abs(x0 - touchX)), touchY);

                    path.lineTo(touchX, touchY);
                }

                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, paintScreen);
                imageCanvas.drawPath(path, paintScreen);
                drawPath.reset();
                path.reset();
                break;
        }
    }
}
