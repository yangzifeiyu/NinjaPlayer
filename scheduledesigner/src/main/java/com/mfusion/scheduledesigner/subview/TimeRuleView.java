package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import com.mfusion.scheduledesigner.R;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class TimeRuleView  extends View implements View.OnLayoutChangeListener {

    int width=0,height=0;

    TextPaint paint_rule=null;

    TextPaint paint_time=null;

    TextPaint paint_line=null;

    public TimeRuleView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        this.paint_rule=new TextPaint();
        this.paint_rule.setAntiAlias(true);
        this.paint_rule.setColor(context.getResources().getColor(R.color.colorPrimary));
        this.paint_rule.setStyle(Paint.Style.FILL);

        this.paint_time=new TextPaint();
        this.paint_time.setAntiAlias(true);
        this.paint_time.setTextSize(context.getResources().getDimension(R.dimen.textSize));
        this.paint_time.setColor(Color.BLACK);
        this.paint_time.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        this.paint_time.setStyle(Paint.Style.STROKE);

        this.paint_line=new TextPaint();
        this.paint_line.setAntiAlias(true);
        this.paint_line.setColor(Color.WHITE);
        this.paint_line.setStyle(Paint.Style.STROKE);

        this.addOnLayoutChangeListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        if(canvas!=null)
        {
            if(width==0||height==0)
                return;

            int color_h=(int)(height*0.6);
            //draw background
            canvas.drawColor(Color.WHITE);
            canvas.drawRect(0, color_h, width, height, this.paint_rule);

            float hour_w=width/24.0f;
            float line_x=0;
            for (int i = 1; i < 24; i++) {
                line_x+=hour_w;
                canvas.drawLine(line_x, color_h, line_x, height, this.paint_line);
                canvas.drawText((i>9?i:("0"+i))+":00", line_x-20, color_h-5, this.paint_time);
            }
        }
    }

    @Override
    public void onLayoutChange(View view, int l, int t, int r,
                               int b, int oldl, int oldt, int oldr,int oldb) {
        // TODO Auto-generated method stub
        width=r-l;
        height=b-t;
    }

}
