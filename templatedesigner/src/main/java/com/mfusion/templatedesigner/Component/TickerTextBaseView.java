package com.mfusion.templatedesigner.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;

import org.w3c.dom.Element;

/**
 * Created by 1B15182 on 25/7/2016 0025.
 */
public class TickerTextBaseView extends BaseComponentView {
    private static final String TAG = "TickerTextBaseView";
    protected String text;
    private int sleepInterval;
    private int textXPosition;
    private float textLength;
    private Thread scollerThread;
    private Handler scollerHandler;
    private boolean scrollingFlag;
    protected Paint tickerPaint;
    public TickerTextBaseView(Context context) {
        this(context,null);
    }

    public TickerTextBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tickerPaint=new Paint();
        tickerPaint.setTextSize(50f);
        text="Sample Text";


    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //center the text vertically
        canvas.drawText(text,textXPosition,(int) ((canvas.getHeight() / 2) - ((tickerPaint.descent() + tickerPaint.ascent()) / 2)),tickerPaint);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startScrolling();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.i(TAG, "onWindowFocusChanged: "+hasWindowFocus);
        if(!hasWindowFocus)
        {
            Log.i(TAG, "onWindowFocusChanged: stopping scrolling");
            scrollingFlag=false;

        }
        else{
            startScrolling();
        }
    }

    @Override
    public void setComponentEntity(ComponentEntity entity) {
        super.setComponentEntity(entity);
        for(Element current:componentEntity.property){
            String attribute=current.getAttribute("name");


            if (attribute.equals("Speed")){
                convertSpeedToSleepInterval(Integer.valueOf(current.getTextContent()));
            }
        }

        scollerHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==0){
                    TickerTextBaseView.this.invalidate();
                }
            }
        };

    }

    /**
     * convert speed from xml file to thread sleep interval
     * speed 100 => 5ms sleep
     * speed 1 => 30ms sleep
     * result will be stored into instance variable
     * @param speed from xml file
     */
    private void convertSpeedToSleepInterval(int speed){
        sleepInterval=Math.round(1.0f*(100-speed)/100*25+5);
        Log.i(TAG, "convertSpeedToSleepInterval: sleep interval="+sleepInterval);
    }
    public void startScrolling(){

        scrollingFlag=true;
        if(scollerThread==null){
            scollerThread=new Scroller();
            //scollerThread.start();
        }
        if(!scollerThread.isAlive()){
            Log.i(TAG, "startScrolling: starting new scroller thread");


            scollerThread=new Scroller();
            scollerThread.start();
        }


    }

    /**
     * there's a problem with screen orientation change
     * this thread will not be interrupted on orientation change
     * and i can't find a way to get its reference after this
     * view is reconstructed
     */
    private class Scroller extends Thread{

        @Override
        public void run() {

            while (scrollingFlag){
                textXPosition-=1;
                textLength=tickerPaint.measureText(text);
                if(textXPosition<=(-textLength))
                    textXPosition=getWidth();
                //Log.i(TAG, "run: current textXposition="+textXPosition);
                Message msg=scollerHandler.obtainMessage();
                msg.arg1=0;
                scollerHandler.sendMessage(msg);
                SystemClock.sleep(sleepInterval);
            }
            interrupt();



        }

    }

}
