package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.scheduledesigner.R;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.ButtonHoverStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class WeekSelectView extends RelativeLayout {

    Button select_view;

    ImageButton pre_view,next_view;

    private Context context;

    Calendar calendar;

    CallbackBundle change_week_call;
    public WeekSelectView(Context context) {
        super(context);
        this.context=context;
    }

    public WeekSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public WeekSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    public WeekSelectView(Context context,CallbackBundle changeWeekHandler) {
        super(context);
        // TODO Auto-generated constructor stub

        this.change_week_call=changeWeekHandler;

        this.createPropertyView();
    }

    public void initWeek(Calendar calendar){
        if(this.calendar!=null&&calendar!=null&&this.calendar.getTime().compareTo(calendar.getTime())==0)
            return;
        this.calendar=(Calendar)calendar.clone();

        this.calendar.add(Calendar.DAY_OF_WEEK, 1);

        this.select_view.setText(DateConverter.convertToDisplayStr(this.calendar.getTime()));
    }

    protected void createPropertyView() {
        try {
            this.context=this.getContext();

            View parent= LayoutInflater.from(this.getContext()).inflate(R.layout.view_week_selecter, this,true);

            this.select_view=(Button)parent.findViewById(R.id.sche_week_value);

            this.pre_view=(ImageButton)parent.findViewById(R.id.sche_week_pre);
            this.pre_view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    changeCurrentWeek(-7);
                }
            });
            this.next_view=(ImageButton)parent.findViewById(R.id.sche_week_next);
            this.next_view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    changeCurrentWeek(7);
                }
            });

            ButtonHoverStyle.bindingHoverEffect(this.pre_view,getResources());

            ButtonHoverStyle.bindingHoverEffect(this.next_view,getResources());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void changeCurrentWeek(int dates) {
        Bundle result=new Bundle();
        result.putInt("dates", dates);
        if(change_week_call!=null)
            change_week_call.callback(result);

        this.calendar.add(Calendar.DAY_OF_WEEK, dates);

        this.select_view.setText(DateConverter.convertToDisplayStr(this.calendar.getTime()));
    }
}
