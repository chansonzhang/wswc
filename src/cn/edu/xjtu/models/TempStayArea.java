package cn.edu.xjtu.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangchen on 2015/10/30.
 * 表示在某一时刻的停留区
 */
public class TempStayArea extends Area{
    private Timestamp moment;

    public List<StayPoint> getStayPoints() {
        return stayPoints;
    }

    private List<StayPoint> stayPoints;
    /**
     *
     * @param moment
     * @param stayPoints
     * @param ne
     * @param sw
     */
    public TempStayArea(Timestamp moment,List<StayPoint> stayPoints,Location ne,Location sw) {
        super(sw,ne);
        this.setMoment(moment);
        this.stayPoints= new ArrayList<>(Arrays.asList(new StayPoint[stayPoints.size()]));
        Collections.copy(this.stayPoints,stayPoints);
    }

    public Timestamp getMoment() {
        return moment;
    }

    public void setMoment(Timestamp moment) {
        this.moment = new Timestamp(moment.getTime());
    }

    public void setStayPoints(List<StayPoint> stayPoints) {
        this.stayPoints= new ArrayList<>(Arrays.asList(new StayPoint[stayPoints.size()]));
        Collections.copy(this.stayPoints,stayPoints);
    }
}
