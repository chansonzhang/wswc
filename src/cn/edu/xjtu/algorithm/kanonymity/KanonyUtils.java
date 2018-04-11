package cn.edu.xjtu.algorithm.kanonymity;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Grid;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.utils.MapUtils;
import cn.edu.xjtu.utils.PrivacyConfig;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by ygz on 2015/11/14.
 */
public class KanonyUtils {
    public static final String DATA_PATH = "F:\\WorkSpace\\hwPrivacy\\demo\\data\\Geolife Trajectories 1.2\\Data";
    public static Grid generateranGrid() {      //�����ã������������
        double lon1, lon2, lonmax, lonmin, lat1, lat2, latmax, latmin;
        do {
            Random random = new Random();

            lon1 = 116.28 + 0.18 * random.nextDouble();
            lon2 = 116.28 + 0.18 * random.nextDouble();
            lat1 = 39.96 + 0.12 * random.nextDouble();
            lat2 = 39.96 + 0.12 * random.nextDouble();
            lonmax = Math.max(lon1, lon2);
            lonmin = Math.min(lon1, lon2);
            latmax = Math.max(lat1, lat2);
            latmin = Math.min(lat1, lat2);
        } while ((lon1 == lon2 && lat1 == lat2) || Math.abs(lon1-lon2)>0.36/50 || Math.abs(lat1-lat2)>0.24/50);
        Grid rangrid = new Grid(new Location(lonmin, latmin), new Location(lonmax, latmax));
        return rangrid;
    }

    public static Grid generateGrid(){
        double lonmin = 116.28 + 23.5*0.18/50;
        double lonmax = 116.28 + 25.5*0.18/50;
        double latmin = 39.96 + 23.5*0.12/50;
        double latmax = 39.96 + 25.5*0.12/50;
        Grid grid = new Grid(new Location(lonmin,latmin), new Location(lonmax,latmax));
        return grid;
    }
/*
* ��ʼ������*/
    public static ArrayList<Grid> inti_Grids(double min_lon,double min_lat,double ave_lon,double ave_lat,int m,int n) {
        ArrayList<Grid> gr = new ArrayList<>();
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                Grid g = new Grid();
                g.setSouthWest(new Location(min_lon + j * ave_lon, min_lat + i
                        * ave_lat));
                g.setNorthEast(new Location(min_lon + (j + 1) * ave_lon,
                        min_lat + (i + 1) * ave_lat));
                gr.add(g);
            }
        }
        return gr;
    }
/*
* ��ȡ�ļ�ȡdataPoints*/
    /*
    public static ArrayList<DataPoint> getDataPoints() {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        Scanner scanner = null;
        File userFolder = new File("F:\\WorkSpace\\hwPrivacy\\demo\\����\\2008-11-16");
        File[] files = userFolder.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            String filename = file.getName();
            try {
                scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int k = 0; k < 6; k++)//��Ч����
            {
                scanner.nextLine();
            }
            do {
                String currentLine = scanner.nextLine();
                if (currentLine != null) {
                    String[] values = currentLine.split("\t");
                    DataPoint dataPoint = new DataPoint(filename.substring(0, 3)
                            , new Location(Double.parseDouble(values[1]), Double.parseDouble(values[0]))
                            , Timestamp.valueOf(values[5].replace("/","-") + " " + values[6]));
                    dataPoints.add(dataPoint);
                }
            }
            while (scanner.hasNextLine());
        }
        return dataPoints;
    }
*/
    public static void writeFile(ArrayList<DataPoint> dataPoints){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("F://in.txt"));
            String s = null;
            for(int i=0;i<dataPoints.size();i++){
                s = String.valueOf(dataPoints.get(i).getLocation().getLongitude());
                bw.write(s+"\t");
                s = String.valueOf(dataPoints.get(i).getLocation().getLatitude());
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void writeFile1(ArrayList<DataPoint> dataPoints){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("F://out.txt"));
            String s = null;
            for(int i=0;i<dataPoints.size();i++){
                s = String.valueOf(dataPoints.get(i).getLocation().getLongitude());
                bw.write(s+"\t");
                s = String.valueOf(dataPoints.get(i).getLocation().getLatitude());
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static double setDisturb(DataPoint dataPoint){
        Random random = new Random();
        double ave_lat = PrivacyConfig.getInstance().getBc() / 1000.0 /111;
        double ave_lon = PrivacyConfig.getInstance().getBc() /1000.0 /111;
        double lon = dataPoint.getLocation().getLongitude()-ave_lon+2*ave_lon*random.nextDouble();
        double lat = dataPoint.getLocation().getLatitude()-ave_lat+2*ave_lat*random.nextDouble();
        double reserveBit = 6.0;
        double longitude = (Math.round(lon*Math.pow(10.0,reserveBit)))/(Math.pow(10.0,reserveBit));
        double latitude = (Math.round(lat*Math.pow(10.0,reserveBit)))/(Math.pow(10.0,reserveBit));
        Location location1 = dataPoint.getLocation();
        Location location2 = new Location(longitude,latitude);
        double res = MapUtils.distance(location1,location2);
        dataPoint.setLocation(new Location(lon,lat));
        return res;
    }
/*
    public static ArrayList<DataPoint> setDataPoints() {
        Random random =new Random();
        random.setSeed(20);
        ArrayList<DataPoint> dp = new ArrayList<>();
        for(int i = 0;i<10000000;i++){    //3504995
            DataPoint dpt = new DataPoint(Integer.toString(i),new Location(116.28+0.18*random.nextDouble(),39.96+0.12*random.nextDouble()));
            dp.add(dpt);
        }
        return dp;
    }
*/
/*
    public static ArrayList<DataPoint> setGauPoints(){
        Random random =new Random();
        random.setSeed(10);
        ArrayList<DataPoint> dp = new ArrayList<>();
        for(int i = 0;i<3504995;i++){
            DataPoint dpt = new DataPoint(Integer.toString(i),new Location(116.37+0.02*random.nextGaussian(),40.02+0.04/3*random.nextGaussian()));
            dp.add(dpt);
        }
        return dp;
    }
*/
}
