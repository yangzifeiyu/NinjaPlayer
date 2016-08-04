package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.mfusion.ninjaplayer.New.MainActivity;
import com.mfusion.ninjaplayer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfigurationFragment extends Fragment {
    ViewPager viewPager;
    Button Save, Check;
    ImageButton shut, wake;
    EditText pass, passagain;
    TextView status;
    EditText tvtime, tvwtime;
    RadioGroup radioGroup;
    RadioButton Landscape, Portrait;
    TimePickerDialog timePickerDialog;
    CheckBox ckpass, ckwake, ckshut;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);

        Save = (Button) rootView.findViewById(R.id.btnContinue);
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);

        status = (TextView) rootView.findViewById(R.id.txtStatusPa);
        tvtime = (EditText) rootView.findViewById(R.id.edTvtime);
        tvwtime = (EditText) rootView.findViewById(R.id.edTvwtime);

        pass = (EditText) rootView.findViewById(R.id.etPassword);

        passagain = (EditText) rootView.findViewById(R.id.etMatch);

        ckpass = (CheckBox) rootView.findViewById(R.id.chPassword2);
        ckwake = (CheckBox) rootView.findViewById(R.id.chwake3);
        ckshut = (CheckBox) rootView.findViewById(R.id.chshut3);

        viewPager = (ViewPager) getActivity().findViewById(R.id.photosViewPager);

        initmethodforeverything();

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
                String strPass1 = pass.getText().toString();


                if (isValidPassword(strPass1)) {
                    status.setText("Password Accepted");
                } else if (!isValidPassword(strPass1)) {
                    //pass.setError("Password must be min6,max12,0-9,a-z or A-Z");
//                    status.setText("");
                    status.setText("Password must be min6,max12,0-9,a-z or A-Z");

                }

            }

        });

        passagain.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String strPass1 = pass.getText().toString();
                String strPass2 = passagain.getText().toString();
                if (isValidPassword(strPass1) && strPass1.equals(strPass2)) {
                    status.setText("Password match");
                } else {
                    //passagain.setError("Password do not match");
                    status.setText("Password do not match");
                    //status.setText("");

                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.landscape) {
                    // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    //Oritantion();
                    //Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.portrait) {

                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    // Oritantion();
                    //Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            String password = pass.getText().toString().trim();
            String match = passagain.getText().toString().trim();


            @Override
            public void onClick(View v) {

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
        ckpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //do stuff
                if (isChecked) {
                    Toast.makeText(getActivity(), "User set password field unlocked", Toast.LENGTH_SHORT).show();

                    pass.setFocusableInTouchMode(true);
                    pass.setClickable(true);

                    passagain.setFocusableInTouchMode(true);
                    passagain.setClickable(true);
                } else {
                    Toast.makeText(getActivity(), "Default password will be used, User set password field locked", Toast.LENGTH_SHORT).show();
                    pass.setFocusable(false);
                    pass.setClickable(false);

                    passagain.setFocusable(false);
                    passagain.setClickable(false);

                    pass.setText("");
                    passagain.setText("");
                    status.setText("");
                }

            }
        });

        ckshut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //do stuff
                if (isChecked) {
                    Toast.makeText(getActivity(), "Shutdown Button and field unlocked", Toast.LENGTH_SHORT).show();

                    shut.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            //set shudown settings

                            setshut();

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Shutdown Button and field locked", Toast.LENGTH_SHORT).show();
                    shut.setFocusable(false);
                    shut.setClickable(false);

                    tvtime.setFocusable(false);
                    tvtime.setClickable(false);

                    tvtime.setText("");
                }

            }
        });

        ckwake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //do stuff
                if (isChecked) {
                    Toast.makeText(getActivity(), "Wakeup Button and field unlocked", Toast.LENGTH_SHORT).show();

                    wake.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {

                            setwake();

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Wakeup Button and field locked", Toast.LENGTH_SHORT).show();
                    wake.setFocusable(false);
                    wake.setClickable(false);

                    tvwtime.setFocusable(false);
                    tvwtime.setClickable(false);

                    tvwtime.setText("");
                }

            }
        });

        //RetrieveSettings();//Retrieve settings from database if there is previous settings


        return rootView;
    }//oncreate

    private void initmethodforeverything() {

        Landscape.setChecked(true);
        Portrait.setChecked(false);

        pass.setFocusable(false);
        pass.setClickable(false);

        passagain.setFocusable(false);
        passagain.setClickable(false);

        shut.setFocusable(false);
        shut.setClickable(false);

        wake.setFocusable(false);
        wake.setClickable(false);

        tvtime.setFocusable(false);
        tvtime.setClickable(false);

        tvwtime.setFocusable(false);
        tvwtime.setClickable(false);
//

    }

