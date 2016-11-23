package com.mfusion.templatedesigner.previewcomponent.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class MediaListAdapter  extends BaseAdapter {

	Context context =null;

	List<ScheduleMediaEntity> data=null;

	List<ScheduleMediaEntity> selected_data=null;

	int layout= R.layout.temp_comp_media_item;

	OnClickListener deleteListener;

	Boolean canSelected=false;

	int selected_count=0;

	SelectItemChangedListener selectItemChangedListener;

	List<CheckBox> selected_view_list;

	public MediaListAdapter(Context context,List<ScheduleMediaEntity> data,OnClickListener deleteListener,Boolean canSelected,List<ScheduleMediaEntity> select_medias) {

		this.context=context;
		this.data=data;
		this.canSelected=canSelected;
		this.selected_view_list=new ArrayList<>();
		this.selected_data=select_medias;

		this.deleteListener=deleteListener;
	}

    public MediaListAdapter(Context context,List<ScheduleMediaEntity> data,OnClickListener deleteListener) {
		
		this.context=context;
		this.data=data;
		this.selected_view_list=new ArrayList<>();
		
		this.deleteListener=deleteListener;
	}

	public void setOnSelectItemChangedListener(SelectItemChangedListener listener){
		this.selectItemChangedListener=listener;
	}
	@Override  
    public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView!=null)
			return convertView;

		for(int i=0;i<parent.getChildCount();i++)
			if(parent.getChildAt(i).getTag()==position)
				return parent.getChildAt(i);
		ScheduleMediaEntity item = (ScheduleMediaEntity)this.getItem(position);  
        convertView = LayoutInflater.from(context).inflate(layout, null);
		convertView.setTag(position);
		convertView.setClickable(true);
		convertView.setFocusable(true);
		convertView.setFocusableInTouchMode(true);

		final CheckBox selectView=(CheckBox) convertView.findViewById(R.id.pm_select);
		selectView.setVisibility(View.GONE);
		selectView.setTag(position);
		selected_view_list.add(selectView);
		if(canSelected){
			selectView.setVisibility(View.VISIBLE);
			selectView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					selected_count=selected_count+(isChecked?1:-1);
					if(selectItemChangedListener!=null)
						selectItemChangedListener.selectChanged(selected_count);
				}
			});

			convertView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_UP){
						selectView.setChecked(!selectView.isChecked());
					}
					return false;
				}
			});
			if(this.selected_data!=null&&this.selected_data.contains(item))
				selectView.setChecked(true);
		}

        ImageView typeImage=(ImageView)convertView.findViewById(R.id.pm_type);
        typeImage.setImageResource(FileOperator.convertTypeToImage(item.mediaType.toString()));
        
        ((TextView)convertView.findViewById(R.id.pm_name)).setText(item.mediaName+" ("+item.durationString+")");
        ((TextView)convertView.findViewById(R.id.pm_path)).setText(item.mediaPath);
        
        EditText duration=(EditText)convertView.findViewById(R.id.pm_duration);
        duration.setText(item.durationString);
        duration.addTextChangedListener(new EditChangedListener(duration,item));
        
        ImageButton deleteButton=(ImageButton)convertView.findViewById(R.id.pm_delete);
        deleteButton.setTag(position);
		deleteButton.setFocusable(true);
        deleteButton.setOnClickListener(this.deleteListener);
          
        return convertView;  
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}  

	public List<Integer> getSelectedList(){
		List<Integer> selected_position_list=new ArrayList<>();
		for (CheckBox checkBox : selected_view_list)
			if(checkBox.isChecked())
				selected_position_list.add(Integer.parseInt(checkBox.getTag().toString()));

		return  selected_position_list;
	}

	public List<ScheduleMediaEntity> getSelectedObjectList(){
		List<ScheduleMediaEntity> selected_list=new ArrayList<>();
		for (CheckBox checkBox : selected_view_list)
			if(checkBox.isChecked())
				selected_list.add((ScheduleMediaEntity) this.getItem(Integer.parseInt(checkBox.getTag().toString())));

		return  selected_list;
	}

	public void setSelect(Boolean isSelect){
		for (CheckBox checkBox : selected_view_list)
			checkBox.setChecked(isSelect);
	}

	public interface SelectItemChangedListener{
		void selectChanged(int selectCount);
	}
	public List<ScheduleMediaEntity> getSourceData() {
		return this.data;
	}

	class EditChangedListener implements TextWatcher {  
		
		EditText inpuText;
    	
    	String old_value="";
    	
    	ScheduleMediaEntity mediaEntity;
    	
    	public EditChangedListener(EditText view,ScheduleMediaEntity item){
    		this.inpuText=view;
    		this.mediaEntity=item;
    	}
       @Override  
       public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    	   this.old_value=s.toString();
       }  
  
       @Override  
       public void onTextChanged(CharSequence s, int start, int before, int count) {
    	   try {
    		   if(count<1)
    			   return;

        	   if(s.toString().length()>8){
        		   int selectIndex=this.inpuText.getSelectionStart();
        		   this.inpuText.setText(old_value);
       			   this.inpuText.setSelection(selectIndex-1);
       			   return;
        	   }
        	   
    		   String add_value=s.toString().substring(start,start+count).toLowerCase();
        	   if(add_value.equals("a")||add_value.equals("p")||add_value.equals("m")){
        		   int selectIndex=this.inpuText.getSelectionStart();
       			   this.inpuText.setText(old_value);
       			   this.inpuText.setSelection(selectIndex-count);
       			   return;
        	   }
        	   if(add_value.equals(":")){
        		   if (isSplitCharEnough(old_value)) {
        			   int selectIndex=this.inpuText.getSelectionStart();
           			   this.inpuText.setText(old_value);
           			   this.inpuText.setSelection(selectIndex-count);
           			   return;
        		   }
        	   }

        	   String beforeString=s.toString().substring(0,start+1);
        	   int index=beforeString.lastIndexOf(":");
        	   if((!isSplitCharEnough(s.toString()))&&((index<0&&start==1)||(index>=0&&(start-index)==2))){
        		   this.inpuText.setText(s.toString()+":");
       			   this.inpuText.setSelection(s.toString().length()+1);
       			   return;
        	   }
		   } catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
		   }
		   finally {
			   String[] times=inpuText.getText().toString().split(":");

			   int second=times.length>0&&!times[times.length-1].isEmpty()?Integer.parseInt(times[times.length-1]):0;
			   int  mins=times.length>1&&!times[times.length-2].isEmpty()?Integer.parseInt(times[times.length-2]):0;
			   int  hour=times.length>2&&!times[times.length-3].isEmpty()?Integer.parseInt(times[times.length-3]):0;

			   if(second>=60) {
				   mins+=second/60;
				   second = second % 60;
			   }if(mins>=60) {
				   hour+=mins/60;
				   mins = mins % 60;
			   }
			   this.mediaEntity.durationString =(hour>=10?"":"0")+hour+":"+(mins>=10?"":"0")+mins+":"+(second>=10?"":"0")+second;

		   }
       }  
  
       @Override  
       public void afterTextChanged(Editable s) {
       }  
       
       private Boolean isSplitCharEnough(String source) {
		   int split_count=0;
		   int search_index=-1;
		   while (search_index<=source.length()){
			   search_index=source.indexOf(":",search_index+1);
			   if(search_index<0)
				   break;
			   split_count++;
		   }

    	   if(split_count==2)
    		   return true;
    	   
    	   return false;
       }
   };  
}
