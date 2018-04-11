package cn.edu.xjtu.models;

import cn.edu.xjtu.utils.PrivacyUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangchen on 2015/8/6.
 */
public class StayPoint {
    private static int updateThreshold=50;
    private Location location;
    private Timestamp beginTime;
    private Timestamp endTime;
    private List<DataPoint> dataPoints=new ArrayList<>();

    public StayPoint() {      //-----------后期加的
    }

    public  StayPoint(Location location){
        this.location = location;
    }


    public StayPoint(StayPoint s) {
        this.location=new Location(s.location);
        this.beginTime=new Timestamp(s.beginTime.getTime());
        this.endTime=new Timestamp(s.endTime.getTime());
        this.dataPoints=new ArrayList<>();
        for(DataPoint dp:s.getDataPoints())
        {
            this.dataPoints.add(dp);
        }
    }

    public Location getLocation() {
        return location;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public Timestamp getEndTime() {

        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = new Timestamp(endTime.getTime());
    }

    public Timestamp getBeginTime() {

        return beginTime;
    }


    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = new Timestamp(beginTime.getTime());
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints=new ArrayList<>(Arrays.asList(new DataPoint[dataPoints.size()]));
        Collections.copy(this.dataPoints,dataPoints);
    }

    /**
     *
     * @param dataPoints
     * @return
     */
    public boolean addDataPoints(List<DataPoint> dataPoints) {
        for(int i=0;i<dataPoints.size();i++)
        {
            addDataPoint(dataPoints.get(i));
        }
        return true;
    }

    /**
     *
     * @param dataPoint
     * @return
     */
    public boolean addDataPoint(DataPoint dataPoint) {
        this.dataPoints.add(dataPoint);
        //每50个点重新计算一次中心
        if (this.dataPoints.size()%updateThreshold==0)
            updateLocation_mass();
        return false;
    }

    /**
     * 重新计算StayPoint的形心
     * @return 返回更新后的Location
     */
    public Location updateLocation()
    {
        List<DataPoint> dataPoints=this.dataPoints;
        double maxLongitude = 0, maxLatitude = 0, minLongitude = 180, minLatitude = 90;
        if (dataPoints == null || dataPoints.size() == 0) return new Location(0, 0);
        for (int i = 0; i < dataPoints.size(); i++) {
            double currentLongitude = dataPoints.get(i).getLocation().getLongitude();
            double currentLatitude = dataPoints.get(i).getLocation().getLatitude();
            if (currentLongitude > maxLongitude)
                maxLongitude = currentLongitude;
            else if (currentLongitude < minLongitude)
                minLongitude = currentLongitude;

            if (currentLatitude > maxLatitude)
                maxLatitude = currentLatitude;
            else if (currentLatitude < minLatitude)
                minLatitude = currentLatitude;
        }
        this.location=new Location((maxLongitude + minLongitude) / 2, (maxLatitude + minLatitude) / 2);
        return this.location;
    }

    public void updateLocation_mass()
    {
        int totalSize=dataPoints.size();
        double oldLong=this.location.getLongitude();
        double oldLat=this.location.getLatitude();
        int calculatedNum=dataPoints.size()-updateThreshold;
        double addedLong=0;
        double addedLat=0;
        for(int i=calculatedNum;i<dataPoints.size();i++){
            Location loc=dataPoints.get(i).getLocation();
            addedLong+=loc.getLongitude();
            addedLat+=loc.getLatitude();
        }
        double newLong=(oldLong*calculatedNum+addedLong)/totalSize;
        double newLat=(oldLat*calculatedNum+addedLat)/totalSize;
        this.location=new Location(newLong,newLat);
    }
    
    public String getCurrentUserId(Timestamp timestamp){
    	for(int i=0;i<dataPoints.size();i++){
    		DataPoint dp=dataPoints.get(i);
    		if(dp.getTime().before(timestamp)) continue;
    		if(dp.getTime().getTime()-timestamp.getTime()>PrivacyUtils.TIME_INTERVAL*1000) break;
    		//因为时间间隔未对齐造成的差值最多为一个interval
    		return dp.getUserId();
    	}
    	return null;
    }
}
