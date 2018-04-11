package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public abstract class ResultWriteStrategy {
    public abstract void write(String directory,List<Trajectory> trajectories);
}
