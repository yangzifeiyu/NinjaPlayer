package com.mfusion.templatedesigner.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.mfusion.commons.entity.template.ComponentEntity;

/**
 * Created by 1B15182 on 26/7/2016 0026.
 */
public class ScheduleMediaComponentView extends BaseComponentView {
    private Paint hintPaint;
    public ScheduleMediaComponentView(Context context) {
        super(context);
        hintPaint=new Paint();
        hintPaint.setTextSize(80f);
        hintPaint.setColor(Color.BLACK);
    }

    public ScheduleMediaComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("Schedule Media",getWidth()/2-(hintPaint.measureText("Schedule Media")/2),(int) ((canvas.getHeight() / 2) - ((hintPaint.descent() + hintPaint.ascent()) / 2)),hintPaint);
    }

    @Override
    public void setComponentEntity(ComponentEntity entity) {
        super.setComponentEntity(entity);

    }
}
