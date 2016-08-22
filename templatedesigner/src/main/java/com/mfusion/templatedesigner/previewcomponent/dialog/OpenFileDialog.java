package com.mfusion.templatedesigner.previewcomponent.dialog;

import java.io.File;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

import android.R.bool;
import android.app.Activity;  
import android.app.AlertDialog;  
import android.app.Dialog;  
import android.content.Context;  
import android.content.DialogInterface;
import android.os.Bundle;  
import android.os.Environment;
import android.view.View;  
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.TextView;
import android.widget.Toast;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.RelativeLayout.LayoutParams;

import com.mfusion.commons.view.ImageTextView;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.adapter.FileSelectAdapter;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class OpenFileDialog {
	public static String tag = "OpenFileDialog";  
    static final public String sRoot = "/";   
    static final public String sParent = "...";  
    static final public String sFolder = ".";  
    static final public String sEmpty = "";  
    static final private String sOnErrorMsg = "No rights to access!";  
    
    public static Dialog createDialog(Context context, String title, final CallbackBundle callback, String suffix, final boolean mutilSelect){
        AlertDialog.Builder builder = new AlertDialog.Builder(context); 
        
        Dialog dialog = new Dialog(context);//builder.create();  
        dialog.setCancelable(true);
        dialog.setTitle(title);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        
        dialog.setContentView(R.layout.dialog_file_selecter);

        LinearLayout dialogLayout=(LinearLayout)dialog.findViewById(R.id.file_dialog);

        LinearLayout dialogContent=(LinearLayout)dialog.findViewById(R.id.file_container);
        
        final FileSelectView fileSelectView=new FileSelectView(context, dialog, callback, suffix,mutilSelect);
        
        dialogContent.addView(fileSelectView);

        ImageTextView addView=(ImageTextView)dialog.findViewById(R.id.btn_selectfiles);
        addView.setText("Add");
        addView.setImage(R.drawable.mf_add);

        if(mutilSelect){
            addView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("btn_selectfiles");
                    ArrayList<String> selectedFiles = fileSelectView.getSelectedFiles();
                    Bundle bundle=new Bundle();
                    bundle.putStringArrayList("selectedFiles", (ArrayList<String>) selectedFiles);
                    callback.callback(bundle);
                }
            });
        }else
            addView.setVisibility(View.GONE);

        
        /*dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				if(mutilSelect){
					ArrayList<String> selectedFiles = fileSelectView.getSelectedFiles();
					if(selectedFiles==null||selectedFiles.size()==0)
						return;
					
					Bundle bundle=new Bundle();
					bundle.putStringArrayList("selectedFiles", (ArrayList<String>) selectedFiles);
					callback.callback(bundle);
				}
				
			}
		});*/

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = context.getResources().getDisplayMetrics().widthPixels*2/5;
        params.height= context.getResources().getDisplayMetrics().heightPixels*2/3;
        dialog.getWindow().setAttributes(params);

        dialog.show();
        return dialog;  
    }  
      
    static class FileSelectView extends ListView implements OnItemClickListener{  
          
    	FileSelectAdapter adapter = null;
          
        private CallbackBundle callback = null;  
        private String path = sRoot;  
        private List<Map<String, Object>> list = null;  
        private Dialog dialog;  
          
        private String suffix = null;  

    	boolean mutilSelect=false;
    	
        public FileSelectView(Context context, Dialog dialog, CallbackBundle callback, String suffix,boolean mutilSelect) {  
            super(context);  
            
            this.suffix = suffix==null?"":suffix;  
            this.callback = callback;  
            this.dialog = dialog;  
            
            this.mutilSelect=mutilSelect;
            
            this.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
            
            this.setOnItemClickListener(this);  
            refreshFileList();  
        }  
        
        public ArrayList<String> getSelectedFiles() {
        	ArrayList<String> fileLists=new ArrayList<String>();
			
			if(this.adapter!=null){
				List<? extends Map<String, ?>> fullList=this.adapter.getSourceData();
				for (Map<String, ?> map : fullList) {
					Boolean checked=Boolean.parseBoolean(map.get("checked").toString());
					if(checked)
						fileLists.add(map.get("path").toString());
				}
			}
			
			return fileLists;
		}
        
        private int refreshFileList()  
        {  
            // ˢ���ļ��б�  
            File[] files = null;  
            try{  
                files = new File(path).listFiles();  
            }  
            catch(Exception e){  
                files = null;  
            }  
            if(files==null){  
                // ���ʳ���  
                Toast.makeText(getContext(), sOnErrorMsg,Toast.LENGTH_SHORT).show();  
                return -1;  
            }  
            if(list != null){  
                list.clear();  
            }  
            else{  
                list = new ArrayList<Map<String, Object>>(files.length);  
            }  
              
            // �����ȱ����ļ��к��ļ��е������б�  
            ArrayList<Map<String, Object>> lfolders = new ArrayList<Map<String, Object>>();  
            ArrayList<Map<String, Object>> lfiles = new ArrayList<Map<String, Object>>();  
              
            if(!this.path.equals(sRoot)){  
                // ��Ӹ�Ŀ¼ �� ��һ��Ŀ¼  
                //list.add(this.createListItem(sRoot,sRoot,sRoot,true));  
                  
                list.add(this.createListItem(sParent,path,true,true));  
            }  
              
            for(File file: files)  
            {  
                if(file.isDirectory() && file.listFiles()!=null){  
                    // ����ļ���   
                    lfolders.add(this.createListItem(file.getName(),file.getPath(),true,false));  
                }  
                else if(file.isFile()){  
                    // ����ļ�  
                	Map fileInfoMap=this.createListItem(file.getName(),file.getPath(),false,false);
                	if(fileInfoMap!=null)
                		lfiles.add(this.createListItem(file.getName(),file.getPath(),false,false)); 
                	
                }    
            }  
              
            list.addAll(lfolders); // ������ļ��У�ȷ���ļ�����ʾ������  
            list.addAll(lfiles);    //������ļ�  

            adapter = new FileSelectAdapter(getContext(), list, R.layout.dialog_file_item, new String[]{"checked","img", "name", "path"}, new int[]{R.id.filedialogitem_check,R.id.filedialogitem_img, R.id.filedialogitem_name, R.id.filedialogitem_path},mutilSelect);  
            this.setAdapter(adapter);  
            /*
            ViewGroup.LayoutParams params =this.getLayoutParams();
            params.height = 300;
            params.width=300;
            this.setLayoutParams(params);
            */
            return files.length;  
        }  
        
        private Map createListItem(String name,String path,Boolean isFolder,Boolean isRoot) {
        	
            String type="Unkown";
        	if(isFolder){
        		type=".";
        		if(isRoot)
        			type="...";
        	}else {
				type= PropertyValues.getMeidaType(name);
				if(suffix != null&&suffix.length()>0&&suffix.contains(type)==false)
					return null;
			}

        	Map<String, Object> map = new HashMap<String, Object>();  
        	map.put("isFolder", isFolder); 
            map.put("checked", "false");  
            map.put("name", name);  
            map.put("path", path);  
            map.put("img", PropertyValues.convertTypeToImage(type));
            
            return map;
		}
        
        @Override  
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {  
            // ��Ŀѡ��  
            String pt = (String) list.get(position).get("path");  
            String fn = (String) list.get(position).get("name");  
            if(fn.equals(sRoot) || fn.equals(sParent)){  
                // ����Ǹ�Ŀ¼������һ��  
                File fl = new File(pt);  
                String ppt = fl.getParent();  
                if(ppt != null){  
                    // ������һ��  
                    path = ppt;  
                }  
                else{  
                    // ���ظ�Ŀ¼  
                    path = sRoot;  
                }  
            }  
            else{  
                File fl = new File(pt);  
                if(fl.isFile()){  
                	if(mutilSelect==false){
                		ArrayList<String> selectedFiles = new ArrayList<String>();
                		selectedFiles.add(fl.getPath());
        				Bundle bundle=new Bundle();
        				bundle.putStringArrayList("selectedFiles", (ArrayList<String>) selectedFiles);
        				callback.callback(bundle);
        				
        				this.dialog.dismiss();
                	}
                    return;  
                }  
                else if(fl.isDirectory()){  
                    // ������ļ���  
                    // ��ô����ѡ�е��ļ���  
                    path = pt;  
                }  
            } 
   
            this.refreshFileList();  
        }  
    }  
}
