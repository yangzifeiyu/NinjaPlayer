package com.mfusion.commons.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ThinkPad on 2016/12/27.
 */
public class UsbBroadcastReceiver extends BroadcastReceiver {
    public static String UsbPath;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
            UsbPath = intent.getData().getPath();
            System.out.println(UsbPath);
        }else if (Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction()))
            System.out.println("REMOVED");

        else if (Intent.ACTION_MEDIA_SHARED.equals(intent.getAction()))
            System.out.println( "SHARED");

            // User has expressed the desire to remove the external storage media
        else if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction()))
            System.out.println( "EJECT");
        else
            System.out.println("USB :"+intent.getData().getPath());
    }
}
