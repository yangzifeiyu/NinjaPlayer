/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-09
 *
 *player 主程序入口
 */
package com.mfusion.player.common.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;

import com.mfusion.commons.tools.HashAlgorithm;
import com.mfusion.commons.tools.LicenseDecoder;
import com.mfusion.commons.tools.LicenseStorage;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.QRBuilder;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.FreeTimeHintDialog;
import com.mfusion.player.R;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Callback.DialogCallBack;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.APPExitHelper;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.AppInfo;
import com.mfusion.player.common.Entity.View.LoadingView;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Service.*;
import com.mfusion.player.common.Service.connection.ConnectionManagerService;
import com.mfusion.player.common.Setting.Player.PlayerSetting;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import com.mfusion.player.IMFServiceInterface;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements MyCallInterface{

	public static MainActivity Instance;//静态入口

	private PackageManager pm ;
	public HashMap<String,AppInfo> appInfos;
	public RelativeLayout Screen;
	private LoadingView SplashImage;
	private LinearLayout Splash;
	public TextView Status;
	public PlayerSetting PlayerSetting;
	//public SocketMessageService tcpClient;
	public FileManagerService FileManager;
	public TaskManagerService TaskManager;
	public ScheduleLoaderService ScheduleLoader;
	public PBUDispatcherService PBUDispatcher;
	public DeviceManagerService DeviceManager;
	public InternalClockService Clock;
	public  DynamicResourceCenterService ResourceCenter;
	public IMFServiceInterface mService;
	private InitializationService Init;
	public TCPListenerService TcpListener;
	public HouseKeepingService HouseKeeping;
	public Caller  PBUMouseDown;    //外部调用回调函数
	private Caller AppExit;
	private Handler sHandler;  

	private FreeTimeHintDialog FreeHintDialog;

	public boolean connectState=true;


	public ConnectionManagerService ConnectManagerService;

	/*private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service){

			setmService(IMFServiceInterface.Stub.asInterface(service));
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
			setmService(null);
		}
	};*/

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try 
			{
				LoggerHelper.WriteLogfortxt("Stopping services");
				HouseKeeping.Stop();
				ResourceCenter.Stop();
				FileManager.Stop();
				PBUDispatcher.Stop();
				ScheduleLoader.Stop();

				//Helper.ControlManager.releaseControls();

				LoggerHelper.WriteLogfortxt("Starting services");
				ScheduleLoader.Restart();
				PBUDispatcher.Restart();
				FileManager.Restart(PBUDispatcher.m_pbu_idlist);// download
				DeviceManager.Restart();
				ResourceCenter.Restart();
				HouseKeeping.Restart();
			}
			catch (Exception ex) {
				// TODO Auto-generated catch block
				LoggerHelper.WriteLogfortxt("MainActivity handleMessage==>"+ex.getMessage());
			}
			finally
			{
				msg=null;
			}

		}
	};

	public Handler statusHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try 
			{
				if(msg!=null)
				{
					Bundle b = msg.getData();
					String status=b.getString("status");
					Status.setText(status);

				}
			}
			catch (Exception ex) {
				// TODO Auto-generated catch block
				LoggerHelper.WriteLogfortxt("MainActivity handleMessage==>"+ex.getMessage());
			}
			finally
			{
				msg=null;
			}

		}
	};

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i("Player", "Start");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		WindowsDecorHelper.hideBottomBar(this.getWindow());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		setContentView(R.layout.activity_main);

		/*System.out.println( "FNVHash1 : "+LicenseDecoder.baseString(Math.abs(HashAlgorithm.FNVHash1("201712321235959")),36));
		System.out.println( "oneByOneHash : "+ HashAlgorithm.oneByOneHash("201712321235959"));*/

		//隐藏底部状态栏
		/*try
		{  
			String ProcID = "79";  
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) ProcID = "42"; // ICS  
			// 需要root 权限
			// "su", "-c",
			Process proc = Runtime.getRuntime().exec(new String[] {"service call activity " + ProcID + " s16 com.android.systemui" }); // WAS
			proc.waitFor();  
		}  
		catch (Exception ex)  
		{  
			Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}*/

		this.Initalize();	

		/*if(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE==this.PlayerSetting.ScreenOrientation)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);*/
		LoggerHelper.WriteLogfortxt("Starting Player==>");


	}

	@Override  
	public void setRequestedOrientation(int requestedOrientation) {  
		try {
			int current_screen=getRequestedOrientation();
			/*if(current_screen==-1){
				this.PlayerSetting.ScreenOrientation=-1;
				return;
			}*/
			if(current_screen==requestedOrientation)
				return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		super.setRequestedOrientation(requestedOrientation);  
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Toast.makeText(this, "系统的屏幕方向发生改变", Toast.LENGTH_LONG).show();
		switch (getRequestedOrientation()) {
			case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
				Toast.makeText(this,"当前屏幕朝向为：LANDSCAPE",Toast.LENGTH_LONG);
				break;
			case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
				Toast.makeText(this,"当前屏幕朝向为：PORTRAIT",Toast.LENGTH_LONG);
				break;
			default:
				break;
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override  
	public int getRequestedOrientation() {  
		// TODO Auto-generated method stub  
		return super.getRequestedOrientation();  
	} 

	@Override
	protected void onDestroy()
	{
		this.stop();
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*
	 * 初始化变量
	 */
	private void Initalize() {
		try
		{
			Instance = this;
		    PlayerApp.getInstance().addActivity(Instance);
			Bundle args = new Bundle();
			Intent intent = new Intent("com.example.androidservice.service");
			intent.putExtras(args);
			//this.bindService(intent,mConnection,Context.BIND_AUTO_CREATE);

			this.InitScreen();

			this.TaskManager=new TaskManagerService();
			this.ScheduleLoader = new ScheduleLoaderService();
			this.PBUDispatcher = new PBUDispatcherService();
			this.DeviceManager=new DeviceManagerService();
			this.Clock = new InternalClockService();
			this.PlayerSetting = new PlayerSetting();
			//this.tcpClient = new SocketMessageService();
			this.FileManager= new FileManagerService();

			this.TcpListener=new TCPListenerService();
			this.HouseKeeping=new HouseKeepingService();
			/*Caller caller=new Caller();
			caller.setI(this);
			this.tcpClient.firstTryConnect=caller;*/

			this.ResourceCenter=new DynamicResourceCenterService();
			this.Init = new InitializationService();
			this.AppExit=new Caller();
			this.AppExit.setI(this);

			this.FreeHintDialog=new FreeTimeHintDialog(this);

			this.InitSplash();

			this.LoadBasicService();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void InitScreen() {
		// TODO Auto-generated method stub
		this.Screen = new RelativeLayout(Instance);
		this.Screen.setBackgroundColor(Color.BLACK);
		this.Screen.setVisibility(View.INVISIBLE);
	}

	private void InitSplash()
	{
		
		try
		{
			// TODO Auto-generated method stub
			setContentView(R.layout.splash);
			this.Splash=(LinearLayout)findViewById(R.id.splashlayout);
			SplashImage = (LoadingView)findViewById(R.id.main_imageview);
			int[] imageIds = new int[6];
			imageIds[0] =R.drawable.loader_frame_1;
			imageIds[1] = R.drawable.loader_frame_2;
			imageIds[2] = R.drawable.loader_frame_3;
			imageIds[3] = R.drawable.loader_frame_4;
			imageIds[4] = R.drawable.loader_frame_5;
			imageIds[5] = R.drawable.loader_frame_6; 
			SplashImage.setImageIds(imageIds);

			this.Status=(TextView)this.findViewById(R.id.status);

			this.Status.setText("Connecting to "+PlayerSetting.MediaServerIP+" ...");

			TextView versionNumber = (TextView) this.findViewById(R.id.versionNumber);
			//杩欓噷瑕侀�杩囨柟娉曞緱鍒伴厤缃枃浠剁殑鐗堟湰鍙锋坊鍔犲埌splash琛楅潰涓婂幓
			versionNumber.setText(getVersion());

			new Thread()
			{
				@Override
				public void run()
				{
					SplashImage.startAnim();
				}
			}.start();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/*
	 * 开始加载
	 */
	private void LoadBasicService() {
		// TODO Auto-generated method stub
		try {
			Caller caller=new Caller();
			caller.setI(this);
			ConnectManagerService=new ConnectionManagerService(caller);
			ConnectManagerService.Restart();
			
			this.LoadCurrentPrograms();
			this.Init.Restart();
			//this.tcpClient.Restart();//开始通信尝试
			this.TaskManager.Restart();

		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private AppInfo getAppInfo(ResolveInfo app) {  
		AppInfo appInfo = new AppInfo(); 
		try
		{
			appInfo.setAppLabel((String) app.loadLabel(pm));  
			appInfo.setPkgName(app.activityInfo.packageName);  
			appInfo.setClassName(app.activityInfo.name);
		}catch(Exception ex)
		{}
		return appInfo;  
	}  

	/*
	 * 获取已有程序
	 */
	private void LoadCurrentPrograms() 
	{
		try
		{
			appInfos=new HashMap<String, AppInfo>();
			pm = this.getPackageManager();  
			// 查询所有已经安装的应用程序  
			//List<ApplicationInfo> listAppcations = pm  
			//.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);  
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
			// 通过查询，获得所有ResolveInfo对象.  
			List<ResolveInfo> resolveInfos = pm  
					.queryIntentActivities(mainIntent, 0);  

			Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));  //important

			for (ResolveInfo app : resolveInfos) {
				AppInfo appinfo=getAppInfo(app);
				appInfos.put(appinfo.getAppLabel(),appinfo);  
			}  
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MainActivity LoadCurrentPrograms==>"+ex.getMessage());
		}



	}  


	/*
	 * 设置音量
	 */
	public void setVolume(int value)
	{
		try
		{
			AudioManager audio = (AudioManager)getSystemService(AUDIO_SERVICE);

			audio.setStreamVolume(AudioManager.STREAM_MUSIC,value, 0); 
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MainActivity setVolume==>"+ex.getMessage());
		}
	}

	/*
	 * 获取版本号
	 */
	private String getVersion() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return "Version" + info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "Version";
		}
	}


	public int getVolume()
	{
		try
		{
			AudioManager audio = (AudioManager)getSystemService(AUDIO_SERVICE);
			return audio.getStreamVolume(AudioManager.STREAM_MUSIC)*100/15; 
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MainActivity setVolume==>"+ex.getMessage());
			return 0;
		}
	}

	/*
	 * 键盘事件
	 */
	Boolean isClosingPlayer=false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) { 
		if(event.getAction()==0){
			//ACTION_DOWN
			if(event.getKeyCode()==KeyEvent.KEYCODE_F5||event.getKeyCode()==KeyEvent.KEYCODE_MENU){
				ShowSettingPage();
			}
			else if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
				//Esc

				if(isClosingPlayer==false){
					isClosingPlayer=true;
					DialogCallBack callBack=new DialogCallBack(){
						@Override
						public void onConfim(String content){
							APPExitHelper.CloseApp(); 
						}
						@Override
						public void onCancel(String errorMsg){
							isClosingPlayer=false;
						}
					};
					APPExitHelper.showAppClosingDialog(callBack, this);
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void ShowSettingPage() {PlayerMenu.isModifyScreen=false;
		/*PlayerMenu.isModifySetting=false;
		Intent it =new Intent(MainActivity.this,PlayerMenu.class);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(it);*/
		APPExitHelper.showAppDialogWithPassword("Please input password ","Enter",new DialogCallBack() {
			@Override
			public void onConfim(String content) {
				startActivity(new Intent(MainActivity.this, ActivityViewpage.class));
				finish();
			}

			@Override
			public void onCancel(String errorMsg) {

			}
		},this);
	}

	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}  
		return  super.onKeyDown(keyCode, event);     
	} 

	@Override  
	public boolean onTouchEvent(MotionEvent event)  
	{  
		if(gestureDetector.onTouchEvent(event))  
			return true;  
		else  
			return false;  
	}  

	@SuppressWarnings("deprecation")
	private GestureDetector gestureDetector= new GestureDetector(new OnGestureListener()  
	{  

		//鼠标按下
		public boolean onDown(MotionEvent event) { 

			if(PBUMouseDown!=null)
				PBUMouseDown.call("");

			return false;  
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,    
				float velocityY) {  

			return false;  
		}  

		public void onLongPress(MotionEvent event) {  
			ShowSettingPage();
		}  

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,    
				float distanceY) {  

			return false;  
		}  

		public void onShowPress(MotionEvent event) {  


		}  

		public boolean onSingleTapUp(MotionEvent event) {  

			return false;  
		}  

	});

	/*public IMFServiceInterface getmService() {
		return mService;
	}

	public void setmService(IMFServiceInterface mService) {
		this.mService = mService;
	}
*/
	@Override
	public Object fuc(Object paras) 
	{
		// TODO Auto-generated method stub
		if(paras!=null)
		{
			try
			{
				Object[] args=(Object[])paras;
				ServerConnectStatus state=(ServerConnectStatus) args[0];
				String text="";
				Message msg=new Message();
				Bundle bundle=new Bundle();
				msg.setData(bundle);
				switch(state)
				{
				case Connection:
					text="(Successfully connecting to "+this.PlayerSetting.MFServerIp+".Synchronizing time from server ...)";
					this.FileManager.StartTryConnectMediaServer();

					break;
				case OverLimit:
					text="(Player count of server is overlimit.Synchronizing timefrom internet ...)";
					break;
				case Unconnection:
					text="(Failed to connect to "+this.PlayerSetting.MFServerIp+".Synchronizing time from internet ...)";
					break;
				default:
					break;
				}
				bundle.putString("status",text);
				this.statusHandler.sendMessageDelayed(msg,1000);


				Message msg1=new Message();
				Bundle bundle1=new Bundle();
				msg1.setData(bundle1);
				String date=(String) args[1];
				String timezone=(String)args[2];
				int result=this.Clock.Restart(state,date,timezone);
				switch(result)
				{
				case -1:
					text="(Failed to synchronize from internet.";
					break;
				case 0:
					text="(Successfully  synchronized from internet.";
					break;
				case 1:
					text="(Successfully  synchronized from server.";
					break;
				}
				text+="Loading basic services...)";
				bundle1.putString("status",text);
				this.statusHandler.sendMessageDelayed(msg1,1000);		
				//this.mHandler.sendEmptyMessageDelayed(0, 1000);
				this.mHandler.sendEmptyMessage(0);


				if(FileHelper.IsExists(PlayerStoragePath.XMLStorage + XMLHelper.xmlName))//没有
				{
					XMLHelper xmlHelper=new XMLHelper();

					Element display_run_element = xmlHelper
							.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\Run\\");
					if(display_run_element!=null)
						return null;
				}


				Message msg2=new Message();
				Bundle bundle2=new Bundle();
				msg2.setData(bundle2);
				bundle2.putString("status","(No content to display,waiting...)");
				this.statusHandler.sendMessageDelayed(msg2,1000);

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return null;
	}

	Boolean changeMainActivity=false;
	//切换到后台
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		changeMainActivity=true;
		super.onPause();
		this.FreeHintDialog.stopDisplay();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(this.changeMainActivity) {
			this.PlayerSetting.refreshConfigInfo();
			this.TaskManager.Restart();
			this.mHandler.sendEmptyMessage(0);
			changeMainActivity = false;
		}
		this.FreeHintDialog.startDisplay();
		/*APPExitHelper.RegisterHomeKeyReceiver(this);
		if(PlayerMenu.isModifyScreen||PlayerMenu.isModifySetting){
			PlayerMenu.isModifyScreen=false;
			PlayerMenu.isModifySetting=false;
			APPExitHelper.RestartPlayer(this);
		}
		PlayerMenu.isShowingBoolean=false;*/
	}

	public void Render() {
		// TODO Auto-generated method stub
		this.Splash.setVisibility(View.INVISIBLE);
		this.SplashImage.stopAnim();

		this.setContentView(Screen);
		this.Screen.setVisibility(View.VISIBLE);

	}

	private void stop(){
		try {
			ConnectManagerService.Stop();
			HouseKeeping.Stop();
			ResourceCenter.Stop();
			FileManager.Stop();
			PBUDispatcher.Stop();
			DeviceManager.Stop();
			ScheduleLoader.Stop();

			this.FreeHintDialog.stopDisplay();

			this.TaskManager.Stop();

			this.Clock.Stop();

			Helper.pause();
			//this.unbindService(mConnection);

		}catch (Exception ex){
			LogOperator.WriteLogfortxt("MainActivity==>stop:"+ex.getMessage());
		}
	}

}
