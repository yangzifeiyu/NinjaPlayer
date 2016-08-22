package com.mfusion.ninjaplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;

import java.util.ArrayList;


public class TemplateGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<VisualTemplate> visualTemplates;

    public TemplateGridViewAdapter(Context context, ArrayList<VisualTemplate> visualTemplates) {
        this.context = context;
        this.visualTemplates = visualTemplates;
    }

    @Override
    public int getCount() {
        return visualTemplates.size();
    }

    @Override
    public Object getItem(int position) {
        return visualTemplates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.adapter_template_grid_view,parent,false);
            vh.img=(ImageView)convertView.findViewById(R.id.adapter_template_grid_view_img);
            vh.tvId=(TextView)convertView.findViewById(R.id.adapter_template_grid_view_text);
            convertView.setTag(vh);
        }
        else
            vh= (ViewHolder) convertView.getTag();

        VisualTemplate template=visualTemplates.get(position);
        vh.img.setImageBitmap(template.thumbImageBitmap);
        vh.tvId.setText(template.id);
        return convertView;
    }
    static class ViewHolder{
        ImageView img;
        TextView tvId;
    }
}
