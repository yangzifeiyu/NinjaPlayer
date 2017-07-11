package com.mfusion.player.common.Player;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.controllers.KeyBoardCenter;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.ninjaplayer.adapter.CustomViewPager;
import com.mfusion.ninjaplayer.adapter.TabsPagerAdapter;
import com.mfusion.player.R;
import com.mfusion.player.library.Callback.DialogCallBack;
import com.mfusion.player.library.Helper.APPExitHelper;

import java.io.File;
import java.util.HashMap;

import info.monitorenter.util.FileUtil;

public class ActivityViewpage extends FragmentActivity implements ActionBar.TabListener {

    ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String[] tabs = {"Configuration", "Template", "Schedule", "Log", "About"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //WindowsDecorHelper.hideBottomBar(this.getWindow());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Stetho.initializeWithDefaults(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_activity_viewpage);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//to make sure that keyboard does not effect the GUI

        HashMap<String,Integer> tabInfos=new HashMap<>();
        tabInfos.put(tabs[0],R.drawable.mf_menu_configuration);
        tabInfos.put(tabs[1],R.drawable.mf_menu_template);
        tabInfos.put(tabs[2],R.drawable.mf_menu_schedule);
        tabInfos.put(tabs[3],R.drawable.mf_menu_log);
        //tabInfos.put(tabs[4],R.drawable.mf_menu_log);

        try{
            viewPager = (ViewPager) findViewById(R.id.view);
            ((CustomViewPager)viewPager).setPagingEnabled(false);

            actionBar = getActionBar();
            actionBar.setLogo(getResources().getDrawable(R.drawable.mf_icon_nijia));
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.mf_menu_bluebar));

            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());//tabs adapter

            viewPager.setAdapter(mAdapter);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//set navigation mode tabs

            // Adding Tabs
            for (String tab_name : tabs) {
                View tab_view= LayoutInflater.from(this).inflate(com.mfusion.ninjaplayer.R.layout.active_tab_item_view, null,true);
                ImageView tab_image_view=(ImageView)tab_view.findViewById(com.mfusion.ninjaplayer.R.id.menu_tab_image);
                TextView tab_name_view=(TextView) tab_view.findViewById(com.mfusion.ninjaplayer.R.id.menu_tab_name);
                tab_name_view.setText(tab_name);
                if(tabInfos.containsKey(tab_name))
                    tab_image_view.setImageDrawable(getResources().getDrawable(tabInfos.get(tab_name)));
                else
                    tab_image_view.setVisibility(View.INVISIBLE);
                actionBar.addTab(actionBar.newTab().setCustomView(tab_view)
                        .setTabListener(this));
                /*actionBar.addTab(actionBar.newTab().setText(tab_name)
                        .setTabListener(this));*/
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

    }

    Fragment current_fragment =null;
    @Override
    public void onTabSelected(final ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        current_fragment=null;

        if(viewPager.getCurrentItem()==tab.getPosition())
            return;

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
        if(current_fragment!=null&&((AbstractFragment)current_fragment).getIsEditing()){
            AlertDialogHelper.showAlertDialog(this, "Information", "Do you want to save these modification ?", saveSaveCallback,cancelSaveCallback);
        }
        else
            viewPager.setCurrentItem(tab.getPosition());//pop up window for about fragment

        /*checkFragmentEditStatus(new OperateCallbackBundle() {
            @Override
            public void onConfim(String content) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onCancel(String errorMsg) {
                actionBar.selectTab(actionBar.getTabAt(viewPager.getCurrentItem()));
            }
        });*/
    }

    @Override
    public void onTabUnselected(final ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        System.out.println("");
    }

    private void checkFragmentEditStatus(final OperateCallbackBundle callbackBundle){
        current_fragment=null;

        CallbackBundle cancelSaveCallback=new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                ((AbstractFragment)current_fragment).cancelSaveModification();
                callbackBundle.onConfim("");
            }
        };

        CallbackBundle saveSaveCallback= new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                ((AbstractFragment)current_fragment).saveModification(new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        callbackBundle.onConfim("");
                    }

                    @Override
                    public void onCancel(String errorMsg) {
                        callbackBundle.onCancel("");
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
        if(current_fragment!=null&&((AbstractFragment)current_fragment).getIsEditing()){
            AlertDialogHelper.showAlertDialog(this, "Information", "Do you want to save these modification ?", saveSaveCallback,cancelSaveCallback);
        }
        else
            callbackBundle.onConfim("");

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            //ACTION_DOWN
            if( event.getKeyCode() == KeyEvent.KEYCODE_BACK){

                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_F5) {
                checkFragmentEditStatus(new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        startActivity(new Intent(ActivityViewpage.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancel(String errorMsg) {

                    }
                });
            }
            if(event.getKeyCode()==KeyEvent.KEYCODE_FORWARD_DEL||event.getKeyCode()==KeyEvent.KEYCODE_DEL){
                for(CallbackBundle callbackBundle : KeyBoardCenter.deleteKeyCallbackList){
                    callbackBundle.callback(null);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(this.getRequestedOrientation());
    }
}
