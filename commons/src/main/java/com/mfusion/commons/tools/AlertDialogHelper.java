package com.mfusion.commons.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.mfusion.commons.tools.CallbackBundle;

/**
 * Created by ThinkPad on 2016/8/15.
 */
public class AlertDialogHelper {
    public static void showAlertDialog(Context context,String title, String message, final CallbackBundle positiveCallback, final CallbackBundle canelCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//set title
        builder.setMessage(message);//set message
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(positiveCallback!=null)
                    positiveCallback.callback(null);
            }

        });//if user decides to save setting

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(canelCallback!=null)
                    canelCallback.callback(null);
            }
        });//if user decides not to save setting

        builder.show();//show alert dialop pop up window
    }

    public static void showInformationDialog(Context context,String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//set title
        builder.setMessage(message);//set message
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });//if user decides to save setting

        builder.show();//show alert dialop pop up window
    }
}
