package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class NameChanger {
    NameChangeStrategy strategy;

    public NameChanger(NameChangeStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Trajectory> change(List<Trajectory> trajectories, int changeTimeLimit)
    {
        return strategy.changeName(trajectories,changeTimeLimit);
    }
}
