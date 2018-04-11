package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class StayPointGenerator {
    StayPointGenerateStrategy stayPointGenerateStrategy;

    public StayPointGenerator(StayPointGenerateStrategy stayPointGenerateStrategy) {
        this.stayPointGenerateStrategy = stayPointGenerateStrategy;
    }

    public List<StayPoint> generate(List<Trajectory> trajectories, int timeThreshold,
                                    int distanceThreshold, int leaveThreshold)
    {
        return stayPointGenerateStrategy.generateStayPoints(trajectories,timeThreshold,distanceThreshold,leaveThreshold);
    }
}
