package cn.edu.xjtu.models;

import java.sql.Timestamp;

/**
 * Created by zhangchen on 2015/8/6.
 */
public class DataPoint {
    private String userId;
    private Location location;
    private Timestamp time;
    
    //the following two attributes is a record of data index in source file,
    //so we can write the modified data back without reading all the attribute to memory!
    private String fileName;
    private int lineNumber;

    public DataPoint(String userId,Location location,Timestamp time,String fileName,int lineNumber){
    	this.userId=userId;
    	this.location=new Location(location);
    	this.time=new Timestamp(time.getTime());
    	this.fileName=fileName;
    	this.lineNumber=lineNumber;
    }
    
    public DataPoint(String userId, Location location) {
        this.userId = userId;
        this.location = location;
    }

    public DataPoint(String userId, Location location, Timestamp time) {
        this.userId = userId;
        this.location = new Location(location);
        this.time = new Timestamp(time.getTime());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = new Location(location);
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = new Timestamp(time.getTime());
    }

    public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public boolean isInGrid(Grid gd){      //后期加的
        if(this.getLocation().getLongitude()>=gd.getsouthWest().getLongitude()&&this.getLocation().getLatitude()>=gd.getsouthWest().getLatitude()&&this.getLocation().getLatitude()<gd.getnorthEast().getLatitude()&&this.getLocation().getLongitude()<gd.getnorthEast().getLongitude()) {
            return true;}
        else
            return false;
    }
}
