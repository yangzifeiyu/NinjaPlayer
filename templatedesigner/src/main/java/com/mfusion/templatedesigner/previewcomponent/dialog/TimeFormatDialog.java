package com.mfusion.templatedesigner.previewcomponent.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.adapter.TimeFormatAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class TimeFormatDialog {

	String customise_format="Use Customise Format";

	public  Dialog createDialog(Context context,String format, final CallbackBundle callback){

		LinearLayout dialogContent=(LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_time_format, null);

		((ListView)dialogContent.findViewById(R.id.dialog_time_format_description)).setAdapter(new TimeFormatAdapter(context));
		final EditText format_view=(EditText)dialogContent.findViewById(R.id.dialog_time_format_content);
		WindowsDecorHelper.hideSoftInputInEditText(format_view);

		final LinkedHashMap<String,String> time_format_sample_map=new LinkedHashMap<>();
		time_format_sample_map.put(customise_format,"");
		time_format_sample_map.put(DateConverter.convertCurrentDateToStr("yyyy/MM/dd hh:mm a"),"yyyy/MM/dd hh:mm a");
		time_format_sample_map.put(DateConverter.convertCurrentDateToStr("yyyy/MM/dd HH:mm:ss"),"yyyy/MM/dd HH:mm:ss");
		time_format_sample_map.put(DateConverter.convertCurrentDateToStr("d MMM, hh:mm a"),"d MMM, hh:mm a");
		time_format_sample_map.put(DateConverter.convertCurrentDateToStr("MM/dd/yyyy"),"MM/dd/yyyy");
		time_format_sample_map.put(DateConverter.convertCurrentDateToStr("E,MMM d yyyy"),"E,MMM d yyyy");

		String[] sample_array=new String[time_format_sample_map.size()];
		time_format_sample_map.keySet().toArray(sample_array);

		final DropDownView sample_dropdown_view=(DropDownView)dialogContent.findViewById(R.id.dialog_time_format_sample);
		sample_dropdown_view.setSelectList( Arrays.asList(sample_array), DropDownView.DropDownLocationType.top);
		sample_dropdown_view.setText(time_format_sample_map.containsValue(format)?DateConverter.convertCurrentDateToStr(format):customise_format);

		final TextWatcher format_changed_watcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(!time_format_sample_map.containsValue(s.toString()))
					sample_dropdown_view.setText("Use Customise Format");
			}
		};

		sample_dropdown_view.setOnChangeListener(new DropDownView.OnSelectTextChangedListener() {
			@Override
			public void onSelectTextChange(String selectText) {
				try{
					String select_format=time_format_sample_map.get(selectText);
					if(!select_format.isEmpty())
						format_view.setText(select_format);
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});

		format_view.addTextChangedListener(format_changed_watcher);
		format_view.setText(format);

		SystemInfoDialog.Builder builder =new  SystemInfoDialog.Builder(context)
				.setTitle("Please select time format").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent)
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String input_format=format_view.getText().toString();
						if(input_format.isEmpty())
							return;
						Bundle bundle=new Bundle();
						bundle.putString("format",input_format);
						callback.callback(bundle);
						dialog.dismiss();
					}
				});

		SystemInfoDialog dialog =builder.create();
		dialog.show();
		
		return dialog;
	}


}
