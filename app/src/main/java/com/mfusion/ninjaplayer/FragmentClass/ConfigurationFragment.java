package com.mfusion.ninjaplayer.FragmentClass;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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


import com.mfusion.ninjaplayer.New.DBController;
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
    TextView status, tvtime, tvwtime;
    private RadioGroup radioGroup;
    private RadioButton Landscape, Portrait;

    TimePicker myTimePicker;
    TimePickerDialog timePickerDialog;


    DBController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        controller = new DBController(getActivity(), "", null, 1);

        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);//radio group for screen orientation
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);//portrait button
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);//landscape button

        Save = (Button) rootView.findViewById(R.id.btnSave);//save setting button
        Check = (Button) rootView.findViewById(R.id.btnCheck);//check password button
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);//shutdown image button
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);//wake uo time image button

        status = (TextView) rootView.findViewById(R.id.txtStatusPa);//password validation
        tvtime = (TextView) rootView.findViewById(R.id.tvTime);//shutdown time
        tvwtime = (TextView) rootView.findViewById(R.id.tvW);//wake up time

        pass = (EditText) rootView.findViewById(R.id.etPassword);//enter password
        passagain = (EditText) rootView.findViewById(R.id.etMatch);//confirm password

        Landscape.setChecked(true);
        Portrait.setChecked(false);


        Save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }//call save method
        });


        shut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                openTimePickerDialog(true);


            }
        });//shutdown onclick method

        wake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                openTimePickerDialog2(true);


            }
        });//wakeup onclick method


        Check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                check();//call check password method
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.landscape) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//check if user choose landscape orientation
                    //Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.portrait) {

                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//check if user choose portrait orientation
                    //Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        defaultconfig();//method for getting configuration setting from database
        retrieveinfo();//method for retrieving value from database and set the result at the textview or radiobutton

        return rootView;


    }//oncreate

    private void save() {
        String pa = pass.getText().toString().trim();
        String shut = tvtime.getText().toString().trim();
        String wake = tvwtime.getText().toString().trim();
        String match = passagain.getText().toString().trim();

        //String dis = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();

        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";

            if (pa.isEmpty() && pa.equals(match) && s != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                controller.insert_setting2(s, pa, shut, wake);
                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();//display if password is not null and match together

                configurationLogDefault();//save to log info of configuration

            } else if (pa != null) {

                if (isValidPassword(pa) && pa.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    controller.insert_setting2(s, pa, shut, wake);
                    Toast.makeText(getActivity(), "Password match and Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();//display if password is valid and match together
                    configurationLog();//save password message to display in log info

                } else if (!isValidPassword(pa)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();//display if password is not valid
                    PasswordLog();// password error message to display in log info
                } else if (isValidPassword(pa) && pa != match) {
                    Toast.makeText(getActivity(), "Password Not match", Toast.LENGTH_SHORT).show();//display if password is valid and match together
                    PasswordLog();// password error message to display in log info
                }
            }


        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {
            String a = "Portrait";

            if (pa.isEmpty() && pa.equals(match) && a != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                controller.insert_setting2(a, pa, shut, wake);
                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();//display if password matched and is not null
                configurationLogDefault();//save default password and display in log info


            } else if (pa != null) {

                if (!isValidPassword(pa)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();//display if password is not valid
                    PasswordLog();
                }
                else if (isValidPassword(pa) && pa.equals(match)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    controller.insert_setting2(a, pa, shut, wake);//call from database
                    Toast.makeText(getActivity(), "Password match and Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();//display if password is valid and matches
                    configurationLog();//save message and display in log info


                } else if (isValidPassword(pa) && pa != match) {
                    Toast.makeText(getActivity(), "Password Not match", Toast.LENGTH_SHORT).show();//display if password is valid and not match
                    PasswordLog();//save password error and display in log info
                }


            }

        }

    }//save setting

    private void retrieveinfo() {


        controller.list_setting6(tvtime, tvwtime, status, pass, passagain, radioGroup);//Called DBController method listsettings6 to invoke database


    }//end of retrieve info method

    private void defaultconfig() {
        controller.insert_setting3();
    }


    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,12})";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }//valid password

    private void check() {
        String pa = pass.getText().toString().trim();
        String match = passagain.getText().toString().trim();

        if (isValidPassword(pa) && pa.equals(match) && pa != null) {
            status.setText("Password match and Password Accepted, you have type: " + pass.getText());//display when password match and valid
        } else if (!isValidPassword(pa) && pa != null) {
            status.setText("Password must be min6,max12,0-9,a-z or A-Z");//display when password is not valid
        } else if (isValidPassword(pa) && pa != match && pa != null) {
            status.setText("Password does not match");//display when password does not match
        } else if (!isValidPassword(pa) && pa.equals(match)&& pa != null) {
            status.setText("Password match with wrong format");//display when password is not valid but match
        }




    }//check password

    private void openTimePickerDialog(boolean is24r) {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Shutdown Time");//set shutdown time picker's title

        timePickerDialog.show();//show shutdown time


    }//set shutdown time

    private void openTimePickerDialog2(boolean is24r) {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener2,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Wakeup Time");//set wake up time picker's title

        timePickerDialog.show();//show wake up time

    }//set wake up time

    OnTimeSetListener onTimeSetListener
            = new OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();
            tvtime.setText(String.format("%2d:%02d", hourOfDay, minute));
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                //Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            setAlarm(calSet);

        }
    };//ontimeset

    OnTimeSetListener onTimeSetListener2
            = new OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();
            tvwtime.setText(String.format("%2d:%02d", hourOfDay, minute));
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                //Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            setAlarm2(calSet);

        }
    };//ontimeset2

    private void setAlarm(Calendar targetCal) {

       /* tvtime.setText("Shutdown is set@ " + targetCal.getTime() + "\n");

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);*/
    }//alarmshut

    private void setAlarm2(Calendar targetCal) {

        /*tvtime.setText("Wakeup is set@ " + targetCal.getTime() + "\n");

        Intent intent = new Intent(getActivity(), AlarmReceiver2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);*/
    }//alarmwake




    private void configurationLog() {

        String pa = pass.getText().toString().trim();
        String shut = tvtime.getText().toString().trim();
        String wake = tvwtime.getText().toString().trim();


        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");//text path

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + s + "," + pa + "," + shut + "," + wake + "\n");//display configuration setting message in log info
                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {

            String a = "portrait";
            try {
                File myFile = new File("/sdcard/MFusion/log.txt");//file path

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + a + "," + pa + "," + shut + "," + wake + "\n");//display configuration setting message in log info

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
                File myFile = new File("/sdcard/MFusion/log.txt");//file path

                if (!myFile.exists()) {
                    myFile.createNewFile();
                    //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
                } else if (myFile.exists()) {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + s + "," + "mfusion" + "," + shut + "," + wake + "\n");//display configuration setting message in log info
                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();//display error log
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
                    myOutWriter.append("Configuration Settings saved to log.txt at : " + currentDateandTime + "\n" + a + "," + "mfusion" + "," + shut + "," + wake + "\n");//display configuration setting message in log info

                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();//display error log
            }

        }

    }//end of default log

    private void PasswordLog() {
        String pa = pass.getText().toString().trim();
        String match = passagain.getText().toString().trim();
        if (!isValidPassword(pa) && pa!=match) {
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
                    myOutWriter.append("Attempted to change password but failed due to wrong requirement or Password does not match at : " + currentDateandTime + "\n" + pa + "\n");

                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();//display error log
            }

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
                myOutWriter.append("Exception Error in ConfigurationFragment at : " + currentDateandTime + "\n");//save error message and display in log info
                myOutWriter.close();
                fOut.close();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }


}//clase

