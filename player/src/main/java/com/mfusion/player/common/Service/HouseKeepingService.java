/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-05-02
 *
 *HouseKeeping 
 */
package com.mfusion.player.common.Service;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;

import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import com.mfusion.player.common.Storage.MediaStorageHelper;

import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class HouseKeepingService implements BasicServiceInterface{

	private final int houseKeepingDay=5;
	private Date deleteDay;
	private MediaStorageHelper helper;
	private static final int interval=60*1000;//1min
	private HandleTimer mTimer=new HandleTimer() {
		@Override
		protected void onTime() {
			// TODO Auto-generated method stub
			DeleteFile();//ɾ������ļ�
		}
	};

	public HouseKeepingService()
	{
		helper=new MediaStorageHelper(MainActivity.Instance);
	}

	@Override
	public void Restart()
	{
		//LoggerHelper.WriteLogfortxt("HouseKeeping Start==>");
		// TODO Auto-generated method stub
		deleteDay = DateTimeHelper.GetAddedDate(MainActivity.Instance.Clock.Now,-houseKeepingDay*24*60*60,MainActivity.Instance.PlayerSetting.Timezone);//ɾ����һʱ��֮ǰ��media�ļ�
		mTimer.start(interval,interval);
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		mTimer.stop();
	}

	/*
	 * ɾ���ļ�
	 */
	private void DeleteFile()
	{
		try
		{
			//key���ظ�
			IdentityHashMap<String, String> mediaFiles = helper.getMediaByDate(DateTimeHelper.ConvertToString(this.deleteDay, "yyyy,MM,dd"));
			if (mediaFiles != null)
			{
				for(Iterator<String> itr = mediaFiles.keySet().iterator(); itr.hasNext();)
				{
					String key = (String) itr.next(); 
					String value = (String) mediaFiles.get(key); 
					FileType type=FileType.fromString(value);
					key=key.replace("\\", "/");
					switch (type)
					{
					case Image:

						FileHelper.DeleteFile(PlayerStoragePath.ImageStorage + key);
						FileHelper.DeleteFile(PlayerStoragePath.ImageStorage + key+".temp");
						LoggerHelper.WriteLogfortxt("HouseKeeping delete==>"+PlayerStoragePath.ImageStorage + key);
						break;
					case Video:
						FileHelper.DeleteFile(PlayerStoragePath.VideoStorage + key);
						FileHelper.DeleteFile(PlayerStoragePath.VideoStorage + key+".temp");
						LoggerHelper.WriteLogfortxt("HouseKeeping delete==>"+PlayerStoragePath.VideoStorage + key);
						break;
					case Audio:
						FileHelper.DeleteFile(PlayerStoragePath.AudioStorage + key);
						FileHelper.DeleteFile(PlayerStoragePath.AudioStorage + ".temp");
						LoggerHelper.WriteLogfortxt("HouseKeeping delete==>"+PlayerStoragePath.AudioStorage + key);
						break;
					default:
						break;
					}

				}
				helper.DeleteExpiryMedia(DateTimeHelper.ConvertToString(this.deleteDay, "yyyy,MM,dd"));
				mediaFiles.clear();
				mediaFiles = null;
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("HouseKeepingService DeleteFile==>"+ex.getMessage());
		}
	}

}
