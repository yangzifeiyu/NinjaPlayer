package com.mfusion.ninjaplayer.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.adapter.TemplateGridViewAdapter;

import java.util.ArrayList;



public class TemplateUserCreatedView extends LinearLayout{
    private static final String TAG = "TemplateUserCreatedView";
    
    private GridView gridView;

    private ImageView loadingView;

    private ImageTextView btnNew;

    private View userCreatedView;
    private Context context;
    private TemplateFragmentListener listener;

    private TemplateLoadingAsyncTask loadingTask;

    public TemplateUserCreatedView(Context context) {
        this(context,null);
    }

    public TemplateUserCreatedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userCreatedView = inflater.inflate(R.layout.fragment_template_user_created_view,this,false);
        loadingView = (ImageView) userCreatedView.findViewById(R.id.template_load_image);
        gridView = (GridView) userCreatedView.findViewById(R.id.template_list_user_created_grid_view);
        btnNew=(ImageTextView)userCreatedView.findViewById(R.id.template_new);
        btnNew.setText("New");
        btnNew.setImage(R.drawable.mf_add);
        btnNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goSampleView();
            }
        });

        loadingDatas();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                VisualTemplate visualTemplate = (VisualTemplate) gridView.getItemAtPosition(position);
                listener.goDesigner(visualTemplate);
            }
        });

        addView(userCreatedView);
    }
    public void loadingDatas(){
        try {
            gridView.setVisibility(View.GONE);
            if(loadingTask!=null)
                loadingTask.cancelTask();

            loadingTask=new TemplateLoadingAsyncTask();
            loadingTask.execute("");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }

    class TemplateLoadingAsyncTask extends AsyncTask<String, Integer, String> {
        ArrayList<VisualTemplate> visualTemplates = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                visualTemplates = XMLTemplate.getInstance().getAllTemplates();
            } catch (Exception ex) {
                Log.e(TAG, "refresh: ",ex);
                LogOperator.WriteLogfortxt("TemplateUserCreatedView==>loadingTemplate :"+ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            TemplateGridViewAdapter adapter = new TemplateGridViewAdapter(context, visualTemplates);
            gridView.setAdapter(adapter);
            gridView.setVisibility(View.VISIBLE);
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
