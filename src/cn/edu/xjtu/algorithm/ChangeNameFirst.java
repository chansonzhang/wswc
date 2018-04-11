package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.similarity.TrajectoryDistance;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public class ChangeNameFirst extends PrivacyStrategy{
    @Override
    public List<Trajectory> protectPrivacy(List<Trajectory> trajectories) {
        List<Trajectory> trajectories1= PrivacyUtils.changeName(trajectories);
        List<Trajectory> trajectories2=PrivacyUtils.kNonymity(trajectories1);
        int measure=PrivacyConfig.getInstance().getMeasure();
        if(measure==1)
        	TrajectoryDistance.TraShapeDis(trajectories1, trajectories2);
        return trajectories2;
    }
}
