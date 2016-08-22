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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;

public class SizeDialog {

	public  Dialog createDialog(Context context,int width,int height, final CallbackBundle callback){
		Builder builder =new Builder(context).setTitle("Please input component size")
				.setView(((Activity)context).getLayoutInflater().inflate(R.layout.dialog_size, null))
				.setPositiveButton("Apply",null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface dialog, int which) {
						callback.callback(null);
					}  
				});
		builder.setCancelable(true);
		AlertDialog dialog =builder.show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���  
		Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
		final EditText et_width=(EditText)dialog.findViewById(R.id.dialog_size_width);
		et_width.setText(String.valueOf(width));
		final EditText et_height=(EditText)dialog.findViewById(R.id.dialog_size_height);
		et_height.setText(String.valueOf(height));

		negativeButton.requestFocus();  
		negativeButton.setFocusable(true);
		
		((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Bundle bundle=new Bundle();
		    	bundle.putInt("W", Integer.parseInt(et_width.getText().toString()));
		    	bundle.putInt("H", Integer.parseInt(et_height.getText().toString()));
				callback.callback(bundle);
		    }
		});
		
		return dialog;
	}
}
