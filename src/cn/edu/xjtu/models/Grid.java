package cn.edu.xjtu.models;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by YGZ on 2015/10/21.
 */
public class Grid {
    private Location southWest;
    private Location northEast;
    private Location centerofPoints;         //质心
    private Location centerofGrid;
    private ArrayList<DataPoint> dataPoints = new ArrayList<>();
    private ArrayList<Location> randomPoint=new ArrayList<Location>();
    public Grid (){}
    public Grid(Location southWest,Location northEast){
        this.southWest = southWest;
        this.northEast = northEast;
    }

    public void setCenterofGrid(){
        this.centerofGrid = new Location((this.getsouthWest().getLongitude()+this.getnorthEast().getLongitude())/2,(this.getsouthWest().getLatitude()+this.getnorthEast().getLatitude())/2);
    }
    public Location getCenterofGrid(){
        return this.centerofGrid;
    }

    public void setRandom(){
    	int num=this.dataPoints.size();
    	ArrayList<Location> datapoint=new ArrayList<Location>();
    	for(int i=0;i<num;i++){
    		Location location=new Location(this.minLon()+(this.maxLon()-this.minLon())*Math.random(),
    				this.minLat()+(this.maxLat()-this.minLat())*Math.random());
    		datapoint.add(location);
    	}
    }
    
    public ArrayList<Location> getRandom(){
    	return this.randomPoint;
    }
    public void setcenterofPoints(){
        double lon = 0;
        double lat = 0;
        for (int i=0;i<this.dataPoints.size();i++){
            lon += this.dataPoints.get(i).getLocation().getLongitude();
            lat += this.dataPoints.get(i).getLocation().getLatitude();
        }
        this.centerofPoints = new Location(lon/this.dataPoints.size(),lat/this.dataPoints.size());
    }
    public Location getcenterofPoints(){
        return this.centerofPoints;
    }

    public Location getsouthWest() {
        return this.southWest;
    }
    public Location getnorthEast() {
        return this.northEast;
    }

    public void setSouthWest(Location loc){
        this.southWest = new Location(loc);
    }
    public void setNorthEast(Location loc){
        this.northEast = new Location(loc);
    }

    public ArrayList<DataPoint> getDataPoints(){
        return this.dataPoints;
    }
    public void addDataPoints(DataPoint dataPoint){
        dataPoints.add(dataPoint);
    }

    public int numbersofGrid() {      //格子中点的个数
        return this.dataPoints.size();
    }

    public char flagofGrid(int K) {   //标记不同点数的格子
        if (this.dataPoints.size() < K) {
            return 'a';
        } else if (this.dataPoints.size() < 2 * K) {
            return 'n';
        } else {
            return 'm';
        }
    }
    public Grid RightNeighbor(ArrayList<Grid> gr) {       //右邻格子
        Grid grid = new Grid();
        for(Iterator<Grid> it0=gr.iterator();it0.hasNext();) {
            Grid gr1 = it0.next();
            if (gr1.getsouthWest().getLatitude() == this.getsouthWest().getLatitude() && gr1.getnorthEast().getLatitude() == this.getnorthEast().getLatitude()&&this.getnorthEast().getLongitude()==gr1.getsouthWest().getLongitude()) {
                grid = gr1;
                break;
            }
        }
        return grid;
    }
    public boolean hasRightNeighbor(ArrayList<Grid> gr) {       //是否有右邻格子
        boolean b = false;
        for(Iterator<Grid> it1 = gr.iterator(); it1.hasNext(); ){
            Grid grid = it1.next();
            if (grid.getsouthWest().getLatitude() == this.getsouthWest().getLatitude() && grid.getnorthEast().getLatitude() == this.getnorthEast().getLatitude()&&this.getnorthEast().getLongitude()==grid.getsouthWest().getLongitude()) {
                b = true;
                break;
            }else
                b = false;
        }
        return b;
    }

    public void delete(){            //删除格子
        this.northEast = this.getsouthWest();
    }

    public double minLat(){     //格子的最小纬度
        double minlat = this.dataPoints.get(0).getLocation().getLatitude();
        for(int i = 0;i<this.dataPoints.size();i++){
            if(this.dataPoints.get(i).getLocation().getLatitude()<minlat){
                minlat = this.dataPoints.get(i).getLocation().getLatitude();
            }
        }
        return minlat;
    }
    public double minLon(){     //格子的最小经度
        double minlon = this.dataPoints.get(0).getLocation().getLongitude();
        for(int i = 0;i<this.dataPoints.size();i++){
            if(this.dataPoints.get(i).getLocation().getLongitude()<minlon){
                minlon = this.dataPoints.get(i).getLocation().getLongitude();
            }
        }
        return minlon;
    }
    public double maxLat(){     //格子的最大纬度
        double maxlat = this.dataPoints.get(0).getLocation().getLatitude();
        for(int i = 0;i<this.dataPoints.size();i++){
            if(this.dataPoints.get(i).getLocation().getLatitude()>maxlat){
                maxlat = this.dataPoints.get(i).getLocation().getLatitude();
            }
        }
        return maxlat;
    }
    public double maxLon(){     //格子的最大经度
        double maxlon = this.dataPoints.get(0).getLocation().getLongitude();
        for(int i = 0;i<this.dataPoints.size();i++){
            if(this.dataPoints.get(i).getLocation().getLongitude()>maxlon){
                maxlon = this.dataPoints.get(i).getLocation().getLongitude();
            }
        }
        return maxlon;
    }
}