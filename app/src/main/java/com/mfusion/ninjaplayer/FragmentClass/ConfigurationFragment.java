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
    Button Continue, Check;
    ImageButton shut, wake;
    EditText pass;
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

        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        Portrait = (RadioButton) rootView.findViewById(R.id.portrait);
        Landscape = (RadioButton) rootView.findViewById(R.id.landscape);

        Continue = (Button) rootView.findViewById(R.id.btnContinue);
        Check = (Button) rootView.findViewById(R.id.btnCheck);
        shut = (ImageButton) rootView.findViewById(R.id.btnImgShut);
        wake = (ImageButton) rootView.findViewById(R.id.btnImgWake);

        status = (TextView) rootView.findViewById(R.id.txtStatusPa);
        tvtime = (TextView) rootView.findViewById(R.id.tvTime);
        tvwtime = (TextView) rootView.findViewById(R.id.tvW);

        //click = (TextView) rootView.findViewById(R.id.tvClick);

        pass = (EditText) rootView.findViewById(R.id.etPassword);

        //viewPager = (ViewPager) getActivity().findViewById(R.id.pager);


        Landscape.setChecked(true);
        Portrait.setChecked(false);


        Continue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                continues();
            }
        });



        shut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                openTimePickerDialog(true);


            }
        });

        wake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                openTimePickerDialog2(true);


            }
        });


        Check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                check();
            }
        });


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

        defaultconfig();
        retrieveinfo();//method for retreving value from database and set the result at the textview or radiobutton

        return rootView;


    }//oncreate


    private void retrieveinfo() {


        controller.list_setting6(tvtime, tvwtime, status, pass, radioGroup);//Called DBController method listsettings6 to invoke database


    }//end of retrieveinfo method

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

        if (isValidPassword(pa) && pa != null) {
            status.setText("Password Accepted, you have type: " + pass.getText());
        } else {
            status.setText("Password not Accepted must be min6,max12,0-9,a-z or A-Z");
        }

    }//check password

    private void openTimePickerDialog(boolean is24r) {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Shutdown Time");

        timePickerDialog.show();


    }//set shutdown time

    private void openTimePickerDialog2(boolean is24r) {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener2,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Wakeup Time");

        timePickerDialog.show();

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


    private void continues() {
        String pa = pass.getText().toString().trim();
        String shut = tvtime.getText().toString().trim();
        String wake = tvwtime.getText().toString().trim();

        //String dis = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();

        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {
            String s = "Landscape";

            if (pa.isEmpty() && s != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                controller.insert_setting2(s, pa, shut, wake);
                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();

                configurationLog();


            } else if (pa != null) {

                if (isValidPassword(pa)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    controller.insert_setting2(s, pa, shut, wake);
                    Toast.makeText(getActivity(), "Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();
                    configurationLog();


                }

                if (!isValidPassword(pa)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                }
            }


        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {
            String a = "Portrait";

            if (pa.isEmpty() && a != null) {
                //controller.insert_setting(dis, pa, shut, wake, auto);
                controller.insert_setting2(a, pa, shut, wake);
                Toast.makeText(getActivity(), "Saved with Default PASSWORD", Toast.LENGTH_SHORT).show();
                configurationLog();


            } else if (pa != null) {

                if (isValidPassword(pa)) {
                    //controller.insert_setting(dis, pa, shut, wake, auto);
                    controller.insert_setting2(a, pa, shut, wake);
                    Toast.makeText(getActivity(), "Saved with User set PASSWORD", Toast.LENGTH_SHORT).show();
                    configurationLog();


                }

                if (!isValidPassword(pa)) {
                    Toast.makeText(getActivity(), "Password must be min6,max12,0-9,a-z or A-Z =)", Toast.LENGTH_SHORT).show();
                    PasswordLog();
                }


            }

        }

    }//continue-save setting


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

        }//end of log

    }

    private void PasswordLog() {
        String pa = pass.getText().toString().trim();

        if (!isValidPassword(pa)) {
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
                    myOutWriter.append("Atempted to change password but failed due to wrong requirement at : " + currentDateandTime + "\n" + pa + "\n");

                    myOutWriter.close();
                    fOut.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Execerror();
            }

        }

    }//eng of passwordlog

    private void Execerror()
    {
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
                myOutWriter.append("Exception Error in ConfigurationFragment at : "+ currentDateandTime + "\n");
                myOutWriter.close();
                fOut.close();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }


}//clase

