package cn.edu.xjtu.models;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangchen on 2015/9/25.
 */
public class StayArea extends Area{
   /* public Set<StayPoint> getStayPoints() {
        return stayPoints;
    }

    Set<StayPoint> stayPoints=new HashSet<>();*/
    public List<TempStayArea> getTmpSAs() {
        return tmpSAs;
    }

    List<TempStayArea> tmpSAs;
    private Timestamp beginTime;
    private Timestamp endTime;
    //private Set<String> users

    public StayArea(Location southWest,Location northEast,Timestamp bt,Timestamp et, List<TempStayArea> tmpSAs)
    {
        super(southWest,northEast);
        this.beginTime=new Timestamp(bt.getTime());
        this.endTime=new Timestamp(et.getTime());
        this.tmpSAs=new ArrayList<>(Arrays.asList(new TempStayArea[tmpSAs.size()]));
        Collections.copy(this.tmpSAs,tmpSAs);
        /*for(TempStayArea tmpSA:tmpSAs)
        {
            for(StayPoint sp:tmpSA.getStayPoints())
            {
                this.stayPoints.add(sp);
            }
        }*/
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = new Timestamp(beginTime.getTime());
    }
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime = new Timestamp(endTime.getTime());
    }

    /**
     * 判断一个停留区中是否包含了某个用户
     * @param usrId 用户id
     * @return
     */
    public boolean contain(String usrId)
    {
        for(TempStayArea tmp_sa:this.getTmpSAs())
        {
            for(StayPoint sp:tmp_sa.getStayPoints())
            {
                for (DataPoint dp:sp.getDataPoints())
                {
                    if(dp.getUserId().equals(usrId))
                        return true;
                }
            }
        }
        return false;
    }

    public void setTmpSAs(List<TempStayArea> tmpSAs) {
        this.tmpSAs=new ArrayList<>(Arrays.asList(new TempStayArea[tmpSAs.size()]));
        Collections.copy(this.tmpSAs,tmpSAs);
    }
}
