package cn.edu.xjtu.algorithm.changeName;


import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Point;
import cn.edu.xjtu.models.SPCluster;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.utils.PrivacyConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dbscan {
    private static double e = PrivacyConfig.getInstance().getEps();//e??
    private static int minp = PrivacyConfig.getInstance().getMinp();//??????


    /**
     * ???????е?????е??洢??pointsList??
     *
     * @throws IOException
     */

    private static ArrayList<SPCluster> display(List<List<Point>> resultList) {
        ArrayList<SPCluster> spc = new ArrayList<SPCluster>();
        for (Iterator<List<Point>> it = resultList.iterator(); it.hasNext(); ) {
            List<Point> lst = it.next();
            if (lst.isEmpty()) {
                continue;
            }
            SPCluster sp1 = new SPCluster();
            double tempminx = lst.iterator().next().getLocLong();
            double tempminy = lst.iterator().next().getLocLat();
            double tempmaxx = lst.iterator().next().getLocLong();
            double tempmaxy = lst.iterator().next().getLocLat();
            for (Iterator<Point> it1 = lst.iterator(); it1.hasNext(); ) {
                Point p = it1.next();
                if (p.getLocLong() < tempminx)
                    tempminx = p.getLocLong();
                if (p.getLocLong() > tempmaxx)
                    tempmaxx = p.getLocLong();
                if (p.getLocLat() < tempminy)
                    tempminy = p.getLocLat();
                if (p.getLocLat() > tempmaxy)
                    tempmaxy = p.getLocLat();
                sp1.addStayPoints(p.getStayPoint());
            }
            Location sw = new Location(tempminx, tempminy);
            Location ne = new Location(tempmaxx, tempmaxy);
            sp1.setSouthWest(sw);
            sp1.setNorthEast(ne);
            spc.add(sp1);

        }
        return spc;
    }

//??????п??????????  

    private static List<List<Point>> applyDbscan(ArrayList<StayPoint> st) {
        List<List<Point>> resultList = new ArrayList<>();
        List<Point> pointsList = Utility.getPointsList(st);
        for (Iterator<Point> it = pointsList.iterator(); it.hasNext(); ) {
            Point p = it.next();
            if (!p.isClassed()) {
                List<Point> tmpLst = new ArrayList<Point>();
                if ((tmpLst = Utility.isKeyPoint(pointsList, p, e, minp)) != null) {
                    //????о?????????????
                    Utility.setListClassed(tmpLst);
                    resultList.add(tmpLst);
                }
            }
        }
        return resultList;
    }

//?????п????????????к???????????????????к??  

    private static List<List<Point>> getResult(ArrayList<StayPoint> st) {
        List<List<Point>> resultList = applyDbscan(st);//??????????????
        int length = resultList.size();
        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < length; ++j) {
                if (i != j) {
                    if (Utility.mergeList(resultList.get(i), resultList.get(j))) {
                        resultList.get(j).clear();
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * ??????????
     *
     * @author gzyang
     */

    public static ArrayList<SPCluster> getSpcs(List<StayPoint> stayPoints) {
        ArrayList<StayPoint> st = (ArrayList<StayPoint>) stayPoints;
        List<List<Point>> resultList = getResult(st);
        ArrayList<SPCluster> spcs = display(resultList);
        return spcs;
    }

}
