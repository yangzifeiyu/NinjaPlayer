package com.mfusion.player.common.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.player.R;
import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Entity.DownloadObject;
import com.mfusion.player.common.Enum.ConnectTargetType;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Floatwindows.FocusKeyWord;
import com.mfusion.player.common.Setting.Player.PlayerSettingHelper;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;

public class PlayerMenu extends Activity {
	public static boolean isReloadMedias=false;
	
	TabHost m_tabHost;

	PlayerSettingHelper settinghelper;

	public static Boolean isModifySetting=false;
	public static Boolean isModifyScreen=false;
	Boolean isLoadingLog=false;
	Boolean isFirstInitLogBoolean=false;
	Boolean isFirstInitDownloadBoolean=false;
	public static Boolean isShowingBoolean=false;

	int m_tab_count=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_main_menu);

		this.isShowingBoolean=true;
		this.isFirstInitLogBoolean=this.isFirstInitDownloadBoolean=true;
		
		settinghelper=new PlayerSettingHelper(MainActivity.Instance);

		final LayoutInflater inflater = getLayoutInflater();

		this.InitCommonListener();
		this.BindSettingPage(inflater);
		//this.BindStoragePage(inflater);
		this.BindLogPage(inflater);

		m_tabHost=(TabHost)findViewById(R.id.tabhost);
		m_tabHost.getLayoutParams().width=(int)(getWindowManager().getDefaultDisplay().getWidth()*0.6); 
		m_tabHost.getLayoutParams().height=(int)(getWindowManager().getDefaultDisplay().getHeight()*0.75); 
		m_tabHost.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		m_tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
            public void onTabChanged(String tabId) {
                //do what you want to do
				if(tabId.equalsIgnoreCase(FocusKeyWord.page_log.toString())&&isFirstInitLogBoolean){
                	if(isLoadingLog==false)
                		LoadPlayerLog();  
                	SetFocusToView(refreshButton);
                	isFirstInitLogBoolean=false;
                }
                else if(tabId.equalsIgnoreCase(FocusKeyWord.page_download.toString())&&isFirstInitDownloadBoolean){
                	StartDownloadDatas();
                	isFirstInitDownloadBoolean=false;
                }
            }
		});
		m_tabHost.setup();  
		
		this.addTab(R.id.page_setting, FocusKeyWord.page_setting.toString(),"Setting", R.drawable.settingtab, m_tabHost);
		if(MainActivity.Instance.PlayerSetting.ScreenOrientation!=-1){
			this.BindScreenControlPage(inflater);
			this.addTab(R.id.page_screencontrol, FocusKeyWord.page_screencontrol.toString(),"Screen Control", R.drawable.settingtab, m_tabHost);
		}
		
		if(MainActivity.Instance.PlayerSetting.getConntectTarget()!=ConnectTargetType.local){
			this.BindDownloadPage(inflater);
			this.addTab(R.id.page_download, FocusKeyWord.page_download.toString(),"Download", R.drawable.downloadtab, m_tabHost);
		}
		//this.addTab(R.id.page_storage, FocusKeyWord.page_storage.toString(),"Storage", R.drawable.storagetab, m_tabHost);
		this.addTab(R.id.page_log, FocusKeyWord.page_log.toString(),"Log Info.", R.drawable.logtab, m_tabHost);
	}
	
	private void addTab(int layout,String tabID,String tabTitle, int drawableId,TabHost tabHost) {
		m_tab_count++;
		TabHost.TabSpec spec = tabHost.newTabSpec(tabID);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.activity_tab_example, tabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.tab_title);
		title.setText(tabTitle);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_ico);
		icon.setImageResource(drawableId);
		 
		spec.setIndicator(tabIndicator);
		spec.setContent(layout);
		tabHost.addTab(spec);
	}
	
	OnHoverListener buttOnHoverListener=null;
	OnFocusChangeListener onFocusChangeListener=null;
	private void InitCommonListener(){
		buttOnHoverListener=new OnHoverListener() {
			
			@Override
			public boolean onHover(View but_view, MotionEvent arg1) {
				// TODO Auto-generated method stub
				int what = arg1.getAction();  
                switch(what){  
                 case MotionEvent.ACTION_HOVER_ENTER:  //������view  
                	 but_view.setBackgroundColor(getResources().getColor(R.color.blue));
                     break;  
                 case MotionEvent.ACTION_HOVER_MOVE:  //�����view��  
                     System.out.println("bottom ACTION_HOVER_MOVE");  
                     break;  
                 case MotionEvent.ACTION_HOVER_EXIT:  //����뿪view  
                	 but_view.setBackground(getResources().getDrawable(R.drawable.button_style)); 
                     break;  
                }  
                return false;  
			}
		};
		
		onFocusChangeListener = new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View button, boolean isFocus) {
				// TODO Auto-generated method stub
				if(isFocus){
					button.setBackgroundColor(getResources().getColor(R.color.blue));
				}else 
					button.setBackground(getResources().getDrawable(R.drawable.button_style)); 
			}
		};
	}

	//Button applyButton=null;
	ImageButton ipconfigButton=null;
	Button te_ipEditText=null;
	Button te_portEditText=null;
	Builder dialogBuilder =null;
	AlertDialog dialog;
	Button positiveButton=null;
	Boolean isShowDialog=false;
	private void BindSettingPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)findViewById(R.id.page_setting);
			final RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_ip_setting, null); 
			tabContent.addView(subLayout);
			
			//Init value
			OnClickListener editbuttonClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					if(isShowDialog)
						return;
					isShowDialog=true;
					final Button selectedButton=(Button)view;
					String dialogTitle="";
					final EditText contentEditText = new EditText(PlayerMenu.this);
					if(selectedButton==te_ipEditText){
						dialogTitle="Server IP :";
					}
					else if(selectedButton==te_portEditText){
						dialogTitle="Server Port :";
					}
					contentEditText.setText(selectedButton.getText());
					contentEditText.setSelection(selectedButton.getText().length());
					dialogBuilder =new Builder(PlayerMenu.this)
					.setTitle("Please input "+dialogTitle)  
					.setIcon(android.R.drawable.ic_dialog_info)  
					.setView(contentEditText)  
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//���ȷ����ť  
				    	 @Override  
				         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼� 
				    		 isShowDialog=false;
				    	 }
					}).setPositiveButton("OK", new DialogInterface.OnClickListener() {//���ȷ����ť  
				    	 @Override  
				         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼� 
				    		 String editTextString=contentEditText.getText().toString();
				    		 selectedButton.setText(editTextString);
				    		 if(selectedButton==te_ipEditText){
				    			 MainActivity.Instance.PlayerSetting.setServerIP(editTextString);
							 }
							 else if(selectedButton==te_portEditText){
								 MainActivity.Instance.PlayerSetting.setServerPort(editTextString);
							 }
				    		 isModifySetting=true;
				    		 isShowDialog=false;
				         }  
				     });
					dialogBuilder.setCancelable(false);
					dialog=dialogBuilder.show(); 
					positiveButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
					contentEditText.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
							String filter="";
							if(selectedButton==te_ipEditText)
								filter="^(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$";
							else filter="^[1-9]\\d*";
								
							if(text.toString().matches(filter))
								positiveButton.setEnabled(true);
							else {
								positiveButton.setEnabled(false);
							}
						}
						
						@Override
						public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
						
						@Override
						public void afterTextChanged(Editable arg0) {}
					});
				}
			};
			try {
				
				WindowManager windowManager = getWindowManager();    
		        Display display = windowManager.getDefaultDisplay(); 
		        DisplayMetrics dm = new DisplayMetrics();  
		        display.getRealMetrics(dm);
		        ((TextView)subLayout.findViewById(R.id.tv_playerscreen)).setText(dm.widthPixels+" X "+dm.heightPixels);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			((TextView)subLayout.findViewById(R.id.tv_playerip)).setText(getLocalInetAddress());
			((TextView)subLayout.findViewById(R.id.tv_playerlicense)).setText(MainActivity.Instance.PlayerSetting.getLicense());

			te_ipEditText=(Button)subLayout.findViewById(R.id.server_ip);
			te_ipEditText.setOnFocusChangeListener(onFocusChangeListener);
			te_ipEditText.setOnHoverListener(this.buttOnHoverListener);
			te_ipEditText.setOnClickListener(editbuttonClickListener);
			te_ipEditText.setText(MainActivity.Instance.PlayerSetting.MFServerIp);
			te_portEditText=(Button)subLayout.findViewById(R.id.server_port);
			te_portEditText.setOnFocusChangeListener(onFocusChangeListener);
			te_portEditText.setOnHoverListener(this.buttOnHoverListener);
			te_portEditText.setOnClickListener(editbuttonClickListener);
			te_portEditText.setText(MainActivity.Instance.PlayerSetting.MFServerPort);
			
			ipconfigButton=(ImageButton)subLayout.findViewById(R.id.btn_setip);
			ipconfigButton.setOnFocusChangeListener(onFocusChangeListener);
			ipconfigButton.setOnHoverListener(this.buttOnHoverListener);
			ipconfigButton.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View textView, int arg1, KeyEvent keyEvent) {
					// TODO Auto-generated method stub
					if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP)
						return true; 
					return false;
				}
			});
			ipconfigButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	protected String getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
            if(ip!=null)
            	return ip.getHostAddress();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

	Integer oldScreenOrientation=0;
	ImageView screenStatus;
	Button btn_landscape=null;
	Button btn_portrait=null;
	Button btn_rportrait=null;
	private void BindScreenControlPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)findViewById(R.id.page_screencontrol);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_screencontrol_view, null); 
			tabContent.addView(subLayout);

			//Init value
			oldScreenOrientation=MainActivity.Instance.PlayerSetting.ScreenOrientation;
			final Bitmap sourcebitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.logo1)).getBitmap();
			OnClickListener screenBtnListener=new OnClickListener() {
				
				@Override
				public void onClick(View button) {
					// TODO Auto-generated method stub
					Integer screenOrientation=0;
					if(btn_portrait==button)
						screenOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					else if(btn_rportrait==button)
						screenOrientation=ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					
					changeScreenStatus(screenOrientation,sourcebitmap);
					MainActivity.Instance.PlayerSetting.setScreenOrientation(screenOrientation.toString());
					if(oldScreenOrientation!=screenOrientation)
						isModifyScreen=true;
					else {
						isModifyScreen=false;
					}
				}
			};
			screenStatus=(ImageView)findViewById(R.id.iv_screen_status);
			btn_landscape=(Button)findViewById(R.id.btn_landscape);
			btn_landscape.setOnFocusChangeListener(onFocusChangeListener);
			btn_landscape.setOnHoverListener(this.buttOnHoverListener);
			btn_landscape.setOnClickListener(screenBtnListener);
			
			btn_portrait=(Button)findViewById(R.id.btn_portrait);
			btn_portrait.setOnClickListener(screenBtnListener);
			btn_portrait.setOnFocusChangeListener(onFocusChangeListener);
			btn_portrait.setOnHoverListener(this.buttOnHoverListener);
			
			btn_rportrait=(Button)findViewById(R.id.btn_rportrait);
			btn_rportrait.setOnClickListener(screenBtnListener);
			btn_rportrait.setOnFocusChangeListener(onFocusChangeListener);
			btn_rportrait.setOnHoverListener(this.buttOnHoverListener);
			
			changeScreenStatus(MainActivity.Instance.PlayerSetting.ScreenOrientation,sourcebitmap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void changeScreenStatus(Integer screenOrientation,Bitmap sourcebitmap) {
		try {
			Integer rotateValue=0;
			if(screenOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				rotateValue=-90;
			else if(screenOrientation==ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
				rotateValue=90;
			Matrix matrix = new Matrix();
			matrix.postRotate(rotateValue);
			Bitmap bitmap = Bitmap.createBitmap(sourcebitmap, 0, 0, sourcebitmap.getWidth(),sourcebitmap.getHeight(), matrix, true);  
			screenStatus.setImageBitmap(bitmap); 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private ListView lv_downloadListView=null;
	Runnable media_runnable = new Runnable() {    
        @Override  
        public void run() {//run()���µ��߳�������  
        	try {
            	PlayerMenu.isReloadMedias=true;
        		while(isShowingBoolean){
        			if(PlayerMenu.isReloadMedias){
            			//mHandler.obtainMessage(MSG_DownloadList,null).sendToTarget();
                		List<Map<String, Object>> list=GetDownloadViewData();
                		if(list!=null)
                			mHandler.obtainMessage(MSG_DownloadList,list).sendToTarget();
        			}
            		Thread.sleep(10000);
        		}
			} catch (Exception e) {
				// TODO: handle exception
			}
        }  
    };
    
    private void StartDownloadDatas(){
    	Thread mThread = new Thread(media_runnable);  
        mThread.start();//�߳�����
    }
    
	private void BindDownloadPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)findViewById(R.id.page_download);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_download_processer, null); 
			tabContent.addView(subLayout);

			//Init value
			lv_downloadListView=(ListView)subLayout.findViewById(R.id.lv_download);
			((TextView)subLayout.findViewById(R.id.tv_mediapath)).setText(PlayerStoragePath.MediaStorage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private List<Map<String, Object>> GetDownloadViewData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list_download = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list_waiting = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list_complete = new ArrayList<Map<String, Object>>();
        
        int completedCount=0;
        Map<String, Object> map =null;
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            for (Map.Entry<String, DownloadObject> entry : MainActivity.Instance.FileManager.m_play_medias.entrySet()) {  
    			DownloadObject valueDownloadObject=(DownloadObject)entry.getValue();
    		    map = new HashMap<String, Object>();
    	        map.put("title", valueDownloadObject.FileName);
    	        if(valueDownloadObject.DownloadStatus==-1){
    	        	map.put("info", "Waiting");
    	        	list_waiting.add(map);
    	        }
    	        else if(valueDownloadObject.DownloadStatus==0){
    	        	Long percent=valueDownloadObject.LoadingLength*100/valueDownloadObject.MediaLength; 
    	        	map.put("info", percent+"%");
    	        	list_download.add(map);
    	        }
    	        else if(valueDownloadObject.DownloadStatus==1){
    	        	map.put("info", "Complete");
    	        	completedCount++;
    	        	list_complete.add(map);
    	        }
    	        if(valueDownloadObject.FileType==FileType.Audio)
    	        	map.put("img", R.drawable.sound);
    	        else if(valueDownloadObject.FileType==FileType.Video)
    	        	map.put("img", R.drawable.video);
    	        else if(valueDownloadObject.FileType==FileType.Image)
    	        	map.put("img", R.drawable.pictures);
    	        
    	        try {
    	        	String lengthString=GetFileSize(valueDownloadObject.MediaLength);
        	        if(valueDownloadObject.ModifyTime!=null)
        	        	lengthString=lengthString+"\n"+formatter.format(valueDownloadObject.ModifyTime);
        	        map.put("time",lengthString);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
    	        
    		}
            if(completedCount==MainActivity.Instance.FileManager.m_play_medias.size()){
            	PlayerMenu.isReloadMedias=false;
            }
            
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        for(Map<String, Object> item:list_download)
        	list.add(item);
        list_download.clear();
        list_download=null;
        for(Map<String, Object> item:list_waiting)
        	list.add(item);
        list_waiting.clear();
        list_waiting=null;
        for(Map<String, Object> item:list_complete)
        	list.add(item);
        list_complete.clear();
        list_complete=null;
        
        return list;
    }
	
	private String GetFileSize(long mediaLength){
		String  unitStr="";
		Double lengthDouble=0.0;
		if(mediaLength<1024){
			unitStr="B";
			lengthDouble=mediaLength*1.0;
		}else if(mediaLength<1024*1024){
			lengthDouble=mediaLength/1024.0;
			unitStr="KB";
		}else{
			lengthDouble=mediaLength/1024/1024.0;
			unitStr="MB";
		}
		BigDecimal bd = new BigDecimal(lengthDouble);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);  
		return bd.doubleValue()+unitStr;
	}
	
	private void BindDownloadViewData(List<Map<String, Object>> datas,ListView downloadInfo){
		try {
			if(datas==null){
				downloadInfo.setVisibility(View.INVISIBLE);
				return;
			}
	        SimpleAdapter adapter = new SimpleAdapter(this,datas,R.layout.acticity_list_example,
	                new String[]{"title","info","img","time"},
	                new int[]{R.id.list_title,R.id.list_info,R.id.list_img,R.id.list_loading_time});
	        downloadInfo.setAdapter(adapter);
	        downloadInfo.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void BindStoragePage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)findViewById(R.id.page_storage);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_storage_view, null); 
			tabContent.addView(subLayout);

			//Init value
			((TextView)subLayout.findViewById(R.id.tv_internal_storage)).setText(this.phoneCapacity());
			((TextView)subLayout.findViewById(R.id.tv_sdcard_storage)).setText(this.sdcardCapacity());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	TextView m_logWarningView=null;
	ListView m_logContent=null;
	ImageButton refreshButton=null;
	SimpleAdapter adapter_log = null;
	List<Map<String, Object>> logList = new ArrayList<Map<String, Object>>();
	private void BindLogPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)findViewById(R.id.page_log);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_log_view, null); 
			tabContent.addView(subLayout);
			
			m_logWarningView=(TextView)subLayout.findViewById(R.id.tv_log_warning);			
			m_logContent=(ListView)subLayout.findViewById(R.id.lv_logs);

			adapter_log = new SimpleAdapter(this,logList,R.layout.activity_list_log_example,
	                new String[]{"info"},
	                new int[]{R.id.tv_log_item});
			m_logContent.setAdapter(adapter_log);
			m_logContent.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent keyEvent) {
					// TODO Auto-generated method stub
					if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){
						//m_loglTextView.scrollBy(x, y)
					}else if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){
						
					}
					return false;
				}
			});
			refreshButton=(ImageButton)subLayout.findViewById(R.id.ib_log_refresh);
			refreshButton.setOnFocusChangeListener(onFocusChangeListener);
			refreshButton.setOnHoverListener(this.buttOnHoverListener);
			refreshButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					LoadPlayerLog();  
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void BindLogPageDatas(){
		adapter_log.notifyDataSetChanged();
		m_logWarningView.setVisibility(View.INVISIBLE);
		m_logContent.setVisibility(View.VISIBLE);
	}

	int m_loading_count=50;
	Runnable log_runnable = new Runnable() {    
        @Override  
        public void run() {//run()���µ��߳�������  
        	try {
        		logList.clear();
				BufferedReader br = new BufferedReader(new FileReader(new File(InternalKeyWords.Log_Path)));
				String line = "";
				Map<String, Object> map=null;
				int index=0;
				while((line = br.readLine())!=null&&PlayerMenu.isShowingBoolean){
					index++;
					map = new HashMap<String, Object>();
		    	    map.put("info", line);
		    	    logList.add(map);
					if(index%m_loading_count==0){
						mHandler.obtainMessage(MSG_LogList,"").sendToTarget();
						Thread.sleep(1000);
					}
				}
				br.close();
				mHandler.obtainMessage(MSG_LogList,"").sendToTarget();
    			isLoadingLog=false;
			} catch (Exception e) {
				// TODO: handle exception
			}
        }  
    }; 

	private void LoadPlayerLog() {
		if(m_logContent!=null&&this.isLoadingLog==false){
			this.isLoadingLog=true;
			m_logWarningView.setVisibility(View.VISIBLE);
	    	Thread mThread = new Thread(log_runnable);  
	        mThread.start();//�߳�����
		}
	}
	// ��ȡ����������Ϣ  
	private String phoneCapacity() {  
	    // ��ȡ������Ϣ  
	    File data = Environment.getDataDirectory();  
	    StatFs statFs = new StatFs(data.getPath());  
	    int availableBlocks = statFs.getAvailableBlocks();// ���ô洢�������  
	    int blockCount = statFs.getBlockCount();// �ܴ洢�������  
	  
	    int size = statFs.getBlockSize();// ÿ��洢��Ĵ�С  
	  
	    int totalSize = blockCount / 1024 * size/ 1024;// �ܴ洢��  
	  
	    int availableSize = availableBlocks / 1024 * size/ 1024;// ��������  
	  
	    String phoneCapacity = Integer.toString(availableSize)  
	            + "MB/" + Integer.toString(totalSize) + "MB";  
	  
	    return phoneCapacity;  
	}  
	  
	// ��ȡsdcard������Ϣ  
	private String sdcardCapacity() {  
	    // ��ȡsdcard��Ϣ  
	    File sdData = Environment.getExternalStorageDirectory();  
	    StatFs sdStatFs = new StatFs(sdData.getPath());  
	  
	    int sdAvailableBlocks = sdStatFs.getAvailableBlocks();// ���ô洢�������  
	    int sdBlockcount = sdStatFs.getBlockCount();// �ܴ洢�������  
	    int sdSize = sdStatFs.getBlockSize();// ÿ��洢��Ĵ�С  
	    int sdTotalSize = sdBlockcount / 1024 * sdSize/ 1024;  
	    int sdAvailableSize = sdAvailableBlocks / 1024 * sdSize/ 1024;  
	  
	    String sdcardCapacity = Integer.toString(sdAvailableSize)  
	            + "MB/" + Integer.toString(sdTotalSize) + "MB";  
	    return sdcardCapacity;  
	}

	private static final int MSG_LogList = 0;//��ȡͼƬ�ɹ��ı�ʶ  
	private static final int MSG_DownloadList = 1;//��ȡͼƬ�ɹ��ı�ʶ 
	Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {//�˷�����ui�߳�����  
            switch(msg.what) {  
            case MSG_LogList:  
            	//Init value
            	try {
            		BindLogPageDatas();
				} catch (Exception e) {
					// TODO: handle exception
				}
            	break; 
            case MSG_DownloadList:
            	try {
            		if(msg.obj!=null){
                		BindDownloadViewData((List<Map<String, Object>>)(msg.obj),lv_downloadListView);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
            	break;
            }  
        }  
    };

	private void ChangeTabIndex(){
		int move=(m_tabHost.getCurrentTab()+1)%m_tab_count;
		m_tabHost.setCurrentTab(move);
	}

	private void SetFocusToView(View currentView){
		if(currentView==null)
			return;
		currentView.setFocusable(true);
		currentView.requestFocus();
		currentView.setFocusableInTouchMode(true);
		currentView.requestFocusFromTouch();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player_menu, menu);
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) { 
		if(event.getAction()==0){
			if(event.getKeyCode()==KeyEvent.KEYCODE_F5||event.getKeyCode()==KeyEvent.KEYCODE_TAB||event.getKeyCode()==KeyEvent.KEYCODE_MENU){
				this.ChangeTabIndex();
			}
			else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
				if(m_tabHost.getCurrentTab()==3){
					View rootview = this.getWindow().getDecorView();
					int focuseViewId = rootview.findFocus().getId();
					if(focuseViewId==R.id.ib_log_refresh){
						m_logContent.setScrollBarFadeDuration(0);
						if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT) SetFocusToView(m_logContent);
					}else if(focuseViewId==R.id.lv_logs){
						if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT) SetFocusToView(refreshButton);
						m_logContent.setScrollBarFadeDuration(5);
					}
				}
				return true;
			}else if(event.getKeyCode()==KeyEvent.KEYCODE_ESCAPE||event.getKeyCode()==KeyEvent.KEYCODE_BACK){
				finish();
				//return true; 
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void ExitActivity(){
		if(isModifySetting){
			//settinghelper.UpdatePlayerSetting(te_ipEditText.getText().toString(),te_portEditText.getText().toString(),newScreenOrientation.toString());
			new Builder(this).setTitle("System Information")//���öԻ������
		     .setMessage("The Config is modified. Do you want to restart this player?")//������ʾ������  
		     .setPositiveButton("Yes",new DialogInterface.OnClickListener() {//���ȷ����ť  
		    	 @Override  
		         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
		    		 try {
		    			 MainActivity.Instance.mService.RestartPlayer(te_ipEditText.getText().toString(),te_portEditText.getText().toString(),MainActivity.Instance.PlayerSetting.getMediaport());
						 //APPExitHelper.CloseApp();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
		    		finish();
		         }  
		     }).setNegativeButton("No",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
		         @Override  
		         public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  
		        	 finish();
		         }  
		     }).show();
		}
		else {
			finish();
		}		
	}
}
