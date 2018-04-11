package cn.edu.xjtu.algorithm.kanonymity;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.sql.SQLSyntaxErrorException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Grid;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.MapUtils;

import javax.xml.crypto.Data;

import java.util.*;

/**
 * Created by YGZ on 2015/10/22.
 */
public class Kanony {
	private static SecureRandom srnd = new SecureRandom();
	public static Grid mergeGrid(Grid gr1, Grid gr2) { // 合并两个格子
		gr1.setNorthEast(gr2.getnorthEast());
		for (int i=0;i<gr2.getDataPoints().size();i++){
			gr1.addDataPoints(gr2.getDataPoints().get(i));
		}
		gr2.delete();
		return gr1;
	}

	public static ArrayList<Grid> start(double min_lon,double min_lat,double ave_lon,double ave_lat,int m,int n){
		ArrayList<Grid> gr = new ArrayList<>();
		gr = KanonyUtils.inti_Grids(min_lon,min_lat,ave_lon,ave_lat,m,n);
		return gr;
	}

	public static ArrayList<Grid> getkGrid1(ArrayList<DataPoint> dp,int K,double min_lon,double min_lat,double max_lon,double max_lat){
		ArrayList<Grid> res = new ArrayList<Grid>();
		Grid grid = new Grid(new Location(min_lon,min_lat),new Location(max_lon,max_lat));
		for (int i=0;i<dp.size();i++){
			if (dp.get(i).isInGrid(grid)){
				grid.addDataPoints(dp.get(i));
			}
		}
		/*Comparator<DataPoint> comparator = new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return (int)(o1.getLocation().getLongitude()-o2.getLocation().getLongitude());
			}
		};
		Collections.sort(dp,comparator);*/
		/*System.out.println("二分法：");*/
	//	splitGrid(res, grid, K, false);
		System.out.println("基于密度：");
		splitGrid1(res, grid, K, false);
		return res;
	}

