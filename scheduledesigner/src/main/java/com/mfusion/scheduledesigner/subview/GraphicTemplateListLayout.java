package com.mfusion.scheduledesigner.subview;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.scheduledesigner.R;
import com.mfusion.scheduledesigner.values.ButtonHoverStyle;
import com.mfusion.scheduledesigner.values.ScaleDragShadowBuilder;
import com.mfusion.scheduledesigner.values.TemplateThumbAdapter;

import java.util.List;

/**
 * Created by Guoyu on 2016/7/13.
 */
public class GraphicTemplateListLayout extends LinearLayout{

    private GraphicTemplateListLayout owner;

    private List<VisualTemplate> template_list;

    private Context context;

    private LinearLayout templates_container;

    private GridView template_grid;

    private ImageButton btn_refresh;

    private ImageView loading_imageview;

    public GraphicTemplateListLayout(Context context) {
        super(context);
        this.initView(context);
    }

    public GraphicTemplateListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public GraphicTemplateListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context){
        this.context=context;
        this.owner=this;

        templates_container =(LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_graphic_template_list, this,true);
        loading_imageview=(ImageView) templates_container.findViewById(R.id.sche_temp_load_image);
        template_grid=(GridView)templates_container.findViewById(R.id.sche_temp_gv);
        template_grid.setVisibility(GONE);

        btn_refresh=(ImageButton)templates_container.findViewById(R.id.sche_temp_refresh_btn);
        btn_refresh.setEnabled(false);
        btn_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingTemplates();
            }
        });
        ButtonHoverStyle.bindingHoverEffect(btn_refresh,getResources());

        bindingTemplates();
    }

    public void bindingTemplates(){
        template_grid.setVisibility(GONE);
        LoadingAsyncTask async=new LoadingAsyncTask(this.context,template_grid);
        async.execute("");

        /*this.getAllTemplates();

        template_grid.setAdapter(new FileThumbAdapter(this.context,this.template_list));*/
    }

    /*private void getAllTemplates(){
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
    }*/

    class LoadingAsyncTask extends AsyncTask<String, Integer, String> {

        Context context;

        GridView gridView;

        public LoadingAsyncTask(Context context,GridView view){
            this.context=context;
            this.gridView=view;
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            try {
                this.getAllTemplates();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                gridView.setAdapter(new TemplateThumbAdapter(this.context,owner,template_list));
                gridView.setVisibility(VISIBLE);
                btn_refresh.setEnabled(true);
                super.cancel(true);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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

        public void cancelTask(){
            try {
                super.cancel(true);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
}
