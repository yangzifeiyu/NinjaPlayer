package com.mfusion.templatedesigner.previewcomponent.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;

public class LocationDialog {
	
	public  Dialog createDialog(Context context,int left,int top, final CallbackBundle callback){
		Builder builder =new Builder(context).setTitle("Please input component location")
				.setView(((Activity)context).getLayoutInflater().inflate(R.layout.dialog_location, null))
				.setPositiveButton("Apply",null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface dialog, int which) {
						callback.callback(null);
					}  
				});
		builder.setCancelable(true);
		final AlertDialog dialog =builder.show();
		Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
		final EditText et_left=(EditText)dialog.findViewById(R.id.dialog_location_x);
		et_left.setText(String.valueOf(left));
		final EditText et_top=(EditText)dialog.findViewById(R.id.dialog_location_y);
		et_top.setText(String.valueOf(top));

		negativeButton.requestFocus();  
		negativeButton.setFocusable(true);
		
		((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Bundle bundle=new Bundle();
		    	bundle.putInt("L", Integer.parseInt(et_left.getText().toString()));
		    	bundle.putInt("T", Integer.parseInt(et_top.getText().toString()));
				callback.callback(bundle);
				dialog.dismiss();
		    }
		});
		
		return dialog;
	}
}
