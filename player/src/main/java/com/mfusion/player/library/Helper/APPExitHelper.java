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
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

	public static void showAppDialogWithPassword(String title, String positiveText, final DialogCallBack call, final Activity activity){
		if(MainActivity.Instance.PlayerSetting.getExitPassword().isEmpty()){
			call.onConfim("");
			return;
		}

		Builder builder =new Builder(activity).setTitle(title)//���öԻ������
				.setView(activity.getLayoutInflater().inflate(R.layout.dialog_exit_verify_view, null))
				.setPositiveButton(positiveText,null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť
					@Override
					public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�
						call.onCancel("");
					}
				});
		builder.setCancelable(false);
		final AlertDialog dialog =builder.show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
		Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
		final TextView et_warning=(TextView)dialog.findViewById(R.id.dialog_edit_name_warning);
		et_warning.setVisibility(View.GONE);
		final EditText et_password=(EditText)dialog.findViewById(R.id.dialog_edit_name_value);
		/*View.OnClickListener pass_edit_view=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
				}
			}
		};
		et_password.setOnClickListener(pass_edit_view);
		pass_edit_view.onClick(null);*/

		final View.OnClickListener clickListener = new View.OnClickListener() {
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
		};

		/*et_password.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER)
					clickListener.onClick(null);
				return false;
			}
		});*/

		Button posiiveButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
		posiiveButton.setOnClickListener(clickListener);
		/*posiiveButton.requestFocus();
		posiiveButton.setFocusable(true);*/
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
			showAppDialogWithPassword("Please input password to exit","Apply",call,activity);
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