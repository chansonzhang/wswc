package cn.edu.xjtu.models;

public class Point {
	private StayPoint staypoint;
	private Location location;  
	private boolean isKey;  
	private boolean isClassed;  
	public boolean isKey() {  
		return isKey;  
	}
	public void setKey(boolean isKey) {   
		this.isKey = isKey;  
		this.isClassed=true;  
	}  
 
	public boolean isClassed() {  
		return isClassed;  
	}  
 
	public void setClassed(boolean isClassed) {  
		this.isClassed = isClassed;  
	}   
	public double getLocLong() {  
		return location.getLongitude();  
	}  
	public double getLocLat(){
		return location.getLatitude();
	}
	
	public void setLocation(Location loc) {
		this.location = new Location(loc);
	}
	
	public StayPoint getStayPoint(){
		return staypoint;
	}
	
	
	public void setStayPoint(StayPoint s){
		this.staypoint = new StayPoint(s);
	}
}  