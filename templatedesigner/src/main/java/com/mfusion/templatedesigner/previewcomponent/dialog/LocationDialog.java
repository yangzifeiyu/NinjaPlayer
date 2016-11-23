package com.mfusion.templatedesigner.previewcomponent.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;

public class LocationDialog {
	
	public  Dialog createDialog(Context context,int left,int top, final CallbackBundle callback){

		LinearLayout dialogContent=(LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.dialog_location, null);

		final EditText et_left=(EditText)dialogContent.findViewById(R.id.dialog_location_x);
		et_left.setText(String.valueOf(left));
		WindowsDecorHelper.hideSoftInputInEditText(et_left);
		final EditText et_top=(EditText)dialogContent.findViewById(R.id.dialog_location_y);
		et_top.setText(String.valueOf(top));
		WindowsDecorHelper.hideSoftInputInEditText(et_top);

		SystemInfoDialog.Builder builder =new SystemInfoDialog.Builder(context)
				.setTitle("Please input component location").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent)
				.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Bundle bundle=new Bundle();
						bundle.putInt("L", Integer.parseInt(et_left.getText().toString()));
						bundle.putInt("T", Integer.parseInt(et_top.getText().toString()));
						callback.callback(bundle);
						dialog.dismiss();
					}
				}).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		Dialog dialog=builder.create();
		dialog.show();

		return dialog;
	}

}
