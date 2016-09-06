package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.data.DALSettings;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfigurationFragment extends AbstractFragment {

    ImageTextView Save, Check;
    ImageButton shut, wake;
    EditText pass, passagain;
    TextView status;
    EditText tvtime, tvwtime;
    RadioGroup radioGroup;
    RadioButton Landscape, Portrait;
    TimePickerDialog timePickerDialog;
    CheckBox ckpass, ckwake, ckshut;

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
        Drawable drawable_portrait = getResources().getDrawable(R.drawable.logo_portrait);
        drawable_portrait.setBounds(0, 0, radioGroup.getLayoutParams().height, radioGroup.getLayoutParams().height);
        Portrait.setCompoundDrawables(drawable_portrait,null,null,null);
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);//landscape orientation
        Drawable drawable_landscape = getResources().getDrawable(R.drawable.logo_landscape);
        drawable_landscape.setBounds(0, 0, radioGroup.getLayoutParams().height, radioGroup.getLayoutParams().height);
        Landscape.setCompoundDrawables(drawable_landscape,null,null,null);

        ckshut = (CheckBox) rootView.findViewById(R.id.chshut3);//shutdown time checkbox
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);//wake up image button
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settimeDialog(tvtime,"Set Shutdown Time");
            }
        });
        tvtime = (EditText) rootView.findViewById(R.id.edTvtime);//text view for shutdown time

        ckwake = (CheckBox) rootView.findViewById(R.id.chwake3);//wake up time checkboc
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);//wake up time image button
        wake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settimeDialog(tvwtime,"Set Wakeup Time");
            }
        });
        tvwtime = (EditText) rootView.findViewById(R.id.edTvwtime);//text view for shutdown time

        ckpass = (CheckBox) rootView.findViewById(R.id.chPassword2);//password checkbox
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

        Save = (ImageTextView) rootView.findViewById(R.id.btnContinue);//save setting button
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

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if(ckpass.isChecked())
                isEditing=true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!ckpass.isChecked())
                return;

            String strPass1 = pass.getText().toString().trim();
            if (isValidPassword(strPass1)) {
                status.setText(R.string.password_match);//display when password is valid
            } else {
                status.setText(R.string.password_limit);//display when password is not valid
            }
        }
    };

    TextWatcher passwordAgainWatcher= new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if(ckpass.isChecked())
                isEditing=true;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(!ckpass.isChecked())
                return;
            String strPass1 = pass.getText().toString();
            String strPass2 = passagain.getText().toString();
            if (isValidPassword(strPass1) && strPass1.equals(strPass2)) {
                status.setText(R.string.password_match);//display when passwords match
            } else {
                status.setText(R.string.password_notmatch);//display when passwords do not match
            }
        }
    };

    CompoundButton.OnCheckedChangeListener checkedPasswordListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //do stuff
            isEditing=true;
            int textColor=Color.LTGRAY;
            if (isChecked) {
                textColor=getResources().getColor(R.color.config_text);
            } else {
                status.setText("");
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

    CompoundButton.OnCheckedChangeListener checkedShutdownListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //do stuff
            isEditing=true;
            if (isChecked) {
                tvtime.setTextColor(getResources().getColor(R.color.config_text));
            } else {
                tvtime.setText("00:00:00");
                tvtime.setTextColor(Color.LTGRAY);
            }

            shut.setFocusable(isChecked);
            shut.setClickable(isChecked);
        }
    };

    CompoundButton.OnCheckedChangeListener checkedWakeupListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //do stuff
            isEditing=true;
            if (isChecked) {
                tvwtime.setTextColor(getResources().getColor(R.color.config_text));
            } else {
                tvwtime.setText("00:00:00");
                tvwtime.setTextColor(Color.LTGRAY);
            }
            wake.setFocusable(isChecked);
            wake.setClickable(isChecked);
        }
    };

    private void bindingConfiguration(){

        pass.removeTextChangedListener(passwordWatcher);//check password

        passagain.removeTextChangedListener(passwordAgainWatcher);//confirm password validation

        ckpass.setOnCheckedChangeListener(null);//password checkbox

        ckshut.setOnCheckedChangeListener(null);//shutdown time check box

        ckwake.setOnCheckedChangeListener(null);//wake up time checkbox

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
        ckpass.setChecked(enabled);
        pass.setText(passwordStr);
        passagain.setText(passwordStr);

        pass.setTextColor(textColor);
        pass.setHintTextColor(textColor);
        passagain.setTextColor(textColor);
        passagain.setHintTextColor(textColor);

        pass.setClickable(enabled);
        passagain.setClickable(enabled);
        status.setText(R.string.password_limit);

        enabled=false;
        textColor=Color.LTGRAY;
        String shutdowndStr=DALSettings.getInstance().getShutDownTime();
        if(shutdowndStr!=null&&!shutdowndStr.isEmpty()){
            enabled=true;
            textColor=getResources().getColor(R.color.config_text);
        }else
            shutdowndStr="00:00:00";

        ckshut.setChecked(enabled);
        tvtime.setText(shutdowndStr);
        tvtime.setTextColor(textColor);

        shut.setFocusable(enabled);
        shut.setClickable(enabled);

        tvtime.setFocusable(false);
        tvtime.setClickable(false);

        enabled=false;
        textColor=Color.LTGRAY;
        String wakeupdStr=DALSettings.getInstance().getWakeUpTime();
        if(wakeupdStr!=null&&!wakeupdStr.isEmpty()){
            enabled=true;
            textColor=getResources().getColor(R.color.config_text);
        }else
            wakeupdStr="00:00:00";

        ckwake.setChecked(enabled);
        tvwtime.setText(wakeupdStr);
        tvwtime.setTextColor(textColor);

        wake.setFocusable(enabled);
        wake.setClickable(enabled);

        tvwtime.setFocusable(false);
        tvwtime.setClickable(false);

        pass.addTextChangedListener(passwordWatcher);//check password

        passagain.addTextChangedListener(passwordAgainWatcher);//confirm password validation

        ckpass.setOnCheckedChangeListener(checkedPasswordListener);//password checkbox

        ckshut.setOnCheckedChangeListener(checkedShutdownListener);//shutdown time check box

        ckwake.setOnCheckedChangeListener(checkedWakeupListener);//wake up time checkbox

        m_warning_view.setText("");
        isEditing=false;
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
            int orientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            if(Portrait.isChecked())
                orientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

            String password="";
            if (ckpass.isChecked()) {
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

            DALSettings.getInstance().setOrientation(orientation);
            DALSettings.getInstance().setExitPassword(password);

            String shutdownValue="";
            if(ckshut.isChecked()){
                shutdownValue=tvtime.getText().toString();
            }
            DALSettings.getInstance().setShutDownTime(shutdownValue);

            String wakeupValue="";
            if(ckwake.isChecked()){
                wakeupValue=tvwtime.getText().toString();
            }
            DALSettings.getInstance().setWakeUpTime(wakeupValue);

            m_warning_view.setText("Save Successfully");
            isEditing=false;
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
        bindingConfiguration();
    }

    @Override
    public void showFragment() {
        if(isCreated){
            bindingConfiguration();
        }
    }

    @Override
    public void hideFragment() {

    }
}//clase

