package cn.edu.xjtu.models;

import java.util.ArrayList;
import java.util.Iterator;

public class SPCluster {
    private ArrayList<StayPoint> stayPoints=new ArrayList<>();
    private Location southWest;
    private Location northEast;


    public ArrayList<StayPoint> getStayPoints(){
        return stayPoints;
    }

    public void setStayPoints(ArrayList<StayPoint> st){
        for(Iterator<StayPoint> it = st.iterator();it.hasNext();){
            StayPoint s1 = it.next();
            stayPoints.add(s1);
        }
    }

    public void setSouthWest(Location loc){

        this.southWest=new Location(loc);
    }

    public void setNorthEast(Location loc){

        this.northEast=new Location(loc);

    }

    public void addStayPoints(StayPoint s1){
        stayPoints.add(s1);
    }

    public Location getSouthWest() {
        return southWest;
    }

    public Location getNorthEast() {
        return northEast;
    }
}
