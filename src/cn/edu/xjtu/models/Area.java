package cn.edu.xjtu.models;

/**
 * Created by zhangchen on 2015/10/30.
 */
public class Area {

    private Location southWest;
    private Location northEast;

    public Area(Location southWest,Location northEast)
    {
        this.southWest=new Location(southWest);
        this.northEast=new Location(northEast);
    }

    public Location getSouthWest() {
        return southWest;
    }
    public void setSouthWest(Location southWest) {
        this.southWest = new Location(southWest);
    }
    public Location getNorthEast() {
        return northEast;
    }
    public void setNorthEast(Location northEast) {
        this.northEast = new Location(northEast);
    }
}
