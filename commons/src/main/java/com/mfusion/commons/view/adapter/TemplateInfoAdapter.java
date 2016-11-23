package com.mfusion.commons.view.adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSelectType;
import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.view.ScaleDragShadowBuilder;
import com.mfusion.commontools.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Guoyu on 2016/8/25.
 */
public class TemplateInfoAdapter  extends BaseAdapter {

    View parentView;

    Context context =null;

    List<VisualTemplate> temp_list=null;

    List<ImageView> imageViewList;

    List<CheckBox> checkBoxViewList;

    CheckBox pre_checked_box,current_checked_box;

    CheckBox checkBox_select_all;

    View selected_view;

    Boolean canDrag=false;

    FileSelectType item_select_type=FileSelectType.None;

    CallbackBundle openEventCall,deleteEventCall,exportEventCall,renameEventCall;

    ArrayList<VisualTemplate> selected_list;

    OnSelectItemChangedListener selectItemChangedListener;

    int layout= R.layout.template_thumb_view;

    public TemplateInfoAdapter(Context context,View parent,List<VisualTemplate> temp_list) {

        this.initAdapter(context,parent,temp_list);
    }

    public TemplateInfoAdapter(Context context,View parent,List<VisualTemplate> temp_list,Boolean canDrag) {

        this.canDrag=canDrag;
        this.initAdapter(context,parent,temp_list);
    }

    public TemplateInfoAdapter(Context context, View parent, List<VisualTemplate> temp_list, CallbackBundle openCallback, CallbackBundle deleteCallback, CallbackBundle exportCallback, CallbackBundle renameCallback,FileSelectType selectType) {

        this.item_select_type=selectType;
        this.openEventCall=openCallback;
        this.deleteEventCall=deleteCallback;
        this.exportEventCall=exportCallback;
        this.renameEventCall=renameCallback;
        this.initAdapter(context,parent,temp_list);
    }

    public TemplateInfoAdapter(Context context, View parent, List<VisualTemplate> temp_list, CallbackBundle openCallback, CallbackBundle deleteCallback, CallbackBundle exportCallback, CallbackBundle renameCallback, FileSelectType selectType, CheckBox select_all) {

        this.item_select_type=selectType;
        this.checkBox_select_all=select_all;
        this.openEventCall=openCallback;
        this.deleteEventCall=deleteCallback;
        this.exportEventCall=exportCallback;
        this.renameEventCall=renameCallback;
        this.initAdapter(context,parent,temp_list);
    }

    public void setOnSelectItemChangedListener(OnSelectItemChangedListener listener){
        this.selectItemChangedListener=listener;
    }

    private void initAdapter(Context context,View parent,List<VisualTemplate> temp_list){

        this.context=context;
        this.parentView=parent;
        this.temp_list=temp_list;
        this.imageViewList=new ArrayList<ImageView>();
        this.checkBoxViewList=new ArrayList<>();
        this.selected_list=new ArrayList<>();
    }

    Boolean isPrepareDrag=false;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final VisualTemplate temp_info = (VisualTemplate)this.getItem(position);
        convertView = LayoutInflater.from(context).inflate(layout, null);
        convertView.setTag(temp_info.id);

