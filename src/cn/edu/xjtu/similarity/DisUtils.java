package cn.edu.xjtu.similarity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DisUtils {
	  public static Long toUnixTime(String local){
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String unix ="";
	        try {
	            unix = df.parse(local).getTime() +"";
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        Long unixlong=(Long.parseLong(unix))/1000;
	        return unixlong;
	    }
	    public static String toLocalTime(String unix) {
	        Long timestamp = Long.parseLong(unix) * 1000;
	        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
	        return date;
	    }
}
