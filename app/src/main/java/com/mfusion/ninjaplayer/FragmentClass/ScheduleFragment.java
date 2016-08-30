package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.data.XMLSchedule;
import com.mfusion.commons.entity.exception.IllegalScheduleException;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.exception.ScheduleNotFoundException;
import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;
import com.mfusion.scheduledesigner.ScheduleDesigner;


public class ScheduleFragment extends AbstractFragment {

    private ScheduleDesigner scheduleView=null;

    private TextView m_warning_text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView!=null){
            //this.openSchedule();
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        m_warning_text=(TextView)rootView.findViewById(R.id.sche_warning_view);

        scheduleView=(ScheduleDesigner)rootView.findViewById(R.id.schedule_view);
        scheduleView.parentFragment=this;

        ((ImageButton)rootView.findViewById(R.id.sche_open)).setVisibility(View.GONE);

        ImageTextView btn_save=(ImageTextView)rootView.findViewById(R.id.sche_save);
        btn_save.setText("Save");
        btn_save.setImage(R.drawable.mf_save);
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                saveModification(null);
            }
        });

        ImageTextView btn_assign=(ImageTextView)rootView.findViewById(R.id.sche_assign);
        btn_assign.setText("Assign");
        btn_assign.setImage(android.R.drawable.ic_menu_upload_you_tube);
        btn_assign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                saveModification(new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        try {
                            XMLSchedule.getInstance().assignSchedule(InternalKeyWords.DefaultScheduleName);
                            m_warning_text.setText("Assign successfully");
                        }catch (IllegalScheduleException ex){
                            m_warning_text.setText("Schedule has invalid property");
                            ex.printStackTrace();
                        }catch (Exception ex){
                            m_warning_text.setText("Assign failed");
                            ex.printStackTrace();
                            LogOperator.WriteLogfortxt("ScheduleFragment==>assignSchedule :"+ex.getMessage());
                        }
                    }

                    @Override
                    public void onCancel(String errorMsg) {

                    }
                });

            }
        });

        this.openSchedule();
        return rootView;
    }

    private void openSchedule(){
        try{
            this.isEditing=false;
            if(scheduleView!=null)
                scheduleView.openSchedule(XMLSchedule.getInstance().LoadSchedule());
            m_warning_text.setText("");
        }catch (ScheduleNotFoundException ex){
            m_warning_text.setText("Can't found this schedule");
            ex.printStackTrace();
        }catch (IllegalScheduleException ex){
            m_warning_text.setText("Schedule has invalid property");
            ex.printStackTrace();
        }catch (Exception ex) {
            m_warning_text.setText("Open failed");
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("ScheduleFragment==>openSchedule :"+ex.getMessage());
        }
    }

    @Override
    public void saveModification(OperateCallbackBundle callbackBundle) {
        try {

            XMLSchedule.getInstance().SaveSchedule(scheduleView.saveSchedule());
            this.isEditing=false;
            m_warning_text.setText("Save successfully");
            if(callbackBundle!=null)
                callbackBundle.onConfim("");
            return;
        }catch (PathAccessException ex){
            ex.printStackTrace();
            m_warning_text.setText("Can't fond the path to save schedule");
        }catch (IllegalScheduleException ex){
            ex.printStackTrace();
            m_warning_text.setText(ex.getMessage());
        }catch (Exception ex){
            ex.printStackTrace();
            m_warning_text.setText("Save Failed");
            LogOperator.WriteLogfortxt("ScheduleFragment==>saveSchedule :"+ex.getMessage());
        }
        if(callbackBundle!=null)
            callbackBundle.onCancel("");
    }

    @Override
    public void cancelSaveModification() {

    }

    @Override
    public void showFragment() {
        if(scheduleView!=null)
            this.openSchedule();
    }

    @Override
    public void hideFragment() {

    }
}
