package com.mfusion.ninjaplayer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.exception.TemplateNotFoundException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSelectType;
import com.mfusion.commons.entity.values.FileSortType;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.FileNameTextWatcher;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.FileNameEditor;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.OpenFileDialog;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.commons.view.adapter.TemplateInfoAdapter;
import com.mfusion.ninjaplayer.R;

import java.util.ArrayList;
import java.util.List;


public class TemplateUserCreatedView extends LinearLayout{
    private static final String TAG = "TemplateUserCreatedView";
    
    private GridView gridView;
    private TemplateInfoAdapter templateInfoAdapter;

    private TextView m_info_view;

    private ImageView loadingView;

    private CheckBox cb_select_all;
    private ImageTextVerticalView btn_new,btn_import,btn_delete,btn_export,btn_edit,btn_copy,btn_rename;
    private ImageTextHorizontalView btn_sort_name,btn_sort_time;

    private View userCreatedView;
    private Context context;
    private TemplateFragmentListener listener;

    private ProgressDialog loadingPage;
    private TemplateLoadingAsyncTask loadingTask;

    ArrayList<VisualTemplate> visualTemplates = null;
    public TemplateUserCreatedView(Context context) {
        this(context,null);
    }

    public TemplateUserCreatedView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userCreatedView = inflater.inflate(R.layout.fragment_template_user_created_view,this,false);
        m_info_view = (TextView) userCreatedView.findViewById(R.id.template_listinfo);
        m_info_view.setText("Template List ( Less than "+InternalKeyWords.MaxTemplateCount+" )");
        loadingView = (ImageView) userCreatedView.findViewById(R.id.template_load_image);
        gridView = (GridView) userCreatedView.findViewById(R.id.template_list_user_created_grid_view);
        gridView.setVisibility(View.VISIBLE);
        btn_new=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_new);
        btn_new.setText("New");
        btn_new.setImage(R.drawable.mf_add);
        btn_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goSampleView();
            }
        });
        btn_import=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_import);
        btn_import.setText("Import");
        btn_import.setImage(R.drawable.mf_import);
        btn_import.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = OpenFileDialog.createDialog(context, "Select a impoer zip file", new CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        try {
                            if(bundle==null)
                                return;

                            importTemplates(bundle.getStringArrayList("selectedFiles"));
                        }catch (Exception ex){
                            ex.printStackTrace();
                            AlertDialogHelper.showWarningDialog(context, "Warning", ex.getMessage(), null);
                        }
                    }
                },"zip",true,true);
            }
        });
        btn_delete=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_delete);
        btn_delete.setText("Delete");
        btn_delete.setImage(R.drawable.mf_trash);
        btn_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTemplates(templateInfoAdapter.getSelectTemplateEntities());
            }
        });
        btn_export=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_export);
        btn_export.setText("Export");
        btn_export.setImage(R.drawable.mf_export);
        btn_export.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exportTemplates(templateInfoAdapter.getSelectTemplates());
            }
        });
        btn_edit=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_edit);
        btn_edit.setText("Edit");
        btn_edit.setImage(R.drawable.mf_edit);
        btn_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editTemplate(templateInfoAdapter.getSelectTemplateEntities());
            }
        });
        btn_rename=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_rename);
        btn_rename.setText("Rename");
        btn_rename.setImage(R.drawable.mf_rename);
        btn_rename.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                renameTemplate(templateInfoAdapter.getSelectTemplateEntities());
            }
        });
        btn_copy=(ImageTextVerticalView)userCreatedView.findViewById(R.id.template_copy);
        btn_copy.setText("Save As");
        btn_copy.setImage(R.drawable.mf_save);
        btn_copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTemplate(templateInfoAdapter.getSelectTemplateEntities());
            }
        });

        cb_select_all=(CheckBox) userCreatedView.findViewById(R.id.template_select_all);
        cb_select_all.setVisibility(GONE);
        cb_select_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(templateInfoAdapter!=null)
                    templateInfoAdapter.selectAllItem(cb_select_all.isChecked());
            }
        });

        btn_sort_name=(ImageTextHorizontalView)userCreatedView.findViewById(R.id.template_sort_name);
        btn_sort_name.setVisibility(GONE);
        btn_sort_name.setText("Name");
        btn_sort_name.setImage(R.drawable.sort_name_asce);
        btn_sort_name.setTag(FileSortType.NameAsce);
        btn_sort_name.setOnClickListener(sortTypeListener);
        btn_sort_time=(ImageTextHorizontalView)userCreatedView.findViewById(R.id.template_sort_time);
        btn_sort_time.setVisibility(GONE);
        btn_sort_time.setText("Modify Time");
        btn_sort_time.setImage(R.drawable.sort_time_desc);
        btn_sort_time.setTag(FileSortType.TimeDesc);
        btn_sort_time.setOnClickListener(sortTypeListener);

        addView(userCreatedView);
    }
    public void loadingDatas(){
        try {
            //gridView.setVisibility(View.GONE);

            refreshOperatorBtn(false);

            cb_select_all.setChecked(false);
            loadingPage = ProgressDialog.show(getContext(), null, "Loading...");
            loadingPage.show();
            if(loadingTask!=null)
                loadingTask.cancelTask();

            loadingTask=new TemplateLoadingAsyncTask();
            loadingTask.execute("");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(loadingPage!=null&&loadingPage.isShowing()) {
                loadingPage.dismiss();
            }
            loadingPage=null;
        }
    };

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }

    private void refreshOperatorBtn(Boolean enable){
        Boolean buttonEnable=enable;
        if(enable&&visualTemplates!=null&&visualTemplates.size()>= InternalKeyWords.MaxTemplateCount)
            buttonEnable=false;

        btn_new.setEnabled(buttonEnable);
        btn_import.setEnabled(buttonEnable);
        btn_copy.setEnabled(buttonEnable);

        btn_delete.setEnabled(false);
        btn_edit.setEnabled(false);
        btn_export.setEnabled(false);
        btn_rename.setEnabled(false);
    }
    OnClickListener sortTypeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            FileSortType sortType=(FileSortType) view.getTag();
            if(sortType==FileSortType.NameAsce){
                ((ImageTextVerticalView)view).setImage(R.drawable.sort_name_desc);
                sortType=FileSortType.NameDesc;
            }else if(sortType==FileSortType.NameDesc){
                ((ImageTextVerticalView)view).setImage(R.drawable.sort_name_asce);
                sortType=FileSortType.NameAsce;
            }else if(sortType==FileSortType.TimeAsce){
                ((ImageTextVerticalView)view).setImage(R.drawable.sort_time_desc);
                sortType=FileSortType.TimeDesc;
            }else if(sortType==FileSortType.TimeDesc){
                ((ImageTextVerticalView)view).setImage(R.drawable.sort_time_asce);
                sortType=FileSortType.TimeAsce;
            }

            if(visualTemplates==null)
                return;

            view.setTag(sortType);
            if(sortType==FileSortType.NameAsce||sortType==FileSortType.NameDesc) {
                btn_sort_name.setSelected(true);
                btn_sort_time.setSelected(false);
                FileOperator.orderByName(visualTemplates, (FileSortType) btn_sort_name.getTag());
            }
            if(sortType==FileSortType.TimeAsce||sortType==FileSortType.TimeDesc) {
                btn_sort_time.setSelected(true);
                btn_sort_name.setSelected(false);
                FileOperator.orderByDate(visualTemplates, (FileSortType) btn_sort_time.getTag());
            }
            templateInfoAdapter.notifyDataSetChanged();
        }
    };

    CallbackBundle openCallback =new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {
            int position=bundle.getInt("position");
            VisualTemplate visualTemplate = (VisualTemplate) gridView.getItemAtPosition(position);
            listener.goDesigner(visualTemplate);
        }
    };

    CallbackBundle deleteCallback =new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {
            if(bundle==null)
                return;

            try {
                int position=bundle.getInt("position");
                VisualTemplate visualTemplate = (VisualTemplate) gridView.getItemAtPosition(position);
                List<VisualTemplate> delete_list=new ArrayList<>();
                delete_list.add(visualTemplate);
                deleteTemplates(delete_list);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };

    CallbackBundle exportCallback =new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {

            final Dialog dialog = OpenFileDialog.createDialog(context, "Select a export folder", new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            try {
                                if(bundle==null)
                                    return;
                                int position=bundle.getInt("position");
                                VisualTemplate visualTemplate = (VisualTemplate) gridView.getItemAtPosition(position);
                                ArrayList<String> select_list=new ArrayList<String>();
                                select_list.add(visualTemplate.id);
                                exportTemplates(select_list);

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    },null,true,true,true);

        }
    };

    private void editTemplate(List<VisualTemplate> select_list){
        if(select_list==null||select_list.size()==0)
            return;
        listener.goDesigner(select_list.get(0));
        if(templateInfoAdapter!=null)
            templateInfoAdapter.clearImageResource();
    }

    private void renameTemplate(final List<VisualTemplate> select_list){
        if(select_list==null||select_list.size()==0)
            return;
        FileNameEditor.createDialog(context, "Rename Template", "Please input name for this template",select_list.get(0).id, new OperateCallbackBundle() {
            @Override
            public void onConfim(String content) {
                try {
                    XMLTemplate.getInstance().renameTemplate(select_list.get(0).id,content);
                    loadingDatas();
                }catch (TemplateNotFoundException ex) {
                    ex.printStackTrace();
                    AlertDialogHelper.showWarningDialog(context, "Warning", "Template can't find", null);
                }catch (Exception ex) {
                    ex.printStackTrace();
                    AlertDialogHelper.showWarningDialog(context, "Warning", "Rename failed", null);
                    LogOperator.WriteLogfortxt("TemplateDesigningView==>addTemplate :"+ex.getMessage());
                }
            }

            @Override
            public void onCancel(String errorMsg) {

            }
        });
    }

    private void copyTemplate(final List<VisualTemplate> select_list){
        if(select_list==null||select_list.size()==0)
            return;

        FileNameEditor.createDialog(context, "Copy To New Template", "Please input name for this template", new OperateCallbackBundle() {
            @Override
            public void onConfim(String content) {
                try {
                    XMLTemplate.getInstance().copyTemplate(select_list.get(0).id,content);
                    loadingDatas();
                }catch (Exception ex){
                    ex.printStackTrace();

                    AlertDialogHelper.showWarningDialog(context, "Warning", ex.getMessage(), null);
                }
            }

            @Override
            public void onCancel(String errorMsg) {
            }
        });

    }

    private void importTemplates(List<String> zip_list){

        if(zip_list==null||zip_list.size()==0)
            return;

        int import_count=InternalKeyWords.MaxTemplateCount-visualTemplates.size();
        for (String zipPath : zip_list) {
            if(import_count<=0)
                break;
            XMLTemplate.getInstance().importTemplate(zipPath);
            import_count--;
        }
        loadingDatas();
    }

    private void deleteTemplates(final List<VisualTemplate> delete_list){
        if(delete_list==null)
            return;

        String message="Do you want to delete these "+delete_list.size()+" templates ?";
        if(delete_list.size()==1)
            message="Do you want to delete " + delete_list.get(0).id + " ?";

        AlertDialogHelper.showAlertDialog(context, "Information", message, new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                try {
                    for (VisualTemplate visualTemplate : delete_list) {
                        XMLTemplate.getInstance().deleteTemplate(visualTemplate.id);
                        visualTemplates.remove(visualTemplate);
                    }

                    templateInfoAdapter.notifyDataSetChanged();
                    refreshOperatorBtn(true);

                } catch (Exception ex) {
                    ex.printStackTrace();

                    AlertDialogHelper.showWarningDialog(context, "Warning", ex.getMessage(), null);
                }
            }
        }, null);
    }

    private void exportTemplates(final List<String> temp_list){

        if(temp_list==null||temp_list.size()==0)
            return;

        final Dialog dialog = OpenFileDialog.createDialog(context, "Select a export folder", new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                try {
                    if(bundle==null)
                        return;
                    ArrayList<String> selectFiles=bundle.getStringArrayList("selectedFiles");
                    if(selectFiles==null||selectFiles.size()==0)
                        return;

                    String exportPath=selectFiles.get(0);
                    for(String temp_id : temp_list)
                        XMLTemplate.getInstance().exportTemplate(temp_id, exportPath);

                    cb_select_all.setChecked(false);

                    AlertDialogHelper.showInformationDialog(context, "Information", "These templates export successfully .", null);

                    return;
                }catch (TemplateNotFoundException ex){
                    ex.printStackTrace();
                }catch (PathAccessException ex){
                    ex.printStackTrace();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                AlertDialogHelper.showWarningDialog(context, "Warning", "These templates export failed .", null);
            }
        },null,true,true,true);
    }

    class TemplateLoadingAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                visualTemplates = XMLTemplate.getInstance().getAllTemplates();

                FileOperator.orderByName(visualTemplates,FileSortType.NameAsce);
            } catch (Exception ex) {
                Log.e(TAG, "refresh: ",ex);
                LogOperator.WriteLogfortxt("TemplateUserCreatedView==>loadingTemplate :"+ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if(templateInfoAdapter!=null)
                templateInfoAdapter.clearImageResource();

            //templateInfoAdapter=new TemplateInfoAdapter(context, null, visualTemplates, openCallback,deleteCallback,exportCallback,null,true,cb_select_all);
            templateInfoAdapter=new TemplateInfoAdapter(context, null, visualTemplates, null,null,null,null, FileSelectType.SingleSelect);
            templateInfoAdapter.setOnSelectItemChangedListener(new TemplateInfoAdapter.OnSelectItemChangedListener() {
                @Override
                public void onSelectItemChange(String template, Boolean checked) {
                    btn_edit.setEnabled(checked);
                    btn_rename.setEnabled(checked);
                    btn_delete.setEnabled(checked);
                    if(visualTemplates!=null&&visualTemplates.size()< InternalKeyWords.MaxTemplateCount)
                        btn_copy.setEnabled(checked);
                    btn_export.setEnabled(checked);
                }
            });
            gridView.setAdapter(templateInfoAdapter);

            btn_sort_name.setImage(R.drawable.sort_name_asce);
            btn_sort_name.setTag(FileSortType.NameAsce);
            btn_sort_name.setSelected(true);
            btn_sort_time.setSelected(false);

            refreshOperatorBtn(true);
            
            /*try {
                this.wait(1000);
            }catch (Exception ex){
                ex.printStackTrace();
            }*/
            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);
        }

        public void cancelTask(){
            try {
                super.cancel(true);
                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
}
