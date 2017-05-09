package fr.arfntz.pilot;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {

    private SurfaceHolder msurfaceHolder;
    private ControlView mcontrolView;
    private boolean mrun =false;

    public DrawThread(SurfaceHolder holder, ControlView controlView) {
        msurfaceHolder = holder;
        mcontrolView = controlView;
    }

    public void startrun(boolean run) {
        mrun=run;
    }

    @Override
    public void run() {
        super.run();
        Canvas canvas;
        while (mrun) {
            canvas=null;
            try {
                canvas = msurfaceHolder.lockCanvas(null);
                synchronized (msurfaceHolder) {
                    mcontrolView.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    msurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}
