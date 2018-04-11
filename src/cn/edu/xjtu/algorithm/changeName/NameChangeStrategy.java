package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public abstract class NameChangeStrategy {
    public abstract List<Trajectory> changeName(List<Trajectory> trajectories,int changeTimeLimit);
}
