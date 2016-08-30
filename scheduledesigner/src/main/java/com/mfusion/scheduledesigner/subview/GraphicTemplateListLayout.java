package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSortType;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.commons.view.adapter.TemplateInfoAdapter;
import com.mfusion.scheduledesigner.R;
import com.mfusion.commons.tools.ButtonHoverStyle;
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

    private TemplateInfoAdapter template_adapter;

    private ImageView loading_imageview;

    private ImageTextView btn_sort_name,btn_sort_time;

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

        btn_sort_name=(ImageTextView)templates_container.findViewById(R.id.sche_temp_sort_name);
        //btn_sort_name.setText("Name");
        btn_sort_name.setImage(R.drawable.sort_name_asce);
        btn_sort_name.setTag(FileSortType.NameAsce);
        btn_sort_name.setOnClickListener(sortTypeListener);
        btn_sort_time=(ImageTextView)templates_container.findViewById(R.id.sche_temp_sort_time);
        //btn_sort_time.setText("Modify Time");
        btn_sort_time.setImage(R.drawable.sort_time_desc);
        btn_sort_time.setTag(FileSortType.TimeDesc);
        btn_sort_time.setOnClickListener(sortTypeListener);
    }
    OnClickListener sortTypeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            FileSortType sortType = (FileSortType) view.getTag();
            if (sortType == FileSortType.NameAsce) {
                ((ImageTextView) view).setImage(R.drawable.sort_name_desc);
                sortType = FileSortType.NameDesc;
            } else if (sortType == FileSortType.NameDesc) {
                ((ImageTextView) view).setImage(R.drawable.sort_name_asce);
                sortType = FileSortType.NameAsce;
            } else if (sortType == FileSortType.TimeAsce) {
                ((ImageTextView) view).setImage(R.drawable.sort_time_desc);
                sortType = FileSortType.TimeDesc;
            } else if (sortType == FileSortType.TimeDesc) {
                ((ImageTextView) view).setImage(R.drawable.sort_time_asce);
                sortType = FileSortType.TimeAsce;
            }

            if (template_list == null)
                return;

            view.setTag(sortType);
            if (sortType == FileSortType.NameAsce || sortType == FileSortType.NameDesc) {
                btn_sort_name.setSelected(true);
                btn_sort_time.setSelected(false);
                FileOperator.orderByName(template_list, (FileSortType) btn_sort_name.getTag());
            }
            if (sortType == FileSortType.TimeAsce || sortType == FileSortType.TimeDesc) {
                btn_sort_time.setSelected(true);
                btn_sort_name.setSelected(false);
                FileOperator.orderByDate(template_list, (FileSortType) btn_sort_time.getTag());
            }
            template_adapter.notifyDataSetChanged();
        }
    };

    public void bindingTemplates(){
        template_grid.setVisibility(GONE);
        if(template_adapter!=null){
            template_adapter.clearImageResource();
            template_adapter=null;
        }
        if(template_list!=null){
            template_list.clear();
            template_list=null;
        }
        LoadingAsyncTask async=new LoadingAsyncTask(context,template_grid);
        async.execute("");
    }

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

                FileOperator.orderByName(template_list,FileSortType.NameAsce);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                if(template_adapter!=null)
                    template_adapter.clearImageResource();

                template_adapter=new TemplateInfoAdapter(this.context,owner,template_list,true);
                gridView.setAdapter(template_adapter);
                gridView.setVisibility(VISIBLE);

                btn_sort_name.setImage(R.drawable.sort_name_asce);
                btn_sort_name.setTag(FileSortType.NameAsce);
                btn_sort_name.setSelected(true);
                btn_sort_time.setSelected(false);

                super.cancel(true);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                LogOperator.WriteLogfortxt("GraphicTemplateListLayout==>onPostExecute :"+e.getMessage());
            }
        }

        private void getAllTemplates(){
            try{

                if(template_list!=null) {
                    for(VisualTemplate template:template_list){
                        ImageHelper.recycleBitmap(template.thumbImageBitmap);
                    }
                    template_list.clear();
                    System.gc();
                }
                template_list = XMLTemplate.getInstance().getAllTemplates();
            }catch (PathAccessException ex){
                ex.printStackTrace();
                LogOperator.WriteLogfortxt("GraphicTemplateListLayout==>getAllTemplates :"+ex.getMessage());
            }catch (Exception ex){
                ex.printStackTrace();
                LogOperator.WriteLogfortxt("GraphicTemplateListLayout==>getAllTemplates :"+ex.getMessage());
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
