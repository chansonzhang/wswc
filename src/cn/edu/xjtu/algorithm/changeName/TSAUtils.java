package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.SPCluster;
import cn.edu.xjtu.models.StayArea;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.models.TempStayArea;
import cn.edu.xjtu.utils.PrivacyConfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangchen on 2015/10/30.
 */
public class TSAUtils {
    public static final int SCAN_INTERVAL = PrivacyConfig.getInstance().getTimeInterval(); //扫描间隔，单位s
    private static final int DURATION_THRESHOLD = PrivacyConfig.getInstance().getDURATION_THRESHOLD(); //最小持续时间，单位s
    private static final int CROWD_THRESHOLD = PrivacyConfig.getInstance().getCROWD_THRESHOLD();//最低人数阈值

    /**
     * @param date 扫描的日期，格式必须为"2008-10-23"这样的字符串
     * @return
     */
    public static List<StayArea> getStayArea(String date,List<SPCluster> spClusters) {
        List<StayArea> sas = new ArrayList<>();
        Iterator it = spClusters.iterator();
        while (it.hasNext()) {
            SPCluster spc = (SPCluster) it.next();

            Timestamp t0 = Timestamp.valueOf(date + " 00:00:00");
            Timestamp t24 = Timestamp.valueOf(date + " 24:00:00");

            //find specific scan section
            Timestamp minBeginTime=new Timestamp(t24.getTime());
            Timestamp maxEndTime=new Timestamp(t0.getTime());
            for(StayPoint stayPoint:spc.getStayPoints()){
                Timestamp beginTime=stayPoint.getBeginTime();
                Timestamp endTime=stayPoint.getEndTime();
                if(beginTime.before(minBeginTime))
                    minBeginTime.setTime(beginTime.getTime());
                if(endTime.after(maxEndTime))
                    maxEndTime.setTime(endTime.getTime());
            }
            t0.setTime(minBeginTime.getTime());
            t24.setTime(maxEndTime.getTime());

            for (Timestamp ts = t0; ts.before(t24); ts.setTime(ts.getTime() + SCAN_INTERVAL * 1000)) {
                Timestamp currentTime=new Timestamp(ts.getTime());
                boolean flag = false;
                TempStayArea tmpSA=null;
                List<TempStayArea> tmpSAs=new ArrayList<>();
                StayArea newSA=null;
                tmpSA= TimeCluster.getTmpSA(spc, currentTime.toString(), CROWD_THRESHOLD);
                if (tmpSA != null)
                {
                    flag = true;
                    tmpSAs.add(tmpSA);
                }
                else continue;
                while (flag) {
                    flag = false;
                    currentTime.setTime(currentTime.getTime() + SCAN_INTERVAL * 1000);
                    tmpSA = TimeCluster.getTmpSA(spc, currentTime.toString(), CROWD_THRESHOLD);
                    if (tmpSA != null)
                    {
                        flag = true;
                        tmpSAs.add(tmpSA);
                    }
                }
                Timestamp prev=new Timestamp(currentTime.getTime()-SCAN_INTERVAL*1000);
                if((prev.getTime()-ts.getTime())>DURATION_THRESHOLD*1000)
                {
                    newSA=new StayArea(spc.getSouthWest(),spc.getNorthEast(),ts,prev,tmpSAs);
                    sas.add(newSA);
                    ts.setTime(currentTime.getTime());
                }
            }
        }
        return sas;
    }

}
