package com.mfusion.player.common.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mfusion.player.common.Setting.Player.PlayerStoragePath;


public class PlayerApp extends Application {
	
	public static PlayerApp instance;
	// activity�����б�,����activityͳһ����
	private static List<Activity> activityList=new ArrayList<Activity>();
	// �쳣����
	protected boolean isNeedCaughtExeption = true;// �Ƿ񲶻�δ֪�쳣
	private PendingIntent restartIntent;
	private MyUncaughtExceptionHandler uncaughtExceptionHandler;
	private String packgeName;

	@Override
	public void onCreate() {
		super.onCreate();


		packgeName = getPackageName();
		
		

		if (isNeedCaughtExeption) {
			cauchException();
		}
	}
	
	public static PlayerApp getInstance()
	{
		if(instance==null)
			instance=new PlayerApp();
		return instance;
	}

	// -------------------�쳣����-----�����쳣������ϵͳ-----------------//

	private void cauchException() {
		Intent intent = new Intent();
		// ����1���������2��������ڵ�activity
		intent.setClassName(packgeName, packgeName + ".MainActivity");
		restartIntent = PendingIntent.getActivity(getApplicationContext(), -1, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		// �������ʱ�����߳�
		uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		
		
	}

	// �����������ڲ�������쳣
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// ���������־
			saveCatchInfo2File(ex);

			// 1���Ӻ�����Ӧ��
			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

			// �رյ�ǰӦ��
			//finishAllActivity();
			//finishProgram();
		}
	};

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @return �����ļ����
	 */
	private String saveCatchInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String time = formatter.format(new Date());
			String fileName = time + ".txt";
			System.out.println("fileName:" + fileName);

			String filePath = PlayerStoragePath.CrashStorage;
			System.out.println("filePath + fileName:" + filePath + fileName);
			FileOutputStream fos = new FileOutputStream(filePath + fileName);
			fos.write(sb.getBytes());
			fos.close();
			//�ļ���������֮��,��Ӧ���´�������ʱ��ȥ��������־,�����µĴ�����־,�ͷ��͸���

			return fileName;
		} catch (Exception e) {
			System.out.println("an error occured while writing file..." + e.getMessage());
		}
		return null;
	}

	// ------------------------------activity����-----------------------//

	// activity���?���б����Ƴ�activity
	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	// activity���?���activity���б�
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// activity���?��������activity
	public void finishAllActivity() {
		for (Activity activity : activityList) {
			if (null != activity) {
				activity.finish();
			}
		}
	}

	// �����߳�,һ����finishAllActivity()һ��ʹ��
	// ����: finishAllActivity;finishProgram();
	public void finishProgram() {
		//android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
}

