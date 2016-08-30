package com.mfusion.templatedesigner.previewcomponent.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mfusion.commons.view.ImageTextView;
import com.mfusion.commons.view.OpenFileDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.adapter.MediaListAdapter;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.MediaInfoHelper;

public class MediaSelectDialog {

	private Context m_context;
	
	private ListView sche_medias;
	
	private List<ScheduleMediaEntity> mediaList;
	
	public  Dialog createDialog(Context context, final CallbackBundle callback, final List<ScheduleMediaEntity> mediaList, final String filters){
		this.m_context=context;
		this.mediaList=mediaList;
		
		Dialog sche_media_dialog = new Dialog(this.m_context);
		sche_media_dialog.setTitle("Media List");
		sche_media_dialog.setContentView(R.layout.dialog_medias);
		
		LinearLayout containerLayout=(LinearLayout)sche_media_dialog.findViewById(R.id.temp_sche_media_layout);

		sche_medias=(ListView)sche_media_dialog.findViewById(R.id.comp_comp_medias);
        this.bindMediaList();

		ImageTextView addView=(ImageTextView)sche_media_dialog.findViewById(R.id.comp_media_add);
		addView.setText("Add Medias");
		addView.setImage(R.drawable.mf_add);
		addView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	            Dialog dialog = OpenFileDialog.createDialog(m_context, "Select Medias", new CallbackBundle() {
	                @Override  
	                public void callback(Bundle bundle) {  
	                    ArrayList<String> medias=bundle.getStringArrayList("selectedFiles");
	                    if(medias!=null){
	                    	
	                    	for (String path : medias) {
	                    		ScheduleMediaEntity mediaItem= MediaInfoHelper.getMediaInfo(path);
	                    		if(mediaItem==null)
	                    			continue;
	                    		mediaList.add(mediaItem);
	                    		
								System.out.println(path);
							}
                    		bindMediaList();
	                    }
	                }  
	            },   
	            filters,true,false);

			}
		});
        
        sche_media_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				for(ScheduleMediaEntity mediaEntity: mediaList){
					mediaEntity.setStringDuration(mediaEntity.durationString);
				}

				callback.callback(null);
			}
		});

		WindowManager.LayoutParams params = sche_media_dialog.getWindow().getAttributes();
		params.width = m_context.getResources().getDisplayMetrics().widthPixels*3/5;
		params.height= m_context.getResources().getDisplayMetrics().heightPixels*3/4;
		sche_media_dialog.getWindow().setAttributes(params);

		containerLayout.setFocusable(true);
		containerLayout.setFocusableInTouchMode(true);

        sche_media_dialog.show();
        
        return sche_media_dialog;
	}
	
	private void bindMediaList() {
		
		this.sche_medias.setAdapter(new MediaListAdapter(this.m_context, this.mediaList, new OnClickListener() {
			
			@Override
			public void onClick(View deleteBtn) {
				// TODO Auto-generated method stub
				int index=Integer.parseInt(deleteBtn.getTag().toString());
				mediaList.remove(index);
				
				bindMediaList();
			}
		}));
		
	}
	
}
