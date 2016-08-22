package com.mfusion.player.common.MathPBU;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.TimelinePBUBlock;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.ScheduleStorage;

public class MergePBUHelper {
	public static Stack<PBU> pbulist;
	public static ArrayList<PBU> pbucollection;


	public static void MergeAllTimelilneUnit(List<TimelinePBUBlock> collection) {
		// TODO Auto-generated method stub
		pbulist = new Stack<PBU>();// �Ƚ���������ȳ���ˮͰ��ˮ

		Date starttime = MainActivity.Instance.Clock.Now;
		try {
			// ���û��timeline�����޷���ȡ��ȷʱ�䣬��һֱ����default pbu
			if (collection == null || collection.size() == 0||!MainActivity.Instance.connectState)
			{
				if(ScheduleStorage.defaultPBU!=null)
				{
					PBU defaultPBU =(PBU)ScheduleStorage.defaultPBU.clone();
					defaultPBU.StartTime = starttime;
					defaultPBU.EndTime = DateTimeHelper.CreateDateTime(starttime,23, 59, 59,MainActivity.Instance.PlayerSetting.Timezone);
					defaultPBU.Duration = DateTimeHelper.GetDuration(
							defaultPBU.EndTime, defaultPBU.StartTime);
					pbulist.add(defaultPBU);

				}
				return;
			}
			int size=collection.size();
			for (int i = 0; i <size ; i++) {
				TimelinePBUBlock timelinepbu = collection.get(i);
				if (timelinepbu.PBUList != null
						&& timelinepbu.PBUList.size() != 0) {
					// ֮ǰû�зŹ�
					if (pbulist.size() == 0) {
						int m =collection.get(i).PBUList.size() - 1;
						Date datetime = DateTimeHelper.CreateDateTime(starttime,23, 59,
								59,MainActivity.Instance.PlayerSetting.Timezone);
						Date endtime = collection.get(i).PBUList.get(m).EndTime;
						// ���ջ������Ľ���ʱ��
						if (endtime.compareTo(datetime) < 0) {
							PBU defaultPBU = (PBU)ScheduleStorage.defaultPBU.clone();
							defaultPBU.StartTime = endtime;
							defaultPBU.EndTime = datetime;
							defaultPBU.Duration = DateTimeHelper.GetDuration(
									defaultPBU.EndTime, defaultPBU.StartTime);
							if (defaultPBU.Duration != 0)
								pbulist.add(defaultPBU);
						}
						for (int j = m; j >= 0; j--) {
							collection.get(i).PBUList.get(j).Duration = DateTimeHelper
									.GetDuration(
											collection.get(i).PBUList.get(j).EndTime,
											collection.get(i).PBUList.get(j).StartTime);
							if (collection.get(i).PBUList.get(j).Duration != 0)
								pbulist.add(collection.get(i).PBUList.get(j));
						}
					} else {
						// �����������Block�в���DefaultPBU
						int index = collection.get(i).PBUList.size() - 1;
						PBU top = pbulist.peek();// ջ��Ԫ��
						PBU last = collection.get(i).PBUList.get(index);// ��һ��Ҫ��ջ�Ķ���
						if (pbulist.peek().StartTime != collection.get(i).PBUList
								.get(index).EndTime) {
							PBU defaultPBU = (PBU)ScheduleStorage.defaultPBU.clone();
							if (top.ID.equalsIgnoreCase(defaultPBU.ID)) {
								top.StartTime = last.EndTime;
								top.Duration = DateTimeHelper.GetDuration(
										top.EndTime, top.StartTime);
								if (top.Duration == 0)
									pbulist.pop();
							} else if (last.ID.equalsIgnoreCase( defaultPBU.ID)) {
								last.EndTime = top.StartTime;
								last.Duration = DateTimeHelper.GetDuration(
										last.EndTime, last.StartTime);
							} else {
								defaultPBU.StartTime = last.EndTime;
								defaultPBU.EndTime = top.StartTime;
								defaultPBU.Duration = DateTimeHelper
										.GetDuration(defaultPBU.EndTime,
												defaultPBU.StartTime);
								if (defaultPBU.Duration != 0)
									pbulist.add(defaultPBU);
								top = pbulist.peek();
							}
						}
						if (top.ID.equalsIgnoreCase(last.ID)) {
							top.StartTime = collection.get(i).PBUList.get(0).StartTime;
							top.Duration = DateTimeHelper.GetDuration(
									top.EndTime, top.StartTime);
							if (top.Duration == 0)
								pbulist.pop();
							index--;
						}
						for (int j = index; j >= 0; j--) {
							collection.get(i).PBUList.get(j).Duration = DateTimeHelper
									.GetDuration(
											collection.get(i).PBUList.get(j).EndTime,
											collection.get(i).PBUList.get(j).StartTime);
							if (collection.get(i).PBUList.get(j).Duration != 0)
								pbulist.add(collection.get(i).PBUList.get(j));
						}
					}
				}
			}
			if (pbulist.size() == 0) {
				PBU defaultPBU = (PBU)ScheduleStorage.defaultPBU.clone();
				defaultPBU.StartTime = starttime;
				defaultPBU.EndTime = DateTimeHelper.CreateDateTime(starttime,23, 59, 59,MainActivity.Instance.PlayerSetting.Timezone);
				defaultPBU.Duration = DateTimeHelper.GetDuration(
						defaultPBU.EndTime, defaultPBU.StartTime);
				if (defaultPBU.Duration != 0)
					pbulist.add(defaultPBU);
			} else {
				PBU top = pbulist.peek();// ջ��Ԫ��
				// ���ջ��Ԫ�ص���ʼʱ��

				if (top.StartTime.compareTo(starttime) > 0) {
					PBU defaultPBU =(PBU)ScheduleStorage.defaultPBU.clone();
					if (top.ID.equalsIgnoreCase(defaultPBU.ID)) {
						top.StartTime = starttime;
						top.Duration = DateTimeHelper.GetDuration(top.EndTime,
								top.StartTime);
						if (top.Duration == 0)
							pbulist.pop();
					} else {
						defaultPBU.StartTime = starttime;
						defaultPBU.EndTime = top.StartTime;
						defaultPBU.Duration = DateTimeHelper.GetDuration(
								defaultPBU.EndTime, defaultPBU.StartTime);
						if (defaultPBU.Duration != 0)
							pbulist.add(defaultPBU);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void MergeBlockUnit(ArrayList<PBU> collection) {
		try {
			pbucollection = collection;
			if (pbucollection == null || pbucollection.size() == 0)
				return;

			String unitID=pbucollection.get(0).ID;
			int cursor = 0;
			int size= pbucollection.size();
			for (int i = 1; i <size;) {
				// �ж���������PBU�Ƿ���ͬ
				if (!pbucollection.get(i).ID.equalsIgnoreCase(unitID)) {
					int pre = i - 1;
					if (cursor < pre) {
						pbucollection.get(cursor).EndTime = pbucollection
								.get(pre).EndTime;

						List<PBU> list = pbucollection.subList(cursor + 1, pre
								- cursor);
						pbucollection.removeAll(list);

						i = cursor + 1;
					}
					unitID = pbucollection.get(i).ID;
					cursor = i;
				}
				i++;
			}
			if (pbucollection.size() - 1 == cursor)
				return;
			else {
				((PBU) pbucollection.get(cursor)).EndTime = ((PBU) pbucollection
						.get(pbucollection.size() - 1)).EndTime;
				List<PBU> list = pbucollection.subList(cursor + 1,
						pbucollection.size() - cursor);
				if(pbucollection.containsAll(list))
				{
					List<PBU> sub = new ArrayList<PBU>(list);
					pbucollection.removeAll(sub);
				}



			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
