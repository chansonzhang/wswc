package cn.edu.xjtu.utils;

import java.sql.Timestamp;
import java.util.Date;
/**
 * 
 * @author zhangchen
 *
 */
public class TimeUtils {
	public static Timestamp Date2TimeStamp(Date date){
		Timestamp timestamp=new Timestamp(date.getTime());
		return timestamp;
	}
	
	public static Date TimeStamp2Date(Timestamp timestamp){
		return new Date(timestamp.getTime());
	}
}
