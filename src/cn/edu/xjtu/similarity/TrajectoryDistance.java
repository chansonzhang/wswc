package cn.edu.xjtu.similarity;

import java.util.LinkedList;
import java.util.List;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;
/**
 * 本函数用于计算原始轨迹和变换后的轨迹的形状距离和位置距离
 * @author daizhaosheng
 * 目前是针对k匿名计算的轨迹距离
 *
 */
public class TrajectoryDistance {
   public static void TraShapeDis(List<Trajectory> originTs,List<Trajectory> resultTrajectories) {
	   //首先把每一条轨迹得到
	   long time0=System.currentTimeMillis();
	   List<Double> distanceTra=new LinkedList<Double>();
	   double delta=0.5;
	   int traNumOri=originTs.size();
	   int traNumRes=resultTrajectories.size();
	   if(traNumOri!=traNumRes)
	   {
		   System.out.println("前后得到的轨迹数不等");
		   return;
	   }
	   else
	   {
		   //遍历每一个轨迹
		   for(int i=0;i<traNumOri;i++)
		   {
			   List<DataPoint> dpOri=originTs.get(i).getDataPoints();
			   List<DataPoint> dpRes=resultTrajectories.get(i).getDataPoints();
			   int dpNumOri=dpOri.size();
			   int dpNumRes=dpRes.size();
			   if(dpNumOri!=dpNumRes)
			   {
				   System.out.println("对于第"+i+"条轨迹得到的数据点个数不一样");
				   return;
			   }
			   else
			   {
				   int ot=0;  //记录每个轨迹点数
				   Double dshape=0.0;
			       Double dloc=0.0;
			       DataPoint dp1,dp2;
			       long tsAve=0;
			       Double x1=0.0,y1=0.0,x2=0.0,y2=0.0;
				   for(int j=0;j<dpNumOri-1;j++)
				   {
					   
			           dp1=dpOri.get(j);
			           DataPoint dp1next=dpOri.get(j+1);
			           dp2=dpRes.get(j);
			           DataPoint dp2next=dpRes.get(j+1);
                        
			           long ts=(dp1.getTime().getTime()-dp1next.getTime().getTime())/1000;
			           tsAve+=Math.abs(ts);
			           //   System.out.println("间隔时间: "+ts);
			           x1=dp1.getLocation().getLatitude();
			           x2=dp2.getLocation().getLatitude();
			           y1=dp1.getLocation().getLongitude();
			           y2=dp2.getLocation().getLongitude();
			      //     System.out.println(x1+" "+x2+" "+y1+" "+y2);
			           dloc=dloc+Math.pow(x1-x2,2)+Math.pow(y1-y2,2);
			       //    System.out.println("轨迹位置距离 dLoc: "+dloc);
			           x1=dp1next.getLocation().getLatitude()-x1;
			           x2=dp2next.getLocation().getLatitude()-x2;
			           y1=dp1next.getLocation().getLongitude()-y1;
			           y2=dp2next.getLocation().getLongitude()-y2;
			           if(ts==0){
			        	   //ts=0.5;
			        	   dshape=dshape+Math.pow((x1-x2)/ts,2)+Math.pow((y1-y2)/ts,2);
			        	   if(dshape.isInfinite()||dshape.isNaN()){
			        		   dshape=0.0;
			        	   }
			           }
			           else{
			        	   dshape=dshape+Math.pow((x1-x2)/ts,2)+Math.pow((y1-y2)/ts,2);
			           }
				   }
				   tsAve=tsAve/(dpNumOri-1);
				   ot=dpNumOri-1;
				   dloc=Math.sqrt(dloc)/ot;
			//       System.out.println("轨迹位置距离 dLoc: "+dloc);
			       dshape=Math.sqrt(dshape)/ot*tsAve;
			//       System.out.println("轨迹形状距离 dShape: "+dshape);
			       Double distance=(dshape*delta+(1-delta)*dloc);
			//       System.out.println("轨迹距离 d(T1,T2)=a*dShape+(1-a)*dLoc : "+distance+" (a="+delta+")");
			       distanceTra.add(distance);
			   }
		   }
		   Double ave=0.0;
		   int size=distanceTra.size();
		   for(int k=0;k<size;k++){
			   ave+=distanceTra.get(k);
		   }
		   ave=ave/size;
		   long time1=System.currentTimeMillis();
		   System.out.println("所有轨迹的平均距离 d(T1,T2)=a*dShape+(1-a)*dLoc : "+ave+"m"+" (a="+delta+")");
		   System.out.println("计算轨迹距离耗时"+(time1-time0)/1000+"s");
	   }
	   }
}
