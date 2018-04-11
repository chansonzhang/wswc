package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zhangchen(chansonzhang@163.com) on 2016/4/27.
 */
public class SamplingUtils {
    /**
     * 每个采样间隔内只取一个点
     * 如果采样间隔为0，保留所有采样点
     * @param trajectories trajectories to sampling
     */
    public static List<Trajectory> sampling(List<Trajectory> trajectories){
        int intervalMilliSeconds=PrivacyConfig.getInstance().getTimeInterval()*1000;
        if(intervalMilliSeconds==0) return trajectories;
        for(int i=0;i<trajectories.size();i++){
            Trajectory trajectory=trajectories.get(i);
            List<DataPoint> dataPoints=trajectory.getDataPoints();
            String dateBegin=dataPoints.get(0).getTime().toString().split(" ")[0];
            String dateEnd=dataPoints.get(dataPoints.size()-1).getTime().toString().split(" ")[0];
            Timestamp t0=Timestamp.valueOf(dateBegin+" "+"00:00:00");
            Timestamp t24=Timestamp.valueOf(dateEnd+" "+"24:00:00");
            Timestamp current=new Timestamp(t0.getTime());

            for(int j=0;j<dataPoints.size();j++){
                DataPoint dataPoint=dataPoints.get(j);
                //skip uncontinued time section
                while(dataPoint.getTime().getTime()-current.getTime()>=intervalMilliSeconds)
                    current.setTime(current.getTime()+intervalMilliSeconds);
                if(dataPoint.getTime().equals(current)||dataPoint.getTime().after(current)){
                    //保留当前点，查看下一采样时刻
                    current.setTime(current.getTime()+intervalMilliSeconds);
                    continue;
                }else {
                    dataPoints.remove(j);
                    j--;
                }
            }

        }
        return trajectories;
    }
}
