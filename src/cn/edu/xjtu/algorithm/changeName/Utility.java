package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.Point;
import cn.edu.xjtu.models.StayPoint;
import cn.edu.xjtu.utils.MapUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utility {   

/** 
 * 
* ����������֮��ľ��� 

* @param p �� 

* @param q �� 

* @return ����������֮��ľ��� 

*/  
 
	public static double getDistance(Point p,Point q){
  
		double dx=p.getLocLong()-q.getLocLong();  
		double dy=p.getLocLat()-q.getLocLat();   
		double distance= MapUtils.getDistance(p.getLocLong(), p.getLocLat(), q.getLocLong(), q.getLocLat());
			
			return distance;  
 
	}  
	/** 
	  
	����* ���������ǲ��Ǻ��ĵ� 
	  
	����* @param lst ��ŵ������ 
	  
	����* @param p �����Եĵ� 
	  
	����* @param e e�뾶 
	  
	����* @param minp �ܶ���ֵ 
	  
	����* @return ��ʱ��ŷ��ʹ��ĵ� 
	  
	����*/  
	   
	public static List<Point> isKeyPoint(List<Point> lst,Point p,double e,int minp){  
	   
		int count=0;  //���ĵ����Ŀ
		List<Point> tmpLst=new ArrayList<Point>();  	   
		for(Iterator<Point> it=lst.iterator();it.hasNext();){  	   
			Point q=it.next();  
			if(getDistance(p,q)<=e){  	   
					++count;  	   
					if(!tmpLst.contains(q)){  	   
						tmpLst.add(q);  	   
					}   
				}	   
		}	   
			if(count>=minp){  	   
				p.setKey(true);  	   
					return tmpLst;  	   
			}	   
				return null;  	   
	}  
	   
	public static void setListClassed(List<Point> lst){  
	   
		for(Iterator<Point> it=lst.iterator();it.hasNext();){  	   
		Point p=it.next();  	   
		if(!p.isClassed()){  	   
			p.setClassed(true);  	   
			}  	   
		}  	   
}  
	   
	/** 
	  
	����* ���b�к���a�а�����Ԫ�أ�����������Ϻϲ� 
	  
	����* @param a 
	  
	����* @param b 
	  
	����* @return a 
	  
	����*/  
	   
	public static boolean mergeList(List<Point> a,List<Point> b){  
	   
			boolean merge=false;  	   
			for(int index=0;index<b.size();++index){
				Point bp=b.get(index);
				boolean flag=a.contains(bp);
				if(flag){
				merge=true;  	   
					break;	   
				}
	   
			}
	   
		if(merge){  
	   
					for(int index=0;index<b.size();++index){  	   
						if(!a.contains(b.get(index))){  	   
							a.add(b.get(index));  	   
						}	   
					}	   
				}	   
			return merge;  
	   }  
	   
	/**
	  
	����* �����ı��еĵ㼯�� 
	  
	����* @return �����ı��е�ļ��� 
	  
	����* @throws IOException 
	  
	����*/  
	   
	public static List<Point> getPointsList(ArrayList<StayPoint> st) {
			List<Point> lst=new ArrayList<Point>();
			for(Iterator<StayPoint> it = st.iterator();it.hasNext();){
				Point p = new Point();
				StayPoint sp = new StayPoint();
				sp = it.next();
				p.setStayPoint(sp);
				p.setLocation(sp.getLocation());
				lst.add(p);
			}
				return lst;  	   
	}
	
}
