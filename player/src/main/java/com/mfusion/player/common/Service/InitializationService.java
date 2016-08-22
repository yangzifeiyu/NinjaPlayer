package com.mfusion.player.common.Service;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class InitializationService implements BasicServiceInterface {


	/*
	 * ȷ���ļ����Ƿ����
	 */
	private void VerifyDirectory() {
		try {
			// ���Ŀ¼�����ڣ��ȴ���Ŀ¼

			FileHelper.IsExistsAndCreate(PlayerStoragePath.MediaStorage);
			
			FileHelper.IsExistsAndCreate(PlayerStoragePath.LogStorage);
			
			FileHelper.IsExistsAndCreate(PlayerStoragePath.CrashStorage);

			FileHelper.IsExistsAndCreate(PlayerStoragePath.DataStorage);

			FileHelper.IsExistsAndCreate(PlayerStoragePath.ImageStorage);

			FileHelper.IsExistsAndCreate(PlayerStoragePath.VideoStorage);

			FileHelper.IsExistsAndCreate(PlayerStoragePath.AudioStorage);

		} 
		catch (Exception ex)
		{
			LoggerHelper.WriteLogfortxt("InitializationService VerifyDirectory==>"+ex.getMessage() );
		}

	}


	@Override
	public void Restart() {
		// TODO Auto-generated method stub
		try {
			
			// �ļ�·���Ƿ����
			this.VerifyDirectory();
			
			 new Helper();

		} catch (Exception ex) {
			LoggerHelper.WriteLogfortxt("InitializationService Restart==>"+ex.getMessage() );
		}
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub

	}

}
