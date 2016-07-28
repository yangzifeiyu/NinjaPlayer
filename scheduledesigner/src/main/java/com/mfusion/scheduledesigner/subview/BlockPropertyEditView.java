package com.mfusion.scheduledesigner.subview;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.scheduledesigner.R;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.scheduledesigner.entity.CallbackBundle;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;
import com.mfusion.scheduledesigner.values.ButtonHoverStyle;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class BlockPropertyEditView extends LinearLayout {

    Context m_context;

    Button time_start_view,time_end_view;
    CheckBox recurrence_view;
    ListView item_list_view;
    List<CheckBox> recurrence_list=new ArrayList<CheckBox>();
    Button date_start_bt,date_end_bt;
    CheckBox date_end_cb,date_no_end_cb;
    ImageButton apply_btn;

    LinearLayout propertiesLayout =null;

    BlockView block_view=null;
    BlockUIEntity block_entity=null;

    public BlockPropertyEditView(Context context){
        super(context);
    }

    public BlockPropertyEditView(Context context,final CallbackBundle editResponse) {
        super(context);
        // TODO Auto-generated constructor stub

        this.m_context=context;
        try {
            propertiesLayout =(LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_block_properties, this,true);

            time_start_view=(Button)propertiesLayout.findViewById(R.id.block_property_time_start);
            time_start_view.setOnClickListener(time_click_listener);
            time_end_view=(Button)propertiesLayout.findViewById(R.id.block_property_time_end);
            time_end_view.setOnClickListener(time_click_listener);

            recurrence_view=(CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence);
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_mon));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_tuse));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_web));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_thur));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_fir));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_sat));
            recurrence_list.add((CheckBox)propertiesLayout.findViewById(R.id.block_property_recurrence_sun));

            recurrence_view.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub
                    for (CheckBox checkBox : recurrence_list){
                        checkBox.setEnabled(checked);
                        checkBox.setChecked(checked);
                    }

                    date_start_bt.setEnabled(checked);
                    date_end_bt.setEnabled(checked);
                    date_end_cb.setEnabled(checked);
                    date_no_end_cb.setEnabled(checked);
                }
            });

            date_start_bt=(Button)propertiesLayout.findViewById(R.id.block_property_bt_startdate);
            date_start_bt.setOnClickListener(date_click_listener);
            date_end_bt=(Button)propertiesLayout.findViewById(R.id.block_property_bt_enddate);
            date_end_bt.setOnClickListener(date_click_listener);

            date_end_cb=(CheckBox)propertiesLayout.findViewById(R.id.block_property_cb_hasend);
            date_end_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub
                    date_no_end_cb.setChecked(!checked);
                }
            });
            date_no_end_cb=(CheckBox)propertiesLayout.findViewById(R.id.block_property_cb_noend);
            date_no_end_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub
                    date_end_cb.setChecked(!checked);
                    date_end_bt.setEnabled(!checked);
                }
            });

            apply_btn=(ImageButton)propertiesLayout.findViewById(R.id.block_property_apply);
            apply_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    updateBlockInfo();
                    editResponse.callback(null);
                }
            });

            ButtonHoverStyle.bindingHoverEffect(apply_btn,getResources());

            initPropertyView();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void initPropertyView(){
        String current_date= DateConverter.convertCurrentDateToStr();
        date_start_bt.setText(current_date);
        date_end_bt.setText(current_date);

        time_start_view.setText("00:00");
        time_end_view.setText("00:00");

        setPropertyEditability(false);
    }

    public void setPropertyEditability(Boolean editable){

        time_start_view.setEnabled(editable);
        time_end_view.setEnabled(editable);
        recurrence_view.setEnabled(editable);
        apply_btn.setEnabled(editable);

        if(editable)
            return;

        for (int i = 0; i < 7 ;i++) {
            recurrence_list.get(i).setEnabled(editable);
        }
        date_start_bt.setEnabled(editable);
        date_no_end_cb.setEnabled(editable);
        date_end_cb.setEnabled(editable);
        date_end_bt.setEnabled(editable);
    }

    public void bindingBlockInfo(BlockView blockView,Boolean forceBinding){

        try {

            if(!forceBinding&&this.block_view==blockView)
                return;

            setPropertyEditability(true);

            this.block_view=blockView;
            this.block_entity=blockView.block_info;

            time_start_view.setText(DateConverter.convertShortTimeToStr(block_entity.startTime));
            time_end_view.setText(DateConverter.convertShortTimeToStr(block_entity.endTime));

            date_start_bt.setText(DateConverter.convertDateToStr(block_entity.startDate));
            if(block_entity.endDate==null){
                date_end_bt.setText(date_start_bt.getText());
                date_no_end_cb.setChecked(true);
                date_end_bt.setEnabled(false);
            }else {
                date_end_bt.setText(DateConverter.convertDateToStr(block_entity.endDate));
                date_end_cb.setChecked(true);
                date_end_bt.setEnabled(true);
            }

            date_start_bt.setEnabled(block_entity.isRecurrence);
            date_end_bt.setEnabled(block_entity.isRecurrence);
            date_end_cb.setEnabled(block_entity.isRecurrence);
            date_no_end_cb.setEnabled(block_entity.isRecurrence);
            recurrence_view.setChecked(block_entity.isRecurrence);
            if(block_entity.isRecurrence){
                for (int i = 0; i < block_entity.recurrence.length() ;i++) {
                    recurrence_list.get(i).setChecked(ScheduleDrawHelper.checkBlockRecurrence(block_entity, i));
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void updateBlockInfo() {

        block_entity.startTime=DateConverter.convertShortStrToTime(time_start_view.getText().toString());
        block_entity.endTime=DateConverter.convertShortStrToTime(time_end_view.getText().toString());
        block_entity.duration=ScheduleDrawHelper.getDurtion(block_entity.startTime, block_entity.endTime);

        block_entity.startDate=DateConverter.convertStrToDate(date_start_bt.getText().toString());
        if(date_end_cb.isChecked())
            block_entity.endDate=DateConverter.convertStrToDate(date_end_bt.getText().toString());
        else
            block_entity.endDate=null;

        block_entity.isRecurrence=recurrence_view.isChecked();
        if(block_entity.isRecurrence){
            StringBuilder recurrence=new StringBuilder();
            for (CheckBox checkBox : this.recurrence_list) {
                recurrence.append(checkBox.isChecked()?"1":"0");
            }
            block_entity.recurrence=recurrence.toString();
        }else {
            block_entity.endDate=block_entity.startDate;
            block_entity.recurrence=ScheduleDrawHelper.getRecurrenceByStartDate(block_entity.startDate);
        }

    }

    OnClickListener date_click_listener=new OnClickListener() {

        @Override
        public void onClick(final View view) {
            // TODO Auto-generated method stub
            String dialogTitle="";
            Calendar date=Calendar.getInstance();
            if(view==date_start_bt) {
                date.setTime(block_entity.startDate);
                dialogTitle="Set Start Date :";
            }else {
                date.setTime(block_entity.endDate);
                dialogTitle="Set End Date :";
            }

            DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newCalendar=Calendar.getInstance();
                    newCalendar.set(year,monthOfYear,dayOfMonth,0,0,0);
                    if(view==date_start_bt) {
                        block_entity.startDate = newCalendar.getTime();
                        date_start_bt.setText(DateConverter.convertDateToStr(block_entity.startDate));
                    }
                    else {
                        block_entity.endDate = newCalendar.getTime();
                        date_end_bt.setText(DateConverter.convertDateToStr(block_entity.endDate));
                    }
                }
            };

            DatePickerDialog dpd=new DatePickerDialog(m_context,Datelistener,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
            dpd.setTitle(dialogTitle);
            dpd.getDatePicker().setCalendarViewShown(false);
            dpd.show();

        }
    };

    OnClickListener time_click_listener=new OnClickListener() {

        @Override
        public void onClick(final View view) {
            // TODO Auto-generated method stub
            String dialogTitle="";
            Calendar date=Calendar.getInstance();
            if(view==time_start_view){
                date.setTime(block_entity.startTime);
                dialogTitle="Set Start Time :";
            }else {
                date.setTime(block_entity.endTime);
                dialogTitle="Set End Time :";
            }

            TimePickerDialog.OnTimeSetListener timeListener=new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    Calendar newCalendar=Calendar.getInstance();
                    newCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    newCalendar.set(Calendar.MINUTE,minute);
                    if(view==time_start_view){
                        block_entity.startTime = newCalendar.getTime();
                        time_start_view.setText(DateConverter.convertShortTimeToStr(block_entity.startTime));
                    }else{
                        block_entity.endTime = newCalendar.getTime();
                        time_end_view.setText(DateConverter.convertShortTimeToStr(block_entity.endTime));
                    }
                }
            };

            TimePickerDialog tpd=new TimePickerDialog(m_context,timeListener,date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),false);
            tpd.setTitle(dialogTitle);
            tpd.show();

        }
    };
    class EditChangedListener implements TextWatcher {

        EditText inpuText;

        String old_value="";

        public EditChangedListener(EditText view){
            this.inpuText=view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.old_value=s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if(count<1)
                    return;

                if(s.toString().length()>5){
                    int selectIndex=this.inpuText.getSelectionStart();
                    this.inpuText.setText(old_value);
                    this.inpuText.setSelection(selectIndex-1);
                    return;
                }

                String add_value=s.toString().substring(start,start+count).toLowerCase();
                if(add_value.equals("a")||add_value.equals("p")||add_value.equals("m")){
                    int selectIndex=this.inpuText.getSelectionStart();
                    this.inpuText.setText(old_value);
                    this.inpuText.setSelection(selectIndex-count);
                    return;
                }
                if(add_value.equals(":")){
                    if (start==0||s.toString().lastIndexOf(":")>=0) {
                        int selectIndex=this.inpuText.getSelectionStart();
                        this.inpuText.setText(old_value);
                        this.inpuText.setSelection(selectIndex-count);
                        return;
                    }
                }

                int index=s.toString().lastIndexOf(":");
                if(index<0&&(start+count)==2){
                    this.inpuText.setText(s.toString()+":");
                    this.inpuText.setSelection(s.toString().length()+1);
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
