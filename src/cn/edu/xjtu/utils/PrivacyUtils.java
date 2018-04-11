package cn.edu.xjtu.utils;

import cn.edu.xjtu.algorithm.*;
import cn.edu.xjtu.algorithm.changeName.*;
import cn.edu.xjtu.algorithm.kanonymity.Kanony;
import cn.edu.xjtu.models.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangchen on 2015/8/6.
 * v14
 */
public class PrivacyUtils {
    public static final int TIME_INTERVAL = PrivacyConfig.getInstance().getTimeInterval();//每隔8s发布一次数据
    public static List<Trajectory> protectPrivacy(List<Trajectory> trajectories)
    {
        int methodType=PrivacyConfig.getInstance().getProtectMethod();
        PrivacyStrategy strategy;
        switch (methodType)
        {
            case 1:
                strategy=new ChangeNameOnly();
                break;
            case 2:
                strategy=new KanonymityOnly();
                break;
            case 3:
                strategy=new ChangeNameFirst();
                break;
            case 4:
                strategy=new KnanonymityFirst();
                break;
            default:
                strategy=new ChangeNameOnly();
                break;
        }
        PrivacyProtector protector=new PrivacyProtector(strategy);
        List<Trajectory> result=protector.protect(trajectories);
        return result;
    }
    public static List<Trajectory> kNonymity(List<Trajectory> trajectories){
        System.out.println("开始k匿名……");
        long time0=System.currentTimeMillis();
        String date = trajectories.get(0).getDataPoints().get(0).getTime().toString().substring(0,10);
       // int scan_interval = 3600;
        Timestamp t0 = trajectories.get(0).getDataPoints().get(0).getTime();
        int size = trajectories.get(0).getDataPoints().size();
        Timestamp t24 = trajectories.get(0).getDataPoints().get(size - 1).getTime();
        for (Trajectory trajectory :trajectories) {
        	Timestamp timestamp0 = trajectory.getDataPoints().get(0).getTime();
        	size = trajectory.getDataPoints().size();
        	Timestamp timestamp24 = trajectory.getDataPoints().get(size - 1).getTime();
        	if (timestamp0.before(t0)) t0 = timestamp0;
        	if (timestamp24.after(t24)) t24 = timestamp24;
        }
        List<DataPoint> dp = new ArrayList<>();
    //  Timestamp t0 = Timestamp.valueOf(date + " 00:00:00");
    //  Timestamp t24 = Timestamp.valueOf(date + " 24:00:00");
        double cen_long = 0;
        double cen_lat = 0;
        long count = 0;   //统计数据点总数
        for (int i=0;i <trajectories.size();i++){
        	for (int j = 0; j<trajectories.get(i).getDataPoints().size();j++){
        		count++;
        		cen_long = trajectories.get(i).getDataPoints().get(j).getLocation().getLongitude()+cen_long;
        		cen_lat = trajectories.get(i).getDataPoints().get(j).getLocation().getLatitude() + cen_lat;
        	}
        }
        cen_lat = cen_lat / count;
        cen_long = cen_long /count;
        double ave_lat = PrivacyConfig.getInstance().getBc() / 1000.0 /111;
        double ave_lon = PrivacyConfig.getInstance().getBc() /1000.0 /111;
        double min_lon = cen_long - PrivacyConfig.getInstance().getLeng() /1000.0 /111;
        double min_lat = cen_lat - PrivacyConfig.getInstance().getLeng() /1000.0 /111;
        double max_lon = cen_long + PrivacyConfig.getInstance().getLeng() /1000.0 /111;
        double max_lat = cen_lat + PrivacyConfig.getInstance().getLeng() /1000.0 /111;
        int m = (int) ((max_lon - min_lon) / ave_lon);
        int n = (int) ((max_lat - min_lat) / ave_lat);
        int count1 = 0;    //统计删除异常点后的数据点总数
        Grid grid = new Grid(new Location(min_lon,min_lat), new Location(max_lon, max_lat));
        for (int i=0 ;i<trajectories.size(); i++){
        	for(int j=0;j<trajectories.get(i).getDataPoints().size();j++){
        		if(trajectories.get(i).getDataPoints().get(j).isInGrid(grid))
        			count1++;
        	}
        }
        double sum=0;
        double mis=0;
        double con = 0;
        for (Timestamp ts = t0; ts.before(t24); ts.setTime(ts.getTime() + PrivacyConfig.getInstance().getInterval()*1000)) {
            dp = Kanony.getDataPoints(trajectories, ts,PrivacyConfig.getInstance().getInterval());      //用轨迹找到的点
            List<Double> doubles = Kanony.getkdataPoints(dp,PrivacyConfig.getInstance().getK(),min_lon,min_lat,ave_lon,ave_lat,m-1,n-1);
            sum+=doubles.get(0);
            mis+=doubles.get(1);
            con+=doubles.get(2);
        }
        System.out.println("数据点个数："+count1);
        LogUtils.getInstance().append("数据点个数："+count1);
        System.out.println("匿名率："+sum/con);
        LogUtils.getInstance().append("匿名率："+sum/con);
        System.out.println("匿名误差："+mis/sum);
        LogUtils.getInstance().append("匿名误差："+mis/sum);
        long time1=System.currentTimeMillis();
        System.out.println("k匿名耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("k匿名耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        return trajectories;
    }
    
    public static List<Trajectory> changeName(List<Trajectory> trajectories) {
    	System.out.println("开始换名……");
    	long time0=System.currentTimeMillis();
        NameChanger changer=new NameChanger(new EnterChange());
        List<Trajectory> result=changer.change(trajectories,PrivacyConfig.getInstance().getChangeTimeLimit());
        long time1=System.currentTimeMillis();
        System.out.println("换名耗时："+(time1-time0)/1000/60+"min"+((time1-time0)/1000)%60+"s");
        LogUtils.getInstance().append("换名耗时："+(time1-time0)/1000/60+"min"+((time1-time0)/1000)%60+"s");
        return result;
    }

    public static List<StayArea> getStayArea(String date,List<SPCluster> spClusters)
    {
        System.out.println("开始生成停留区……");
        long time0=System.currentTimeMillis();
        List<StayArea> stayAreas=TSAUtils.getStayArea(date,spClusters);
        long time1=System.currentTimeMillis();
        System.out.println("一共生成了"+spClusters.size()+"个停留区,耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("一共生成了"+spClusters.size()+"个停留区,耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        return stayAreas;
    }
    public static List<StayPoint> getStayPoints(List<Trajectory> trajectories) {
        StayPointGenerator stayPointGenerator=new StayPointGenerator(new VaryingIntervalMassCenter());
        System.out.println("开始生成停留点");
        long time0=System.currentTimeMillis();
        List<StayPoint> stayPoints=stayPointGenerator.generate(trajectories, PrivacyConfig.getInstance().getTimeThreshold(), PrivacyConfig.getInstance().getDistanceThreshold(), PrivacyConfig.getInstance().getLeaveThreshold());
        long time1=System.currentTimeMillis();
        System.out.println("一共生成了"+stayPoints.size()+"个停留点，耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("一共生成了"+stayPoints.size()+"个停留点，耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        return stayPoints;
    }

    public static List<SPCluster> getSpcs(List<StayPoint> stayPoints) {
        System.out.println("开始聚类……");
        long time0=System.currentTimeMillis();
        List<SPCluster> spClusters=Dbscan.getSpcs(stayPoints);
        long time1=System.currentTimeMillis();
        System.out.println("一共生成了"+spClusters.size()+"个聚类结果,耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("一共生成了"+spClusters.size()+"个聚类结果,耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        return spClusters;
    }

    public static List<Trajectory> getTrajectories(List<DataPoint> dataPoints)
    {
        TrajectoryGenerator generator=new TrajectoryGenerator();
        List<Trajectory> trajectories=generator.getTrajectories2(dataPoints);
        return SamplingUtils.sampling(trajectories);
    }

    public static List<Trajectory> getTrajectories(String[] usrIds) {
        //去重
        Set<String> uids = new HashSet<>();
        for (int i = 0; i < usrIds.length; i++) {
            uids.add(usrIds[i]);
        }
        List<Trajectory> trajectories = PrivacyUtils.getTrajectories();
        List<Trajectory> results = new ArrayList<>();
        for (Trajectory t : trajectories) {
            for (String usrId : uids) {
                if (t.getDataPoints().get(0).getUserId().equals(usrId)) {
                    results.add(t);
                }
            }
        }
        return results;
    }

    public static List<Trajectory> getTrajectories() {
        String date = FileUtils.findDays(1)[0];
        System.out.println("日期：" + date);
        System.out.println("开始读取数据……");
        List<DataPoint> dataPoints=FileUtils.getDataPoints(date);
        System.out.print("一共读取了" + dataPoints.size() + "个原始数据点,");
        LogUtils.getInstance().append("一共读取了" + dataPoints.size() + "个原始数据点,");
        TrajectoryGenerator trajectoryGenerator=new TrajectoryGenerator();
        List<Trajectory> trajectories=trajectoryGenerator.getTrajectories2(dataPoints);
        return trajectories;
    }



    public static <T> T randomPick(Collection<T> collection) {
    	if(collection.size()==0) return null;
        Random random = new Random();
        int i_random = Math.abs(random.nextInt()) % collection.size();
        T result = null;
        Iterator it = collection.iterator();
        for (int i = 0; i <= i_random; i++) {
            result = (T) it.next();
        }
        return result;
    }

    public static List<String> getUserIds(List<Trajectory> trajectories) {
        Set<String> usrIds = new HashSet<>();
        for (Trajectory trajectory : trajectories) {
            usrIds.add(trajectory.getDataPoints().get(0).getUserId());
        }
        return new ArrayList<>(usrIds);
    }

    

    /**
     * 是否在某个停留区内
     *
     * @param dataPoint
     * @param stayAreas
     * @return
     * @author zhangchen
     */
    public static boolean is_in_any_StayArea(DataPoint dataPoint, List<StayArea> stayAreas) {
        return pass_by_which_StayArea(dataPoint, stayAreas) != null;
    }

    /**
     * 查找给定时刻给定用户在那个停留区内
     *
     * @param ts
     * @return
     * @author zhangchen
     */
    
    public static TempStayArea contain_by_which_TempStayArea(Timestamp ts, StayArea stayArea) {
        
        
                    for (TempStayArea tmpSA : stayArea.getTmpSAs()) {
                        if(tmpSA.getMoment().before(ts)) continue;
                        if(tmpSA.getMoment().getTime()-ts.getTime()>TSAUtils.SCAN_INTERVAL*1000) break;
                        return tmpSA;
                    
        }
        return null;
    }
    /**
     * 经过哪个停留区
     *
     * @param dataPoint
     * @param stayAreas
     * @return
     * @author zhangchen
     */
    public static StayArea pass_by_which_StayArea(DataPoint dataPoint, List<StayArea> stayAreas) {
        for (int i=0;i<stayAreas.size();i++) {
            StayArea stayArea=stayAreas.get(i);
                    Location sw = stayArea.getSouthWest();
                    Location ne = stayArea.getNorthEast();
                    Location loc = dataPoint.getLocation();
                    Timestamp ts = dataPoint.getTime();
                    if(stayArea.getEndTime().before(ts)){
                    	stayAreas.remove(i);
                        i--;
                    	continue;
                    }
                    if (loc.getLongitude() >= sw.getLongitude()
                            && loc.getLongitude() <= ne.getLongitude()
                            && loc.getLatitude() >= sw.getLatitude()
                            && loc.getLatitude() <= ne.getLatitude()
                            && (ts.after(stayArea.getBeginTime()) || ts.equals(stayArea.getBeginTime())) && (ts.before(stayArea.getEndTime()) || ts.equals(stayArea.getEndTime()))
                            )
                        return stayArea;

        }
        return null;
    }

    public static List<StayArea> sortSA_byBeginTime(List<StayArea> stayAreas) {
        stayAreas.sort(new Comparator<StayArea>() {
            @Override
            public int compare(StayArea o1, StayArea o2) {
                long t1 = o1.getBeginTime().getTime();
                long t2 = o2.getBeginTime().getTime();
                if (t1 < t2) return -1;
                else if (t1 > t2) return 1;
                else return 0;
            }
        });
        return stayAreas;
    }

    public static Trajectory getTrajectory_by_userId(String userId, List<Trajectory> trajectories) {
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getDataPoints().get(0).getUserId().equals(userId))
                return trajectory;
        }
        return null;
    }
}
