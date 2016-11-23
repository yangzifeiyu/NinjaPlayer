package com.mfusion.templatedesigner.previewcomponent.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;

public class SizeDialog {

	public  Dialog createDialog(final Context context, int width, int height, final CallbackBundle callback){

		final LinearLayout dialogContent=(LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_size, null);

		final EditText et_width=(EditText)dialogContent.findViewById(R.id.dialog_size_width);
		et_width.setText(String.valueOf(width));
		WindowsDecorHelper.hideSoftInputInEditText(et_width);
		final EditText et_height=(EditText)dialogContent.findViewById(R.id.dialog_size_height);
		et_height.setText(String.valueOf(height));
		WindowsDecorHelper.hideSoftInputInEditText(et_height);

		SystemInfoDialog.Builder builder =new  SystemInfoDialog.Builder(context)
				.setTitle("Please input component size").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent)
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}  
				}).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Bundle bundle=new Bundle();
						bundle.putInt("W", Integer.parseInt(et_width.getText().toString()));
						bundle.putInt("H", Integer.parseInt(et_height.getText().toString()));
						callback.callback(bundle);
						dialog.dismiss();
					}
				});

		SystemInfoDialog dialog =builder.create();
		dialog.show();

		return dialog;
	}
}
