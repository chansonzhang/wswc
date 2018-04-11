package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public class ChangeNameOnly extends PrivacyStrategy {
    @Override
    public List<Trajectory> protectPrivacy(List<Trajectory> trajectories) {
        return PrivacyUtils.changeName(trajectories);
    }
}
