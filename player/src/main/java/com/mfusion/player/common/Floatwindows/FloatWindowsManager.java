package com.mfusion.player.common.Floatwindows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mfusion.player.R;
import com.mfusion.player.common.Entity.DownloadObject;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerSettingHelper;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class FloatWindowsManager {
	public Boolean isShowingBoolean=false;
	
	PlayerSettingHelper settinghelper;
	AudioManager m_audioManager;
	WindowManager mWindowManager;  
	LayoutParams wmParams;
	RelativeLayout mFloatLayout;  
	TabHost m_tabHost;
	Activity m_mainWindow; 
	Boolean isLoadingLog=false;
	Boolean isFirstInitLogBoolean=false;
	Boolean isFirstInitDownloadBoolean=false;

	String m_current_focuString="";
	Map<String, ViewFocusChain> m_focus_queueMap=new HashMap<String, ViewFocusChain>();
	Map<String, View> m_focus_views=new HashMap<String, View>();
	public void createFloatView(Activity mainWindow)  
	{  
		try{
			if(mWindowManager!=null&&mFloatLayout!=null&&this.isShowingBoolean){
				ChangeTabIndex();
				return;
			}
			this.isShowingBoolean=true;
			this.isFirstInitLogBoolean=this.isFirstInitDownloadBoolean=true;
			this.m_mainWindow=mainWindow;

			settinghelper=new PlayerSettingHelper(MainActivity.Instance);
			m_audioManager = (AudioManager)this.m_mainWindow.getSystemService("audio");
			
			//��ȡLayoutParams����  
			wmParams = new LayoutParams();

			//��ȡ����LocalWindowManager����  
			mWindowManager = mainWindow.getWindowManager();  

			wmParams.type = LayoutParams.TYPE_PHONE;  
			wmParams.format = PixelFormat.RGBA_8888;;  
			wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;  
			wmParams.gravity = Gravity.CENTER_HORIZONTAL;  
			wmParams.width = (int)(mWindowManager.getDefaultDisplay().getWidth()*0.6);  
			wmParams.height = (int)(mWindowManager.getDefaultDisplay().getHeight()*0.75);

			final LayoutInflater inflater = mainWindow.getLayoutInflater();

			/*mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.activity_config_main_menu, null); 
	        // ����PopupWindow���� 
	        final PopupWindow pop = new PopupWindow(mFloatLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false); 
	        // ��Ҫ����һ�´˲�������߿���ʧ 
	        //pop.setBackgroundDrawable(new BitmapDrawable()); 
	        //���õ��������ߴ�����ʧ 
	        pop.setOutsideTouchable(true); 
	        // ���ô˲����ý��㣬�����޷���� 
	        pop.setFocusable(true); */
	        
			mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.activity_config_main_menu, null);  
			
			mWindowManager.addView(mFloatLayout, wmParams);  

			this.InitCommonListener();
			this.BindSettingPage(inflater);
			this.BindStoragePage(inflater);
			this.BindDownloadPage(inflater);
			this.BindLogPage(inflater);
			
			m_tabHost=(TabHost)mFloatLayout.findViewById(R.id.tabhost);
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
                    	//LoadPlayerLog();  
                    	isFirstInitLogBoolean=false;
                    }
                    else if(tabId.equalsIgnoreCase(FocusKeyWord.page_download.toString())&&isFirstInitDownloadBoolean){
                    	StartDownloadDatas();
                    	isFirstInitDownloadBoolean=false;
                    }
                    m_current_focuString=tabId;
                }
			});
			m_tabHost.setup();  
			
			this.addTab(R.id.page_setting, FocusKeyWord.page_setting.toString(),"Setting", R.drawable.settingtab, m_tabHost);
			this.addTab(R.id.page_download, FocusKeyWord.page_download.toString(),"Download", R.drawable.downloadtab, m_tabHost);
			this.addTab(R.id.page_storage, FocusKeyWord.page_storage.toString(),"Storage", R.drawable.storagetab, m_tabHost);
			this.addTab(R.id.page_log, FocusKeyWord.page_log.toString(),"Log Info.", R.drawable.logtab, m_tabHost);

			this.m_current_focuString=FocusKeyWord.page_setting.toString();
			//pop.showAtLocation(mFloatLayout, Gravity.CENTER_HORIZONTAL, 0, 0);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	

