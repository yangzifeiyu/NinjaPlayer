package com.mfusion.player.library.Helper;


import com.mfusion.player.R;
import com.mfusion.player.common.Player.MainActivity;

import com.mfusion.player.library.Callback.DialogCallBack;
import com.mfusion.player.services.HomeKeyReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class APPExitHelper {
	public static void RestartPlayer(Context context){
		Builder builder =new Builder(context).setTitle("System Information")//���öԻ������
				.setMessage("The Config is modified. Do you want to restart this player?")//������ʾ������  
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {//���ȷ����ť  
					@Override  
					public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
						try {
							MainActivity.Instance.mService.RestartPlayer(MainActivity.Instance.PlayerSetting.MFServerIp,MainActivity.Instance.PlayerSetting.MFServerPort,MainActivity.Instance.PlayerSetting.getMediaport());
							//APPExitHelper.CloseApp();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}  
				}).setNegativeButton("No",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
					@Override  
					public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  

					}  
				});
		builder.setCancelable(false);
		AlertDialog dialog =builder.show();
	}

	public static void CloseApp(){
		try {

			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			// TODO: handle exception
		} 
	}

	public static void showAppDialogWithPassword(String title,final DialogCallBack call,Activity activity){
		if(MainActivity.Instance.PlayerSetting.getExitPassword().isEmpty()){
			call.onConfim("");
			return;
		}
		Builder builder =new Builder(activity).setTitle(title)//���öԻ������
				.setView(activity.getLayoutInflater().inflate(R.layout.dialog_exit_verify_view, null))
				.setPositiveButton("Apply",null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť
					@Override
					public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�
						call.onCancel("");
					}
				});
		builder.setCancelable(false);
		final AlertDialog dialog =builder.show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
		Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
		final EditText et_password=(EditText)dialog.findViewById(R.id.dialog_edit_name_value);
		final TextView et_warning=(TextView)dialog.findViewById(R.id.dialog_edit_name_warning);
		et_warning.setVisibility(View.GONE);
		negativeButton.requestFocus();
		negativeButton.setFocusable(true);

		((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String input=et_password.getText().toString();
				if(input.equals(MainActivity.Instance.PlayerSetting.getExitPassword())){
					call.onConfim("");
					dialog.dismiss();
				} else {
					et_warning.setVisibility(View.VISIBLE);
					et_warning.setText("Password is not match");
				}
			}
		});
	}

	public static void showAppClosingDialogNoPassword(final DialogCallBack call,Activity activity){
		Builder builder =new Builder(activity).setTitle("System Information")//���öԻ������
				.setMessage("Do you want to exit?")//������ʾ������
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {//���ȷ����ť
					@Override
					public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�
						try {
							call.onConfim("");
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}).setNegativeButton("No",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť
					@Override
					public void onClick(DialogInterface dialog, int which) {call.onCancel("");

					}
				});
		builder.setCancelable(false);
		AlertDialog dialog =builder.show();
	}

	public static void showAppClosingDialog(DialogCallBack call,Activity activity){

		if(MainActivity.Instance.PlayerSetting.getExitPassword().isEmpty())
			showAppClosingDialogNoPassword(call,activity);
		else
			showAppDialogWithPassword("Please input password to exit",call,activity);
	}

	public static HomeKeyReceiver mHomeKeyReceiver = null;

	public static void RegisterHomeKeyReceiver(Context context) {
		try {
			if (null != mHomeKeyReceiver)
				return;
			mHomeKeyReceiver = new HomeKeyReceiver();
			final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

			context.registerReceiver(mHomeKeyReceiver, homeFilter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void UnregisterHomeKeyReceiver(Context context) {
		if (null != mHomeKeyReceiver) {
			context.unregisterReceiver(mHomeKeyReceiver);
		}
	}
}
