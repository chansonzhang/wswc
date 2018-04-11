package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public class FixedIntervalShapeCenter extends StayPointGenerateStrategy {
    @Override
    /**
     * 处理的数据具有统一的时间间隔，由常数TIME_INTERVAL指定
     *
     * @param trajectories      所有的轨迹
     * @param timeThreshold     时间阈值：单位min
     * @param distanceThreshold 距离阈值：单位m
     * @param leaveThreshold    离开区域时间:单位s
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
                int lastDataPointIndex = dataPointIndex + (int) (timeThreshold * 60 / TIME_INTERVAL);
                boolean flag = false;
                for (int i = lastDataPointIndex + 1; i <= lastDataPointIndex + leaveThreshold / TIME_INTERVAL; i++) {
                    if (i > dataPoints.size() - 1) break;
                    if (MapUtils.distance(firstDataPoint.getLocation(), dataPoints.get(i).getLocation()) < 2 * distanceThreshold) {
                        lastDataPointIndex = i;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    DataPoint lastDataPoint = dataPoints.get(lastDataPointIndex);
                    Location centerLocation =
                            new Location(
                                    (lastDataPoint.getLocation().getLongitude()
                                            + firstDataPoint.getLocation().getLongitude()) / 2
                                    , (lastDataPoint.getLocation().getLatitude()
                                    + firstDataPoint.getLocation().getLatitude()) / 2);
                    double leave = 0; //离开时间
                    for (int i = firstDataPointIndex + 1; i < lastDataPointIndex + 1; i++) {
                        double distance = MapUtils.distance(dataPoints.get(i).getLocation(), centerLocation);
                        if (leave > leaveThreshold) {
                            flag = false;
                            break;
                        } else if (distance > distanceThreshold)
                            leave += TIME_INTERVAL;
                        else
                            leave = 0;
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
                        newStayPoint.addDataPoints(dataPoints.subList(firstDataPointIndex, lastDataPointIndex));
                        //调用成员函数addDataPoints后，中心可能会更新
                        centerLocation = newStayPoint.getLocation();
                        //解决短时间内出去又进来的问题，比如我在西一楼，中途出去买了瓶水，那么西一楼依然算是我的停留点
                        leave = 0; //离开时间为0
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
                            } else {
                                leave += TIME_INTERVAL;
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
