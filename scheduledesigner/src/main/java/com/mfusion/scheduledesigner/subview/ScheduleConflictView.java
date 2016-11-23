package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.mfusion.scheduledesigner.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ThinkPad on 2016/11/3.
 */
public class ScheduleConflictView  extends View {

    Context context;

    TextPaint paint_border,paint_body;

    int width,height;

    String conflict_msg;

    Date display_time;

    int conflict_show_interval=5;

    RelativeLayout.LayoutParams area_layoutparas;

    public ScheduleConflictView(Context context) {
        super(context);
        init(context);
    }

    public ScheduleConflictView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScheduleConflictView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

        this.context=context;

        this.conflict_msg=context.getResources().getString(R.string.sche_block_conflict);

        this.paint_border=new TextPaint();
        this.paint_border.setAntiAlias(true);
        this.paint_border.setColor(context.getResources().getColor(R.color.colorBlockTitle));
        this.paint_border.setStyle(Paint.Style.FILL_AND_STROKE);

        this.paint_body=new TextPaint();
        this.paint_body.setAntiAlias(true);
        this.paint_body.setTextSize(context.getResources().getDimension(R.dimen.textSizeSmall));
        this.paint_body.setColor(Color.WHITE);

    }

    public void show(int point_x,int point_y,int x_max_range){
        area_layoutparas=(RelativeLayout.LayoutParams)this.getLayoutParams();
        area_layoutparas.width=width=(int)paint_body.measureText(conflict_msg)+(int)(context.getResources().getDimension(R.dimen.global_margin_medium)*2);
        area_layoutparas.height=height=(int)((paint_body.getFontMetricsInt().bottom-paint_body.getFontMetricsInt().top)*1.8);
        area_layoutparas.leftMargin=point_x-width/2;
        area_layoutparas.topMargin=point_y-height/2;
        area_layoutparas.leftMargin=x_max_range>(area_layoutparas.leftMargin+width)?area_layoutparas.leftMargin:x_max_range-width;
        this.setLayoutParams(area_layoutparas);

        this.setVisibility(VISIBLE);

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.SECOND,conflict_show_interval);
        display_time= calendar.getTime();

    }

    public void refresh(){
        if(display_time==null||display_time.compareTo( Calendar.getInstance().getTime())>0)
            return;
        this.hide();
    }

    public void hide(){
        display_time=null;
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_hide);
        this.startAnimation(anim);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        this.setVisibility(GONE);
        if(this.getParent()!=null)
            ((ViewGroup)this.getParent()).removeView(this);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas != null) {
            //canvas.drawRect(1, 1, width-1, height-1, this.paint_border);
            canvas.drawRoundRect(new RectF(0, 0, width, height),5,5,this.paint_border);
            canvas.drawText(conflict_msg,context.getResources().getDimension(R.dimen.global_margin_medium),(height-(paint_body.getFontMetricsInt().bottom-paint_body.getFontMetricsInt().top))/2-paint_body.getFontMetricsInt().top,paint_body);
        }
    }
}
