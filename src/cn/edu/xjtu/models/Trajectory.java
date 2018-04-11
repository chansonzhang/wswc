package cn.edu.xjtu.models;

import cn.edu.xjtu.utils.PrivacyUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchen on 2015/8/6.
 */
public class Trajectory {
    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
    private String userId;
    
    public String getUserId() {
		return userId;
	}

	public void setUserId(List<DataPoint> dp) {
		this.userId = dp.get(0).getUserId();
	}
	private List<DataPoint> dataPoints;
    private Map<String,Double> probabilities; 
    public Trajectory() {
        this.dataPoints = new ArrayList<DataPoint>();
        this.probabilities=new HashMap<String,Double>();
    }

    public boolean addDataPoint(DataPoint d) {
        return dataPoints.add(d);
    }

    public boolean removeDataPoint(DataPoint dataPoint) {
        this.dataPoints.remove(dataPoint);
        return true;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }



	public Map<String, Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(String key,Double value) {
		this.probabilities.put(key, value);
	}

	/**
     * 给出指定时间的数据点
     *
     * @param timestamp
     * @return
     */
    public DataPoint getDataPoint_of_given_time(Timestamp timestamp,int beginIndex) {
    	if(beginIndex<0) return null;
    	for(int i=beginIndex;i<dataPoints.size();i++){
    		DataPoint dp=dataPoints.get(i);
    		 if (dp.getTime().before(timestamp)) continue;
             if (dp.getTime().getTime() - timestamp.getTime() > PrivacyUtils.TIME_INTERVAL*1000) break;
             //因为间隔未对齐造成的差值最多为一个interval
             return dp;
    	}
           
        return null;
    }
}
