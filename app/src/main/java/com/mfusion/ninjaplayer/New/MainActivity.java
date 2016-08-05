package com.mfusion.ninjaplayer.New;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.mfusion.commons.data.DALSettings;
import com.mfusion.ninjaplayer.adapter.CustomViewPager;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.adapter.TabsPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    CustomViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    CheckBox ckpass, ckwake, ckshut;
    EditText pass, passagain;
    TextView status;


    //DBController controller;


    // Tab titles
    private String[] tabs = {"Configuration", "Template", "Schedule", "Log", "About"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//to make sure that keyboard does not effect the GUI




        // Initilization
        pass = (EditText) findViewById(R.id.etPassword);//password
        passagain = (EditText) findViewById(R.id.etMatch);//confirm password
        status = (TextView) findViewById(R.id.txtStatusPa);//password validation textview
        ckpass = (CheckBox) findViewById(R.id.chPassword2);//password checkboc
        ckwake = (CheckBox) findViewById(R.id.chwake3);//wake up time checkbox
        ckshut = (CheckBox) findViewById(R.id.chshut3);//shutdown time checkboc
    


        viewPager = (CustomViewPager) findViewById(R.id.photosViewPager);//customer view pager
        viewPager.setPagingEnabled(false);//set paging not swipable


        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());//tabs adapter

        viewPager.setAdapter(mAdapter);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//set navigation mode tabs


        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
