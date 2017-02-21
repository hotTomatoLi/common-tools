package com.leegebe.common.tools.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 */
public class DateUtil {

	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String YEAR_MONTH_TIME_FORMAT = "yyyy-MM";
	
	/**
	 * 计算某个日期+月
	 * @param curDate
	 * @return
	 */
	public static Date addMonth(Date curDate, int month){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH,month);
		return calendar.getTime();
	}

	/**
	 * 根据日期增加天数
	 * @param curDate
	 * @param day
	 * @return
	 */
	public static Date addDay(Date curDate, int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.DATE,day);
		return calendar.getTime();
	}

	/**
	 * 增加年份
	 * @param curDate
	 * @param year
     * @return
     */
	public static Date addYear(Date curDate, int year){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.YEAR,year);
		return calendar.getTime();
	}

	/**
	 * 得到默认格式字符串
	 * 日期
	 * @param date
	 * @return
	 */
	public static String getFormatedDate(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		return simpleDateFormat.format(date);
	}

	/**
	 * 得到特定格式的日期字符串
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String getFormatedDate(Date date, String formatStr){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
		return simpleDateFormat.format(date);
	}

	/**
	 * 从Long类型转换日期
	 * @param dateTime
	 * @return
     */
	public static String getFormatedDateFromLong(Long dateTime){
		if(dateTime == null){
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
		Date date = new Date(dateTime);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * 获取制定格式时间字符串 
	 * 日期+时间  秒级别
	 * @date 2016年6月22日下午4:50:36
	 * @param dateTime
	 * @return
	 */
	public static String getFormatedDateTime(Date dateTime){
		if(dateTime == null){
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
		return simpleDateFormat.format(dateTime);
	}
	
	/**
	 * 获取年月时间格式的字符串，如：2017-01
	 * @param dateTime
	 * @return
	 */
	public static String getYearMonthFormatedDate(Date dateTime){
		if(dateTime == null){
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_TIME_FORMAT);
		return simpleDateFormat.format(dateTime);
	}
	
	/**
	 * 从Long类型转换到日期字符串
	 * @param dateTime
	 * @param dateFormat
     * @return
     */
	public static String getFormatedDateFromLong(Long dateTime, String dateFormat){
		if(dateTime == null){
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Date date = new Date(dateTime);
		return simpleDateFormat.format(date);
	}


	/**
	 * 获取时间年份
	 * @date 2016年7月15日下午3:32:16
	 * @author QuanXiaolong
	 * @param date
	 * @return
	 */
	public static Integer getYear(Date date){
		if(date==null)
			return null;
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取dayOfMonth
	 * @param date
	 * @return
     */
	public static Integer getDayOfMonth(Date date){
		if(date==null){
			return null;
		}
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 设置为当月的第一天
	 * @param date
	 * @return
     */
	public static Date setFirstDayOfMonth(Date date){
		if(date == null){
			return null;
		}
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}

	/**
	 * 根据当前时间计算起订日期
	 * @return
	 */
	public static Date calculateStartDate(){
		Date now = new Date();
		Date result = null;
		Integer day = DateUtil.getDayOfMonth(now);
		if(day != null){
			if(day > 15){
				result = DateUtil.addMonth(now,3);
			}else {
				result = DateUtil.addMonth(now,2);
			}
			result = DateUtil.setFirstDayOfMonth(result);
		}
		return result;
	}

}
