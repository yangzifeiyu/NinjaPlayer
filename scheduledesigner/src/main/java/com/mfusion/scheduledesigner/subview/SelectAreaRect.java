package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by ThinkPad on 2016/9/5.
 */
public class SelectAreaRect extends View{

    TextPaint paint_border;

    int width,height;

    RelativeLayout.LayoutParams area_layoutparas;

    float point_start_x,point_start_y,point_end_x,point_end_y;

    public SelectAreaRect(Context context) {
        super(context);
        init();
    }

    public SelectAreaRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectAreaRect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        this.paint_border=new TextPaint();
        this.paint_border.setAntiAlias(true);
        this.paint_border.setColor(Color.DKGRAY);
        this.paint_border.setStyle(Paint.Style.STROKE);
        this.paint_border.setPathEffect(new DashPathEffect(new float[] { 2, 2}, 1));
    }

    public void setStartPoint(float point_x,float point_y){
        point_start_x=point_end_x=point_x;
        point_start_y=point_end_y=point_y;
    }

    public void setEndPoint(float point_x,float point_y){
        area_layoutparas=(RelativeLayout.LayoutParams)this.getLayoutParams();
        area_layoutparas.width=width=(int)Math.abs(point_start_x-point_end_x);
        area_layoutparas.height=height=(int)Math.abs(point_start_y-point_end_y);
        area_layoutparas.leftMargin+=point_x-point_end_x;
        area_layoutparas.topMargin+=point_y-point_end_y;

        point_end_x=point_x;
        point_end_y=point_y;
        this.setLayoutParams(area_layoutparas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas != null) {
            canvas.drawRect(1, 1, width-1, height-1, this.paint_border);
        }
    }
}