        final LinearLayout itemLayout=(LinearLayout) convertView.findViewById(R.id.template_thumb_item);
        itemLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch(what){
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setBackground(context.getResources().getDrawable(R.drawable.temp_thumb_border_hover_style));
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        if(selected_view!=v)
                            v.setBackground(context.getResources().getDrawable(R.drawable.temp_thumb_border_style));
                        break;
                }
                return false;
            }
        });
        final CheckBox selectBox=(CheckBox) convertView.findViewById(R.id.template_thumb_selecter);
        selectBox.setTag(temp_info.id);
        selectBox.setVisibility(View.GONE);

        final CheckBox single_selectBox=(CheckBox) convertView.findViewById(R.id.template_thumb_single_select);
        single_selectBox.setTag(temp_info.id);
        single_selectBox.setVisibility(View.GONE);

        ImageView imageView=(ImageView)convertView.findViewById(R.id.template_thumb_image);
        imageViewList.add(imageView);
        imageView.setImageBitmap(temp_info.thumbImageBitmap);

        final TextView nameView=(TextView)convertView.findViewById(R.id.template_thumb_name);
        nameView.setSingleLine(true);
        nameView.setText(temp_info.id);
        nameView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent event) {
                int what = event.getAction();
                switch(what){
                    case MotionEvent.ACTION_HOVER_ENTER:
                        nameView.setSingleLine(false);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        nameView.setSingleLine(true);
                        break;
                }
                return false;
            }
        });

        ImageButton delete_btn=(ImageButton)convertView.findViewById(R.id.template_thumb_delete);
        delete_btn.setVisibility(View.GONE);
        ImageButton export_btn=(ImageButton)convertView.findViewById(R.id.template_thumb_export);
        export_btn.setVisibility(View.GONE);
        ImageButton open_btn=(ImageButton)convertView.findViewById(R.id.template_thumb_open);
        open_btn.setVisibility(View.GONE);
        final ClipData.Item item = new ClipData.Item(temp_info.id);
        final ClipData data = new ClipData("move", new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }, item);
        convertView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if(canDrag)
                            isPrepareDrag=true;
                        if(selected_view!=null)
                            selected_view.setBackground(context.getResources().getDrawable(R.drawable.temp_thumb_border_style));
                        selected_view=itemLayout;
                        selected_view.setBackground(context.getResources().getDrawable(R.drawable.temp_thumb_border_hover_style));
                        //return true;
                    case MotionEvent.ACTION_MOVE:

                        if(canDrag&&parentView.isEnabled()&&isPrepareDrag){
                            isPrepareDrag=false;
                            //调用startDrag方法，第二个参数为创建拖放阴影
                            view.startDrag(data, new ScaleDragShadowBuilder(view), null, 0);
                        }
                        //return true;
                    case MotionEvent.ACTION_UP:
                        if(selectBox.getVisibility()==View.VISIBLE)
                            selectBox.setChecked(!selectBox.isChecked());
                        if(single_selectBox.getVisibility()==View.VISIBLE)
                            single_selectBox.setChecked(!single_selectBox.isChecked());
                        isPrepareDrag=false;
                        //return true;
                }
                return false;
            }
        });

        if(item_select_type==FileSelectType.MultiSelect){
            selectBox.setVisibility(View.VISIBLE);
            this.checkBoxViewList.add(selectBox);
            selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String temp_id=buttonView.getTag().toString();
                    if(isChecked){
                        if(!selected_list.contains(temp_info)){
                            selected_list.add(temp_info);
                        }
                        if(checkBox_select_all!=null&&getSelectTemplatesCount()==temp_list.size()){
                            checkBox_select_all.setChecked(isChecked);
                        }
                    }else {
                        if(checkBox_select_all!=null)
                            checkBox_select_all.setChecked(isChecked);
                        if(selected_list.contains(temp_info)){
                            selected_list.remove(temp_info);
                        }
                    }
                }
            });
        }
        if(item_select_type==FileSelectType.SingleSelect){

            this.checkBoxViewList.add(single_selectBox);
            single_selectBox.setVisibility(View.VISIBLE);
            single_selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String temp_id=buttonView.getTag().toString();
                    if(pre_checked_box!=buttonView||isChecked)
                        selected_list.clear();
                    if(isChecked){
                        selected_list.add(temp_info);
                        pre_checked_box=current_checked_box;
                        current_checked_box=(CheckBox) buttonView;
                        if(pre_checked_box!=null&&pre_checked_box!=current_checked_box)
                            pre_checked_box.setChecked(false);
                    }

                    if(pre_checked_box!=current_checked_box&&pre_checked_box==buttonView)
                        return;
                    if(selectItemChangedListener!=null)
                        selectItemChangedListener.onSelectItemChange(temp_id,isChecked);
                }
            });
        }

        if(openEventCall!=null){
            open_btn.setVisibility(View.VISIBLE);
            ButtonHoverStyle.bindingHoverEffect(open_btn,context.getResources());
            open_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackEvent(openEventCall,position);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callbackEvent(openEventCall,position);
                    return false;
                }
            });
        }

        if(this.deleteEventCall!=null){
            delete_btn.setVisibility(View.VISIBLE);
            ButtonHoverStyle.bindingHoverEffect(delete_btn,context.getResources());
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackEvent(deleteEventCall,position);
                }
            });
        }

        if(this.exportEventCall!=null){
            export_btn.setVisibility(View.VISIBLE);
            ButtonHoverStyle.bindingHoverEffect(export_btn,context.getResources());
            export_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackEvent(exportEventCall,position);
                }
            });
        }

        if(item_select_type!=FileSelectType.MultiSelect&&this.deleteEventCall==null&&this.exportEventCall==null)
            ((LinearLayout) convertView.findViewById(R.id.template_thumb_manager)).setVisibility(View.GONE);

        int name_padding_left=0,name_padding_right=0;
        /*if(item_select_type==FileSelectType.SingleSelect)
            name_padding_right=(int)context.getResources().getDimension(R.dimen.thumb_item_operator_size);
        if(openEventCall!=null)
            name_padding_left=(int)context.getResources().getDimension(R.dimen.thumb_item_btn_width)*/;
        nameView.setPadding(name_padding_left,0,name_padding_right,0);
        return convertView;
    }

    private void callbackEvent(CallbackBundle callEvent,int position){
        Bundle response=new Bundle();
        response.putInt("position",position);
        callEvent.callback(response);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(this.temp_list==null)
            return 0;
        return this.temp_list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return this.temp_list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void clearImageResource(){
        for(ImageView imageView :imageViewList){
            ImageHelper.clearImageView(imageView);
        }
        imageViewList.clear();
        checkBoxViewList.clear();
    }

    public List<VisualTemplate> getSelectTemplateEntities(){
        return this.selected_list;
    }

    public int getSelectTemplatesCount(){
        int count=0;
        for(VisualTemplate visualTemplate :selected_list){
            count++;
        }
        return count;
    }

    public List<String> getSelectTemplates(){
        List<String> temp_is_list=new ArrayList<>();
        for(VisualTemplate visualTemplate :selected_list){
            temp_is_list.add(visualTemplate.id);
        }
        return temp_is_list;
    }

    public void selectAllItem(Boolean selected){
        for(CheckBox checkBox :checkBoxViewList){
            checkBox.setChecked(selected);
        }
    }

    public interface OnSelectItemChangedListener{
        public void onSelectItemChange(String template,Boolean checked);
    }

}