private void addTab(int layout,String tabID,String tabTitle, int drawableId,TabHost tabHost) {

	TabHost.TabSpec spec = tabHost.newTabSpec(tabID);
	View tabIndicator = LayoutInflater.from(this.m_mainWindow).inflate(R.layout.activity_tab_example, tabHost.getTabWidget(), false);
	TextView title = (TextView) tabIndicator.findViewById(R.id.tab_title);
	title.setText(tabTitle);
	ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_ico);
	icon.setImageResource(drawableId);
	 
	spec.setIndicator(tabIndicator);
	spec.setContent(layout);
	tabHost.addTab(spec);
}
	
	OnHoverListener buttOnHoverListener=null;
	private void InitCommonListener(){
		buttOnHoverListener=new OnHoverListener() {
			
			@Override
			public boolean onHover(View but_view, MotionEvent arg1) {
				// TODO Auto-generated method stub
				int what = arg1.getAction();  
                switch(what){  
                 case MotionEvent.ACTION_HOVER_ENTER:  //������view  
                	 but_view.setBackgroundColor(m_mainWindow.getResources().getColor(R.color.blue));
                     break;  
                 case MotionEvent.ACTION_HOVER_MOVE:  //�����view��  
                     System.out.println("bottom ACTION_HOVER_MOVE");  
                     break;  
                 case MotionEvent.ACTION_HOVER_EXIT:  //����뿪view  
                	 but_view.setBackground(m_mainWindow.getResources().getDrawable(R.drawable.button_style)); 
                     break;  
                }  
                return false;  
			}
		};
	}

	Button applyButton=null;
	ImageButton ipconfigButton=null;
	EditText te_ipEditText=null;
	EditText te_portEditText=null;
	EditText te_mportEditText=null;
	private void BindSettingPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)mFloatLayout.findViewById(R.id.page_setting);
			final RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_ip_setting, null); 
			tabContent.addView(subLayout);

			//Init value
			OnFocusChangeListener textFocusChangeListener = new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View textView, boolean isFocus) {
					// TODO Auto-generated method stub
					if(isFocus==false){
						wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
					}else{
						wmParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
						if(textView==te_ipEditText){
							m_current_focuString=FocusKeyWord.item_ip.toString();
						}
						else if(textView==te_portEditText){
							m_current_focuString=FocusKeyWord.item_port.toString();
						}
						else if(textView==te_mportEditText){
							m_current_focuString=FocusKeyWord.item_mport.toString();
						}
						InputMethodManager inputManager =(InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(textView, 0);
					}
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				}
			};
			OnKeyListener textkeyKeyListener=new OnKeyListener() {
				
				@Override
				public boolean onKey(View textView, int arg1, KeyEvent keyEvent) {
					// TODO Auto-generated method stub
					if(keyEvent.getKeyCode()==keyEvent.KEYCODE_BACK )
						removeFloatView();
					else if(keyEvent.getKeyCode()==keyEvent.KEYCODE_DPAD_RIGHT)
						return true; 
					else if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_F5)	
						ChangeTabIndex();
					return false;
				}
			};
			
			((TextView)subLayout.findViewById(R.id.tv_playerip)).setText(getLocalInetAddress());
			te_ipEditText=(EditText)subLayout.findViewById(R.id.server_ip);
			te_ipEditText.setOnFocusChangeListener(textFocusChangeListener);
			te_ipEditText.setOnKeyListener(textkeyKeyListener);
			te_portEditText=(EditText)subLayout.findViewById(R.id.server_port);
			te_portEditText.setOnFocusChangeListener(textFocusChangeListener);
			te_portEditText.setOnKeyListener(textkeyKeyListener);
			te_mportEditText=(EditText)subLayout.findViewById(R.id.download_port);
			te_mportEditText.setOnFocusChangeListener(textFocusChangeListener);
			te_mportEditText.setOnKeyListener(textkeyKeyListener);
			
			Map<String,String> map=settinghelper.GetAllFileds();
			if(map.containsKey("MFServerIp")){
				te_ipEditText.setText(map.get("MFServerIp"));
			}
			if(map.containsKey("MFServerPort")){
				te_portEditText.setText(map.get("MFServerPort"));
			}
			if(map.containsKey("MediaServerPort")){
				te_mportEditText.setText(map.get("MediaServerPort"));
			}
			
			te_ipEditText.addTextChangedListener(new IPTextWatcher(te_ipEditText));

			applyButton=(Button)subLayout.findViewById(R.id.btn_apply);
			applyButton.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View arg0, boolean isFocus) {
					// TODO Auto-generated method stub
					if(isFocus){
						applyButton.setBackgroundColor(m_mainWindow.getResources().getColor(R.color.blue));
						m_current_focuString=FocusKeyWord.item_apply.toString();
					}else 
						applyButton.setBackground(m_mainWindow.getResources().getDrawable(R.drawable.button_style)); 
				}
			});
			applyButton.setOnHoverListener(this.buttOnHoverListener);
			applyButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					settinghelper.UpdatePlayerSetting(te_ipEditText.getText().toString(),te_portEditText.getText().toString(),te_mportEditText.getText().toString());
				}
			});
			
			ipconfigButton=(ImageButton)subLayout.findViewById(R.id.btn_setip);
			ipconfigButton.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View arg0, boolean isFocus) {
					// TODO Auto-generated method stub
					if(isFocus){
						ipconfigButton.setBackgroundColor(m_mainWindow.getResources().getColor(R.color.blue));
						m_current_focuString=FocusKeyWord.item_setip.toString();
					}else 
						ipconfigButton.setBackground(m_mainWindow.getResources().getDrawable(R.drawable.button_style)); 
				}
			});
			ipconfigButton.setOnHoverListener(this.buttOnHoverListener);
			ipconfigButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					m_mainWindow.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); 
				}
			});
			
			ViewFocusChain setting_chain=new ViewFocusChain();
			setting_chain.View_Left=FocusKeyWord.page_log.toString();
			setting_chain.View_Right=FocusKeyWord.page_download.toString();
			setting_chain.View_Bottom=FocusKeyWord.item_ip.toString();
			this.m_focus_queueMap.put(FocusKeyWord.page_setting.toString(), setting_chain);
			
			ViewFocusChain item_setip_chain=new ViewFocusChain();
			item_setip_chain.isButton=true;
			item_setip_chain.View_Bottom=FocusKeyWord.item_ip.toString();
			ViewFocusChain item_ip_chain=new ViewFocusChain();
			item_ip_chain.View_Top=FocusKeyWord.item_setip.toString();
			item_ip_chain.View_Bottom=FocusKeyWord.item_port.toString();
			ViewFocusChain item_port_chain=new ViewFocusChain();
			item_port_chain.View_Top=FocusKeyWord.item_ip.toString();
			item_port_chain.View_Bottom=FocusKeyWord.item_mport.toString();
			ViewFocusChain item_mport_chain=new ViewFocusChain();
			item_mport_chain.View_Top=FocusKeyWord.item_port.toString();
			item_mport_chain.View_Bottom=FocusKeyWord.item_apply.toString();
			ViewFocusChain item_apply_chain=new ViewFocusChain();
			item_apply_chain.isButton=true;
			item_apply_chain.View_Top=FocusKeyWord.item_mport.toString();
			item_apply_chain.View_Bottom=FocusKeyWord.page_download.toString();
			
			this.m_focus_queueMap.put(FocusKeyWord.item_setip.toString(), item_setip_chain);
			this.m_focus_views.put(FocusKeyWord.item_setip.toString(), ipconfigButton);
			this.m_focus_queueMap.put(FocusKeyWord.item_ip.toString(), item_ip_chain);
			this.m_focus_views.put(FocusKeyWord.item_ip.toString(), te_ipEditText);
			this.m_focus_queueMap.put(FocusKeyWord.item_port.toString(), item_port_chain);
			this.m_focus_views.put(FocusKeyWord.item_port.toString(), te_portEditText);
			this.m_focus_queueMap.put(FocusKeyWord.item_mport.toString(), item_mport_chain);
			this.m_focus_views.put(FocusKeyWord.item_mport.toString(), te_mportEditText);
			this.m_focus_queueMap.put(FocusKeyWord.item_apply.toString(), item_apply_chain);
			this.m_focus_views.put(FocusKeyWord.item_apply.toString(), applyButton);
			
			this.SetFocusToView(te_ipEditText);
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
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ip.getHostAddress();
    }

	private ListView lv_downloadListView=null;
	private TextView tv_waitingTextView=null;
	Runnable media_runnable = new Runnable() {    
        @Override  
        public void run() {//run()���µ��߳�������  
        	try {
        		while(isShowingBoolean){
        			//mHandler.obtainMessage(MSG_DownloadList,null).sendToTarget();
            		List<Map<String, Object>> list=GetDownloadViewData();
            		if(list!=null)
            			mHandler.obtainMessage(MSG_DownloadList,list).sendToTarget();
            		
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
			LinearLayout tabContent=(LinearLayout)mFloatLayout.findViewById(R.id.page_download);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_download_processer, null);
			tabContent.addView(subLayout);

			//Init value
			lv_downloadListView=(ListView)subLayout.findViewById(R.id.lv_download);
			((TextView)subLayout.findViewById(R.id.tv_mediapath)).setText(PlayerStoragePath.MediaStorage);

			ViewFocusChain load_chain=new ViewFocusChain();
			load_chain.View_Left=FocusKeyWord.page_setting.toString();
			load_chain.View_Right=FocusKeyWord.page_storage.toString();

			this.m_focus_queueMap.put(FocusKeyWord.page_download.toString(), load_chain);
			
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
    	        	map.put("info", "Downloading");
    	        	list_download.add(map);
    	        }
    	        else if(valueDownloadObject.DownloadStatus==1){
    	        	map.put("info", "Complete");
    	        	list_complete.add(map);
    	        }
    	        if(valueDownloadObject.FileType==FileType.Audio)
    	        	map.put("img", R.drawable.sound);
    	        else if(valueDownloadObject.FileType==FileType.Video)
    	        	map.put("img", R.drawable.video);
    	        else if(valueDownloadObject.FileType==FileType.Image)
    	        	map.put("img", R.drawable.pictures);
    	        
    	        if(valueDownloadObject.ModifyTime==null)
    	        	map.put("time", "0001-010-01 00:00:00");
    	        else
    	        	map.put("time",formatter.format(valueDownloadObject.ModifyTime));
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
	
	private void BindDownloadViewData(List<Map<String, Object>> datas,ListView downloadInfo){
		try {
			if(datas==null){
				downloadInfo.setVisibility(View.INVISIBLE);
				return;
			}
	        SimpleAdapter adapter = new SimpleAdapter(this.m_mainWindow,datas,R.layout.acticity_list_example,
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
			LinearLayout tabContent=(LinearLayout)mFloatLayout.findViewById(R.id.page_storage);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_storage_view, null); 
			tabContent.addView(subLayout);

			//Init value
			((TextView)subLayout.findViewById(R.id.tv_internal_storage)).setText(this.phoneCapacity());
			((TextView)subLayout.findViewById(R.id.tv_sdcard_storage)).setText(this.sdcardCapacity());
			

			ViewFocusChain storage_chain=new ViewFocusChain();
			storage_chain.View_Left=FocusKeyWord.page_download.toString();
			storage_chain.View_Right=FocusKeyWord.page_log.toString();

			this.m_focus_queueMap.put(FocusKeyWord.page_storage.toString(), storage_chain);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void BindLogPage(LayoutInflater inflater){
		try {
			LinearLayout tabContent=(LinearLayout)mFloatLayout.findViewById(R.id.page_log);
			RelativeLayout subLayout = (RelativeLayout) inflater.inflate(R.layout.activity_log_view, null); 
			tabContent.addView(subLayout);
			
			m_loglTextView=(TextView)subLayout.findViewById(R.id.tv_log_warning);
			m_loglTextView.setMovementMethod(ScrollingMovementMethod.getInstance()); 
			final ImageButton refreshButton=(ImageButton)subLayout.findViewById(R.id.ib_log_refresh);
			refreshButton.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View arg0, boolean isFocus) {
					// TODO Auto-generated method stub
					if(isFocus){
						refreshButton.setBackgroundColor(m_mainWindow.getResources().getColor(R.color.blue));
						m_current_focuString=FocusKeyWord.item_refresh.toString();
					}else 
						refreshButton.setBackground(m_mainWindow.getResources().getDrawable(R.drawable.button_style)); 
				}
			});
			refreshButton.setOnHoverListener(this.buttOnHoverListener);
			refreshButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					LoadPlayerLog();  
				}
			});
			
			ViewFocusChain log_chain=new ViewFocusChain();
			log_chain.View_Left=FocusKeyWord.page_storage.toString();
			log_chain.View_Right=FocusKeyWord.page_setting.toString();
			log_chain.View_Bottom=FocusKeyWord.item_refresh.toString();
			this.m_focus_queueMap.put(FocusKeyWord.page_log.toString(), log_chain);
			
			ViewFocusChain item_refresh_chain=new ViewFocusChain();
			item_refresh_chain.isButton=true;
			this.m_focus_queueMap.put(FocusKeyWord.item_refresh.toString(), item_refresh_chain);
			this.m_focus_views.put(FocusKeyWord.item_refresh.toString(), refreshButton);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private TextView m_loglTextView=null;
	Runnable log_runnable = new Runnable() {    
        @Override  
        public void run() {//run()���µ��߳�������  
        	try {
        		StringBuffer sb = new StringBuffer();
				String logPath=Environment.getExternalStorageDirectory().getPath()+"/Log/"+"log.txt";
				BufferedReader br = new BufferedReader(new FileReader(new File(logPath)));
				String line = "";
				while((line = br.readLine())!=null){
					sb.append(line+"\n");
				}
				br.close();
				mHandler.obtainMessage(MSG_LogList,sb.toString()).sendToTarget();
			} catch (Exception e) {
				// TODO: handle exception
			}
        }  
    }; 

	private void LoadPlayerLog() {
		if(m_loglTextView!=null&&this.isLoadingLog==false){
			this.isLoadingLog=true;
	    	m_loglTextView.setText("Loading... ...");
	    	m_loglTextView.setGravity(Gravity.CENTER);
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
	  
	    int totalSize = blockCount * size;// �ܴ洢��  
	  
	    int availableSize = availableBlocks * size;// ��������  
	  
	    String phoneCapacity = Integer.toString(availableSize / 1024 / 1024)  
	            + "MB/" + Integer.toString(totalSize / 1024 / 1024) + "MB";  
	  
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
	    int sdTotalSize = sdBlockcount * sdSize;  
	    int sdAvailableSize = sdAvailableBlocks * sdSize;  
	  
	    String sdcardCapacity = Integer.toString(sdAvailableSize / 1024 / 1024)  
	            + "MB/" + Integer.toString(sdTotalSize / 1024 / 1024) + "MB";  
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
            		m_loglTextView.setGravity(Gravity.LEFT);
        			m_loglTextView.setText(msg.obj.toString());
        			isLoadingLog=false;
				} catch (Exception e) {
					// TODO: handle exception
				}
            	break; 
            case MSG_DownloadList:
            	try {
            		if(msg.obj==null){
                		tv_waitingTextView.setVisibility(View.VISIBLE);
                		BindDownloadViewData(null,lv_downloadListView);
            		}else {
                		tv_waitingTextView.setVisibility(View.GONE);
                		BindDownloadViewData((List<Map<String, Object>>)(msg.obj),lv_downloadListView);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
            	break;
            }  
        }  
    };  
	
	public void removeFloatView(){
		try{
			if(mFloatLayout != null)  
			{  
				mWindowManager.removeView(mFloatLayout);  
			}            
			this.isShowingBoolean=false;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void ChangeTabIndex(){
		int move=(m_tabHost.getCurrentTab()+1)%4;
		m_tabHost.setCurrentTab(move);
	}
	
	public void ChangeViewFocus(int keycode) {
		try {
			if(this.m_current_focuString=="")
				return;
			
			ViewFocusChain currentViewChain=this.m_focus_queueMap.get(this.m_current_focuString);
			
			String nextViewString="";
			if(keycode ==(int)DirectionKeyCode.KeyUP.num){
				nextViewString=currentViewChain.View_Top;
			}else if(keycode ==(int)DirectionKeyCode.KeyDown.num){
				nextViewString=currentViewChain.View_Bottom;
			}
			
			if(nextViewString==""||nextViewString==null)
				return;

			View currentView=this.m_focus_views.get(nextViewString);
			this.SetFocusToView(currentView);
			
			this.m_current_focuString=nextViewString;
			
			Toast.makeText(this.m_mainWindow, this.m_current_focuString, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void SetFocusToView(View currentView){
		currentView.setFocusable(true);
		currentView.requestFocus();
		currentView.setFocusableInTouchMode(true);
		currentView.requestFocusFromTouch();
	}

	public void EnterKeyDown(){
		try {
			ViewFocusChain currentViewChain=this.m_focus_queueMap.get(this.m_current_focuString);
			if(currentViewChain.isButton){
				View currentView=this.m_focus_views.get(this.m_current_focuString);
				currentView.performClick();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
