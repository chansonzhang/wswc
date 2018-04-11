package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;

import java.util.List;

/**
 * Created by zhangchen on 2015/9/29.
 */
public class MapUtils {
    private static final double EARTH_RADIUS = 6378137;  //����뾶
    //���ýǶȱ�ʾ�Ľ�ת��Ϊ������ȵ��û��ȱ�ʾ�Ľ� Math.toRadians
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    public static double distance(Location l1, Location l2) {
        return MapUtils.getDistance(l1.getLongitude(), l1.getLatitude(), l2.getLongitude(), l2.getLatitude());
    }

    /**
     * �ȸ��ͼ�������������ľ���
     * @param lng1  ����1
     * @param lat1  γ��1
     * @param lng2  ����2
     * @param lat2  γ��2
     * @return ���루ǧ�ף�
     */
    public static double getDistance_old(double lng1, double lat1, double lng2, double lat2)
    {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * @author dingxiaoqiang
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2)
    {
        double dx=lng1-lng2;
        double dy=lat1-lat2;
        double b=(lat1+lat2)/2.0;
        double Lx=Math.toRadians(dx)*EARTH_RADIUS*Math.cos(Math.toRadians(b)); //东西距离
        double Ly=EARTH_RADIUS*Math.toRadians(dy); //南北距离
        return Math.sqrt(Lx*Lx+Ly*Ly);
    }

    /**
     * calculate center of a form
     * @param dataPoints
     * @return
     */
    public static Location getCentroid(List<DataPoint> dataPoints)
    {
        double maxLongitude = 0, maxLatitude = 0, minLongitude = 180, minLatitude = 90;
        if (dataPoints == null || dataPoints.size() == 0) return new Location(0, 0);
        for (int i = 0; i < dataPoints.size(); i++) {
            double currentLongitude = dataPoints.get(i).getLocation().getLongitude();
            double currentLatitude = dataPoints.get(i).getLocation().getLatitude();
            if (currentLongitude > maxLongitude)
                maxLongitude = currentLongitude;
            else if (currentLongitude < minLongitude)
                minLongitude = currentLongitude;

            if (currentLatitude > maxLatitude)
                maxLatitude = currentLatitude;
            else if (currentLatitude < minLatitude)
                minLatitude = currentLatitude;
        }
        Location location=new Location((maxLongitude + minLongitude) / 2, (maxLatitude + minLatitude) / 2);
        return location;
    }


    /**
     * get the center of mass
     * @param dataPoints
     * @return
     */
    public static  Location getCenter_Mass(List<DataPoint> dataPoints)
    {
        if (dataPoints == null || dataPoints.size() == 0) return new Location(0, 0);
        double lngSum=0;
        double latSum=0;
        int n=dataPoints.size();
        for(int i=0;i<n;i++)
        {
            lngSum+=dataPoints.get(i).getLocation().getLongitude();
            latSum+=dataPoints.get(i).getLocation().getLatitude();
        }
        Location cm=new Location(lngSum/n,latSum/n);
        return cm;
    }
}
