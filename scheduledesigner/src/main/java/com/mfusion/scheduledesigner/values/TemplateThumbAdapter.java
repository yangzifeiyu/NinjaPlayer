package com.mfusion.scheduledesigner.values;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.view.ScaleDragShadowBuilder;
import com.mfusion.scheduledesigner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2016/7/27.
 */
public class TemplateThumbAdapter extends BaseAdapter {

    View parentView;

    Context context =null;

    List<VisualTemplate> temp_list=null;

    List<ImageView> imageViewList;

    int layout= R.layout.view_graphic_template_item;

    public TemplateThumbAdapter(Context context,View parent,List<VisualTemplate> temp_list) {

        this.context=context;
        this.parentView=parent;
        this.temp_list=temp_list;
        this.imageViewList=new ArrayList<ImageView>();
    }

    Boolean isPrepareDrag=false;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VisualTemplate temp_info = (VisualTemplate)this.getItem(position);
        convertView = LayoutInflater.from(context).inflate(layout, null);
        convertView.setTag(temp_info.id);

        ImageView imageView=(ImageView)convertView.findViewById(R.id.temp_img_thumb);
        imageViewList.add(imageView);
        imageView.setImageBitmap(temp_info.thumbImageBitmap);

        final TextView nameView=(TextView)convertView.findViewById(R.id.temp_tv_name);
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

        final ClipData.Item item = new ClipData.Item(temp_info.id);
        final ClipData data = new ClipData("move", new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }, item);

        convertView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        isPrepareDrag=true;
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        if(parentView.isEnabled()&&isPrepareDrag){
                            isPrepareDrag=false;
                            //调用startDrag方法，第二个参数为创建拖放阴影
                            view.startDrag(data, new ScaleDragShadowBuilder(view), null, 0);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:

                        isPrepareDrag=false;
                        return true;
                }
                return false;
            }
        });
        return convertView;
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
    }
}

