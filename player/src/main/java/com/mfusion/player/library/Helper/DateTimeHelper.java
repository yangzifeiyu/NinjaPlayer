package com.mfusion.player.library.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeHelper {
	// <summary>
	// / ���ַ�ת��Ϊʱ��
	// / </summary>
	// / <param name="time"></param>
	// / <param name="format">0��ת��Ϊ��������ʱ���롱��1��ת��Ϊ�������ա���2��ת��Ϊ��ʱ���롱</param>
	// / <returns></returns>
	@SuppressWarnings("deprecation")
	public static Date ConvertToDateTime(String time, int format,TimeZone timezone) {
		try {
			if (time == "")
				return new Date();
			String[] splitter = time.split(",");
			Calendar calendar = Calendar.getInstance();
			if (format == 0) {
				calendar.setTimeZone(timezone);
				calendar.set(Calendar.YEAR,Integer.parseInt(splitter[0]));
				calendar.set(Calendar.MONTH,Integer.parseInt(splitter[1])-1);
				calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(splitter[2]));
				calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(splitter[3]));
				calendar.set(Calendar.MINUTE,Integer.parseInt(splitter[4]));
				calendar.set(Calendar.SECOND,Integer.parseInt(splitter[5]));
				return calendar.getTime();

			} else if (format == 1) {
				calendar.setTimeZone(timezone);
				calendar.set(Calendar.YEAR,Integer.parseInt(splitter[0]));
				calendar.set(Calendar.MONTH,Integer.parseInt(splitter[1])-1);
				calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(splitter[2]));
				return calendar.getTime();
			} else if (format == 2) {
				return new Date(1990, 1, 1, Integer.parseInt(splitter[0]),
						Integer.parseInt(splitter[1]),
						Integer.parseInt(splitter[2]));
			} else if (format == 3) {
				return new Date(1990, 1, 1, Integer.parseInt(splitter[0]),
						Integer.parseInt(splitter[1]), 0);
			}
			return new Date();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Date(1990, 1, 1);
		}
	}

	// / <summary>
	// / ���ڸ�ʽΪyyyy-mm-dd
	// / </summary>
	// / <param name="date"></param>
	// / <returns></returns>
	@SuppressWarnings("deprecation")
	public static Date ConvertToDate(String date) {
		try {
			if (date == null || date == "")
				return new Date();
			String[] splitterDate = date.split("-");
			if (splitterDate.length == 3)
				return new Date(Integer.parseInt(splitterDate[0]),
						Integer.parseInt(splitterDate[1]),
						Integer.parseInt(splitterDate[2]), 0, 0, 0);
		} catch (Exception ex) {
		}
		return new Date();
	}


	public static Date ConvertToDateTime(String date, String time,TimeZone timezone) {

		try {
			if (time == "" || date == "")
				return new Date();

			String[] splitterTime = time.split(":");
			String[] splitterDate = date.split(",");

			Calendar col = Calendar.getInstance();
			if(timezone!=null)
				col.setTimeZone(timezone);
			col.set(Calendar.YEAR, Integer.parseInt(splitterDate[0]));
			col.set(Calendar.MONTH, Integer.parseInt(splitterDate[1]) - 1);
			col.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitterDate[2]));
			col.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitterTime[0]));
			col.set(Calendar.MINUTE, Integer.parseInt(splitterTime[1]));
			col.set(Calendar.SECOND, Integer.parseInt(splitterTime[2]));
			Date datetime = col.getTime();

			return datetime;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}

	}

	// / <summary>
	// / ����HH:mm:ss����HH:ss��ת��Ϊʱ��
	// / </summary>
	// / <param name="time"></param>
	// / <returns></returns>
	@SuppressWarnings("deprecation")
	public static Date ConvertToDateTime(String time) {
		try {
			if (time == "")
				return new Date();
			String[] splitterTime = time.split(":");
			if (splitterTime.length > 2)
				return new Date(1990, 1, 1, Integer.parseInt(splitterTime[0]),
						Integer.parseInt(splitterTime[1]),
						Integer.parseInt(splitterTime[2]));
			else
				return new Date(1990, 1, 1, Integer.parseInt(splitterTime[0]),
						Integer.parseInt(splitterTime[1]), 0);
		} catch (Exception ex) {
			return new Date(1990, 1, 1);
		}
	}

	// / <summary>
	// / ��ʱ��ת��Ϊ�ַ�
	// / </summary>
	// / <param name="time"></param>
	// / <param name="format">0��ת��Ϊ��������ʱ���롱��1��ת��Ϊ�������ա���2��ת��Ϊ��ʱ���롱</param>
	// / <returns></returns>
	public static String ConvertToString(Date time, int format) {
		try {

			if (format == 0) {
				SimpleDateFormat format0 = new SimpleDateFormat(
						"yyyy,MM,dd HH,mm,ss");
				return format0.format(time);
			} else if (format == 1) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy,MM,dd");
				return format1.format(time);
			} else if (format == 2) {
				SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
				return format2.format(time);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static String ConvertToString(Date time, String format) {
		try {

			if(format.contains("t"))
				format=format.replace("t", "a");
			if(format.contains("gg"))
				format=format.replace("gg", "G");
			if(format.contains("dddd"))
				format=format.replace("dddd", "EEEE");
			if(format.contains("ddd"))
				format=format.replace("ddd", "E");
			SimpleDateFormat format0 = new SimpleDateFormat(format);
			return format0.format(time);


		} catch (Exception e) {
			e.printStackTrace();
			return time.toString();
		}
	}
	// /// <summary>
	// /// ��string���͵�Ϊformat��ʱ���ʽת����shorformat��ʽ��ʱ������
	// /// </summary>
	// /// <param name="datatime"></param>
	// /// <param name="format"></param>
	// /// <param name="showformat"></param>
	// /// <returns></returns>
	// public static string ConvertToFormat(string datatime, int format, string
	// showformat)
	// {
	// if (datatime == null || datatime.Trim().Equals("")) return "";
	// return ConvertToDateTime(datatime, format).ToString(showformat);
	// }

	public static int CompareTime(String pre, Date next, String format) {
		if (pre == "" || next == null)
			return -1;
		if (format == "")
			format = "yyyy,MM,dd";

		SimpleDateFormat myformat = new SimpleDateFormat(format);
		return pre.compareTo(myformat.format(next));
	}

	public static int CompareTime(Date pre, Date next) {
		if (pre ==null || next == null)
			return -1;


		String 	format = "yyyy,MM,dd,HH,mm,ss";

		SimpleDateFormat myformat = new SimpleDateFormat(format);
		return myformat.format(pre).compareTo(myformat.format(next));
	}

	public static int GetDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_WEEK) - 1;
		return day;
	}

	public static int ConvertToMin(String duration) {
		// TODO Auto-generated method stub

		int timeduration = 0;
		if (duration == null || duration == "") {
			timeduration = 0;
		} else {
			String[] splitterDate = duration.split(":");
			if (splitterDate.length == 3)
				timeduration = (Integer.parseInt(splitterDate[0]) * 60 + Integer
						.parseInt(splitterDate[1]))
						* 60
						+ Integer.parseInt(splitterDate[2]);
		}

		return timeduration;
	}


	public static Date CreateDateTime(Date date,int hours, int minutes, int seconds,TimeZone timezone) {
		// TODO Auto-generated method stub
		Calendar col1 = Calendar.getInstance(timezone);
		col1.setTime(date);
		int year=col1.get(Calendar.YEAR);
		int month=col1.get(Calendar.MONTH);
		int day=col1.get(Calendar.DAY_OF_MONTH);
		Calendar col = Calendar.getInstance(timezone);

		//col.setTimeZone(timezone);
		col.set(year,month, day);
		col.set(Calendar.HOUR_OF_DAY, hours);
		col.set(Calendar.MINUTE, minutes);
		col.set(Calendar.SECOND, seconds);
		Date datetime = col.getTime();
		return datetime;

	}

	public static int GetDuration(Date endTime, Date startTime) {
		// TODO Auto-generated method stub

		/*return ((endTime.getHours() - startTime.getHours()) * 60
					+ endTime.getMinutes() - startTime.getMinutes())
		 * 60 + endTime.getSeconds() - startTime.getSeconds();*/
		return (int) ((endTime.getTime() - startTime.getTime())/1000);


	}

	public static Date GetAddedDate(Date date, int duration,TimeZone timezone) {
		// TODO Auto-generated method stub

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(timezone);
			cal.setTime(date);
			cal.add(Calendar.SECOND, duration);
			// cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + duration);
			Date datetime = cal.getTime();
			return datetime;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}

	}
	
	public static Date GetAddedDateFromMinute(Date date, int duration) {
		// TODO Auto-generated method stub

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, duration);
			Date datetime = cal.getTime();
			return datetime;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}
	}
	
	public static Date GetAddedDateFromDay(Date date, int duration) {
		// TODO Auto-generated method stub

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, duration);
			Date datetime = cal.getTime();
			return datetime;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}

	}
}
