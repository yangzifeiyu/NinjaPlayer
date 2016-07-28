package com.mfusion.scheduledesigner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.mfusion.commons.data.XMLSchedule;
import com.mfusion.commons.tools.InternalKeyWords;

public class TestActivity extends AppCompatActivity {

    private ScheduleDesigner scheduleView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        LayoutParams layoutParams=new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);

        scheduleView=new ScheduleDesigner(this);
        ((RelativeLayout)findViewById(R.id.designer)).addView(scheduleView, layoutParams);
        //((RelativeLayout)findViewById(R.id.designer)).addView(new GraphicTemplateListView_1(this), layoutParams);
        ((ImageButton)findViewById(R.id.sche_open)).setOnClickListener(new View.OnClickListener() {

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

        ((ImageButton)findViewById(R.id.sche_save)).setOnClickListener(new View.OnClickListener() {

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

        ((ImageButton)findViewById(R.id.sche_back)).setOnClickListener(new View.OnClickListener() {

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
    }
}