	public static void splitGrid1(ArrayList<Grid> res,Grid grid,int K,boolean flag){
		ArrayList<DataPoint> dp = grid.getDataPoints();
		if (flag == false){
			Comparator<DataPoint> comparator = new Comparator<DataPoint>() {
				@Override
				public int compare(DataPoint o1, DataPoint o2) {
					double result = o1.getLocation().getLatitude()-o2.getLocation().getLatitude();
					if (result>0){
						return 1;
					}else if (result==0){
						return 0;
					}else {
						return -1;
					}
				}
			};
			Collections.sort(dp,comparator);
			Grid grid1 = new Grid(grid.getsouthWest(),new Location(grid.getnorthEast().getLongitude(),dp.get(dp.size()/2).getLocation().getLatitude()));
			Grid grid2 = new Grid(new Location(grid.getsouthWest().getLongitude(),dp.get(dp.size()/2).getLocation().getLatitude()),grid.getnorthEast());
			for(int i=0;i<grid.numbersofGrid();i++){
				if (grid.getDataPoints().get(i).isInGrid(grid1)){
					grid1.addDataPoints(grid.getDataPoints().get(i));
				}else if (grid.getDataPoints().get(i).isInGrid(grid2)){
					grid2.addDataPoints(grid.getDataPoints().get(i));
				}
			}
			if (grid1.flagofGrid(K)=='a'){
				grid1.delete();
			}else if (grid1.flagofGrid(K) == 'n'){
				res.add(grid1);
			}else{
				splitGrid(res,grid1,K,true);
			}
			if (grid2.flagofGrid(K)=='a'){
				grid2.delete();
			}else if (grid2.flagofGrid(K) == 'n'){
				res.add(grid2);
			}else{
				splitGrid(res,grid2,K,true);
			}
		}else{
			Comparator<DataPoint> comparator = new Comparator<DataPoint>() {
				@Override
				public int compare(DataPoint o1, DataPoint o2) {
					double result = o1.getLocation().getLongitude()-o2.getLocation().getLongitude();
					if (result>0){
						return 1;
					}else if (result==0){
						return 0;
					}else {
						return -1;
					}
				}
			};
			Collections.sort(dp,comparator);
			Grid grid1 = new Grid(grid.getsouthWest(),new Location(dp.get(dp.size()/2).getLocation().getLongitude(),grid.getnorthEast().getLatitude()));
			Grid grid2 = new Grid(new Location(dp.get(dp.size()/2).getLocation().getLongitude(),grid.getsouthWest().getLatitude()),grid.getnorthEast());
			for(int i=0;i<grid.numbersofGrid();i++){
				if (grid.getDataPoints().get(i).isInGrid(grid1)){
					grid1.addDataPoints(grid.getDataPoints().get(i));
				}else if (grid.getDataPoints().get(i).isInGrid(grid2)){
					grid2.addDataPoints(grid.getDataPoints().get(i));
				}
			}
			if (grid1.flagofGrid(K)=='a'){
				grid1.delete();
			}else if (grid1.flagofGrid(K) == 'n'){
				res.add(grid1);
			}else{
				splitGrid(res,grid1,K,false);
			}
			if (grid2.flagofGrid(K)=='a'){
				grid2.delete();
			}else if (grid2.flagofGrid(K) == 'n'){
				res.add(grid2);
			}else{
				splitGrid(res,grid2,K,false);
			}
		}
	}
	public static void splitGrid(ArrayList<Grid> res,Grid grid,int K,boolean flag){
		if (flag == false) {
			double mid_lon = (grid.getsouthWest().getLongitude()+grid.getnorthEast().getLongitude())/2;
			//double mid_lat = (grid.getsouthWest().getLatitude()+grid.getnorthEast().getLatitude())/2;
			Grid grid1 = new Grid(new Location(grid.getsouthWest()), new Location(mid_lon,grid.getnorthEast().getLatitude()));
			Grid grid2 = new Grid(new Location(mid_lon,grid.getsouthWest().getLatitude()), new Location(grid.getnorthEast()));
			for (int i=0;i<grid.numbersofGrid();i++){
				if (grid.getDataPoints().get(i).isInGrid(grid1)){
					grid1.addDataPoints(grid.getDataPoints().get(i));
				}else if (grid.getDataPoints().get(i).isInGrid(grid2)){
					grid2.addDataPoints(grid.getDataPoints().get(i));
				}
			}
			if (grid1.flagofGrid(K)=='a'){
				grid1.delete();
			}else if (grid1.flagofGrid(K) == 'n'){
				res.add(grid1);
			}else{
				splitGrid(res,grid1,K,true);
			}
			if (grid2.flagofGrid(K)=='a'){
				grid2.delete();
			}else if (grid2.flagofGrid(K) == 'n'){
				res.add(grid2);
			}else{
				splitGrid(res,grid2,K,true);
			}
		}else{
			double mid_lat = (grid.getnorthEast().getLatitude()+grid.getsouthWest().getLatitude())/2;
			Grid grid1 = new Grid(new Location(grid.getsouthWest()),new Location(grid.getnorthEast().getLongitude(),mid_lat));
			Grid grid2 = new Grid(new Location(grid.getsouthWest().getLongitude(),mid_lat),new Location(grid.getnorthEast()));
			for (int i=0;i<grid.numbersofGrid();i++){
				if (grid.getDataPoints().get(i).isInGrid(grid1)){
					grid1.addDataPoints(grid.getDataPoints().get(i));
				}else if (grid.getDataPoints().get(i).isInGrid(grid2)){
					grid2.addDataPoints(grid.getDataPoints().get(i));
				}
			}
			if (grid1.flagofGrid(K)=='a'){
				grid1.delete();
			}else if (grid1.flagofGrid(K) == 'n'){
				res.add(grid1);
			}else{
				splitGrid(res,grid1,K,false);
			}
			if (grid2.flagofGrid(K)=='a'){
				grid2.delete();
			}else if (grid2.flagofGrid(K) == 'n'){
				res.add(grid2);
			}else{
				splitGrid(res,grid2,K,false);
			}
		}
	}

