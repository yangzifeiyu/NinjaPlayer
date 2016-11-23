package com.mfusion.commons.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.commontools.R;

/**
 * Created by ThinkPad on 2016/8/15.
 */
public class AlertDialogHelper {
    public static void showAlertDialog(Context context,String title, String message, final CallbackBundle positiveCallback, final CallbackBundle cancelCallback){

        showAlertDialog(context,title,R.drawable.mf_information,message,"Yes",positiveCallback,"No",cancelCallback);
    }

    public static void showAlertDialog(Context context,String title,int titleIcon, String message,String positiveText, final CallbackBundle positiveCallback,String negativeText, final CallbackBundle canelCallback){

        SystemInfoDialog.Builder builder =new SystemInfoDialog.Builder(context)
                .setTitle(title).setIcon(titleIcon)
                .setMessage(message)
                .setPositiveButtonImage(R.drawable.mf_apply)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(positiveCallback!=null)
                            positiveCallback.callback(null);
                        dialog.dismiss();

                    }
                }).setCloseButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(canelCallback!=null)
                            canelCallback.callback(null);
                        dialog.dismiss();
                    }
                }).setNegativeButton(negativeText,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(canelCallback!=null)
                            canelCallback.callback(null);
                        dialog.dismiss();
                    }
                });

        Dialog dialog=builder.create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = context.getResources().getDisplayMetrics().widthPixels>context.getResources().getDisplayMetrics().heightPixels? context.getResources().getDisplayMetrics().widthPixels*1/3:context.getResources().getDisplayMetrics().widthPixels*2/3;
        dialog.getWindow().setAttributes(params);

        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showWarningDialog(Context context,String title, String message, final CallbackBundle positiveCallback){

        showAlertDialog(context,title,R.drawable.mf_warning,message,"OK",positiveCallback,null,null);
    }
    public static void showInformationDialog(Context context,String title, String message, final CallbackBundle positiveCallback){

        showAlertDialog(context,title,R.drawable.mf_information,message,"OK",positiveCallback,null,null);
    }
}
