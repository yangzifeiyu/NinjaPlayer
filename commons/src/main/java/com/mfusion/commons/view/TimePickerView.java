package com.mfusion.commons.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commontools.R;

import java.security.spec.ECField;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ThinkPad on 2016/9/23.
 */
public class TimePickerView extends LinearLayout {

    private Context m_context;

    private LinearLayout time_picker_layout;

    private EditText text_hour_view,text_min_view,text_second_view,edited_time_view;

    private TextView text_hm_split_view,text_ms_split_view;

    private ImageButton time_increase_btn,time_decrease_btn;

    private int text_color;

    private Boolean showSecond=false;

    private String default_time_item="00";

    private HashMap<EditText,TextWatcher> editorWatcherMap;

    private OnTimeChangedListener onTimeChangedListener;

    public TimePickerView(Context context) {
        super(context);
        init(context);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.m_context=context;

        this.editorWatcherMap=new HashMap<>();

        time_picker_layout=(LinearLayout) LayoutInflater.from(this.m_context).inflate(R.layout.time_picker_view,this);

        text_hour_view=bindingEditText(R.id.time_picker_hour,24);
        text_min_view=bindingEditText(R.id.time_picker_min,60);
        text_second_view=bindingEditText(R.id.time_picker_second,60);

        text_color=text_hour_view.getCurrentTextColor();

        text_hm_split_view=(TextView)time_picker_layout.findViewById(R.id.time_picker_split_hm);
        text_ms_split_view=(TextView)time_picker_layout.findViewById(R.id.time_picker_split_ms);

        time_increase_btn=(ImageButton) time_picker_layout.findViewById(R.id.time_picker_increase);
        time_increase_btn.setTag(-1);
        time_increase_btn.setOnClickListener(timeChangedListener);
        time_decrease_btn=(ImageButton) time_picker_layout.findViewById(R.id.time_picker_decrease);
        time_decrease_btn.setTag(1);
        time_decrease_btn.setOnClickListener(timeChangedListener);

        edited_time_view=text_hour_view;

        //edited_time_view.setFocusable(true);
    }

    private EditText bindingEditText(int resourceId,int time_range){
        final EditText time_edit_view=(EditText)time_picker_layout.findViewById(resourceId);
        time_edit_view.setTag(time_range);
        time_edit_view.setText(default_time_item);
        time_edit_view.setOnFocusChangeListener(timeEditorFocusListener);
        time_edit_view.setSelectAllOnFocus(true);
        time_edit_view.setCursorVisible(false);
        TextWatcher textWatcher=new TimeTextWatcher(time_edit_view,time_range);
        time_edit_view.addTextChangedListener(textWatcher);
        //editorWatcherMap.put(time_edit_view,textWatcher);
        WindowsDecorHelper.hideSoftInputForEditText(time_edit_view);

        return time_edit_view;
    }

    public void setOnTimeChangedListener(OnTimeChangedListener listener){
        this.onTimeChangedListener=listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        int color=Color.GRAY;
        if(enabled) {
            color=text_color;
            edited_time_view.setBackgroundColor(edited_time_view.getHighlightColor());
        }else {
            edited_time_view.setBackgroundColor(Color.TRANSPARENT);
        }

        edited_time_view.setFocusable(enabled);
        text_hour_view.setTextColor(color);
        text_min_view.setTextColor(color);
        text_second_view.setTextColor(color);
        text_hm_split_view.setTextColor(color);
        text_ms_split_view.setTextColor(color);

        text_hour_view.setEnabled(enabled);
        text_min_view.setEnabled(enabled);
        text_second_view.setEnabled(enabled);
        time_increase_btn.setEnabled(enabled);
        time_decrease_btn.setEnabled(enabled);
    }

    public void setTime(String timeStr,Boolean showSecond){
        this.showSecond=showSecond;
        text_hour_view.setText(default_time_item);
        text_min_view.setText(default_time_item);
        text_second_view.setText(default_time_item);
        ((ViewGroup)text_second_view.getParent()).setVisibility(GONE);
        text_ms_split_view.setVisibility(GONE);

        try {
            if(timeStr!=null&&!timeStr.isEmpty()) {
                String[] timeItems = timeStr.split(":");
                text_hour_view.setText(timeItems[0]);
                text_min_view.setText(timeItems[1]);
                if(showSecond){
                    text_second_view.setText(timeItems[2]);
                    ((ViewGroup)text_second_view.getParent()).setVisibility(VISIBLE);
                    text_ms_split_view.setVisibility(VISIBLE);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        text_ms_split_view.setVisibility(showSecond?VISIBLE:GONE);
        text_second_view.setVisibility(showSecond?VISIBLE:GONE);

        edited_time_view.setBackgroundColor(Color.TRANSPARENT);
        edited_time_view=text_hour_view;
    }

    public String getTime(){

        return formatTimeString(text_hour_view.getText().toString())+":"+formatTimeString(text_min_view.getText().toString())+":"+(showSecond?formatTimeString(text_second_view.getText().toString()):"00");
    }

    OnFocusChangeListener timeEditorFocusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                if(edited_time_view!=null)
                    edited_time_view.setBackgroundColor(Color.TRANSPARENT);
                edited_time_view=((EditText) v);
                edited_time_view.setSelection(0, edited_time_view.length());
                edited_time_view.setBackgroundColor(edited_time_view.getHighlightColor());
            }
        }
    };

    OnClickListener timeChangedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int time_range=Integer.parseInt(edited_time_view.getTag().toString());
            edited_time_view.setText(formatTimeString(String.valueOf((Integer.parseInt(edited_time_view.getText().toString())+(Integer)v.getTag()+time_range)%time_range)));

            if(onTimeChangedListener!=null)
                onTimeChangedListener.onTimeChange();
        }
    };

    private String formatTimeString(String time){
        return time.length()<2?("0"+time):time;
    }

    public interface OnTimeChangedListener{
        public void onTimeChange();
    }

    class TimeTextWatcher implements TextWatcher {

        private EditText m_edit_text;

        private int time_range=0;

        public TimeTextWatcher(EditText editText,int timeRange){
            this.m_edit_text=editText;
            this.time_range=timeRange;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {
                if(s.length()>2){
                    this.m_edit_text.setText(s.subSequence(1,s.length()).toString());
                    return;
                }

                int value=Integer.parseInt(s.toString());
                if(value>=this.time_range){
                    this.m_edit_text.setText(s.subSequence(s.length()-1,s.length()).toString());
                    return;
                }
            }catch (Exception ex){ex.printStackTrace();}
            finally {
                this.m_edit_text.setSelection(this.m_edit_text.getText().length());
                if(onTimeChangedListener!=null)
                    onTimeChangedListener.onTimeChange();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
