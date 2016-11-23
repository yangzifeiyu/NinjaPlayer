package com.mfusion.scheduledesigner.subview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.scheduledesigner.R;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class ScheduleWeeklyView  extends View implements OnLayoutChangeListener{

    int width=0,height=0;

    TextPaint paint_line=null,paint_expiry=null;

    SimpleDateFormat date_format=new SimpleDateFormat(InternalKeyWords.DefaultDisplayDateFormat+" E");

    Calendar sche_calendar=Calendar.getInstance();

    Date current_date;

    public ScheduleWeeklyView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        this.paint_line=new TextPaint();
        this.paint_line.setAntiAlias(true);
        float size=R.dimen.textSize;
        this.paint_line.setTextSize(context.getResources().getDimension(R.dimen.textSize));
        this.paint_line.setColor(Color.GRAY);
        this.paint_line.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        this.paint_line.setStyle(Style.STROKE);

        this.paint_expiry=new TextPaint();
        this.paint_expiry.setAntiAlias(true);
        this.paint_expiry.setColor(Color.LTGRAY);
        this.paint_expiry.setStyle(Style.FILL);

        Calendar calendar = Calendar.getInstance();
        DateConverter.clearCalendarNoneHHmmss(calendar);
        this.current_date=calendar.getTime();

        this.addOnLayoutChangeListener(this);
    }

    public void refreshFirstWeek(Calendar calendar) {
        if(this.sche_calendar!=null&&calendar!=null&&this.sche_calendar.getTime().compareTo(calendar.getTime())==0)
            return;
        this.sche_calendar=(Calendar)calendar.clone();
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        if(canvas!=null)
        {
            if(width==0||height==0)
                return;

            canvas.drawColor(Color.WHITE);
            float day_h=height/7.0f;
            float line_y=0;
            Calendar currentCalendar=(Calendar)sche_calendar.clone();
            for (int i = 1; i <= 7; i++) {
                line_y+=day_h;
                currentCalendar.add(Calendar.DAY_OF_WEEK, 1);
                Date test=currentCalendar.getTime();
                if(currentCalendar.getTime().compareTo(this.current_date)<0) {
                    canvas.drawRect(0, line_y-day_h+2, width,line_y, this.paint_expiry);
                }

                canvas.drawLine(0, line_y, width, line_y, this.paint_line);
                canvas.drawText(date_format.format(currentCalendar.getTime()), 20, line_y-day_h/2+6, this.paint_line);
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
