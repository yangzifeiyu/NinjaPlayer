package com.mfusion.ninjaplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mfusion.ninjaplayer.FragmentClass.AboutFragment;
import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.ninjaplayer.FragmentClass.ConfigurationFragment;
import com.mfusion.ninjaplayer.FragmentClass.LogFragment;
import com.mfusion.ninjaplayer.FragmentClass.ScheduleFragment;
import com.mfusion.ninjaplayer.FragmentClass.TemplateFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public AbstractFragment scheduleFragment;
    public AbstractFragment configurationFragment;
    public AbstractFragment templateFragment;
    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Configuration fragment activity
                if(configurationFragment==null)
                    configurationFragment=new ConfigurationFragment();
                return configurationFragment;
            case 1:
                // Template fragment activity
                if(templateFragment==null)
                    templateFragment=new TemplateFragment();
                return templateFragment;

            case 2:
                // Schedule fragment activity
                if(scheduleFragment==null)
                    scheduleFragment=new ScheduleFragment();
                return scheduleFragment;

            case 3:
                // About fragment activity
                return new LogFragment();
            case 4:
                // About fragment activity
                return new AboutFragment();
        }

        return null;
    }//get fragment

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }//get count

}//end of the class
