package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;


import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.data.DALSettings;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.CheckSwitchButton;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.TimePickerView;
import com.mfusion.ninjaplayer.R;

import java.util.Calendar;


public class ConfigurationFragment extends AbstractFragment {

    ImageTextHorizontalView Save, Check;
    EditText pass, passagain;
    TextView status;
    RadioGroup radioGroup;
    RadioButton Landscape, Portrait;
    TimePickerDialog timePickerDialog;

    TimePickerView shutdown_time_picker,wakeup_time_picker;

    CheckSwitchButton pass_switch_btn,shutdown_switch_btn,wakeup_switch_btn;

    TextView m_warning_view;

    Boolean isCreated=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView!=null){
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_configuration, container, false);
        rootView.setFocusable(true);
        rootView.setFocusableInTouchMode(true);
        m_warning_view = (TextView) rootView.findViewById(R.id.config_warning_view);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);// portrait orientation
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);//landscape orientation

        shutdown_switch_btn = (CheckSwitchButton) rootView.findViewById(R.id.switch_shutdown);//shutdown time checkbox
        shutdown_time_picker=(TimePickerView) rootView.findViewById(R.id.time_shutdown);

        wakeup_switch_btn = (CheckSwitchButton) rootView.findViewById(R.id.switch_wakeup);//wake up time checkboc
        wakeup_time_picker=(TimePickerView) rootView.findViewById(R.id.time_wakeup);

        pass_switch_btn=(CheckSwitchButton) rootView.findViewById(R.id.switch_pass);
        pass = (EditText) rootView.findViewById(R.id.etPassword);//password
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass.isClickable()) {
                    pass.setFocusableInTouchMode(true);
                    pass.setFocusable(true);
                }
            }
        });
        passagain = (EditText) rootView.findViewById(R.id.etMatch);//confirm password
        passagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passagain.isClickable()) {
                    passagain.setFocusable(true);
                    passagain.setFocusableInTouchMode(true);
                }
            }
        });
        status = (TextView) rootView.findViewById(R.id.txtStatusPa);//password validation text view
        status.setTag(status.getCurrentTextColor());

        Save = (ImageTextHorizontalView) rootView.findViewById(R.id.btnContinue);//save setting button
        Save.setText("Apply");
        Save.setImage(R.drawable.mf_save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveModification(null);
            }
        });//save setting

        bindingConfiguration();

        isCreated=true;
        return rootView;
    }//oncreate

    TextWatcher passwordWatcher= new TextWatcher() {

        String oldText;
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            oldText=s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!pass_switch_btn.isChecked())
                return;

            String strPass1 = pass.getText().toString().trim();
            if(strPass1.length()>12){
                pass.setText(oldText);
                pass.setSelection(oldText.length()-1);
                return;
            }
            if(strPass1.isEmpty()){
                status.setText(R.string.password_limit);//display when password is valid
                status.setTextColor((int)status.getTag());
                return;
            }
            if (isValidPassword(strPass1)) {
                status.setText(R.string.password_accept);//display when password is valid
                status.setTextColor(getResources().getColor(R.color.config_match));
            } else {
                status.setText(R.string.password_unaccept);//display when password is not valid
                status.setTextColor(getResources().getColor(R.color.config_unmatch));
            }
        }
    };

    TextWatcher passwordAgainWatcher= new TextWatcher() {
        String oldText;
        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            oldText=s.toString();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(!pass_switch_btn.isChecked())
                return;
            String strPass1 = pass.getText().toString();
            String strPass2 = passagain.getText().toString();
            if(strPass2.length()>12){
                passagain.setText(oldText);
                passagain.setSelection(oldText.length()-1);
                return;
            }
            if (isValidPassword(strPass1) && strPass1.equals(strPass2)) {
                status.setText(R.string.password_match);//display when passwords match
                status.setTextColor(getResources().getColor(R.color.config_match));
            } else {
                status.setText(R.string.password_notmatch);//display when passwords do not match
                status.setTextColor(getResources().getColor(R.color.config_unmatch));
            }
        }
    };

    CheckSwitchButton.OnSwitchChangedListener checkedPasswordListener = new CheckSwitchButton.OnSwitchChangedListener() {
        @Override
        public void onSwitchChange(CheckSwitchButton switchView, boolean isChecked) {
        //do stuff
            int textColor=Color.LTGRAY;
            if (isChecked) {
                textColor=getResources().getColor(R.color.config_text);
            } else {
                status.setText("");
                status.setTextColor((int)status.getTag());
            }

            pass.setTextColor(textColor);
            pass.setHintTextColor(textColor);
            passagain.setTextColor(textColor);
            passagain.setHintTextColor(textColor);

            pass.setFocusableInTouchMode(isChecked);
            passagain.setFocusableInTouchMode(isChecked);
            pass.setFocusable(isChecked);
            pass.setClickable(isChecked);
            passagain.setFocusable(isChecked);
            passagain.setClickable(isChecked);
        }
    };

    CheckSwitchButton.OnSwitchChangedListener checkedShutdownListener = new CheckSwitchButton.OnSwitchChangedListener() {
        @Override
        public void onSwitchChange(CheckSwitchButton switchView, boolean isChecked) {
            //do stuff
            shutdown_time_picker.setEnabled(isChecked);
        }
    };

    CheckSwitchButton.OnSwitchChangedListener checkedWakeupListener = new CheckSwitchButton.OnSwitchChangedListener() {
        @Override
        public void onSwitchChange(CheckSwitchButton switchView, boolean isChecked) {
            //do stuff
            wakeup_time_picker.setEnabled(isChecked);
        }
    };

    private void bindingConfiguration(){

        radioGroup.setOnCheckedChangeListener(null);

        pass.removeTextChangedListener(passwordWatcher);//check password

        passagain.removeTextChangedListener(passwordAgainWatcher);//confirm password validation

        pass_switch_btn.setOnChangeListener(null);//password checkbox

        shutdown_switch_btn.setOnChangeListener(null);//shutdown time check box

        wakeup_switch_btn.setOnChangeListener(null);//wake up time checkbox

        int orientation=DALSettings.getInstance().getOrientation();
        if(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE==orientation)
            Landscape.setChecked(true);
        else Portrait.setChecked(true);

        String passwordStr=DALSettings.getInstance().getExitPassword();
        Boolean enabled=false;
        int textColor=Color.LTGRAY;

        if(passwordStr!=null&&!passwordStr.isEmpty()){
            enabled=true;
            textColor=getResources().getColor(R.color.config_text);
        }
        pass_switch_btn.setTag(enabled);
        pass_switch_btn.setChecked(enabled);
        pass.setTag(passwordStr);
        pass.setText(passwordStr);
        passagain.setText(passwordStr);

        pass.setTextColor(textColor);
        pass.setHintTextColor(textColor);
        passagain.setTextColor(textColor);
        passagain.setHintTextColor(textColor);

        pass.setClickable(enabled);
        pass.setFocusable(enabled);
        passagain.setClickable(enabled);
        passagain.setFocusable(enabled);
        status.setText(R.string.password_limit);
        status.setTextColor((int)status.getTag());

        enabled=false;
        textColor=Color.LTGRAY;
        String shutdowndStr=DALSettings.getInstance().getShutDownTime();
        shutdown_time_picker.setTag(shutdowndStr);
        if(shutdowndStr!=null&&!shutdowndStr.isEmpty()){
            enabled=true;
            textColor=getResources().getColor(R.color.config_text);
        }else
            shutdowndStr="00:00:00";

        shutdown_switch_btn.setChecked(enabled);
        shutdown_switch_btn.setTag(enabled);
        shutdown_time_picker.setTime(shutdowndStr,false);
        shutdown_time_picker.setEnabled(enabled);

        enabled=false;
        textColor=Color.LTGRAY;
        String wakeupdStr=DALSettings.getInstance().getWakeUpTime();
        wakeup_time_picker.setTag(wakeupdStr);
        if(wakeupdStr!=null&&!wakeupdStr.isEmpty()){
            enabled=true;
            textColor=getResources().getColor(R.color.config_text);
        }else
            wakeupdStr="00:00:00";

        wakeup_switch_btn.setChecked(enabled);
        wakeup_switch_btn.setTag(enabled);
        wakeup_time_picker.setTime(wakeupdStr,false);
        wakeup_time_picker.setEnabled(enabled);

        pass.addTextChangedListener(passwordWatcher);//check password

        passagain.addTextChangedListener(passwordAgainWatcher);//confirm password validation

        pass_switch_btn.setOnChangeListener(checkedPasswordListener);//password checkbox

        shutdown_switch_btn.setOnChangeListener(checkedShutdownListener);//shutdown time check box

        wakeup_switch_btn.setOnChangeListener(checkedWakeupListener);//wake up time checkbox

        m_warning_view.setText("");
    }

    private void settimeDialog(final TextView timeText,String title) {

        Calendar calendar = Calendar.getInstance();
        final Boolean[] isApplySetting = {false};
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(isApplySetting.length>0&&isApplySetting[0]){
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                    String time = DateConverter.convertTimeToStrNoSecond(date.getTime());
                    timeText.setText(time);
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setTitle(title);
        timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE,"Apply",timePickerDialog);
        timePickerDialog.show();
        Button applyBtn= timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isApplySetting[0] =true;
                timePickerDialog.dismiss();
            }
        });
    }

    public boolean isValidPassword(final String password) {

        if(password==null||password.isEmpty())
            return false;

        if(password.length()>=6&&password.length()<=12)
            return true;

        return false;

        /*Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,12})";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();*/

    }//valid password

    @Override
    public void saveModification(OperateCallbackBundle callbackBundle) {

        try {
            int orientation=Portrait.isChecked()?ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

            String password="";
            if (pass_switch_btn.isChecked()) {
                String inputPassword=pass.getText().toString().trim();
                Boolean checkResult=isValidPassword(inputPassword);
                if(!checkResult){
                    m_warning_view.setText(R.string.password_limit);
                    if(callbackBundle!=null)
                        callbackBundle.onCancel("");
                    return;
                }
                if (!inputPassword.equals( passagain.getText().toString().trim())) {
                    m_warning_view.setText(R.string.password_notmatch);
                    if(callbackBundle!=null)
                        callbackBundle.onCancel("");
                    return;
                }
                password=inputPassword;
            }

            String shutdownValue=shutdown_switch_btn.isChecked()?shutdown_time_picker.getTime():"";
            String wakeupValue=wakeup_switch_btn.isChecked()?wakeup_time_picker.getTime():"";

            DALSettings.getInstance().saveConfigParameters(orientation,password,shutdownValue,wakeupValue);
            pass_switch_btn.setTag(pass_switch_btn.isChecked());
            pass.setTag(password);
            shutdown_switch_btn.setTag(shutdown_switch_btn.isChecked());
            shutdown_time_picker.setTag(shutdownValue);
            wakeup_switch_btn.setTag(wakeup_switch_btn.isChecked());
            wakeup_time_picker.setTag(wakeupValue);

            m_warning_view.setText("Save Successfully");

            if(callbackBundle!=null)
                callbackBundle.onConfim("");
            return;
        }catch (Exception ex){
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("ConfigurationFragment==>"+ex.getMessage());
            m_warning_view.setText("Save Failed");
        }
        if(callbackBundle!=null)
            callbackBundle.onCancel("");
        return;
    }//save setting method

    @Override
    public void cancelSaveModification() {
        //bindingConfiguration();
    }

    @Override
    public void showFragment() {
        if(isCreated){
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bindingConfiguration();
        }
    };

    @Override
    public void hideFragment() {

    }

    @Override
    public Boolean getIsEditing(){
        if(pass_switch_btn.isChecked()!=(Boolean)pass_switch_btn.getTag()||(pass_switch_btn.isChecked()&&!pass.getText().toString().equals(pass.getTag())))
            return true;
        if(shutdown_switch_btn.isChecked()!=(Boolean)shutdown_switch_btn.getTag()||(shutdown_switch_btn.isChecked()&&!shutdown_time_picker.getTime().equals(shutdown_time_picker.getTag())))
            return true;
        if(wakeup_switch_btn.isChecked()!=(Boolean)wakeup_switch_btn.getTag()||(wakeup_switch_btn.isChecked()&&!wakeup_time_picker.getTime().equals(wakeup_time_picker.getTag())))
            return true;

           return false;
    }

}//clase

