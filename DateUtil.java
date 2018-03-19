package com.yonyou.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static Date getCurrentTime(){
		return new Date(System.currentTimeMillis());
	}
	
	public static String getFormatString(Date date, String dateFormat){
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
	}
}
