package com.mfusion.commons.controllers;

import android.support.v4.app.Fragment;
import android.view.View;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.OperateCallbackBundle;

/**
 * Created by guoyu on 2016/8/15.
 */
public abstract class AbstractFragment extends Fragment {

    protected View rootView;

    //public Boolean isSavedSuccess=true;

    protected Boolean isEditing=false;
    public void setIsEditing(Boolean isEditing){
        this.isEditing=isEditing;
    }
    public Boolean getIsEditing(){
        return this.isEditing;
    }

    public abstract void saveModification(OperateCallbackBundle callbackBundle);

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
