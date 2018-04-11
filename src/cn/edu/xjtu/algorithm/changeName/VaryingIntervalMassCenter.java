package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.MapUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public class VaryingIntervalMassCenter extends StayPointGenerateStrategy {
    @Override
    /**
     * 与旧方法的区别是采用了质心代替形心
     *
     * @param trajectories
     * @param timeThreshold 分钟
     * @param distanceThreshold 半径
     * @param leaveThreshold
     * @return
     */
    public List<StayPoint> generateStayPoints(List<Trajectory> trajectories, int timeThreshold,
                                                          int distanceThreshold, int leaveThreshold) {
        List<StayPoint> stayPoints = new ArrayList<>();
        for (Trajectory trajectory : trajectories) {
            List<DataPoint> dataPoints = trajectory.getDataPoints();
            for (int dataPointIndex = 0; dataPointIndex < dataPoints.size(); dataPointIndex++) {
                int firstDataPointIndex = dataPointIndex;
                DataPoint firstDataPoint = dataPoints.get(firstDataPointIndex);
                int lastDataPointIndex = firstDataPointIndex;
                //找到满足时间阈值的第一个点
                for (int i = firstDataPointIndex + 1; i < dataPoints.size(); i++) {
                    if (dataPoints.get(i).getTime().getTime() - firstDataPoint.getTime().getTime() >= timeThreshold * 60 * 1000) {
                        lastDataPointIndex = i;
                        break;
                    }
                }
                boolean flag = false;
                for (int i = lastDataPointIndex; ; i++) {
                    if (lastDataPointIndex == firstDataPointIndex)
                        break;//由于上一个for循环中直到最后一个数据点依然未找到满足时间阈值的点，此时不能生成停留点
                    if (i > dataPoints.size() - 1)
                        break;
                    //超过leaveThreshold还未回来
                    if (dataPoints.get(i).getTime().getTime() - firstDataPoint.getTime().getTime() > timeThreshold * 60 * 1000 + leaveThreshold * 1000)
                        break;
                    //在leaveThreshold之前回来了
                    if (MapUtils.distance(firstDataPoint.getLocation(), dataPoints.get(i).getLocation()) < 2 * distanceThreshold) {
                        lastDataPointIndex = i;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    DataPoint lastDataPoint = dataPoints.get(lastDataPointIndex);
                    Location centerLocation = MapUtils.getCenter_Mass(dataPoints.subList(firstDataPointIndex, lastDataPointIndex + 1));
                    double leave = 0; //离开时间
                    Timestamp origin = Timestamp.valueOf("1970-01-01 00:00:00");
                    Timestamp leaveTimestamp = origin;
                    for (int i = firstDataPointIndex; i < lastDataPointIndex + 1; i++) {
                        double distance = MapUtils.distance(dataPoints.get(i).getLocation(), centerLocation);
                        if (leave > leaveThreshold) {
                            flag = false;
                            break;
                        } else if (distance > distanceThreshold) {
                            if (leaveTimestamp == origin)//刚刚离开
                                leaveTimestamp = dataPoints.get(i).getTime();
                            leave = (dataPoints.get(i).getTime().getTime() - leaveTimestamp.getTime()) / 1000;
                        } else {
                            leave = 0;
                            leaveTimestamp = origin;
                        }
                    }
                    if (flag) {
                        dataPointIndex += (lastDataPointIndex - firstDataPointIndex);
                        StayPoint newStayPoint =
                                new StayPoint(centerLocation);
                        newStayPoint.setBeginTime(firstDataPoint.getTime());
                        if (lastDataPointIndex < dataPoints.size() - 1) {
                            newStayPoint.setEndTime(dataPoints.get(lastDataPointIndex + 1).getTime());
                        } else {
                            newStayPoint.setEndTime(dataPoints.get(lastDataPointIndex).getTime());
                        }
                        newStayPoint.addDataPoints(dataPoints.subList(firstDataPointIndex, lastDataPointIndex + 1));
                        //调用成员函数addDataPoints后，中心可能会更新
                        centerLocation = newStayPoint.getLocation();
                        //解决短时间内出去又进来的问题，比如我在西一楼，中途出去买了瓶水，那么西一楼依然算是我的停留点，
                        // 同时也在一定程度上解决了波动点的问题
                        leave = 0; //离开时间为0
                        leaveTimestamp = origin;
                        for (int j = lastDataPointIndex + 1; j < dataPoints.size(); j++) {
                            if (leave > leaveThreshold) break;
                            if (MapUtils.distance(dataPoints.get(j).getLocation(), centerLocation) <= distanceThreshold) {
                                dataPointIndex++;
                                if (j == dataPoints.size() - 1)
                                    newStayPoint.setEndTime(dataPoints.get(j).getTime());
                                else
                                    newStayPoint.setEndTime(dataPoints.get(j + 1).getTime());
                                newStayPoint.addDataPoint(dataPoints.get(j));
                                centerLocation = newStayPoint.getLocation();
                                leave = 0;
                                leaveTimestamp = origin;
                            } else {
                                if (leaveTimestamp == origin)//刚刚离开
                                    leaveTimestamp = dataPoints.get(j).getTime();
                                leave = (dataPoints.get(j).getTime().getTime() - leaveTimestamp.getTime()) / 1000;
                            }
                        }
                        stayPoints.add(newStayPoint);
                    }
                }
            }
        }
        return stayPoints;
    }
}