	public static ArrayList<Grid> getkGrid(ArrayList<DataPoint> dataPoints ,int K,double min_lon,double min_lat,double ave_lon,double ave_lat,int m,int n) {
		ArrayList<Grid> gr = new ArrayList<>();
		ArrayList<DataPoint> dp = new ArrayList<>();
		for (int i=0;i<dataPoints.size();i++){
			dp.add(dataPoints.get(i));
		}
		ArrayList<Grid> appendGrid = new ArrayList<>();     //构造一个附加ArrayList，便于后面添加
		Grid grid = new Grid();
		List<Grid> delist = new ArrayList<>();   //要删除的格子集合
		gr = KanonyUtils.inti_Grids(min_lon, min_lat, ave_lon, ave_lat,m,n);    //初始化格子，将其平均
		int lon = 0;
		int lat = 0;
		for (int i = 0;i<dp.size();i++){
			lon = (int)((dp.get(i).getLocation().getLongitude() - min_lon)/ave_lon);
			lat = (int)((dp.get(i).getLocation().getLatitude() - min_lat)/ave_lat);
			if (lon<0||lon>n||lat<0||lat>m){
				break;
			}
			gr.get((n+1)*lat+lon).addDataPoints(dp.get(i));
		}
		/*for (int i = 0;i<gr.size();i++){
			for(int j = 0;j<dp.size();j++){
				if (dp.get(j).isInGrid(gr.get(i))){
					gr.get(i).addDataPoints(dp.get(j));
				}
			}
		}*/
		/*
		* 遍历整个格子，先判断格子中的点的个数，根据个数将格子分类
		* 若格子中少于K个，若为0则删除掉，若不为0则和右边的格子合并，若右边的格子个数也为0，则删掉这两个格子；
		* 若格子中的个数多于K少于2K，则不进行操作，默认为正常；
		* 若格子中的个数多于2K，则就进行拆分；
		* */
		for(int i=0;i<gr.size();i++){
		//	long time50 = System.currentTimeMillis();
			Grid gd = gr.get(i);
			if (gd.getnorthEast()==gd.getsouthWest())   //被合并的格子缩为一个点，先判断
			{continue;}
			switch (gd.flagofGrid(K)) {
				case 'a':
					if(gd.numbersofGrid()==0){       //若本格子点个数为0，则加入删除列表中
						delist.add(gd);
						gd.delete();
						break;
					}
				//	int flag = 1;       //设置flag的目的是为了合并时不要超过两个格子
					while (gd.getDataPoints().size()<2*K && gd.hasRightNeighbor(gr)) {
						grid = gd.RightNeighbor(gr);
						gd = mergeGrid(gd, grid);
						//flag++;
						delist.add(grid);
					}
				case 'n':     //没用break的原因是怕上一个合并后会出现大于2K的情况，还需要拆分
				case 'm':    // 需要拆分
					if(gd.flagofGrid(K) == 'm') {
						ArrayList<DataPoint> dpt = new ArrayList<>();       //dpt中存放有序的dataPoint
						ArrayList<DataPoint> dataPointsInGrid = new ArrayList<>();
						dataPointsInGrid.addAll(gd.getDataPoints());  //先取出本格子的所有dataPoint
						delist.add(gd);
						dpt.add(dataPointsInGrid.get(0));      //先取出第一个点
						for (int j = 0; j < dataPointsInGrid.size(); j++) {    //将格子中的点按距离格子最左边的距离从小到大排序
							for (int k = 0; k < dpt.size(); k++) {
								if(dpt.contains(dataPointsInGrid.get(j))){
									break;
								}else if ((dataPointsInGrid.get(j).getLocation().getLongitude()<= dpt.get(k).getLocation().getLongitude())) {
									dpt.add(k, dataPointsInGrid.get(j));
									break;
								} else if ((dataPointsInGrid.get(j).getLocation().getLongitude() > dpt.get(dpt.size()-1).getLocation().getLongitude())) {
									dpt.add(dataPointsInGrid.get(j));
									break;
								}
							}
						}
						int s = dpt.size() / K;
						for (int l = 1; l <= s; l++) {
							Grid grid1 = new Grid();
							for (int p = 0; p <= K-1; p++) {
								grid1.addDataPoints(dpt.get(0));
								dpt.remove(0);
							}
							appendGrid.add(grid1);
						}
						Grid grid2 = new Grid();
						for (int q=0;q<dpt.size();q++){
							grid2.addDataPoints(dpt.get(q));
						}
						appendGrid.add(grid2);	
					}
					break;
			}
		}
		for (Grid grid1:delist){
			gr.remove(grid1);
		}
		List<Grid> delist1 = new ArrayList<>();
		for (int i = 0;i<appendGrid.size();i++){       //初始化增加的格子
			appendGrid.get(i).setSouthWest(new Location(appendGrid.get(i).minLon(),appendGrid.get(i).minLat()));
			appendGrid.get(i).setNorthEast(new Location(appendGrid.get(i).maxLon(),appendGrid.get(i).maxLat()));
		}
		if (gr.size()==0) {
			gr.addAll(appendGrid);
		}else {
			for (int i = 0; i < gr.size(); i++) {
				if (gr.get(i).numbersofGrid() < K) {
					delist1.add(gr.get(i));
					continue;
				}
				gr.get(i).setSouthWest(new Location(gr.get(i).minLon(), gr.get(i).minLat()));
				gr.get(i).setNorthEast(new Location(gr.get(i).maxLon(), gr.get(i).maxLat()));
			}
			gr.addAll(appendGrid);
		}
		gr.removeAll(delist1);
		return gr;
	}
    /**
     * 此函数读配置项来判断是调用二分法还是原来的方法，以后加上
     * @param dp
     * @param K
     * @param min_lon
     * @param min_lat
     * @param ave_lon
     * @param ave_lat
     * @param m
     * @param n
     * @return
     */
	public static List<Double> getkdataPoints(List<DataPoint> dp ,int K,double min_lon,double min_lat,double ave_lon,double ave_lat,int m,int n){
		ArrayList<Grid> kgrids = getkGrid((ArrayList)dp,K,min_lon,min_lat,ave_lon,ave_lat,m,n);
		//ArrayList<Grid> kgrids = getkGrid1((ArrayList)dp,K,min_lon,min_lat,min_lon+m*ave_lon,min_lat+n*ave_lat);
		double num = 0;
		double mis =0,temp =0;
		List<Double> doubles = new ArrayList<Double>();
		HashMap<DataPoint,Boolean> hashMap = new HashMap<>();
		for (int i=0;i<dp.size();i++){
			if (!hashMap.containsKey(dp.get(i))){
				hashMap.put(dp.get(i),false);
			}
		}
		for (int i = 0; i < kgrids.size(); i++) {
			int size = kgrids.get(i).getDataPoints().size();
			int k ;
			int j = size - 1;
			while(j>0){
				k = srnd.nextInt();
				DataPoint dataPoint1 = kgrids.get(i).getDataPoints().get(k);
				DataPoint dataPoint2 = kgrids.get(i).getDataPoints().get(j);
				Location location1 = dataPoint1.getLocation();
				Location location2 = dataPoint2.getLocation();
				dataPoint1.setLocation(location2);
				dataPoint2.setLocation(location1);
				if (hashMap.containsKey(dataPoint1)&&hashMap.get(dataPoint1)==false){
					hashMap.put(dataPoint1,true);
				}
				if (hashMap.containsKey(dataPoint2)&&hashMap.get(dataPoint2)==false){
					hashMap.put(dataPoint2,true);
				}
				temp = MapUtils.distance(location1, location2);
				mis += 2*temp;
				num +=2;
				j--;
			}
		}
		Iterator<Map.Entry<DataPoint,Boolean>> iterator = hashMap.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<DataPoint,Boolean> entry = iterator.next();
			if (!entry.getValue()){
				KanonyUtils.setDisturb(entry.getKey());
				entry.setValue(true);
			}
		}

