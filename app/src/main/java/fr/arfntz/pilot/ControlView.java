package fr.arfntz.pilot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.Math.abs;

public class ControlView extends SurfaceView implements SurfaceHolder.Callback {
    private static int stepRatio = 20;
    private static int backgroundColor = Color.rgb(40,60,120);
    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static int radius;

    private DrawThread drawThread;
    private Bitmap bitmap;
    private Point position;
    private Point innerPosition;
    private Point steppedPosition;
    private Point dimension;
    private Point center;
    private int minX,minY,maxX,maxY;
    private Point innerDimension;
    private int minStepX,minStepY;
    private int s1,s2,d1,d2;
    private char d='+';

    public ControlView(Context context, Point dimension) {
        super(context);
        this.dimension = dimension;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dfrobot200);
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setTextSize(100);
        minX = bitmap.getWidth()/2;
        minY = bitmap.getHeight()/2;
        maxX = dimension.x-minX;
        maxY = dimension.y-minY;
        center = new Point(dimension.x/2,dimension.y/2);
        radius = center.x/2;
        innerDimension = new Point(maxX-minX,maxY-minY);
        minStepX = innerDimension.x/stepRatio;
        minStepY = innerDimension.y/stepRatio;
        position = new Point(center.x,center.y);
        steppedPosition = new Point(position);
        innerPosition = new Point(position.x-minX,position.y-minY);
        drawThread = new DrawThread(getHolder(),this);
        getHolder().addCallback(this);
        setFocusable(true);
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backgroundColor);
        canvas.drawCircle(center.x,center.y,radius,paint);
        canvas.drawLine(center.x,center.y-radius,center.x,0,paint);
        canvas.drawLine(center.x,center.y+radius,center.x,dimension.y,paint);
        canvas.drawLine(center.x-radius,center.y,0,center.y,paint);
        canvas.drawLine(center.x+radius,center.y,dimension.x,center.y,paint);
        canvas.drawText(""+d+s1,center.x-radius,100,paint);
        canvas.drawText(""+d+s2,dimension.x-radius,100,paint);
        canvas.drawBitmap(bitmap, position.x-minX, position.y-minY, null);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;
        position.x = x; position.y = y;
        // send command only if position has moved at least "minStep" away
        if (abs(position.x-steppedPosition.x) > minStepX || abs(position.y-steppedPosition.y) > minStepY) {
            steppedPosition.set(x,y);
            toCommand(position);
            ((ControlActivity)getContext()).request(s1+"/"+s2+"/"+d1+"/"+d2);
        }
        return true;
    }
    private void toCommand(Point p) {
        innerPosition.set(p.x-minX,p.y-minY);
        scale(innerPosition, innerDimension, new Point(510, 510));
        center(innerPosition, new Point(255,255));
        int x = innerPosition.x, y = innerPosition.y;
        if (y<0) {d1=1;d2=1;d='+';} else {d1=0;d2=0;d='-';}
        s1 = s2 = abs(y);
        if (x<0) {s1-=x/2;s2+=x/2;} else {s1-=x/2;s2+=x/2;}
        if (s1>255) s1=255; if (s2>255) s2=255;
        if (s1<70) s1=0; if (s2<70) s2=0;
    }
    private void scale(Point p,Point dimensionFrom,Point dimensionTo) {
        p.x = p.x * dimensionTo.x / dimensionFrom.x;
        p.y = p.y * dimensionTo.y / dimensionFrom.y;
        // System.out.println("Scale from "+dimensionFrom+" to "+dimensionTo+" = "+p);
    }
    private void center(Point p,Point center) {
        p.x = p.x - center.x;
        p.y = p.y - center.y;
        // System.out.println("Center to "+center+" = "+p);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread.startrun(true);
        drawThread.start();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.startrun(false);
        drawThread.stop();
    }
}
