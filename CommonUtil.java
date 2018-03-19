package com.yonyou.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 通用工具类工
 * @author MaBo 2016-05-25
 */
public class CommonUtil {
	/**
	 *将接收到的多选框数据数组，封装成类似于id1,id2,id3的字符串
	 * @param QyArea  多选框数组
	 * @return id1,id2...
	 */
	public static String multiselectToStr(String[] QyArea){
		if(null != QyArea ){
			String str="";
				for(int i=0;i<QyArea.length;i++){
					str+=","+QyArea[i];
				}
				return str.substring(1);

		}
		return null ;
	}
	/**
	 *将接收到的多选框数据数组，封装成类似于'id1','id2','id3'的带引号的字符串
	 * @param QyArea  多选框数组
	 * @return 'id1','id2'...
	 */
	public static String multiselectToApoStr(String[] QyArea){
		if(null != QyArea ){
			String str="";
			for(int i=0;i<QyArea.length;i++){
				str+="','"+QyArea[i];
			}
			return str.substring(2);
			
		}
		return null ;
	}
    /**
	 * 根据所给格式，返回该格式的当前日期
	 * @param gs 日期格式 : "yyyy-MM-dd"  or "yyyy-MM"  or  "yyyy"  or  "yyyy/MM/dd" or  ……
	 * @return  所需格式的当前日期
	 * @author MaBo 2016-05-25
	 */
	public static String getCurDate(String gs){
		SimpleDateFormat df = new SimpleDateFormat(gs);//设置日期格式
		String date = df.format(new Date());
		return date;
	}
	
	/**
	 * 日期加减
	 * @param type				日期格式，包括'year'、'month'、'day'
	 * @param dateStr			需要计算的日期	
	 * @param num				需要改变的数据，如果是负数为减
	 * @param formatStr			返回的数据格式
	 * @return
	 * @author MaBo 2016-05-25
	 */
	public static String getChangeDate(String type, String dateStr ,Integer num, String formatStr) throws ParseException{
		
		SimpleDateFormat sdf=new SimpleDateFormat(formatStr);
        Date dt=sdf.parse(dateStr);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        if(type.equals("year")){
        	rightNow.add(Calendar.YEAR,num);//日期减1年
        }else if(type.equals("month")){
        	rightNow.add(Calendar.MONTH,num);//日期加3个月
        }else if(type.equals("day")){
        	rightNow.add(Calendar.DAY_OF_YEAR,num);//日期加10天
        }else{
        }
        
        Date dt1=rightNow.getTime();
        String reStr = sdf.format(dt1);
		
		return reStr;
	}
    /**
	 * 根据所给格式，返回该格式的当前日志相差n月的日期  
	 * @param gs 日期格式 : "yyyy-MM-dd"  or "yyyy-MM"  or  "yyyy"  or  "yyyy/MM/dd" or  ……
	 * @param n 相差月数，负数为之前的日期，正数为之后的日期
	 * @return  所需格式的当前日期
	 * @author MaBo 2016-05-25
	 */
	public static String getLastDate(String gs,int n){
		if(gs==null) return null;
		String date1=null;
		SimpleDateFormat df = new SimpleDateFormat(gs);//设置日期格式
		String date = df.format(new Date());
		try {
			date1=getChangeDate("month",date,n,gs);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}
	//add by zhuoqingsen 2017-8-7 start
	
	//获取时Timestamp类型的当前时间
	 public static Timestamp getCurrentTimestamp() throws ParseException {
		 
		 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date now = new Date(System.currentTimeMillis());
	        System.out.println("..............." + now);
	        return new Timestamp(parseString2Date(sdf.format(now), null).getTime());

	    }
	 
	 //String转DATA
	  public static Date parseString2Date(String dateStr, String dateFormat) throws ParseException {
		  	String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	        if (null == dateStr || dateStr.length() <= 0)
	            return null;
	        if (null == dateFormat || dateFormat.length() <= 0)
	            dateFormat = FULL_DATE_FORMAT;
	        DateFormat formatter = new SimpleDateFormat(dateFormat);
	        Date date = formatter.parse(dateStr);
	        return date;
	    }
	//add by zhuoqingsen 2017-8-7 end
}
