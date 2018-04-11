package cn.edu.xjtu.utils;

import cn.edu.xjtu.algorithm.PrivacyProtector;
import cn.edu.xjtu.algorithm.ChangeNameOnly;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.models.Area;
import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.StayPoint;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangchen on 2015/11/24.
 */
public class ExperimentUtils {
    public static void main(String[] args) {
        //CountTest();
    }

    /*public static void CountTest() {
        System.out.println("开始统计实验……");
        List<Trajectory> originTs = PrivacyUtils.getTrajectories();
        List<Trajectory> deepCopyOriginTs = CommonUtils.deepCopyTrajectoryList(originTs);
        PrivacyProtector protector=new PrivacyProtector(new ChangeNameOnly());
        List<Trajectory> changedTs = protector.protect(deepCopyOriginTs);
        Area overall = ExperimentUtils.getOverallArea(originTs);
        String date = originTs.get(0).getDataPoints().get(0).getTime().toString().substring(0, 10);
        int testNum = 10;
        for (int i = 0; i < testNum; i++) {
            Area randomArea = getCornerArea(overall);
            Timestamp randomTS1 = getRandomTime(date);
            Timestamp randomTS2 = getRandomTime(date);
            Timestamp bt, et;
            if (randomTS1.before(randomTS2)) {
                bt = randomTS1;
                et = randomTS2;
            } else {
                bt = randomTS2;
                et = randomTS1;
            }
            int number1 = count(originTs, bt, et, randomArea);
            int number2 = count(changedTs, bt, et, randomArea);
            System.out.println("换名前用户数：" + number1 + "\t换名后用户数：" + number2 + "\t误差：" + (double) (number2 - number1) / number1);

        }

        System.out.println("统计实验结束");
    }
*/
    /*public static void StayPointStatistic() {
        System.out.println("开始停留点统计统计实验……");
        List<Trajectory> originTs = PrivacyUtils.getTrajectories();
        List<StayPoint> stayPoints = PrivacyUtils.getStayPoints(originTs);
        Map<String, List<StayPoint>> statistics = stayPointStatistic(stayPoints);
        String[] users = new String[10];
        for (int i = 0; i < 10; i++) {
            String user = String.format("%03d", i);
            users[i] = user;
        }
        showStayPointStatistic(statistics, users);
        System.out.println("停留点统计统计实验结束.");
    }
*/
    /**
     * 统计每个用户的停留点
     *
     * @param stayPoints 要统计的停留区list
     * @return 返回一个map，key表示用户id，是长度为3的字符串，类似于"000"、"001".value是属于这个用户的所有StayPoint的一个list
     */
    /*public static Map<String, List<StayPoint>> stayPointStatistic(List<StayPoint> stayPoints) {
        Map<String, List<StayPoint>> statistics=new HashMap<>();
        Iterator<StayPoint> it=stayPoints.iterator();
        while(it.hasNext())
        {
            StayPoint stayPoint=it.next();
            if(!statistics.containsKey(stayPoint.getUserId()))
            {
                List<StayPoint> sps=new ArrayList<>();
                sps.add(stayPoint);
                statistics.put(stayPoint.getUserId(),sps);
            }
            else
            {
                statistics.get(stayPoint.getUserId()).add(stayPoint);
            }
        }
        return statistics;
    }
*/
    /**
     * 打印出给定用户的停留点时间长度（结束时间-开始时间）、开始时间、结束时间、位置坐标
     *
     * @param statistics 统计结果
     * @param users      要打印的用户列表
     */
    public static void showStayPointStatistic(Map<String, List<StayPoint>> statistics, String[] users) {
        Iterator iter = statistics.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String tp = (String) entry.getKey();
            if (Arrays.asList(users).contains(tp)) {
                System.out.println("用户：" + tp);
                System.out.println("序号\t位置\t持续时间\t开始时间\t结束时间");
                List<StayPoint> tp2 = (List<StayPoint>) entry.getValue();
                for (int i = 0; i < tp2.size(); i++) {
                    StayPoint tp3 = tp2.get(i);
                    Timestamp bgtm = tp3.getBeginTime();
                    Timestamp edtm = tp3.getEndTime();
                    Long edbg = edtm.getTime() - bgtm.getTime();
                    Location lctn = tp3.getLocation();
                    System.out.println((i + 1) + "\t" + "("+lctn.getLatitude()+","+lctn.getLongitude()+")"+ "\t" + edbg/1000/60+"min"+(edbg/1000)%60+"s" + "\t" + bgtm + "\t" + edtm);
                }
            }
        }
    }


    public static Area getOverallArea(List<Trajectory> trajectories) {
        double maxLongitude = 0, maxLatitude = 0, minLongitude = 180, minLatitude = 90;
        for (Trajectory trajectory : trajectories) {
            for (DataPoint dp : trajectory.getDataPoints()) {
                Location loc = dp.getLocation();
                double currentLongitude = loc.getLongitude();
                double currentLatitude = loc.getLatitude();
                if (currentLongitude > maxLongitude)
                    maxLongitude = currentLongitude;
                else if (currentLongitude < minLongitude)
                    minLongitude = currentLongitude;
                if (currentLatitude > maxLatitude)
                    maxLatitude = currentLatitude;
                else if (currentLatitude < minLatitude)
                    minLatitude = currentLatitude;
            }
        }
        Location southWest = new Location(minLongitude, minLatitude);
        Location northEast = new Location(maxLongitude, maxLatitude);
        return new Area(southWest, northEast);
    }

    public static Area getRandomArea(Area overall) {
        Random r = new Random();
        Location sw = overall.getSouthWest();
        Location ne = overall.getNorthEast();
        double minLng = sw.getLongitude();
        double minlat = sw.getLatitude();
        double maxLng = ne.getLongitude();
        double maxLat = ne.getLatitude();
        double lng1 = (maxLng - minLng) * r.nextDouble() + minLng;
        double lng2 = (maxLng - minLng) * r.nextDouble() + minLng;
        double lat1 = (maxLat - minlat) * r.nextDouble() + minlat;
        double lat2 = (maxLat - minlat) * r.nextDouble() + minlat;
        double west = Math.min(lng1, lng2);
        double east = Math.max(lng1, lng2);
        double south = Math.min(lat1, lat2);
        double north = Math.max(lat1, lat2);
        Location southWest = new Location(west, south);
        Location northEast = new Location(east, north);
        Area area = new Area(southWest, northEast);
        return area;
    }

    public static Area getCornerArea(Area overall) {
        Location sw = overall.getSouthWest();
        Location ne = overall.getNorthEast();
        double minLng = sw.getLongitude();
        double minlat = sw.getLatitude();
        double maxLng = ne.getLongitude();
        double maxLat = ne.getLatitude();
        double west = (minLng + maxLng) / 2;
        double south = (minlat + maxLat) / 2;
        Area area = new Area(new Location(west, south), overall.getNorthEast());
        return area;
    }

    public static Timestamp getRandomTime(String date) {
        Random r = new Random();
        Timestamp t0 = Timestamp.valueOf(date + " 00:00:00");
        Timestamp t24 = Timestamp.valueOf(date + " 24:00:00");
        long time1 = t0.getTime();
        long time2 = t24.getTime();
        long time = (long) ((time2 - time1) * r.nextDouble() + time1);
        Timestamp timestamp = new Timestamp(time);
        return timestamp;
    }

    public static int count(List<Trajectory> trajectories, Timestamp ts, Area area) {
        List<DataPoint> dataPoints = new ArrayList<>();
        Location sw = area.getSouthWest();
        Location ne = area.getNorthEast();
        for (Trajectory trajectory : trajectories) {
            for (DataPoint dataPoint : trajectory.getDataPoints()) {
                if (dataPoint.getTime().before(ts)) continue;
                if (dataPoint.getTime().after(ts) && dataPoint.getTime().getTime() - ts.getTime() > PrivacyUtils.TIME_INTERVAL * 1000)
                    break;
                Location loc = dataPoint.getLocation();
                if (loc.getLongitude() >= sw.getLongitude()
                        && loc.getLongitude() < ne.getLongitude()
                        && loc.getLatitude() >= sw.getLatitude()
                        && loc.getLatitude() < ne.getLatitude())
                    dataPoints.add(dataPoint);
            }
        }
        return dataPoints.size();
    }

    public static int count(List<Trajectory> trajectories, Timestamp bt, Timestamp et, Area area) {
        List<DataPoint> dataPoints = new ArrayList<>();
        Location sw = area.getSouthWest();
        Location ne = area.getNorthEast();
        for (Trajectory trajectory : trajectories) {
            for (DataPoint dataPoint : trajectory.getDataPoints()) {
                if (dataPoint.getTime().after(bt) || dataPoint.getTime().equals(bt)) {
                    if (dataPoint.getTime().before(et)) {
                        Location loc = dataPoint.getLocation();
                        if (loc.getLongitude() >= sw.getLongitude()
                                && loc.getLongitude() < ne.getLongitude()
                                && loc.getLatitude() >= sw.getLatitude()
                                && loc.getLatitude() < ne.getLatitude())
                            dataPoints.add(dataPoint);
                    }

                }
            }
        }
        return dataPoints.size();
    }
}

