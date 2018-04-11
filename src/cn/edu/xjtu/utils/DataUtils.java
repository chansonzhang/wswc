package cn.edu.xjtu.utils;

/**
 * 
 * @author zhangchen
 *
 */
public class DataUtils {
	
	public static boolean isValidLongitude(String data){
		int indexOfPoint=data.indexOf(".");
		if(indexOfPoint==-1) return false;
		if(Integer.parseInt(data.substring(0,indexOfPoint))==-999){
			return false;
		}
		return true;
	}
	
	public static boolean isValidLatitude(String data){
		int indexOfPoint=data.indexOf(".");
		if(indexOfPoint==-1) return false;
		if(Integer.parseInt(data.substring(0,indexOfPoint))==-999){
			return false;
		}
		return true;
	}

}
