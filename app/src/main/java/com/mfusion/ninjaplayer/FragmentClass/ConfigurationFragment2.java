package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);// portrait orientation
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);//landscape orientation

        Save = (Button) rootView.findViewById(R.id.btnContinue);//save setting
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);//shutdown time
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);//wake up time

        status = (TextView) rootView.findViewById(R.id.txtStatusPa);//check password message
        tvtime = (TextView) rootView.findViewById(R.id.tvTime);
        tvwtime = (TextView) rootView.findViewById(R.id.tvW);

        pass = (EditText) rootView.findViewById(R.id.etPassword);//check password

        passagain = (EditText) rootView.findViewById(R.id.etMatch);//confirm password


        //viewPager = (ViewPager) getActivity().findViewById(R.id.pager);


        Landscape.setChecked(true);//set default orientation as landscape  
        Portrait.setChecked(false);


        pass.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isValidPassword(pass.getText().toString())) {
                    //pass.setError("Password Accepted");
                    status.setText("Password Accepted");//display when password is valid

                }

                if (!isValidPassword(pass.getText().toString())) {
                    pass.setError("Password must be min6,max12,0-9,a-z or A-Z");//display when password is not valid
                    //status.setText("Password must be min6,max12,0-9,a-z or A-Z");

                }

            }

        });// check password

        passagain.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                String strPass1 = pass.getText().toString();
                String strPass2 = passagain.getText().toString();
                if (strPass1.equals(strPass2)) {
                    status.setText("Password match");//display when two passwords match
                } else {
                    passagain.setError("Password do not match");//display when two password do not match

                }
            }
        });//confirm password


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.landscape) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//set if user chose landscape orientation
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    Oritantion();
                    //Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.portrait) {

                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set if user chose portrait orientation
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    Oritantion();
                    //Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //set shudown setting

                setshut();

            }
        });

        wake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              //set wake up setting
                setwake();

            }
        });


        Save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //save configuration setting
                SaveSettings();
            }
        });

//        Check.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                check();
//            }
//        });


        return rootView;
    }//oncreate


    private void SaveSettings() {
        //set exit password
        try {
            String shutdowntime = tvtime.getText().toString().trim();
            String wakeuptime = tvwtime.getText().toString().trim();
            String password = pass.getText().toString().trim();
            //DALSettings settings;
            DALSettings.getInstance().setShutDownTime(shutdowntime);//call setShutDownTime method
            DALSettings.getInstance().setWakeUpTime(wakeuptime);//call setWakeUpTime method
            DALSettings.getInstance().setExitPassword(password);//call setExitPassword method

            Toast.makeText(getActivity(), "saved..", Toast.LENGTH_SHORT).show();//display when user save successfully
        } catch (Exception e) {
            Toast.makeText(getActivity(), "ERRORRRRRRRRRRRRRRRR", Toast.LENGTH_SHORT).show();//display when user save unsuccessfully
        }

    }//save button listener

    private void setshut() {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);// set time picker as current time
        timePickerDialog.setTitle("Set Shutdown Time");//set title(shutdown)
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    // set time into textview
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, selectedHour);//set hour
                    date.set(Calendar.MINUTE, selectedMinute);//set min
                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                    String time = new SimpleDateFormat("HH:mm:ss").format(date.getTime());//set format
                    tvtime.setText(time);
                }
            };

    private void setwake() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener2, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);// set time picker as current time
        timePickerDialog.setTitle("Set Wakeup Time");//set title (wake up )
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timePickerListener2 =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                                          // set time into textview
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, selectedHour);//set hour
                    date.set(Calendar.MINUTE, selectedMinute);//set min
                    date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
                    String time = new SimpleDateFormat("HH:mm:ss").format(date.getTime());//set format
                    tvwtime.setText(time);
                }
            };

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,12})";//string password pattern

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }//valid password

    private void PasswordLog() {
        String pa = pass.getText().toString().trim();
        String pass = passagain.getText().toString().trim();
        try {
            File myFile = new File("/sdcard/MFusion/log.txt");//file path

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set date time format
                String currentDateandTime = sdf.format(new Date());
                //display when password does not match ( display in the log information)
                myOutWriter.append("Attempted to change password but failed due to wrong requirement or Password does not match/wrong match format at : " + currentDateandTime + "\n" + pa + ":" + pass + "\n");

                myOutWriter.close();
                fOut.close();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Execerror();
        }
    }//end of passwordlog

    private void Execerror() {

        try {
            File myFile = new File("/sdcard/MFusion/log.txt");//file path

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set date time format
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Exception Error in ConfigurationFragment at : " + currentDateandTime + "\n");//display in log information
                myOutWriter.close();
                fOut.close();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void Oritantion() {

        try {
            File myFile = new File("/sdcard/MFusion/log.txt");//file path

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {

                if(radioGroup.getCheckedRadioButtonId() == R.id.landscape)
                {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set date time format
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Oritantion change to Landscape at : " + currentDateandTime +"\n");//display in log information
                    myOutWriter.close();
                    fOut.close();

                }

                else if(radioGroup.getCheckedRadioButtonId() == R.id.portrait)
                {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set date time format
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Oritantion change to Portrait at : " + currentDateandTime +"\n");//display in log information
                    myOutWriter.close();
                    fOut.close();

                }

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }//end of orientation setting ( to diplay message inside log information)

}//clase

