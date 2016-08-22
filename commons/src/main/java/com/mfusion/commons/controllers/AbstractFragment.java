package com.mfusion.commons.controllers;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by ThinkPad on 2016/8/15.
 */
public abstract class AbstractFragment extends Fragment {

    protected View rootView;

    public Boolean isInitView=false;

    public Boolean isEditing=false;

    public abstract Boolean saveModification();

    public abstract void cancelSaveModification();

    public abstract void showFragment();

    public abstract void hideFragment();

    @Override
    public void setMenuVisibility(boolean menuVisible){
        super.setMenuVisibility(menuVisible);
        if(menuVisible)
            this.showFragment();
        else
            this.hideFragment();
    }
}
