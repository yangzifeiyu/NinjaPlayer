package com.mfusion.ninjaplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mfusion.ninjaplayer.FragmentClass.AboutFragment;
import com.mfusion.ninjaplayer.FragmentClass.ConfigurationFragment;
import com.mfusion.ninjaplayer.FragmentClass.LogFragment;
import com.mfusion.scheduledesigner.ScheduleFragment;
import com.mfusion.templatedesigner.TemplateFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Configuration fragment activity
                return new ConfigurationFragment();

            case 1:
                // Template fragment activity
                return new TemplateFragment();

            case 2:
                // Schedule fragment activity
                return new ScheduleFragment();
            case 3:
                // About fragment activity
                return new AboutFragment();

            case 4:
                // About fragment activity
                return new LogFragment();


        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }

}
