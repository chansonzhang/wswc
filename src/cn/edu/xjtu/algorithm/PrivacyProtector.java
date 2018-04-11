package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class PrivacyProtector {
    private PrivacyStrategy privacyStrategy;

    public PrivacyProtector(PrivacyStrategy privacyStrategy) {
        this.privacyStrategy = privacyStrategy;
    }

    public List<Trajectory> protect(List<Trajectory> trajectories)
    {
        return privacyStrategy.protectPrivacy(trajectories);
    }
}
