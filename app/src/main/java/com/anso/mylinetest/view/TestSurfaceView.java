package com.anso.mylinetest.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.animation.LinearInterpolator;

import com.anso.mylinetest.PointModel;


public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;

    private Paint paint = null;
    private Paint txtPaint = null;
    private SurfaceListener listener;
    private float circleX = 0;

    private float circleY = 0;

    int centerX;

    public TestSurfaceView(Context context) {
        super(context);

        setFocusable(true);

        if (surfaceHolder == null) {
            surfaceHolder = getHolder();
            // Add this as surfaceHolder callback object.
            surfaceHolder.addCallback(this);
        }

        if (paint == null) {
            paint = new Paint();
            txtPaint = new Paint();
            txtPaint.setStrokeWidth(25);
//            paint.setStrokeWidth(25);
            txtPaint.setTextSize(30);
            paint.setColor(Color.WHITE);
            txtPaint.setColor(Color.RED);
        }

        // Set the parent view background color. This can not set surfaceview background color.
        this.setBackgroundColor(Color.BLACK);

        // Set current surfaceview at top of the view tree.
        this.setZOrderOnTop(true);

        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startValueAnimator();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /* This method will be invoked to draw a circle in canvas. */
    public void drawBall() {
        // Get and lock canvas object from surfaceHolder.
        Canvas canvas = surfaceHolder.lockCanvas();

        Paint surfaceBackground = new Paint();
        // Set the surfaceview background color.
        surfaceBackground.setColor(Color.CYAN);
        // Draw the surfaceview background color.
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), surfaceBackground);

        // Draw the circle.
        canvas.drawCircle(circleX, circleY, 100, paint);

        // Unlock the canvas object and post the new draw.
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    /* This method will be invoked to draw a circle in canvas. */
    public void drawRect() {
        Canvas canvas = surfaceHolder.lockCanvas();

        Paint surfaceBackground = new Paint();
        // Set the surfaceview background color.
        surfaceBackground.setColor(Color.BLUE);
        // Draw the surfaceview background color.
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), surfaceBackground);

        // Draw the rectangle.
        canvas.drawRect(circleX, circleY, circleX + 200, circleY + 200, paint);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    ValueAnimator valueAnimator;

    private void startValueAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0f, 6f);
        valueAnimator.setDuration(15 * 1000);
        valueAnimator.setStartDelay(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                animation.getAnimatedValue();
                drawPic((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawTxt();
                listener.finish();
                pointModelOne = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }


    PointModel pointModelOne;
    PointModel pointModelTwo;

    private void drawPic(float percent) {
        Log.e("TAG", "drawPic: " + percent);

        if (pointModelOne == null) {
            pointModelOne = randomPoint();
            pointModelTwo = randomPoint();
        }
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int x1, y1, x2, y2;
        x1 = pointModelOne.getCurrentX(percent);
        y1 = pointModelOne.getCurrentY(percent);
        x2 = pointModelTwo.getCurrentX(percent);
        y2 = pointModelTwo.getCurrentY(percent);
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawPoint(x1, y1, paint);
        canvas.drawPoint(x2, y2, paint);
        canvas.drawLine(x1, y1, x2, y2, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
        Canvas canvas2 = surfaceHolder.lockCanvas();
        canvas2.drawPoint(x1, y1, paint);
        canvas2.drawPoint(x2, y2, paint);
        canvas2.drawLine(x1, y1, x2, y2, paint);
        surfaceHolder.unlockCanvasAndPost(canvas2);
        Canvas canvas3 = surfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
        surfaceHolder.unlockCanvasAndPost(canvas3);
    }

    private void drawTxt() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (pointModelOne != null) {
            canvas.drawText(pointModelOne.toString(), 20, 100, txtPaint);
            canvas.drawText(pointModelTwo.toString(), 20, 300, txtPaint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawClear() {
        for (int i = 0; i < 10; i++) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
//        Canvas canvas1 = surfaceHolder.lockCanvas();
//        canvas1.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        surfaceHolder.unlockCanvasAndPost(canvas1);
    }

    private PointModel generatePointOne() {
        PointModel model = new PointModel(300, 300);
        model.setMaxX("200");
        model.setMaxY("200");
        return model;
    }

    private PointModel generatePointTwo() {
        PointModel model = new PointModel(600, 1000);
        model.setMaxX("200");
        model.setMaxY("200");
        model.setPalstance("1.5");
        return model;
    }

    private PointModel randomPoint() {
        PointModel model = new PointModel();
        model.randomData(centerX);
        return model;
    }


    public void start() {
        if (valueAnimator != null) {
            drawClear();
            valueAnimator.start();
        }
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }


    private void savePic() {


    }

    public interface SurfaceListener {
        void finish();
    }

    public SurfaceListener getListener() {
        return listener;
    }

    public void setListener(SurfaceListener listener) {
        this.listener = listener;
    }
}