//    private void RetrieveSettings() {
////        DALSettings.getInstance().getSystemSetting();
//        String shut = DALSettings.getInstance().getShutDownTime();
//        String wake = DALSettings.getInstance().getWakeUpTime();
//
//
//        if(shut.equals("") || wake.equals(""))
//        {
//            tvtime.setText("");
//            tvwtime.setText("");
//        }
//
//        else if(shut != null || wake != null)
//        {
//            try {
//
//
//                tvtime.setText(shut);
//                tvwtime.setText(wake);
//
//                Toast.makeText(getActivity(), "retrieve..I think ... NOT", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Toast.makeText(getActivity(), "ERRORR2222222222222222222", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//
//
//    }//retrieve button listener

    private void SaveSettings() {


        String shutdowntime = tvtime.getText().toString().trim();
        String wakeuptime = tvwtime.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String match = passagain.getText().toString().trim();


        //String dis = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();

        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";
            String defaultp = "mfusion";


            if (!ckpass.isChecked() && password.isEmpty() && password.equals(match) && s != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                // controller.insert_setting2(s, pa, shut, wake);
                DALSettings.getInstance().setShutDownTime(shutdowntime);
                DALSettings.getInstance().setWakeUpTime(wakeuptime);
                DALSettings.getInstance().setExitPassword(defaultp);

                Toast.makeText(getActivity(), "Saved with: " + s + "," + defaultp + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();

                configurationLogDefault();


            }


            else if (ckpass.isChecked() && password != null) {

                if (isValidPassword(password) && password.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    DALSettings.getInstance().setShutDownTime(shutdowntime);
                    DALSettings.getInstance().setWakeUpTime(wakeuptime);
                    DALSettings.getInstance().setExitPassword(password);
                    Toast.makeText(getActivity(), "Saved with: " + s + "," + password + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();

                    configurationLog();


                } else if (!isValidPassword(password)) {
                    Toast.makeText(getActivity(), "Set password field checked , Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                } else if (isValidPassword(password) && password != match) {
                    Toast.makeText(getActivity(), "Password Not match", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                }

            }


        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {
            String a = "Portrait";
            String defaultp = "mfusion";

            if (password.isEmpty() && password.equals(match) && a != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                DALSettings.getInstance().setShutDownTime(shutdowntime);
                DALSettings.getInstance().setWakeUpTime(wakeuptime);
                DALSettings.getInstance().setExitPassword(defaultp);
                Toast.makeText(getActivity(), "Saved with: " + a + "," + defaultp + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();
                configurationLogDefault();


            } else if (ckpass.isChecked() && password != null) {

                if (isValidPassword(password) && password.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    DALSettings.getInstance().setShutDownTime(shutdowntime);
                    DALSettings.getInstance().setWakeUpTime(wakeuptime);
                    DALSettings.getInstance().setExitPassword(password);
                    Toast.makeText(getActivity(), "Saved with: " + a + "," + password + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();
                    configurationLog();


                } else if (!isValidPassword(password)) {
                    Toast.makeText(getActivity(), "Set password field checked , Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                } else if (isValidPassword(password) && password != match) {
                    Toast.makeText(getActivity(), "Password Not match", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                }

            }

        }

//        try {
//
//            //DALSettings settings;
//
//            DALSettings.getInstance().setShutDownTime(shutdowntime);
//            DALSettings.getInstance().setWakeUpTime(wakeuptime);
//            DALSettings.getInstance().setExitPassword(password);
//
//            Toast.makeText(getActivity(), "saved..I think ... NOT", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "ERRORRRRRRRRRRRRRRRR", Toast.LENGTH_SHORT).show();
//        }

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

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,12})";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }//valid password

    private void PasswordLog() {
        String pa = pass.getText().toString().trim();
        String pass = passagain.getText().toString().trim();
        try {
            File myFile = new File("/sdcard/MFusion/log.txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Attempted to change password but failed due to wrong requirement or Password does not match/wrong match format at : " + currentDateandTime + "\n" + pa + ":" + pass + "\n");

                myOutWriter.close();
                fOut.close();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Execerror();
        }
    }//eng of passwordlog

    private void Execerror() {

        try {
            File myFile = new File("/sdcard/MFusion/log.txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Exception Error in ConfigurationFragment at : " + currentDateandTime + "\n");
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
            File myFile = new File("/sdcard/MFusion/log.txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Oritantion change to Landscape at : " + currentDateandTime + "\n");
                    myOutWriter.close();
                    fOut.close();

                } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Oritantion change to Portrait at : " + currentDateandTime + "\n");
                    myOutWriter.close();
                    fOut.close();

                }

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }


    private void configurationLog() {

        String pa = pass.getText().toString().trim();
        String shut = tvtime.getText().toString().trim();
        String wake = tvwtime.getText().toString().trim();


        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + s + "," + pa + "," + shut + "," + wake + "\n");
                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {

            String a = "Landscape";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + a + "," + pa + "," + shut + "," + wake + "\n");

                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }

        }

    }//end of log

    private void configurationLogDefault() {

        String pa = pass.getText().toString().trim();
        String shut = tvtime.getText().toString().trim();
        String wake = tvwtime.getText().toString().trim();


        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + s + "," + "mfusion" + "," + shut + "," + wake + "\n");
                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {

            String a = "Landscape";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + a + "," + "mfusion" + "," + shut + "," + wake + "\n");

                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }

        }

    }//end of default log


}//clase

