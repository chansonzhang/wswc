package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.SPCluster;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.TempStayArea;

import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * @author xiaodai
 *         这个类就是为了在前一步的SPCluster的基础之上进行加上时间的聚类
 *         我需要根据拥挤阈值crowdThreshold来确定最终的StayArea链表
 */
public class TimeCluster {
    /**
     * 给定一个簇，一个时刻，如果该时刻簇内人数超过阈值，则簇+时刻作为一个TempStayArea返回
     *
     * @param spCluster      一个StayPoint的距离聚类结果
     * @param moment         用户输入的时间点
     * @param crowdThreshold 用户输入的阈值
     */
    public static TempStayArea getTmpSA(SPCluster spCluster, String moment, int crowdThreshold) {
        ArrayList<StayPoint> areaP = new ArrayList<StayPoint>();
        ArrayList<StayPoint> tstayPoints = spCluster.getStayPoints();   //获得这里面的points
        Timestamp time = Timestamp.valueOf(moment);
        //遍历一个类中的所有StayPoint
        for(int i=0;i<tstayPoints.size();i++){
            StayPoint tPoint = tstayPoints.get(i);
            if(tPoint.getEndTime().before(time)){
                tstayPoints.remove(i);
                i--;
                continue;
            }
            if ((tPoint.getBeginTime().before(time)||tPoint.getBeginTime().getTime()==time.getTime())
                    &&
                    (tPoint.getEndTime().after(time)||tPoint.getEndTime().getTime()==time.getTime()))
            {
                areaP.add(tPoint);
            }
        }

        //判断在moment这一时刻簇中StayPoint数量是不是比阈值大
        if (areaP.size() >= crowdThreshold) {
            TempStayArea tmpSA = new TempStayArea(time, areaP, spCluster.getNorthEast(), spCluster.getSouthWest());
            return tmpSA;
        }
        return null;
    }



    /**
     *
     * @param a
     * @param b
     * @return
     */

    /**
     * @param a
     * @param b
     * @return
     */
    public Location southWester(Location a, Location b) {
        if (a.getLatitude() < b.getLatitude() && a.getLongitude() > b.getLongitude())
            return a;
        else
            return b;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public Location northEaster(Location a, Location b) {
        if (a.getLatitude() > b.getLatitude() && a.getLongitude() < b.getLongitude())
            return a;
        else
            return b;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