		Iterator<Map.Entry<DataPoint,Boolean>> iterator1 = hashMap.entrySet().iterator();
		while(iterator1.hasNext()){
			Map.Entry<DataPoint,Boolean> entry = iterator1.next();
			if (entry.getValue()){
				num++;
			}
		}
		doubles.add(num);
		doubles.add(mis);
		doubles.add((double)hashMap.size());
		return doubles;
	}

	public static ArrayList<DataPoint> getDataPoints(List<Trajectory> tj,Timestamp ts,int interval){
		ArrayList<DataPoint> dp = new ArrayList<>();
		for (int i = 0;i<tj.size();i++){
			ArrayList<DataPoint> delete=new ArrayList<DataPoint>();
			for (int j = 0;j<tj.get(i).getDataPoints().size();j++){
				if(tj.get(i).getDataPoints().get(tj.get(i).getDataPoints().size()-1).getTime().getTime()<(ts.getTime()-interval*1000/2))
					break;
				if (tj.get(i).getDataPoints().get(j).getTime().getTime()<(ts.getTime()+interval*1000/2)&&tj.get(i).getDataPoints().get(j).getTime().getTime()>=(ts.getTime()-interval*1000/2)){
					dp.add(tj.get(i).getDataPoints().get(j));
				}
			}
			for(DataPoint dataPoint:delete) {
				tj.get(i).getDataPoints().remove(dataPoint);
			}
				
			
		}
		return dp;
	}
/*
	public static void main(String args[]){
		ArrayList<DataPoint> dp = new ArrayList<>();
		//dp = KanonyUtils.setDataPoints(); //自己随机的点
		System.out.println("随机点结束。。");
		long time = System.currentTimeMillis();
		//dp = getkdataPoints(dp,20,116.28,39.96,0.18/300,0.12/300,300,300);
		long time1 = System.currentTimeMillis();
		System.out.println("生成K点的时间为："+(time1-time)/1000+"秒"+(time1-time)%1000+"ms");
	}
	*/
}
