package com.mfusion.commons.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by guoyu on 7/14/2016.
 */
public class DateConverter {

	static SimpleDateFormat date_format=new SimpleDateFormat("yyyy,MM,dd");
	static SimpleDateFormat time_format=new SimpleDateFormat("HH:mm:ss");
	static SimpleDateFormat short_time_format=new SimpleDateFormat("HH:mm");
	static SimpleDateFormat long_time_format=new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
	static SimpleDateFormat default_display_format=new SimpleDateFormat(InternalKeyWords.DefaultDisplayDateFormat);

	public static String convertToDisplayStr(Date time){
		if(time==null)
			time=Calendar.getInstance().getTime();
		return default_display_format.format(time);
	}

	public static Date convertDisplayStrToDate(String time){
		try {
			return default_display_format.parse(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static String convertDateToStr(String format,Date date){
		if(date==null)
			date=Calendar.getInstance().getTime();
		return (new SimpleDateFormat(format)).format(date);
	}

	public static String convertCurrentFullTimeToStr(){
		return long_time_format.format(Calendar.getInstance().getTime());
	}
	
	public static String convertCurrentDateToStr(){
		return date_format.format(Calendar.getInstance().getTime());
	}

	public static String convertCurrentTimeToStr(){
		return short_time_format.format(Calendar.getInstance().getTime());
	}

	public static String convertCurrentDateToStr(String format){
		return (new SimpleDateFormat(format)).format(Calendar.getInstance().getTime());
	}

	public static String convertTimeToStrNoSecond(Date time){
		return short_time_format.format(time)+":00";
	}

	public static String convertShortTimeToStr(Date time){
		return short_time_format.format(time);
	}

	public static String convertTimeToStr(Date time){
		return time_format.format(time);
	}
	
	public static String convertDateToStr(Date date){
		if(date==null)
			return "0001,01,01";
		return date_format.format(date);
	}

	public static Date convertStrToTime(String time){
		try {
			return time_format.parse(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static Date convertShortStrToTime(String time){
		try {
			return short_time_format.parse(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static Date convertStrToDate(String date){
		try {
			if(date.equalsIgnoreCase("0001,01,01"))
				return null;
			return date_format.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static Calendar clearCalendarNoneHHmmss(Calendar fullTimeCalendar){
		fullTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fullTimeCalendar.set(Calendar.MINUTE, 0);
		fullTimeCalendar.set(Calendar.SECOND, 0);
		fullTimeCalendar.set(Calendar.MILLISECOND, 0);
		return fullTimeCalendar;
	}

	public static Date getCurrentDate(){
		Calendar fullTimeCalendar=Calendar.getInstance();
		clearCalendarNoneHHmmss(fullTimeCalendar);
		return fullTimeCalendar.getTime();
	}
}
