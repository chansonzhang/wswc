package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.similarity.TrajectoryDistance;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public class KnanonymityFirst extends PrivacyStrategy {
    @Override
    public List<Trajectory> protectPrivacy(List<Trajectory> trajectories) {
        List<Trajectory> trajectories1= PrivacyUtils.kNonymity(trajectories);
        int measure=PrivacyConfig.getInstance().getMeasure();
        if(measure==1)
        	TrajectoryDistance.TraShapeDis(trajectories, trajectories1);
        List<Trajectory> trajectories2=PrivacyUtils.changeName(trajectories1);
        return trajectories2;
    }
}
