package com.mfusion.player.common.Storage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.DownloadObject;
import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class MediaStorage {
	public static Hashtable<Integer,MediaFile> mediamap;

	public static MediaFile GetMediaFile(int id) {
		MediaFile mediaFile = null;
		try {
			if(mediamap!=null&&mediamap.size()>0&&mediamap.containsKey(id))
			{
				mediaFile=mediamap.get(id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("MediaStorage GetMediaFile==>"+e.getMessage());
		}
		return mediaFile;
	}

	public static Queue<DownloadObject> GetMediasAndUpdate(
			List<String> pbu_id_list,Map<String,DownloadObject> playMedias) {
		Queue<DownloadObject> downloadFiles = new LinkedBlockingQueue<DownloadObject>();
		if (pbu_id_list == null||pbu_id_list.size()<=0)
			return downloadFiles;

		List<Integer> downloadMediaIds = new ArrayList<Integer>();
		
		
		int size= pbu_id_list.size();
		for (int i = 0; i <size; i++) {
			try {
				String id = pbu_id_list.get(i);
				List<Integer> mediaids = PBUStorage.GetPbuMediaFiles(id);
				for (int mediaid : mediaids) {
					if (!downloadMediaIds.contains(mediaid))
						downloadMediaIds.add(mediaid);
				}

			} catch (Exception ex) {
				LoggerHelper.WriteLogfortxt("MediaStorage GetMediasAndUpdate==>"+ex.getMessage());
			}
		}
		downloadFiles=FilterMedias(downloadMediaIds,playMedias);
		return downloadFiles;
	}

	private static Queue<DownloadObject> FilterMedias(List<Integer> mediaids,Map<String,DownloadObject> playMedias) {
		Queue<DownloadObject> downloadFile = new LinkedBlockingQueue<DownloadObject>();
		List<DownloadObject> imgList = new ArrayList<DownloadObject>();
		List<DownloadObject> videoList = new ArrayList<DownloadObject>();
		List<DownloadObject> audioList = new ArrayList<DownloadObject>();
		if (mediaids != null) {
			int size=mediaids.size();
			for (int i=0;i<size;i++) {
				try {
					int id=mediaids.get(i);
					String dir = "";
					MediaFile media = null;
					media = GetMediaFile(id);
					if(media!=null)
					{
						FileType type = media.Type;
						String name = media.FileName;
						if (type.equals(FileType.Video)) {
							dir = PlayerStoragePath.VideoStorage;
						} 
						else if (type.equals(FileType.Image)) 
						{
							dir = PlayerStoragePath.ImageStorage;
						}
						else if (type.equals(FileType.Audio)) 
						{
							dir = PlayerStoragePath.AudioStorage;
						}
						else {
							continue;
						}
						DownloadObject downentity = new DownloadObject();
						downentity.ExtName = media.ExtName;
						downentity.FileDir = dir;
						downentity.FileType = media.Type;
						downentity.SourceFilePath = media.FilePath;
						downentity.FileName = media.FileName;
						downentity.FileSource=media.MediaSource;
						downentity.MediaLength=media.mediaLength;

						playMedias.put(downentity.FileName, downentity);
						boolean isExits = FileHelper.IsExists(dir + name.replace("\\", "/"));
						if (!isExits) {
							if (type.equals(FileType.Video))
								videoList.add(downentity);
							else if (type.equals(FileType.Image))
								imgList.add(downentity);
							else if (type.equals(FileType.Audio))
								audioList.add(downentity);
						}
						else {
							downentity.DownloadStatus=1;
							downentity.ModifyTime=MainActivity.Instance.Clock.Now;
						}
					}


				} catch (Exception ex) {
					LoggerHelper.WriteLogfortxt("MediaStorage FilterMedias==>"+ex.getMessage());
				}

			}
			for (DownloadObject entity : imgList)
				downloadFile.add(entity);
			for (DownloadObject entity : audioList)
				downloadFile.add(entity);
			for (DownloadObject entity : videoList)
				downloadFile.add(entity);
			
		}
		return downloadFile;
	}

	public static List<DownloadObject> GetMediasListAndUpdate(
			List<String> pbu_id_list,Map<String,DownloadObject> playMedias) {
		List<DownloadObject> downloadFiles = new ArrayList<DownloadObject>();
		if (pbu_id_list == null||pbu_id_list.size()<=0)
			return downloadFiles;

		List<Integer> downloadMediaIds = new ArrayList<Integer>();
		for (int i = 0; i < pbu_id_list.size(); i++) {
			try {
				List<Integer> mediaids = PBUStorage.GetPbuMediaFiles(pbu_id_list.get(i));
				for (int mediaid : mediaids) {
					if (!downloadMediaIds.contains(mediaid))
						downloadMediaIds.add(mediaid);
				}

			} catch (Exception ex) {
				LoggerHelper.WriteLogfortxt("MediaStorage GetMediasAndUpdate==>"+ex.getMessage());
			}
		}
		downloadFiles=FilterMediasForList(downloadMediaIds,playMedias);
		return downloadFiles;
	}

	private static List<DownloadObject> FilterMediasForList(List<Integer> mediaids,Map<String,DownloadObject> playMedias) {
		List<DownloadObject> downloadFile = new ArrayList<DownloadObject>();
		List<DownloadObject> imgList = new ArrayList<DownloadObject>();
		List<DownloadObject> videoList = new ArrayList<DownloadObject>();
		List<DownloadObject> audioList = new ArrayList<DownloadObject>();
		if (mediaids != null) {
			for (int id : mediaids) {
				try {
					String dir = "";
					MediaFile media = null;
					media = GetMediaFile(id);
					FileType type = media.Type;
					String name = media.FileName;
					if (type.equals(FileType.Video)) {
						dir = PlayerStoragePath.VideoStorage;
					} 
					else if (type.equals(FileType.Image)) 
					{
						dir = PlayerStoragePath.ImageStorage;
					}
					else if (type.equals(FileType.Audio)) 
					{
						dir = PlayerStoragePath.AudioStorage;
					}
					else {
						continue;
					}
					DownloadObject downentity = new DownloadObject();
					downentity.ExtName = media.ExtName;
					downentity.FileDir = dir;
					downentity.FileType = media.Type;
					downentity.SourceFilePath = media.FilePath;
					downentity.FileName = media.FileName;
					downentity.FileSource=media.MediaSource;
					
					playMedias.put(downentity.FileName, downentity);
					boolean isExits = FileHelper.IsExists(dir + name.replace("\\", "/"));
					if (!isExits) {
						if (type.equals(FileType.Video))
							videoList.add(downentity);
						else if (type.equals(FileType.Image))
							imgList.add(downentity);
						else if (type.equals(FileType.Audio))
							audioList.add(downentity);
					}
					else {
						downentity.DownloadStatus=1;
						downentity.ModifyTime=MainActivity.Instance.Clock.Now;
					}



				} catch (Exception ex) {
					LoggerHelper.WriteLogfortxt("MediaStorage FilterMedias==>"+ex.getMessage());
				}

			}
			for (DownloadObject entity : imgList)
				downloadFile.add(entity);
			for (DownloadObject entity : videoList)
				downloadFile.add(entity);
			for (DownloadObject entity : audioList)
				downloadFile.add(entity);
		}
		return downloadFile;
	}
}
