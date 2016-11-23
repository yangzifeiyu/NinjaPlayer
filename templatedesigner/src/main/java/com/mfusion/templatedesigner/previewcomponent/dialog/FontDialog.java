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
import android.widget.LinearLayout;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.ComponentFont;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;


public class FontDialog {
	public  Dialog createDialog(Context context, ComponentFont fontEntity, final CallbackBundle callback){

		LinearLayout dialogContent=(LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_font, null);
		final DropDownView ddv_family=(DropDownView)dialogContent.findViewById(R.id.dialog_font_family);
		ddv_family.setText(fontEntity.getFamilyString());
		ddv_family.setSelectList(PropertyValues.getFontFamilyList());
		
		final DropDownView ddv_style=(DropDownView)dialogContent.findViewById(R.id.dialog_font_style);
		ddv_style.setText(fontEntity.getStyleString());
		ddv_style.setSelectList(PropertyValues.getFontStyleList());
		
		final EditText et_size=(EditText)dialogContent.findViewById(R.id.dialog_font_size);
		et_size.setText(String.valueOf(fontEntity.size));
		WindowsDecorHelper.hideSoftInputInEditText(et_size);

		SystemInfoDialog.Builder builder =new SystemInfoDialog.Builder(context)
				.setTitle("Please set font info.").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent)
				.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Bundle bundle=new Bundle();
						if(et_size.getText().toString().isEmpty()||Float.valueOf(et_size.getText().toString())<=0){
							et_size.setText("");
							return;
						}
						bundle.putString("Family", ddv_family.getText().toString());
						bundle.putString("Style", ddv_style.getText().toString());
						bundle.putFloat("Size", Float.valueOf(et_size.getText().toString()));
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
