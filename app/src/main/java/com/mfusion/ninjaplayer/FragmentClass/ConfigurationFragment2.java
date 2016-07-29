package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.mfusion.commons.data.DALSettings;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.SQLiteDBHelper;
import com.mfusion.ninjaplayer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfigurationFragment2 extends Fragment {
    ViewPager viewPager;
    Button Save, Check;
    ImageButton shut, wake;
    EditText pass, passagain;
    TextView status, tvtime, tvwtime;
    RadioGroup radioGroup;
    RadioButton Landscape, Portrait;
    TimePickerDialog timePickerDialog;


    DALSettings settings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);

        Save = (Button) rootView.findViewById(R.id.btnContinue);
        Check = (Button) rootView.findViewById(R.id.btnCheck);
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);

        status = (TextView) rootView.findViewById(R.id.txtStatusPa);
        tvtime = (TextView) rootView.findViewById(R.id.tvTime);
        tvwtime = (TextView) rootView.findViewById(R.id.tvW);

        pass = (EditText) rootView.findViewById(R.id.etPassword);

        passagain = (EditText) rootView.findViewById(R.id.etMatch);


        //viewPager = (ViewPager) getActivity().findViewById(R.id.pager);


        Landscape.setChecked(true);
        Portrait.setChecked(false);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.landscape) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.portrait) {

                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //set shudown settings

                setshut();

            }
        });

        wake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setwake();

            }
        });


        Save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SaveSettings();
            }
        });

        Check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                check();
            }
        });


        return rootView;
    }//oncreate


    private void SaveSettings() {
        //set exit password
        try
        {
            String shutdowntime = tvtime.getText().toString().trim();
            String wakeuptime = tvwtime.getText().toString().trim();
            String password = pass.getText().toString().trim();
            //DALSettings settings;
            DALSettings.getInstance().setShutDownTime(shutdowntime);
            DALSettings.getInstance().setWakeUpTime(wakeuptime);
            DALSettings.getInstance().setExitPassword(password);
        }

        catch (Exception e)
        {
               Toast.makeText(getActivity(), "ERRORRRRRRRRRRRRRRRR", Toast.LENGTH_SHORT).show();
        }

    }//save button listener

    private void setshut() {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setTitle("Set Shutdown Time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    // set time into textview
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, selectedHour);
                    date.set(Calendar.MINUTE, selectedMinute);
                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                    String time = new SimpleDateFormat("HH:mm:ss").format(date.getTime());
                    tvtime.setText(time);
                }
            };

    private void setwake() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener2, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setTitle("Set Wakeup Time");
        timePickerDialog.show();
    }
    TimePickerDialog.OnTimeSetListener timePickerListener2 =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, selectedHour);
                    date.set(Calendar.MINUTE, selectedMinute);
                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                    String time = new SimpleDateFormat("HH:mm:ss").format(date.getTime());
                    tvwtime.setText(time);
                }
            };

    private void check() {
        String pa = pass.getText().toString().trim();
        String match = passagain.getText().toString().trim();

        if (isValidPassword(pa) && pa.equals(match) && pa != null) {
            status.setText("Password match and Password Accepted, you have type: " + pass.getText());
        } else if (isValidPassword(pa) && pa != match && pa != null) {
            status.setText("Password does not match");
        } else if (!isValidPassword(pa) && pa != null) {
            status.setText("Password must be min6,max12,0-9,a-z or A-Z");
        } else if (isValidPassword(pa) && pa != match && pa != null) {
            status.setText("Password does not match");
        } else if (!isValidPassword(pa) && pa.equals(match) && pa != null) {
            status.setText("Password match with wrong format");
        }
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,12})";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }//valid password


}//clase

