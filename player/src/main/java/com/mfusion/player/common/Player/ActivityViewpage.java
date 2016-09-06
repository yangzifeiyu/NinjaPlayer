package com.mfusion.player.common.Player;

import android.app.ActionBar;
import android.app.FragmentTransaction;
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
import com.mfusion.ninjaplayer.adapter.CustomViewPager;
import com.mfusion.ninjaplayer.adapter.TabsPagerAdapter;
import com.mfusion.player.R;

public class ActivityViewpage extends FragmentActivity implements ActionBar.TabListener {

    ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String[] tabs = {"Configuration", "Template", "Schedule", "Log", "About"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_activity_viewpage);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//to make sure that keyboard does not effect the GUI

        try{
            viewPager = (ViewPager) findViewById(R.id.view);
            ((CustomViewPager)viewPager).setPagingEnabled(false);

            actionBar = getActionBar();
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

        try
        {
            String ProcID = "79";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) ProcID = "42"; // ICS
            // 需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "service call activity " + ProcID + " s16 com.android.systemui" }); // WAS
            proc.waitFor();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
        if(current_fragment!=null&&((AbstractFragment)current_fragment).isEditing){
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
        if(current_fragment!=null&&((AbstractFragment)current_fragment).isEditing){
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
}
