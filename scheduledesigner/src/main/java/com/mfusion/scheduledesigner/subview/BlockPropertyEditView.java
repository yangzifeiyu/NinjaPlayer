package com.mfusion.scheduledesigner.subview;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.CheckSwitchButton;
import com.mfusion.commons.view.DatePickerPopupView;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.TimePickerView;
import com.mfusion.scheduledesigner.R;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;
import com.mfusion.scheduledesigner.values.RangeTimePickerDialog;
import com.mfusion.scheduledesigner.values.ScheduleConflictChecker;
import com.mfusion.scheduledesigner.values.StaticTitleDatePickerDialog;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class BlockPropertyEditView extends LinearLayout implements View.OnLayoutChangeListener{

    Context m_context;

    TimePickerView time_start_view,time_end_view;
    CheckSwitchButton recurrence_view;
    ListView item_list_view;
    List<CheckBox> recurrence_list=new ArrayList<CheckBox>();
    DatePickerPopupView date_start_bt,date_end_bt;
    CheckBox date_end_cb,date_no_end_cb;
    ImageTextHorizontalView apply_btn;

    TextView property_warning_view;

    LinearLayout properties_layout =null;
    
    RelativeLayout property_recurrence_layout;
    RelativeLayout property_date_layout;

    BlockView block_view=null;
    BlockUIEntity block_entity=null;
    List<BlockUIEntity> all_block_list;

    Date currentDate;

    private CallbackBundle editResponseCall;

    public BlockPropertyEditView(Context context){
        super(context);
    }

    public BlockPropertyEditView(Context context,List<BlockUIEntity> all_block_list,CallbackBundle editResponse) {
        super(context);
        // TODO Auto-generated constructor stub

        this.m_context=context;
        this.all_block_list=all_block_list;
        this.editResponseCall=editResponse;
        try {
            properties_layout =(LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_block_properties, this,true);

            property_recurrence_layout=(RelativeLayout) properties_layout.findViewById(R.id.block_property_recurrence_layout);
            property_date_layout=(RelativeLayout) properties_layout.findViewById(R.id.block_property_date_layout);

            time_start_view=(TimePickerView) properties_layout.findViewById(R.id.block_property_time_start);
            time_end_view=(TimePickerView)properties_layout.findViewById(R.id.block_property_time_end);

            recurrence_view=(CheckSwitchButton)properties_layout.findViewById(R.id.block_property_recurrence);
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_mon));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_tuse));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_web));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_thur));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_fir));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_sat));
            recurrence_list.add((CheckBox)properties_layout.findViewById(R.id.block_property_recurrence_sun));

            recurrence_view.setOnChangeListener(new CheckSwitchButton.OnSwitchChangedListener() {
                @Override
                public void onSwitchChange(CheckSwitchButton switchView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    for (CheckBox checkBox : recurrence_list){
                        checkBox.setEnabled(isChecked);
                        checkBox.setChecked(isChecked);
                    }

                    if(isChecked){
                        date_end_cb.setChecked(!isChecked);
                        date_no_end_cb.setChecked(isChecked);
                    }

                    //updateRecurrence();

                    bindingRecurrenceCheckEvent(isChecked);

                    date_start_bt.setEnabled(isChecked);
                    //date_end_bt.setEnabled(isChecked);
                    date_end_cb.setEnabled(isChecked);
                    date_no_end_cb.setEnabled(isChecked);

                    property_date_layout.setEnabled(isChecked);
                    property_recurrence_layout.setEnabled(isChecked);
                }
            });

            date_start_bt=(DatePickerPopupView)properties_layout.findViewById(R.id.block_property_bt_startdate);
            //date_start_bt.setOnClickListener(date_click_listener);
            date_end_bt=(DatePickerPopupView)properties_layout.findViewById(R.id.block_property_bt_enddate);
            //date_end_bt.setOnClickListener(date_click_listener);

            date_end_cb=(CheckBox)properties_layout.findViewById(R.id.block_property_cb_hasend);
            date_end_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub
                    date_no_end_cb.setChecked(!checked);
                    //updateEndDate();
                }
            });
            date_no_end_cb=(CheckBox)properties_layout.findViewById(R.id.block_property_cb_noend);
            date_no_end_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub
                    date_end_cb.setChecked(!checked);
                    date_end_bt.setEnabled(!checked);
                    //updateEndDate();
                }
            });

            property_warning_view=(TextView)properties_layout.findViewById(R.id.block_property_warning);

            apply_btn=(ImageTextHorizontalView)properties_layout.findViewById(R.id.block_property_apply);
            apply_btn.setText("Apply");
            apply_btn.setImage(R.drawable.apply);
            apply_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    updateBlockInfo();
                }
            });

            initPropertyView();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        this.addOnLayoutChangeListener(this);
    }

    public void initPropertyView(){
        String current_date= DateConverter.convertToDisplayStr(null);
        //date_start_bt.setText(current_date);
        //date_end_bt.setText(current_date);

        currentDate=DateConverter.getCurrentDate();
        time_start_view.setTime("00:00",false);
        time_end_view.setTime("00:00",false);

        setPropertyEditability(false);
    }

    public void setPropertyEditability(Boolean editable){

        property_warning_view.setVisibility(GONE);

        property_recurrence_layout.setEnabled(editable);
        property_date_layout.setEnabled(editable);

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

    private void bindingRecurrenceCheckEvent(Boolean checked){

        for (CheckBox checkBox : recurrence_list){
            if(checked)
                checkBox.setOnCheckedChangeListener(recurrence_change_listener);
            else
                checkBox.setOnCheckedChangeListener(null);
        }
    }

    public void bindingBlockInfo(BlockView blockView,Boolean forceBinding){

        try {

            if(!forceBinding&&this.block_view==blockView)
                return;

            setPropertyEditability(true);

            this.block_view=blockView;
            this.block_entity=blockView.block_info;

            time_start_view.setTime(DateConverter.convertShortTimeToStr(block_entity.startTime),false);
            time_end_view.setTime(DateConverter.convertShortTimeToStr(block_entity.endTime),false);

            Date dateRange=getBlockDateRange(block_entity.startDate);
            date_start_bt.setDateRange(dateRange,null,block_entity.startDate);
            //date_start_bt.setText(DateConverter.convertToDisplayStr(block_entity.startDate));

            recurrence_view.setChecked(block_entity.isRecurrence);
            if(block_entity.isRecurrence){
                for (int i = 0; i < block_entity.recurrence.length() ;i++) {
                    recurrence_list.get(i).setChecked(ScheduleDrawHelper.checkBlockRecurrence(block_entity, i));
                    recurrence_list.get(i).setEnabled(true);
                }
            }

            if(block_entity.endDate==null){
                date_end_bt.setDateRange(dateRange,null,block_entity.startDate);
                //date_end_bt.setText(date_start_bt.getText());
                date_no_end_cb.setChecked(true);
                date_end_cb.setChecked(false);
                date_end_bt.setEnabled(false);
            }else {
                date_end_bt.setDateRange(dateRange,null,block_entity.endDate);
                //date_end_bt.setText(DateConverter.convertToDisplayStr(block_entity.endDate));
                date_end_cb.setChecked(true);
                date_no_end_cb.setChecked(false);
                date_end_bt.setEnabled(true);
            }

            date_start_bt.setEnabled(block_entity.isRecurrence);
            date_end_bt.setEnabled(block_entity.isRecurrence);
            date_end_cb.setEnabled(block_entity.isRecurrence);
            date_no_end_cb.setEnabled(block_entity.isRecurrence);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LogOperator.WriteLogfortxt("BlockPropertyEditView==>bindingBlockInfo :"+e.getMessage());
        }
    }

    private Date getBlockDateRange(Date startDate){
        return startDate.compareTo(currentDate)>=0?currentDate:startDate;
    }

    public void updateBlockInfo() {

        Date startDate=DateConverter.convertDisplayStrToDate(date_start_bt.getText().toString()),endDate=null;
        if(recurrence_view.isChecked()&&date_end_cb.isChecked()){
            endDate=DateConverter.convertDisplayStrToDate(date_end_bt.getText().toString());
            if(endDate.compareTo(startDate)<0){
                property_warning_view.setText("EndDate is not greater than the StartDate");
                property_warning_view.setVisibility(VISIBLE);
                return;
            }
        }

        BlockUIEntity cloneBlock=block_entity.clone();

        block_entity.startDate=startDate;
        block_entity.endDate=endDate;

        block_entity.startTime=DateConverter.convertShortStrToTime(time_start_view.getTime().toString());
        block_entity.endTime=DateConverter.convertShortStrToTime(time_end_view.getTime().toString());
        block_entity.duration=ScheduleDrawHelper.getDuration(block_entity.startTime, block_entity.endTime);

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
        Boolean conflict = ScheduleConflictChecker.isBlockConflict(all_block_list,block_entity);
        if(conflict) {
            property_warning_view.setText(m_context.getResources().getString(R.string.sche_block_conflict));
            property_warning_view.setVisibility(VISIBLE);
            block_entity.copy(cloneBlock);
            return;
        }
        editResponseCall.callback(null);

    }
/*

    private void updateRecurrence(){
        Boolean isRecurrence=recurrence_view.isChecked();
        String recurrenceStr="0000000";
        Date endDate=null;
        if(isRecurrence){
            if(date_end_cb.isChecked())
                endDate=DateConverter.convertDisplayStrToDate(date_end_bt.getText().toString());
            else
                endDate=null;
            StringBuilder recurrence=new StringBuilder();
            for (CheckBox checkBox : this.recurrence_list) {
                recurrence.append(checkBox.isChecked()?"1":"0");
            }
            recurrenceStr=recurrence.toString();
        }else {
            endDate=block_entity.startDate;
            recurrenceStr=ScheduleDrawHelper.getRecurrenceByStartDate(block_entity.startDate);
        }
        if(block_entity.isRecurrence!=isRecurrence||!block_entity.recurrence.equalsIgnoreCase(recurrenceStr)){
            block_entity.isRecurrence=isRecurrence;
            block_entity.recurrence=recurrenceStr;
            block_entity.endDate=endDate;
            if(editResponseCall!=null)
                editResponseCall.callback(null);
        }
    }

    private void updateStartTime(){
        Date startTime=DateConverter.convertShortStrToTime(time_start_view.getText().toString());
        if(startTime.compareTo(block_entity.startTime)!=0) {
            block_entity.startTime=startTime;
            block_entity.duration=ScheduleDrawHelper.getDurtion(block_entity.startTime, block_entity.endTime);
            if (editResponseCall != null)
                editResponseCall.callback(null);
        }
    }

    private void updateEndTime(){
        Date endTime=DateConverter.convertShortStrToTime(time_end_view.getText().toString());
        if(endTime.compareTo(block_entity.endTime)!=0) {
            block_entity.endTime=endTime;
            block_entity.duration=ScheduleDrawHelper.getDurtion(block_entity.startTime, block_entity.endTime);
            if (editResponseCall != null)
                editResponseCall.callback(null);
        }
    }

    private void updateStartDate(){
        Date startDate=DateConverter.convertDisplayStrToDate(date_start_bt.getText().toString());
        if(startDate.compareTo(block_entity.startDate)!=0) {
            block_entity.startDate=startDate;
            if (editResponseCall != null)
                editResponseCall.callback(null);
        }
    }

    private void updateEndDate(){
        if(!property_date_layout.isEnabled())
            return;

        Date endDate=null;
        if(recurrence_view.isChecked()){
            if(date_end_cb.isChecked())
                endDate=DateConverter.convertDisplayStrToDate(date_end_bt.getText().toString());
            else
                endDate=null;

        }else {
            endDate = block_entity.startDate;
        }

        if((endDate==null&&block_entity.endDate!=null)||(endDate!=null&&block_entity.endDate==null)||(endDate!=null&&endDate.compareTo(block_entity.endDate)!=0)) {
            block_entity.endDate=endDate;
            if (editResponseCall != null)
                editResponseCall.callback(null);
        }
    }
*/

    OnCheckedChangeListener recurrence_change_listener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            /*if(property_recurrence_layout.isEnabled())
                updateRecurrence();*/
        }
    };

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

            final Boolean[] isApplySetting = {false};
            DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    if(isApplySetting.length>0&&isApplySetting[0]){
                        Calendar newCalendar=Calendar.getInstance();
                        newCalendar.set(year,monthOfYear,dayOfMonth,0,0,0);
                        if(view==date_start_bt) {
                            //block_entity.startDate = newCalendar.getTime();
                            //date_start_bt.setText(DateConverter.convertToDisplayStr(newCalendar.getTime()));
                            //updateStartDate();
                        }
                        else {
                            //block_entity.endDate = newCalendar.getTime();
                            //date_end_bt.setText(DateConverter.convertToDisplayStr(newCalendar.getTime()));
                            //updateEndDate();
                        }
                    }
                }
            };

            final DatePickerDialog dpd=new StaticTitleDatePickerDialog(m_context,Datelistener,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
            dpd.setTitle(dialogTitle);

            dpd.setButton(TimePickerDialog.BUTTON_POSITIVE,"Apply",dpd);
            dpd.getDatePicker().setCalendarViewShown(false);
            if(view==date_start_bt) {
                if(!date_no_end_cb.isChecked())
                    dpd.getDatePicker().setMaxDate(block_entity.endDate.getTime());
            }
            else {
                dpd.getDatePicker().setMinDate(block_entity.startDate.getTime());
            }
            dpd.show();
            Button applyBtn= dpd.getButton(TimePickerDialog.BUTTON_POSITIVE);
            applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isApplySetting[0] =true;
                    dpd.dismiss();
                }
            });
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

            final Boolean[] isApplySetting = {false};
            TimePickerDialog.OnTimeSetListener timeListener=new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    if(isApplySetting.length>0&&isApplySetting[0]){
                        Calendar newCalendar=Calendar.getInstance();
                        newCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        newCalendar.set(Calendar.MINUTE,minute);
                        if(view==time_start_view){
                            //block_entity.startTime = newCalendar.getTime();
                            time_start_view.setTime(DateConverter.convertShortTimeToStr(newCalendar.getTime()),false);
                            //updateStartTime();
                        }else{
                            //block_entity.endTime = newCalendar.getTime();
                            time_end_view.setTime(DateConverter.convertShortTimeToStr(newCalendar.getTime()),false);
                            //updateEndTime();
                        }
                    }
                }
            };

            final RangeTimePickerDialog tpd=new RangeTimePickerDialog(m_context,timeListener,date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),false);
            tpd.setTitle(dialogTitle);
            tpd.setButton(TimePickerDialog.BUTTON_POSITIVE,"Apply",tpd);
            if(view==time_start_view) {
                tpd.setMax(block_entity.endTime.getHours(),block_entity.endTime.getMinutes()-1);
            }
            else {
                tpd.setMin(block_entity.startTime.getHours(),block_entity.startTime.getMinutes()+1);
            }
            tpd.show();
            Button applyBtn= tpd.getButton(TimePickerDialog.BUTTON_POSITIVE);
            applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isApplySetting[0] =true;
                    tpd.dismiss();
                }
            });
        }
    };

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(top!=oldTop||bottom!=oldBottom){
            //ScreenAdjustHelper.getScaleValusByHeight(original_height,bottom-top,properties_layout);
        }
    }

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