//                Toast.makeText(MainActivity.this,
//                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        AutoStart();//call auto start up method
        Advertisment();//call advertisement(logo endorsement) method
        ErrorLog();////call errorLog method
    }//oncreate


    private void AutoStart() {

//            Toast.makeText(MainActivity.this, "AutoStart Init", Toast.LENGTH_SHORT).show();


    }//Autostart init


    private void Advertisment() {

        new Handler().postDelayed(new Runnable() {
            public void run() {
                test();//call test method
            }
        }, 60 * 1000);//display after 1 min


    }//Advertisment init

    private void test() {

        if (!isFinishing()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("MediaFusion Ultimate Lite");//set pop up window tabs
            builder.setMessage("Download our Pro Edition now to try more!");//set pop up message
            builder.setIcon(R.drawable.logo);//set pop up icon
            builder.setCancelable(false);//to make sure user can not close the pop up window(by clicking anywhere) unless they click the button

            final ImageView image = new ImageView(this);//image view
            image.setImageResource(R.drawable.splash);//set image 

            builder.setView(image);

            builder.setPositiveButton("Close Popup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            test();//call test method
                        }
                    }, 300 * 1000);//display every 5 min

                }
            });//end of close pop up window method 


            builder.show();//show alert dialog


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), "Clicked", Toast.LENGTH_SHORT).show();//display when user click the image inside the pop up window

                    Intent intent = new Intent();//intent
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://play.google.com/store?hl=en"));//direct to playstore
                    startActivity(intent);

                }
            });//button click-direct to playstore


        }

    }//popup alertdialog

    private void ErrorLog() {


        try {
            File myFile = new File("/sdcard/MFusion/log.txt");//log path
            String separator = System.getProperty("line.separator");//set text to display line by line

            if (!myFile.exists()) {
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set time display format
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Created 'log.txt' at : " + currentDateandTime);//display in log information
                myOutWriter.append(separator);
                myOutWriter.close();
                fOut.close();
                
                
                
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                myOutWriter.append(separator);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set time format
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Application started :" + currentDateandTime);//display in log info
                myOutWriter.append("\n");
                myOutWriter.close();
                fOut.close();
            }


        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();//display message
        }

    }//end of error log


    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {


    }

    @Override
    public void onTabSelected(final Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        final RadioGroup radioGroup;
        final EditText tvtime, tvwtime;
        final EditText pass;
        pass = (EditText) findViewById(R.id.etPassword);//password
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);//radio group button
        tvtime = (EditText) findViewById(R.id.edTvtime);//shutdown time
        tvwtime = (EditText) findViewById(R.id.edTvwtime);//wake up time


        if (viewPager.getCurrentItem() == 0) {


            if (tab.getPosition() == 1 || tab.getPosition() == 2 || tab.getPosition() == 3 || tab.getPosition() == 4) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);//alert dialog builder
                builder.setTitle("SETTINGS");//set title
                builder.setMessage("Do you want to save the settings you have set?");//set message
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String shutdowntime = tvtime.getText().toString().trim();
                        String wakeuptime = tvwtime.getText().toString().trim();
                        String password = pass.getText().toString().trim();
                        String defaultp = "mfusion";//default password
                        String s = "Landscape";//landscape orientation
                        String a = "Portrait";//portrait orientation
                        //String dis = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();


                        if (radioGroup.getCheckedRadioButtonId() == R.id.landscape) {


                            if (password.isEmpty() && s != null) {


                                DALSettings.getInstance().setShutDownTime(shutdowntime);//call setshutdowntime method
                                DALSettings.getInstance().setWakeUpTime(wakeuptime);//call wakeuptime method
                                DALSettings.getInstance().setExitPassword(defaultp);//call setexitpassword method
                                Toast.makeText(MainActivity.this, "Saved with: " + s + "," + defaultp + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();
                                viewPager.setCurrentItem(tab.getPosition());
                                configurationLogDefault();//call configuration default method
                                
                            } else if (password != null) {
                                DALSettings.getInstance().setShutDownTime(shutdowntime);
                                DALSettings.getInstance().setWakeUpTime(wakeuptime);
                                DALSettings.getInstance().setExitPassword(password);
                                Toast.makeText(MainActivity.this, "Saved with: " + s + "," + password + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();//display when password is not null
                                viewPager.setCurrentItem(tab.getPosition());
                                configurationLog();//call configuration Log method

                            }


                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.portrait) {

                            if (password.isEmpty() && a != null) {

                                DALSettings.getInstance().setShutDownTime(shutdowntime);
                                DALSettings.getInstance().setWakeUpTime(wakeuptime);
                                DALSettings.getInstance().setExitPassword(defaultp);
                                Toast.makeText(MainActivity.this, "Saved with: " + a + "," + defaultp + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();
                                viewPager.setCurrentItem(tab.getPosition());
                                configurationLogDefault();//call configuration default method

                            } else if (password != null) {
                                DALSettings.getInstance().setShutDownTime(shutdowntime);
                                DALSettings.getInstance().setWakeUpTime(wakeuptime);
                                DALSettings.getInstance().setExitPassword(password);
                                Toast.makeText(MainActivity.this, "Saved with: " + a + "," + password + "," + shutdowntime + "," + wakeuptime, Toast.LENGTH_SHORT).show();
                                viewPager.setCurrentItem(tab.getPosition());
                                configurationLog();//call configuration Log method

                            }


                        }

                    }

                });//if user decided to save setting
                
                
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(tab.getPosition());//view selected tab(page)

                    }
                });//if user decide not to save setting

                builder.show();//show pop up window


            }//pop up window will appear if user click other page

        }//set start page as configuration page----------------------------------------------------



        if (viewPager.getCurrentItem() == 1) {

            if (tab.getPosition() == 0 || tab.getPosition() == 2 || tab.getPosition() == 3 || tab.getPosition() == 4) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Template Settings");
                builder.setMessage("Do you want to save the settings you have set?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "Saved ", Toast.LENGTH_SHORT).show();//display when user clicks yes
                        viewPager.setCurrentItem(tab.getPosition());//view selected tab(page)

                    }

                });//if user decides to save setting

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(tab.getPosition());//view selected tab(page)

                    }
                });//if user decides not to save setting

                builder.show();//show alert dialop pop up window
            }

            }//pop up window for Template fragment



            if (viewPager.getCurrentItem() == 2) {

                if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 3 || tab.getPosition() == 4) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Schedule Settings");//set title
                    builder.setMessage("Do you want to save  settings you have set?");//set message
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(MainActivity.this, "Saved ", Toast.LENGTH_SHORT).show();//display when user clicks yes
                            viewPager.setCurrentItem(tab.getPosition());//view selected tab(page)

                        }

                    });//if user decides to save setting

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewPager.setCurrentItem(tab.getPosition());//view selected tab(page)

                        }
                    });//if user decides not to save setting

                    builder.show();//show alert dialop pop up window
                }

            }//pop up window for schedule fragment

            if (viewPager.getCurrentItem() == 3) {

                if (tab.getPosition() == 0) {

                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 2) {


                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 4) {

                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 1) {

                    viewPager.setCurrentItem(tab.getPosition());
                }

            }//pop up window for log fragment

            if (viewPager.getCurrentItem() == 4) {

                if (tab.getPosition() == 0) {

                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 2) {


                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 3) {

                    viewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 1) {

                    viewPager.setCurrentItem(tab.getPosition());
                }

            }//pop up window for about fragment



    }
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Password");//setTitle
            builder.setMessage("Enter Default or User set Password:");//set message

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get exit password

                    String value = input.getText().toString().trim();

                    if (value.equals(DALSettings.getInstance().getExitPassword())) {
                        Toast.makeText(MainActivity.this, "User Exit Successfully", Toast.LENGTH_LONG).show();//display when user enters password correctly
                        finish();
                    } else if (value != DALSettings.getInstance().getExitPassword()) {
                        Toast.makeText(MainActivity.this, "Password Wrong", Toast.LENGTH_LONG).show();//display when user enters wrongly
                    }


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });//if user decides not to exit this project

            builder.show();//show alert dialop pop up window

            return true;
        }//keycode back

        return super.onKeyDown(keyCode, event);
    }//onkeydownbooean


    private void configurationLog() {
        final RadioGroup radioGroup;
        final EditText tvtime, tvwtime;
        final EditText pass;

        pass = (EditText) findViewById(R.id.etPassword);
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        tvtime = (EditText) findViewById(R.id.edTvtime);
        tvwtime = (EditText) findViewById(R.id.edTvwtime);


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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }

    }//end of log

    private void configurationLogDefault() {
        final RadioGroup radioGroup;
        final EditText tvtime, tvwtime;
        final EditText pass;

        pass = (EditText) findViewById(R.id.etPassword);
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        tvtime = (EditText) findViewById(R.id.edTvtime);
        tvwtime = (EditText) findViewById(R.id.edTvwtime);

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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }

    }//end of default log

}//class
