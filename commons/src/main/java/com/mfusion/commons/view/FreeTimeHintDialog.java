package com.mfusion.commons.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LicenseDecoder;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commontools.R;

/**
 * Created by ThinkPad on 2016/10/25.
 */
public class FreeTimeHintDialog {

    Context m_context;

    HandleTimer dialog_display_timer,dialog_hide_timer;

    int dialog_display_timer_interval=40000,dialog_hide_timer_interval=10000;

    SystemInfoDialog display_dialog;

    public FreeTimeHintDialog(Context context){
        this.m_context=context;
    }

    public void startDisplay(){
        this.startDisplay(dialog_display_timer_interval);
    }

    public void startDisplay(int timerInterval){
        try{
            if(dialog_display_timer==null){
                dialog_display_timer=new HandleTimer() {
                    @Override
                    protected void onTime() {

                        if(LicenseDecoder.checkLicenseValidity()){
                            dialog_display_timer.stop();
                            return;
                        }
                        showDialog();
                        dialog_hide_timer.start(dialog_hide_timer_interval,dialog_hide_timer_interval);
                    }
                };
            }

            if(dialog_hide_timer==null){
                dialog_hide_timer=new HandleTimer() {
                    @Override
                    protected void onTime() {
                        hideDialog();
                        dialog_hide_timer.stop();
                    }
                };
            }

            dialog_display_timer.restart(timerInterval,timerInterval);
        }catch (Exception ex){
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("FreeTimeHintDialog==>startDisplay:"+ex.getMessage());
        }
    }

    public void stopDisplay(){
        try {
            if (dialog_display_timer != null)
                dialog_display_timer.stop();
            if (dialog_hide_timer != null)
                dialog_hide_timer.stop();
            if (display_dialog != null && display_dialog.isShowing())
                display_dialog.hide();
        }catch (Exception ex){
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("FreeTimeHintDialog==>stopDisplay:"+ex.getMessage());
        }
    }

    private void showDialog(){
        try {
            if(display_dialog==null){
                /*ImageView hint_image=new ImageView(m_context);
                hint_image.setScaleType(ImageView.ScaleType.FIT_XY);
                hint_image.setImageDrawable(m_context.getResources().getDrawable(R.drawable.mf_ninja_splash));*/

                RelativeLayout dialogContent=(RelativeLayout) ((Activity)m_context).getLayoutInflater().inflate(R.layout.layout_trialhint, null);
                ImageView qr_image=(ImageView) dialogContent.findViewById(R.id.trial_img_qr);
                qr_image.setImageBitmap(ImageHelper.getBitmap(InternalKeyWords.DeviceQR_Path,-1));
                TextView device_txt=(TextView) dialogContent.findViewById(R.id.trial_tv_deviceId);
                device_txt.setText(InternalKeyWords.DeviceId);

                SystemInfoDialog.Builder builder =new  SystemInfoDialog.Builder(m_context)
                        .setIcon(R.drawable.mf_information)
                        .setTitle("MediaFusion Ultimate Lite")
                        .setContentView(dialogContent, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                display_dialog =builder.create();

                WindowManager.LayoutParams params = display_dialog.getWindow().getAttributes();
                params.width = m_context.getResources().getDisplayMetrics().widthPixels>m_context.getResources().getDisplayMetrics().heightPixels? m_context.getResources().getDisplayMetrics().widthPixels*2/5:m_context.getResources().getDisplayMetrics().widthPixels*2/3;
                params.height = m_context.getResources().getDisplayMetrics().widthPixels>m_context.getResources().getDisplayMetrics().heightPixels? m_context.getResources().getDisplayMetrics().heightPixels*2/5:m_context.getResources().getDisplayMetrics().heightPixels*1/4;
                display_dialog.getWindow().setAttributes(params);
            }

            display_dialog.show();
        }catch (Exception ex){
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("FreeTimeHintDialog==>showDialog:"+ex.getMessage());
        }
    }

    private void hideDialog(){
        if(display_dialog!=null)
            display_dialog.hide();
    }
}
