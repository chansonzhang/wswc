package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public abstract class StayPointGenerateStrategy {
    public static final int TIME_INTERVAL = PrivacyUtils.TIME_INTERVAL;//每隔8s发布一次数据
    public abstract List<StayPoint> generateStayPoints(List<Trajectory> trajectories, int timeThreshold,
                                                       int distanceThreshold, int leaveThreshold);
}
