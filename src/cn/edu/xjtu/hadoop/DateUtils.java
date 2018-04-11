package cn.edu.xjtu.hadoop;

/**
 * 
 * @author zhangchen
 *
 */
public class DateUtils {
	
	/**
	 * 将时间向下对齐，
	 * @param time 要进行向下对齐的时间,格式为hh,24小时制
	 * @param howManyHour 一天中的所有时间将被向下对齐到24/howManyHour个整点，这些整点从00开始
	 * @return 对齐后的时间，格式为hh
	 */
	public static String floorHour(String time,int howManyHour){
		String flooredTime;
		int hh=Integer.parseInt(time);
		int newhh=hh-(hh%howManyHour);
		flooredTime=String.valueOf(newhh);
		if(newhh<10) flooredTime="0"+flooredTime;
		return flooredTime;
	}

}
