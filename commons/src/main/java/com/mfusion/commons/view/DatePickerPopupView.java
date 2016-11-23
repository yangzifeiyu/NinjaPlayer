package com.mfusion.commons.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.calendar.CalendarPickerView;
import com.mfusion.commontools.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ThinkPad on 2016/9/21.
 */
public class DatePickerPopupView extends RelativeLayout {

    private Context m_context;
    
    private RelativeLayout date_display_layout;

    private TextView date_display_text_view;

    private ImageView date_select_btn;

    private PopupWindow popup_view;

    private DropDownView.DropDownLocationType popup_location_type= DropDownView.DropDownLocationType.bottom;

    private CalendarPickerView date_picker_view;

    private ImageButton mouth_pre_btn,mouth_next_btn;

    private Calendar calendar;

    private Date date_range_start,date_range_end,selected_date,current_highlight_date,current_month,start_month;

    private OnSelectDateChangedListener onSelectDateChangedListener;

    public DatePickerPopupView(Context context) {
        super(context);
        this.init(context);
    }

    public DatePickerPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public DatePickerPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    private void init(Context context){
        this.m_context=context;

        this.setOnClickListener(this.dropListener);

        date_display_layout =(RelativeLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.date_picker_view, this,true);

        date_display_text_view=(TextView)date_display_layout.findViewById(R.id.date_picker_content);
        date_display_text_view.setTag(date_display_text_view.getCurrentTextColor());
        date_select_btn=(ImageView)date_display_layout.findViewById(R.id.date_picker_selector);
        date_select_btn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupSelectDatePicker();
                return false;
            }
        });

        ButtonHoverStyle.bindingHoverEffectWithBorder(date_display_layout,m_context.getResources());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled)
            date_display_text_view.setTextColor((int)date_display_text_view.getTag());
        else
            date_display_text_view.setTextColor(Color.GRAY);
    }

    public void setOnSelectDateChangedListener(OnSelectDateChangedListener listener){
        this.onSelectDateChangedListener=listener;
    }

    public String getText(){
        return DateConverter.convertToDisplayStr(this.selected_date);
    }

    public void setDateRange(Date startDate,Date endDate,Date selected_date){
        this.date_range_start=startDate;
        this.date_range_end=endDate;
        this.selected_date=this.current_highlight_date=selected_date;

        calendar=Calendar.getInstance();
        DateConverter.clearCalendarNoneHHmmss(calendar);

        if(date_range_start==null)
            date_range_start=calendar.getTime();
        if(selected_date==null)
            this.selected_date=calendar.getTime();

        this.date_display_text_view.setText(DateConverter.convertToDisplayStr(this.selected_date));
    }

    OnClickListener dropListener=new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            popupSelectDatePicker();
        }
    };

    Dialog date_select_dialog;
    private void popupSelectDatePicker(){

        if(date_select_dialog!=null&&date_select_dialog.isShowing()){
            date_select_dialog.dismiss();
        }else {

            if(date_range_start==null)
                date_range_start=Calendar.getInstance().getTime();
            if(selected_date==null)
                selected_date=Calendar.getInstance().getTime();

            calendar.setTime(date_range_start);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            start_month=current_month=calendar.getTime();

            calendar.setTime(selected_date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            current_month=calendar.getTime();

            LinearLayout containerLayout=(LinearLayout)((Activity)this.m_context).getLayoutInflater().inflate(R.layout.popup_date_picker_layout,null);

            mouth_pre_btn=(ImageButton)containerLayout.findViewById(R.id.date_picker_pre);
            mouth_pre_btn.setTag(-1);
            mouth_pre_btn.setOnClickListener(mouthChangedListener);
            mouth_next_btn=(ImageButton)containerLayout.findViewById(R.id.date_picker_next);
            mouth_next_btn.setTag(1);
            mouth_next_btn.setOnClickListener(mouthChangedListener);

            date_picker_view = (CalendarPickerView) containerLayout.findViewById(R.id.date_picker_view);
            showSelectedMouth(0,selected_date);
            date_picker_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    selected_date=current_highlight_date=date;
                    date_display_text_view.setText(DateConverter.convertToDisplayStr(selected_date));
                    if(onSelectDateChangedListener!=null)
                        onSelectDateChangedListener.onSelectDateChange(date);
                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });

            int popup_width=(int)m_context.getResources().getDimension(R.dimen.calendar_width);
            int popup_height=(int)m_context.getResources().getDimension(R.dimen.calendar_height);

            date_select_dialog=new Dialog(m_context);
            date_select_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            date_select_dialog.setContentView(containerLayout,new ViewGroup.LayoutParams(popup_width, ViewGroup.LayoutParams.WRAP_CONTENT));

            int[] location = new int[2];
            this.getLocationOnScreen(location);

            date_select_dialog.getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
            WindowManager.LayoutParams params = date_select_dialog.getWindow().getAttributes();
            params.x = location[0]-date_display_text_view.getPaddingLeft();
            params.y= location[1]+this.getHeight();
            date_select_dialog.getWindow().setAttributes(params);

            date_select_dialog.show();
        }
    }

    /*private void popupSelectDatePicker(){

        if(popup_view!=null&&popup_view.isShowing()){
            popup_view.dismiss();
        }else {

            if(date_range_start==null)
                date_range_start=Calendar.getInstance().getTime();
            if(selected_date==null)
                selected_date=Calendar.getInstance().getTime();

            calendar.setTime(date_range_start);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            start_month=current_month=calendar.getTime();

            calendar.setTime(selected_date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            current_month=calendar.getTime();

            LinearLayout containerLayout=(LinearLayout)((Activity)this.m_context).getLayoutInflater().inflate(R.layout.popup_date_picker_layout,null);

            mouth_pre_btn=(ImageButton)containerLayout.findViewById(R.id.date_picker_pre);
            mouth_pre_btn.setTag(-1);
            mouth_pre_btn.setOnClickListener(mouthChangedListener);
            mouth_next_btn=(ImageButton)containerLayout.findViewById(R.id.date_picker_next);
            mouth_next_btn.setTag(1);
            mouth_next_btn.setOnClickListener(mouthChangedListener);

            date_picker_view = (CalendarPickerView) containerLayout.findViewById(R.id.date_picker_view);
            showSelectedMouth(0,selected_date);
            date_picker_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    selected_date=current_highlight_date=date;
                    date_display_text_view.setText(DateConverter.convertToDisplayStr(selected_date));
                    if(onSelectDateChangedListener!=null)
                        onSelectDateChangedListener.onSelectDateChange(date);
                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });

            int popup_width=(int)m_context.getResources().getDimension(R.dimen.calendar_width);
            int popup_height=(int)m_context.getResources().getDimension(R.dimen.calendar_height);
            popup_view = new PopupWindow(containerLayout, popup_width,LayoutParams.WRAP_CONTENT);

            ColorDrawable cd = new ColorDrawable();
            popup_view.setBackgroundDrawable(cd);
            popup_view.setAnimationStyle(R.style.popwin_anim_style);
            popup_view.update();
            popup_view.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popup_view.setTouchable(true); // ����popupwindow�ɵ��
            popup_view.setOutsideTouchable(true); // ����popupwindow�ⲿ�ɵ��
            popup_view.setFocusable(true); // ��ȡ����

            popup_view.showAsDropDown(this, 0, 0);

            popup_view.setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popup_view.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }*/

    OnClickListener mouthChangedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int addMouth=(int)v.getTag();
            calendar.setTime(current_highlight_date);
            calendar.add(Calendar.MONTH, addMouth);
            showSelectedMouth(addMouth,calendar.getTime());
        }
    };

    private void showSelectedMouth(int mouthAdd,Date currentDate){

        calendar.setTime(current_month);
        calendar.add(Calendar.MONTH, mouthAdd);

        int compareResult=start_month.compareTo(calendar.getTime());
        if(compareResult>=0) {
            mouth_pre_btn.setEnabled(false);
            if(compareResult>0)
                return;
        }else
            mouth_pre_btn.setEnabled(true);

        current_highlight_date=currentDate;

        current_month=calendar.getTime();
        Date start_date=current_month;
        if(date_range_start.compareTo(current_month)>=0)
            start_date=date_range_start;

        calendar.setTime(current_month);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        Date end_date=calendar.getTime();
        if(date_range_end!=null&&end_date.compareTo(date_range_end)<=0)
            end_date=date_range_end;

        date_picker_view.init(start_date,end_date) //
                .withSelectedDate(currentDate);
    }

    public interface OnSelectDateChangedListener{
        public void onSelectDateChange(Date selectDate);
    }

}
