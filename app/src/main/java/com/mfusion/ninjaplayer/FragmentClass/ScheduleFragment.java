package com.mfusion.ninjaplayer.FragmentClass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mfusion.commons.data.XMLSchedule;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.ninjaplayer.New.Home;
import com.mfusion.ninjaplayer.R;
import com.mfusion.scheduledesigner.ScheduleDesigner;


public class ScheduleFragment extends Fragment {
    private ScheduleDesigner scheduleView=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_test, container, false);

        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);

        scheduleView=new ScheduleDesigner(getActivity());
        ((RelativeLayout)rootView.findViewById(com.mfusion.scheduledesigner.R.id.designer)).addView(scheduleView, layoutParams);
        //((RelativeLayout)findViewById(R.id.designer)).addView(new GraphicTemplateListView_1(this), layoutParams);
        ((ImageButton)rootView.findViewById(com.mfusion.scheduledesigner.R.id.sche_open)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try{

                    scheduleView.openSchedule(XMLSchedule.getInstance().LoadSchedule());
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ((ImageButton)rootView.findViewById(com.mfusion.scheduledesigner.R.id.sche_save)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {

                    XMLSchedule.getInstance().SaveSchedule(scheduleView.saveSchedule());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        ((ImageButton)rootView.findViewById(com.mfusion.scheduledesigner.R.id.sche_back)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {

                    XMLSchedule.getInstance().assignSchedule(InternalKeyWords.DefaultScheduleName);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });


        return rootView;


    }


}
