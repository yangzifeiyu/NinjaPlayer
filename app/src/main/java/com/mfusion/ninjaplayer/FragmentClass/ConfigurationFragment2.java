package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);//landscape 

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
                    status.setText("Password Accepted");

                }

                if (!isValidPassword(pass.getText().toString())) {
                     pass.setError("Password must be min6,max12,0-9,a-z or A-Z");//display when password is not valid

                    status.setText("");
                    //status.setText("Password must be min6,max12,0-9,a-z or A-Z");

                }

            }

        }); // check password

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
                    status.setText("");

                }
            }
        });//confirm password


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.landscape) {
                     getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//set if user chose landscape orientation
                    Oritantion();
                    //Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.portrait) {

                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set if user chose portrait orientation
                    Oritantion();
                    //Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_SHORT).show();
                }
            }
        });//radio button for screen orientation

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


        RetrieveSettings();//Retrieve settings from database if there is previous settings

        return rootView;
    }//oncreate

    private void RetrieveSettings() {
//        DALSettings.getInstance().getSystemSetting();
        try {

            String shut = DALSettings.getInstance().getShutDownTime();//retrive shutdown time
            String wake = DALSettings.getInstance().getWakeUpTime();//retrive wakeup time

            tvtime.setText(shut);
            tvwtime.setText(wake);

            Toast.makeText(getActivity(), "retrieve..I think ... NOT", Toast.LENGTH_SHORT).show();//display when user save successfully

        } catch (Exception e) {
            Toast.makeText(getActivity(), "ERRORR2222222222222222222", Toast.LENGTH_SHORT).show();//display when retrive unsuccessfully
        }

    }//save button listener

    private void SaveSettings() {


        String shutdowntime = tvtime.getText().toString().trim();
        String wakeuptime = tvwtime.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String match = passagain.getText().toString().trim();


        //String dis = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();

        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";
            String defaultp = "mfusion";

            if (password.isEmpty() && password.equals(match) && s != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
               // controller.insert_setting2(s, pa, shut, wake);
                DALSettings.getInstance().setShutDownTime(shutdowntime); //call setShutDownTime method
                DALSettings.getInstance().setWakeUpTime(wakeuptime); //call setWakeUpTime method
                DALSettings.getInstance().setExitPassword(defaultp); //call setExitPassword method 

                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();//display when user save successfully


                configurationLogDefault();


            } else if (password != null) {

                if (isValidPassword(password) && password.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    DALSettings.getInstance().setShutDownTime(shutdowntime); //call setShutDownTime method
                    DALSettings.getInstance().setWakeUpTime(wakeuptime); //call setWakeUpTime method
                    DALSettings.getInstance().setExitPassword(defaultp); //call setExitPassword method 
                    Toast.makeText(getActivity(), "Password match and Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();//display when passords match and save succesfully

                    configurationLog();


                } else if (!isValidPassword(password)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();//display when passord is not valid
                    PasswordLog();
                } else if (isValidPassword(password) && password != match) {
                    Toast.makeText(getActivity(), "Password Not match", Toast.LENGTH_SHORT).show();//display when passords not match
                    PasswordLog();//call passwordLog method
                }
            }


        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {
            String a = "Portrait";
            String defaultp = "mfusion";

            if (password.isEmpty() && password.equals(match) && a != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                DALSettings.getInstance().setShutDownTime(shutdowntime); //call setShutDownTime method
                DALSettings.getInstance().setWakeUpTime(wakeuptime); //call setWakeUpTime method
                DALSettings.getInstance().setExitPassword(defaultp); //call setExitPassword method 
                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();
                configurationLogDefault();


            } else if (password != null) {

                if (isValidPassword(password) && password.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    DALSettings.getInstance().setShutDownTime(shutdowntime);
                    DALSettings.getInstance().setWakeUpTime(wakeuptime);
                    DALSettings.getInstance().setExitPassword(password);
                    Toast.makeText(getActivity(), "Password match and Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();
                    configurationLog();


                } else if (!isValidPassword(password)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();
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
    }//eng of passwordlog

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

                else if(radioGroup.getCheckedRadioButtonId() == R.id.portrait)
                {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Oritantion change to Portrait at : " + currentDateandTime +"\n");
                    myOutWriter.close();
                    fOut.close();

                }

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }//end of orientation setting ( to diplay message inside log information)



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

