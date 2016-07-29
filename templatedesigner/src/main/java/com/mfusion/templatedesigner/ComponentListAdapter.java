package com.mfusion.templatedesigner;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 1B15182 on 22/7/2016 0022.
 */
public class ComponentListAdapter extends BaseAdapter {
    private static final String TAG = "ComponentListAdapter";
    private List<ComponentListItem> items;
    private Context context;

    static class ViewHolder{
        ImageView img;
        TextView name;
    }

    public ComponentListAdapter(Context context, List<ComponentListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.adapter_component_list_view,parent,false);
            vh.img= (ImageView) convertView.findViewById(R.id.adapter_component_list_view_img_icon);
            vh.name= (TextView) convertView.findViewById(R.id.adapter_component_list_view_tv_name);
            convertView.setTag(vh);
        }
        else
            vh= (ViewHolder) convertView.getTag();
        vh.img.setImageDrawable(context.getResources().getDrawable(items.get(position).getDrawableId()));
        vh.name.setText(items.get(position).getName());
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewHolder vh= (ViewHolder) v.getTag();
                Log.i(TAG, "onItemTouch: "+vh.name.getText());
                View.DragShadowBuilder builder=new View.DragShadowBuilder(vh.img);
                //clip data is the string of component type
                ClipData.Item item=new ClipData.Item(items.get(position).getType().toString());
                ClipData data=new ClipData("desc",new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},item);
                v.startDrag(data,builder,null,0);
                return false;
            }
        });
        return convertView;
    }
}
