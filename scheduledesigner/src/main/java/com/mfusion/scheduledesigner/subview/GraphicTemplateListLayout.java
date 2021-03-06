package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSortType;
import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.adapter.TemplateInfoAdapter;
import com.mfusion.scheduledesigner.R;
import com.mfusion.scheduledesigner.values.TemplateThumbAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guoyu on 2016/7/13.
 */
public class GraphicTemplateListLayout extends LinearLayout{

    private GraphicTemplateListLayout owner;

    private List<VisualTemplate> template_list;

    private CallbackBundle loadFinishCall;

    private Context context;

    private LinearLayout templates_container;

    private GridView template_grid;

    private ImageButton btn_scroll_up,btn_scroll_down;
    
    private int current_position;

    private TemplateThumbAdapter template_adapter;

    private ImageView loading_imageview;

    private ImageTextHorizontalView btn_sort_name,btn_sort_time;

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

        btn_sort_name=(ImageTextHorizontalView)templates_container.findViewById(R.id.sche_temp_sort_name);
        btn_sort_name.setImage(R.drawable.sort_name_asce);
        btn_sort_name.setTag(FileSortType.NameAsce);
        btn_sort_name.setOnClickListener(sortTypeListener);
        btn_sort_name.setVisibility(GONE);
        btn_sort_time=(ImageTextHorizontalView)templates_container.findViewById(R.id.sche_temp_sort_time);
        btn_sort_time.setImage(R.drawable.sort_time_desc);
        btn_sort_time.setTag(FileSortType.TimeDesc);
        btn_sort_time.setOnClickListener(sortTypeListener);
        btn_sort_time.setVisibility(GONE);

        btn_scroll_up=(ImageButton)templates_container.findViewById(R.id.sche_temp_scroll_up);
        btn_scroll_down=(ImageButton)templates_container.findViewById(R.id.sche_temp_scroll_down);
        btn_scroll_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScrollPosition(-1);
            }
        });
        btn_scroll_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScrollPosition(1);
            }
        });
        ButtonHoverStyle.bindingHoverEffect(btn_scroll_up,context.getResources());
        ButtonHoverStyle.bindingHoverEffect(btn_scroll_down,context.getResources());
    }

    private void changeScrollPosition(int move){
        if(template_list==null||template_list.size()==0)
            return;

        current_position = template_grid.getFirstVisiblePosition()+move;
        if(current_position<0)
            current_position=0;
        if(current_position>=template_list.size())
            current_position=template_list.size()-1;
        template_grid.setSelection(current_position);
    }

    OnClickListener sortTypeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            FileSortType sortType = (FileSortType) view.getTag();
            if (sortType == FileSortType.NameAsce) {
                ((ImageTextVerticalView) view).setImage(R.drawable.sort_name_desc);
                sortType = FileSortType.NameDesc;
            } else if (sortType == FileSortType.NameDesc) {
                ((ImageTextVerticalView) view).setImage(R.drawable.sort_name_asce);
                sortType = FileSortType.NameAsce;
            } else if (sortType == FileSortType.TimeAsce) {
                ((ImageTextVerticalView) view).setImage(R.drawable.sort_time_desc);
                sortType = FileSortType.TimeDesc;
            } else if (sortType == FileSortType.TimeDesc) {
                ((ImageTextVerticalView) view).setImage(R.drawable.sort_time_asce);
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

    public void bindingTemplates(CallbackBundle loadFinish){
        template_grid.setVisibility(GONE);
        if(template_adapter!=null){
            template_adapter.clearImageResource();
            template_adapter=null;
        }
        if(template_list!=null){
            template_list.clear();
            template_list=null;
        }
        loadFinishCall=loadFinish;
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

                if(loadFinishCall!=null){
                    ArrayList<String> template_id_list=new ArrayList<>();
                    for(VisualTemplate visualTemplate : template_list){
                        template_id_list.add(visualTemplate.id);
                    }
                    Bundle result=new Bundle();
                    result.putStringArrayList("temp_list",template_id_list);
                    loadFinishCall.callback(result);
                }

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

                template_adapter=new TemplateThumbAdapter(this.context,owner,template_list);
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
                    //System.gc();
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
