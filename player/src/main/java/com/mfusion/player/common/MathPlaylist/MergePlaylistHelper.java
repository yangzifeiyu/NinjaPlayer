package com.mfusion.player.common.MathPlaylist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Entity.TimelinePlaylist;
import com.mfusion.player.common.Player.MainActivity;


public class MergePlaylistHelper {
	public static Stack<Playlist> playlist;
	public static List<Playlist> playlistcollection;

	public static void MergeAllTimelilneUnit(List<TimelinePlaylist> collection,Playlist defaultList) {
		// TODO Auto-generated method stub
		playlist = new Stack<Playlist>();// �Ƚ���������ȳ���ˮͰ��ˮ
		Date starttime = MainActivity.Instance.Clock.Now;


		try {
			// ���û��timeline����һֱ����default pbu
			if (collection == null || collection.size() == 0) {
				Playlist defaultplaylist = (Playlist) defaultList.clone();
				defaultplaylist.StartTime = starttime;
				defaultplaylist.EndTime = DateTimeHelper.CreateDateTime(starttime,23, 59, 59,MainActivity.Instance.PlayerSetting.Timezone);
				defaultplaylist.Duration = DateTimeHelper.GetDuration(
						defaultplaylist.EndTime, defaultplaylist.StartTime);
				playlist.push(defaultplaylist);
				return;
			}
			int size=collection.size();
			for (int i = size-1; i >=0; i--) {
				TimelinePlaylist timelineplaylist = collection.get(i);
				if (timelineplaylist.playlistList != null
						&& timelineplaylist.playlistList.size() != 0) {
					// ֮ǰû�зŹ�
					if (playlist.size() == 0) {
						int m =collection.get(i).playlistList.size() - 1;
						Date datetime = DateTimeHelper.CreateDateTime(starttime,23, 59,
								59,MainActivity.Instance.PlayerSetting.Timezone);
						Date endtime = collection.get(i).playlistList.get(m).EndTime;
						// ���ջ������Ľ���ʱ��
						if (endtime.compareTo(datetime) < 0) {
							Playlist defaultPlaylist = (Playlist) defaultList.clone();
							defaultPlaylist.StartTime = endtime;
							defaultPlaylist.EndTime = datetime;
							defaultPlaylist.Duration = DateTimeHelper.GetDuration(
									defaultPlaylist.EndTime, defaultPlaylist.StartTime);
							if (defaultPlaylist.Duration != 0)
								playlist.push(defaultPlaylist);
						}
						for (int j = m; j >= 0; j--) {
							collection.get(i).playlistList.get(j).Duration = DateTimeHelper
									.GetDuration(
											collection.get(i).playlistList.get(j).EndTime,
											collection.get(i).playlistList.get(j).StartTime);
							if (collection.get(i).playlistList.get(j).Duration >0)
								playlist.push(collection.get(i).playlistList.get(j));
						}
					} else {
						// �����������Block�в���DefaultPBU
						int index = collection.get(i).playlistList.size() - 1;
						Playlist top = playlist.peek();// ջ��Ԫ��
						Playlist last = collection.get(i).playlistList.get(index);// ��һ��Ҫ��ջ�Ķ���
						if (playlist.peek().StartTime != collection.get(i).playlistList
								.get(index).EndTime) {
							Playlist defaultPlaylist = (Playlist) defaultList.clone();
							if (top.ID == defaultPlaylist.ID) {
								top.StartTime = last.EndTime;
								top.Duration = DateTimeHelper.GetDuration(
										top.EndTime, top.StartTime);
								if (top.Duration == 0)
									playlist.pop();
							} else if (last.ID == defaultPlaylist.ID) {
								last.EndTime = top.StartTime;
								last.Duration = DateTimeHelper.GetDuration(
										last.EndTime, last.StartTime);
							} else {
								defaultPlaylist.StartTime = last.EndTime;
								defaultPlaylist.EndTime = top.StartTime;
								defaultPlaylist.Duration = DateTimeHelper
										.GetDuration(defaultPlaylist.EndTime,
												defaultPlaylist.StartTime);
								if (defaultPlaylist.Duration> 0)
									playlist.push(defaultPlaylist);
								top = playlist.peek();
							}
						}
						if (top.ID == last.ID) {
							top.StartTime = collection.get(i).playlistList.get(0).StartTime;
							top.Duration = DateTimeHelper.GetDuration(
									top.EndTime, top.StartTime);
							if (top.Duration == 0)
								playlist.pop();
							index--;
						}
						for (int j = index; j >= 0; j--) {
							collection.get(i).playlistList.get(j).Duration = DateTimeHelper
									.GetDuration(
											collection.get(i).playlistList.get(j).EndTime,
											collection.get(i).playlistList.get(j).StartTime);
							if (collection.get(i).playlistList.get(j).Duration> 0)
								playlist.push(collection.get(i).playlistList.get(j));
						}
					}
				}
			}
			if (playlist.size() == 0) {
				Playlist defaultPlaylist =defaultList;
				defaultPlaylist.StartTime = starttime;
				defaultPlaylist.EndTime = DateTimeHelper.CreateDateTime(starttime,23, 59, 59,MainActivity.Instance.PlayerSetting.Timezone);
				defaultPlaylist.Duration = DateTimeHelper.GetDuration(
						defaultPlaylist.EndTime, defaultPlaylist.StartTime);
				if (defaultPlaylist.Duration > 0)
					playlist.push(defaultPlaylist);
			} else {
				Playlist top = playlist.peek();// ջ��Ԫ��
				// ���ջ��Ԫ�ص���ʼʱ��

				if (top.StartTime.compareTo(starttime) > 0) {
					Playlist defaultPlaylist =defaultList;
					if (top.ID == defaultPlaylist.ID) {
						top.StartTime = starttime;
						top.Duration = DateTimeHelper.GetDuration(top.EndTime,
								top.StartTime);
						if (top.Duration == 0)
							playlist.pop();
					} else {
						defaultPlaylist.StartTime = starttime;
						defaultPlaylist.EndTime = top.StartTime;
						defaultPlaylist.Duration = DateTimeHelper.GetDuration(
								defaultPlaylist.EndTime, defaultPlaylist.StartTime);
						if (defaultPlaylist.Duration != 0)
							playlist.push(defaultPlaylist);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void MergePlaylistUnit(List<Playlist> collection) {
		try {
			playlistcollection = collection;
			if (playlistcollection == null || playlistcollection.size() == 0)
				return;

			int unitID = playlistcollection.get(0).ID;
			int cursor = 0;
			int size= playlistcollection.size();
			for (int i = 1; i <size;) {
				// �ж���������PBU�Ƿ���ͬ
				if (playlistcollection.get(i).ID != unitID) {
					int pre = i - 1;
					if (cursor < pre) {
						playlistcollection.get(cursor).EndTime = playlistcollection
								.get(pre).EndTime;

						List<Playlist> list = playlistcollection.subList(cursor + 1, pre
								- cursor);
						playlistcollection.removeAll(list);

						i = cursor + 1;
					}
					unitID = playlistcollection.get(i).ID;
					cursor = i;
				}
				i++;
			}
			if (playlistcollection.size() - 1 == cursor)
				return;
			else {
				((Playlist) playlistcollection.get(cursor)).EndTime = ((Playlist) playlistcollection
						.get(playlistcollection.size() - 1)).EndTime;
				List<Playlist> list = playlistcollection.subList(cursor + 1,
						playlistcollection.size() - cursor);
				if(playlistcollection.containsAll(list))
				{
					List<Playlist> sub = new ArrayList<Playlist>(list);
					playlistcollection.removeAll(sub);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

