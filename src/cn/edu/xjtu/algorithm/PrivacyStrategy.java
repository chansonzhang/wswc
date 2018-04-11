package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/17.
 */
public abstract class PrivacyStrategy {
    public abstract List<Trajectory> protectPrivacy(List<Trajectory> trajectories);
}
