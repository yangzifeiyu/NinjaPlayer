package com.mfusion.player.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mfusion.player.library.Helper.APPExitHelper;

public class HomeKeyReceiver extends BroadcastReceiver {
	private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
	@Override
	public void onReceive(Context content, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // android.intent.action.CLOSE_SYSTEM_DIALOGS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // �̰�Home��
            	APPExitHelper.CloseApp();
            }
            else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // ����Home�� ���� activity�л���
            	Toast.makeText(content, "Key Press : SYSTEM_DIALOG_REASON_RECENT_APPS", Toast.LENGTH_LONG).show();
            }
            else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                // ����
            	Toast.makeText(content, "Key Press : SYSTEM_DIALOG_REASON_LOCK", Toast.LENGTH_LONG).show();
            }
            else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // samsung ����Home��
            	Toast.makeText(content, "Key Press : SYSTEM_DIALOG_REASON_ASSIST", Toast.LENGTH_LONG).show();
            }

        }
	}

}
