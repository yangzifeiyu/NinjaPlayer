package com.mfusion.ninjaplayer.FragmentClass;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.ninjaplayer.R;
import com.mfusion.scheduledesigner.ScheduleDesigner;


public class ScheduleFragment extends AbstractFragment {

    private ScheduleDesigner scheduleView=null;

    private TextView m_warning_text;

    ImageTextVerticalView btn_save,btn_assign;
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

        btn_save=(ImageTextVerticalView)rootView.findViewById(R.id.sche_save);
        btn_save.setText("Save");
        btn_save.setImage(R.drawable.mf_save);
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                saveModification(null);
                btn_save.setEnabled(true);
                btn_assign.setEnabled(true);
            }
        });

        btn_assign=(ImageTextVerticalView)rootView.findViewById(R.id.sche_assign);
        btn_assign.setText("Publish");
        btn_assign.setImage(R.drawable.mf_publish);
        btn_assign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                saveModification(new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        m_warning_text.setText("Assigning schedule ...");
                        AsyncAssignTask assignTask=new AsyncAssignTask();
                        assignTask.execute("");
                    }

                    @Override
                    public void onCancel(String errorMsg) {
                        btn_save.setEnabled(true);
                        btn_assign.setEnabled(true);
                    }
                });

            }
        });

        this.openSchedule();
        return rootView;
    }

    private void openSchedule(){
        String result="";
        try{
            this.isEditing=false;
            if(scheduleView!=null)
                scheduleView.openSchedule(XMLSchedule.getInstance().LoadSchedule());
            return;
        }catch (ScheduleNotFoundException ex){
            result="Can't found this schedule";
            ex.printStackTrace();
        }catch (IllegalScheduleException ex){
            result="Schedule has invalid property";
            ex.printStackTrace();
        }catch (Exception ex) {
            result="Open failed";
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("ScheduleFragment==>openSchedule :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Open Schedule",result,null);
    }

    @Override
    public void saveModification(OperateCallbackBundle callbackBundle) {
        btn_save.setEnabled(false);
        btn_assign.setEnabled(false);

        String result="Save successfully";
        try {

            XMLSchedule.getInstance().SaveSchedule(scheduleView.saveSchedule());
            this.isEditing=false;
            if(callbackBundle!=null)
                callbackBundle.onConfim("");
            else {
                AlertDialogHelper.showInformationDialog(getContext(), "Save Schedule", result, null);
            }
            return;
        }catch (PathAccessException ex){
            ex.printStackTrace();
            result="Can't fond the path to save schedule";
        }catch (IllegalScheduleException ex){
            ex.printStackTrace();
            result=ex.getMessage();
        }catch (Exception ex){
            ex.printStackTrace();
            result="Save Failed";
            LogOperator.WriteLogfortxt("ScheduleFragment==>saveSchedule :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Save Schedule",result,null);
        if(callbackBundle!=null)
            callbackBundle.onCancel("");
    }

    @Override
    public void cancelSaveModification() {
        isEditing=false;
    }

    @Override
    public void showFragment() {
        if(scheduleView!=null) {
            this.openSchedule();
            btn_save.setEnabled(true);
            btn_assign.setEnabled(true);
        }
    }

    @Override
    public void hideFragment() {

    }

    public class AsyncAssignTask extends AsyncTask<String, Integer, String> {

        String assign_result="";
        @Override
        protected String doInBackground(String... params) {
            try {
                XMLSchedule.getInstance().assignSchedule(InternalKeyWords.DefaultScheduleName);
                assign_result="Assign successfully";
            }catch (IllegalScheduleException ex){
                assign_result="Schedule has invalid property";
                ex.printStackTrace();
            }catch (Exception ex){
                assign_result="Assign failed";
                ex.printStackTrace();
                LogOperator.WriteLogfortxt("ScheduleFragment==>assignSchedule :"+ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                super.cancel(true);
                m_warning_text.setText("");
                btn_save.setEnabled(true);
                btn_assign.setEnabled(true);
                AlertDialogHelper.showInformationDialog(getContext(),"Assign Schedule",assign_result,null);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
