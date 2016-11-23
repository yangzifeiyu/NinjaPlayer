package com.mfusion.templatedesigner.previewcomponent.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mfusion.commons.tools.AsyncMediaThumbTask;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.OpenFileDialog;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.adapter.MediaListAdapter;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.MediaInfoHelper;
import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaType;

public class MediaSelectDialog {

	private Context m_context;
	
	private ListView sche_medias;
	
	private List<ScheduleMediaEntity> mediaList;

	CheckBox media_select_box;

	ImageTextHorizontalView media_delete_btn,media_move_top_btn,media_move_up_btn,media_move_down_btn,media_move_bottom_btn;
	
	public  Dialog createDialog(Context context, final CallbackBundle callback, final List<ScheduleMediaEntity> mediaList, final String filters){
		this.m_context=context;
		this.mediaList=mediaList;

		LinearLayout dialogContent=(LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_medias, null);

		LinearLayout containerLayout=(LinearLayout)dialogContent.findViewById(R.id.temp_sche_media_layout);

		sche_medias=(ListView)dialogContent.findViewById(R.id.comp_comp_medias);

		media_select_box=(CheckBox)dialogContent.findViewById(R.id.comp_media_select);
		media_delete_btn=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_delete);
		media_delete_btn.setImage(R.drawable.mf_rubbish);
		media_move_top_btn=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_move_top);
		media_move_top_btn.setImage(R.drawable.mf_top);
		media_move_up_btn=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_move_up);
		media_move_up_btn.setImage(R.drawable.mf_up);
		media_move_down_btn=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_move_down);
		media_move_down_btn.setImage(R.drawable.mf_down);
		media_move_bottom_btn=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_move_bottom);
		media_move_bottom_btn.setImage(R.drawable.mf_bottom);
		media_select_box.setOnCheckedChangeListener(all_select_listener);
		media_delete_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMedias(((MediaListAdapter)sche_medias.getAdapter()).getSelectedList());
			}
		});
		media_move_top_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveMediasTop(((MediaListAdapter)sche_medias.getAdapter()).getSelectedList());
			}
		});
		media_move_up_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveMediasUp(((MediaListAdapter)sche_medias.getAdapter()).getSelectedList());
			}
		});
		media_move_down_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveMediasDown(((MediaListAdapter)sche_medias.getAdapter()).getSelectedList());
			}
		});
		media_move_bottom_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moveMediasBottom(((MediaListAdapter)sche_medias.getAdapter()).getSelectedList());
			}
		});
		ImageTextHorizontalView addView=(ImageTextHorizontalView)dialogContent.findViewById(R.id.comp_media_add);
		addView.setText("Add Medias");
		//addView.setImage(R.drawable.mf_add);
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

								if(mediaItem.mediaType== ScheduleMediaType.Video) {
									AsyncMediaThumbTask thumbTask = new AsyncMediaThumbTask(m_context, mediaItem.mediaPath);
									thumbTask.execute("");
								}
	                    		mediaList.add(mediaItem);
	                    		
								System.out.println(path);
							}
                    		bindMediaList(((MediaListAdapter)sche_medias.getAdapter()).getSelectedObjectList());
	                    }
	                }  
	            },   
	            filters,true,false);

			}
		});

		this.bindMediaList(null);

		SystemInfoDialog.Builder builder =new  SystemInfoDialog.Builder(context)
				.setTitle("Media List").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent);

		Dialog sche_media_dialog=builder.create();

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
		params.width =m_context.getResources().getDisplayMetrics().widthPixels>m_context.getResources().getDisplayMetrics().heightPixels? m_context.getResources().getDisplayMetrics().widthPixels*3/5:m_context.getResources().getDisplayMetrics().widthPixels*4/5;
		params.height= m_context.getResources().getDisplayMetrics().heightPixels*3/4;
		sche_media_dialog.getWindow().setAttributes(params);

		containerLayout.setFocusable(true);
		containerLayout.setFocusableInTouchMode(true);

        sche_media_dialog.show();
        
        return sche_media_dialog;
	}
	
	private void bindMediaList(List<ScheduleMediaEntity> select_medias) {

		MediaListAdapter mediaListAdapter=new MediaListAdapter(this.m_context, this.mediaList, new OnClickListener() {

			@Override
			public void onClick(View deleteBtn) {
				// TODO Auto-generated method stub
				int index=Integer.parseInt(deleteBtn.getTag().toString());
				mediaList.remove(index);

				bindMediaList(((MediaListAdapter)sche_medias.getAdapter()).getSelectedObjectList());
			}
		},true,select_medias);
		mediaListAdapter.setOnSelectItemChangedListener(new MediaListAdapter.SelectItemChangedListener() {
			@Override
			public void selectChanged(int selectCount) {
				media_select_box.setOnCheckedChangeListener(null);
				setEnableForMediaTools(selectCount>0?true:false);
				media_select_box.setChecked(selectCount<mediaList.size()?false:true);
				media_select_box.setOnCheckedChangeListener(all_select_listener);
			}
		});
		this.sche_medias.setAdapter(mediaListAdapter);
		setEnableForMediaTools(false);
	}

	CompoundButton.OnCheckedChangeListener all_select_listener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			((MediaListAdapter)sche_medias.getAdapter()).setSelect(isChecked);
		}
	};

	private void setEnableForMediaTools(Boolean enable){
		media_delete_btn.setEnabled(enable);
		media_move_top_btn.setEnabled(enable);
		media_move_up_btn.setEnabled(enable);
		media_move_down_btn.setEnabled(enable);
		media_move_bottom_btn.setEnabled(enable);
	}

	private void removeMedias(List<Integer> selectedList){
		for (int index=selectedList.size()-1;index>=0;index--){
			mediaList.remove(((MediaListAdapter)sche_medias.getAdapter()).getItem(index));
		}
		bindMediaList(null);
		media_select_box.setChecked(false);
	}
	private void moveMediasUp(List<Integer> selectedList){
		List<ScheduleMediaEntity> select_medias=new ArrayList<>();
		int pre_select_item_position=-1;
		for (Integer position : selectedList){
			ScheduleMediaEntity mediaEntity=(ScheduleMediaEntity)((MediaListAdapter)sche_medias.getAdapter()).getItem(position);
			if((position-1)!=pre_select_item_position) {
				this.mediaList.remove(mediaEntity);
				this.mediaList.add(position-1,mediaEntity);
				pre_select_item_position=position-1;
			}else
				pre_select_item_position=position;
			select_medias.add(mediaEntity);
		}
		bindMediaList(select_medias);
	}
	private void moveMediasTop(List<Integer> selectedList){
		List<ScheduleMediaEntity> select_medias=new ArrayList<>();
		int insert_index=0;
		for (Integer position : selectedList){
			ScheduleMediaEntity mediaEntity=(ScheduleMediaEntity)((MediaListAdapter)sche_medias.getAdapter()).getItem(position);
			if(position!=insert_index) {
				this.mediaList.remove(mediaEntity);
				this.mediaList.add(insert_index,mediaEntity);
			}
			select_medias.add(mediaEntity);
			insert_index++;
		}
		bindMediaList(select_medias);
	}
	private void moveMediasDown(List<Integer> selectedList){
		List<ScheduleMediaEntity> select_medias=new ArrayList<>();
		int pre_select_item_position=this.mediaList.size();
		for (int index=selectedList.size()-1;index>=0;index--){
			int position=selectedList.get(index);
			ScheduleMediaEntity mediaEntity=(ScheduleMediaEntity)((MediaListAdapter)sche_medias.getAdapter()).getItem(position);
			if((position+1)!=pre_select_item_position) {
				this.mediaList.remove(mediaEntity);
				this.mediaList.add(position+1,mediaEntity);
				pre_select_item_position=position+1;
			}else pre_select_item_position=position;
			select_medias.add(mediaEntity);
		}
		bindMediaList(select_medias);
	}
	private void moveMediasBottom(List<Integer> selectedList){
		List<ScheduleMediaEntity> select_medias=new ArrayList<>();
		int insert_index=this.mediaList.size()-1;
		for (int index=selectedList.size()-1;index>=0;index--){
			int position=selectedList.get(index);
			ScheduleMediaEntity mediaEntity=(ScheduleMediaEntity)((MediaListAdapter)sche_medias.getAdapter()).getItem(position);
			if(position!=insert_index) {
				this.mediaList.remove(mediaEntity);
				this.mediaList.add(insert_index,mediaEntity);
			}
			select_medias.add(mediaEntity);
			insert_index--;
		}
		bindMediaList(select_medias);
	}
}
