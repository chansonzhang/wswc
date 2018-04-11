package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.Trajectory;

import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class ResultWriter {
    ResultWriteStrategy strategy;

    public ResultWriter(ResultWriteStrategy strategy) {
        this.strategy = strategy;
    }

    public void write(String resultDir,List<Trajectory> trajectories)
    {
        strategy.write(resultDir,trajectories);
    }
}
