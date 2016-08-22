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
import com.mfusion.templatedesigner.previewcomponent.subview.DropDownView;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.ComponentFont;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;


public class FontDialog {
	public  Dialog createDialog(Context context, ComponentFont fontEntity, final CallbackBundle callback){
		Builder builder =new Builder(context).setTitle("Please input component size")
				.setView(((Activity)context).getLayoutInflater().inflate(R.layout.dialog_font, null))
				.setPositiveButton("Apply",null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface dialog, int which) {
						callback.callback(null);
					}  
				});
		builder.setCancelable(true);
		final AlertDialog dialog =builder.show();
		Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
		
		final DropDownView ddv_family=(DropDownView)dialog.findViewById(R.id.dialog_font_family);
		ddv_family.setText(fontEntity.getFamilyString());
		ddv_family.setSelectList(PropertyValues.getFontFamilyList());
		
		final DropDownView ddv_style=(DropDownView)dialog.findViewById(R.id.dialog_font_style);
		ddv_style.setText(fontEntity.getStyleString());
		ddv_style.setSelectList(PropertyValues.getFontStyleList());
		
		final EditText et_size=(EditText)dialog.findViewById(R.id.dialog_font_size);
		et_size.setText(String.valueOf(fontEntity.size));

		negativeButton.requestFocus();  
		negativeButton.setFocusable(true);
		
		((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Bundle bundle=new Bundle();
		    	bundle.putString("Family", ddv_family.getText().toString());
		    	bundle.putString("Style", ddv_style.getText().toString());
		    	bundle.putFloat("Size", Float.valueOf(et_size.getText().toString()));
				callback.callback(bundle);
				
				dialog.dismiss();
		    }
		});

		return dialog;
	}
}
