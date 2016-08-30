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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.controllers.AbstractFragment;
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

    // Tab titles
    private String[] tabs = {"Configuration", "Template", "Schedule", "Log", "About"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//to make sure that keyboard does not effect the GUI

        try{
            actionBar = getActionBar();
            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());//tabs adapter

            viewPager = (CustomViewPager) findViewById(R.id.photosViewPager);//customer view pager
            viewPager.setPagingEnabled(false);//set paging not swipable

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
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }


        Advertisment();//call advertisement(logo endorsement) method
        ErrorLog();////call errorLog method
    }//oncreate

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            //ACTION_DOWN
            if (event.getKeyCode() == KeyEvent.KEYCODE_F5 || event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                finish();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void Advertisment() {
/*

        new Handler().postDelayed(new Runnable() {
            public void run() {
                test();//call test method
            }
        }, 60 * 1000);//display after 1 min
*/


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

    Fragment current_fragment =null;
    @Override
    public void onTabSelected(final Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        current_fragment=null;

        CallbackBundle cancelSaveCallback=new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                ((AbstractFragment)current_fragment).cancelSaveModification();
                viewPager.setCurrentItem(tab.getPosition());
            }
        };

        CallbackBundle saveSaveCallback= new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                ((AbstractFragment)current_fragment).saveModification(new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onCancel(String errorMsg) {
                        actionBar.selectTab(actionBar.getTabAt(viewPager.getCurrentItem()));
                    }
                });
            }
        };
        if (viewPager.getCurrentItem() == 0) {
            current_fragment =mAdapter.configurationFragment;
        }//set start page as configuration page----------------------------------------------------
        if (viewPager.getCurrentItem() == 1) {
            current_fragment = mAdapter.templateFragment;
        }//pop up window for Template fragment
        if (viewPager.getCurrentItem() == 2) {
            current_fragment = mAdapter.scheduleFragment;
        }//pop up window for schedule fragment
        if(current_fragment!=null&&((AbstractFragment)current_fragment).isEditing){
            AlertDialogHelper.showAlertDialog(this, "Information", "Do you want to save these modification?", saveSaveCallback,cancelSaveCallback);
        }else
            viewPager.setCurrentItem(tab.getPosition());//pop up window for about fragment
    }
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
*/
            return true;
        }//keycode back

        return super.onKeyDown(keyCode, event);
    }//onkeydownbooean

}//class
