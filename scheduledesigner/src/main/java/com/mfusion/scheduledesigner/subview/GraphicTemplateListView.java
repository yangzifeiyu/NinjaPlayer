package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.GridView;

import java.util.List;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.scheduledesigner.R;

/**
 * Created by Guoyu on 2016/7/13.
 */
public class GraphicTemplateListView extends GridView{

    private List<VisualTemplate> template_list;

    private Context context;

    private GridView owner;

    public GraphicTemplateListView(Context context) {
        super(context);
        this.context=context;
        this.owner=this;
    }

    public GraphicTemplateListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.owner=this;
    }

    public GraphicTemplateListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.owner=this;
    }

    public void bindingTemplates(){

        this.getAllTemplates();

        super.setAdapter(new FileThumbAdapter(this.context,this.template_list));
    }

    private void getAllTemplates(){
        try{

            if(template_list!=null) {
                for(VisualTemplate template:template_list){
                    template.thumbImageBitmap.recycle();
                }
                template_list.clear();
                System.gc();
            }
            template_list = XMLTemplate.getInstance().getAllTemplates();
        }catch (PathAccessException ex){
            ex.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class FileThumbAdapter  extends BaseAdapter {

        Context context =null;

        List<VisualTemplate> temp_list=null;

        int layout= R.layout.view_graphic_template_item;

        public FileThumbAdapter(Context context,List<VisualTemplate> temp_list) {

            this.context=context;

            this.temp_list=temp_list;
        }

        Boolean isPrepareDrag=false;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            VisualTemplate temp_info = (VisualTemplate)this.getItem(position);
            convertView = LayoutInflater.from(context).inflate(layout, null);
            convertView.setTag(temp_info.id);

            ((ImageView)convertView.findViewById(R.id.temp_img_thumb)).setImageBitmap(temp_info.thumbImageBitmap);

            final TextView nameView=(TextView)convertView.findViewById(R.id.temp_tv_name);
            nameView.setSingleLine(true);
            nameView.setText(temp_info.id);
            nameView.setOnHoverListener(new OnHoverListener() {
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

            convertView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    // TODO Auto-generated method stub
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:

                            isPrepareDrag=true;
                            return true;
                        case MotionEvent.ACTION_MOVE:

                            if(owner.isEnabled()&&isPrepareDrag){
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
    }

    private static class ScaleDragShadowBuilder extends View.DragShadowBuilder {
        // 拖动阴影的图像， 作为一个drawable来定义
        private static Drawable shadow;

        int width;
        int height;
        // 构造函数
        public ScaleDragShadowBuilder(View v) {
            // 通过myDragShadowBuilder存储View参数
            super(v);
            // 创建一个可拖拽的图像，此图像可以通过系统的Canvas来填充
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        // 定义一个回调方法，将阴影的维度和触摸点返回给系统
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            // 定义当地的变量
            // 设置阴影的宽度为视图一半
            width = getView().getWidth() / 2;
            // 设置阴影的高度为视图一半
            height = getView().getHeight() / 2;

            super.onProvideShadowMetrics(size,touch);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.translate(width / 2, height / 2);
            canvas.scale(0.5f,0.5f);
            super.onDrawShadow(canvas);
        }
    }
}
