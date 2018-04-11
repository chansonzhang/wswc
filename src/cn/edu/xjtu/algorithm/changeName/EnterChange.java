package cn.edu.xjtu.algorithm.changeName;

import cn.edu.xjtu.models.*;
import cn.edu.xjtu.utils.LogUtils;
import cn.edu.xjtu.utils.MapUtils;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class EnterChange extends NameChangeStrategy {
    @Override
    public List<Trajectory> changeName(List<Trajectory> trajectories,int changeTimeLimit ) {
    	 double alf=0.5;
         List<Double> entropy=new LinkedList<Double>();
     	for(Trajectory tra:trajectories) {
     	   String id=tra.getDataPoints().get(0).getUserId();
     	   tra.setProbabilities(id,1.0);
        }
        //以上初始化熵值
    	
    	String date=trajectories.get(0).getDataPoints().get(0).getTime().toString().substring(0,10);
        List<StayPoint> stayPoints=PrivacyUtils.getStayPoints(trajectories);
        List<SPCluster> spClusters=PrivacyUtils.getSpcs(stayPoints);
        int clusterDpNum=0; 
        for(SPCluster spCluster:spClusters){
        	Location sw=spCluster.getSouthWest();
        	Location es=new Location(spCluster.getNorthEast().getLongitude(),spCluster.getSouthWest().getLatitude());
        	Location ne=spCluster.getNorthEast();
        	//东西方向距离
        	double length=MapUtils.distance(sw, es);
        	//南北方向距离
        	double height=MapUtils.distance(es, ne);
        	clusterDpNum+=spCluster.getStayPoints().size();
        }
        System.out.println("clusterDpNum:"+clusterDpNum);
        List<StayArea> stayAreas= PrivacyUtils.getStayArea(date, spClusters);
        System.out.println("开始换名……");
        
        //通过找到数据点在时间轴上的具体起始点，可以在后面避免很多不必要的计算
        Timestamp t0=trajectories.get(0).getDataPoints().get(0).getTime();
        int size=trajectories.get(0).getDataPoints().size();
        Timestamp t24=trajectories.get(0).getDataPoints().get(size-1).getTime();
        for(Trajectory trajectory:trajectories){
        	size=trajectory.getDataPoints().size();
        	Timestamp timestamp0=trajectory.getDataPoints().get(0).getTime();
        	Timestamp timestamp24=trajectory.getDataPoints().get(size-1).getTime();
        	if(timestamp0.before(t0)) t0=timestamp0;
        	if(timestamp24.after(t24)) t24=timestamp24;
        }
        
       
        List<String> usrIds = PrivacyUtils.getUserIds(trajectories);
        usrIds.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
        //用来记录换名次数
        Map<String, Integer> changeTimes = new HashMap<>();
        for (String userId : usrIds) {
            changeTimes.put(userId, 0);
        }
        
        /**
         * To record the corresponding of trajectory and user with the clapse of time
         * every name changing should update this map, so that when you want to find out
         * the trajectory corresponding with the given userId at a spacified time,  you don't
         * need to traverse all the trajectories, but just check in this map.
         */
        Map<String,Trajectory> whoseTrajectory=new HashMap<>();
        for(Trajectory trajectory:trajectories){
        	whoseTrajectory.put(trajectory.getDataPoints().get(0).getUserId(), trajectory);
        }
        
        Map<StayPoint, Trajectory> whoseStayPoint=new HashMap<>();
        
        /**
         * 将每条轨迹上次扫描到的时间点记录下来，再查找该轨迹中给定时间的数据点时，将上次的断点作为参数传入，
         * 这样可以避免每次都从头扫描
         */
        Map<Trajectory,Integer> lastCheckedIndices=new HashMap<>();
        
        for (Timestamp ts = new Timestamp(t0.getTime()); ts.before(t24); ts.setTime(ts.getTime() + PrivacyUtils.TIME_INTERVAL * 1000)) {
            Timestamp previous_timestamp;
            if (ts.equals(t0)) {
                previous_timestamp = t0;
            } else {
                previous_timestamp = new Timestamp(ts.getTime() - PrivacyUtils.TIME_INTERVAL * 1000);
            }

            //记录当前时刻是否已换名，规定一个时刻一个用户最多换名一次可以防止[又换回自己]等问题
            Map<String, Boolean> user_is_changed = new HashMap<>();
            for (String usrId : usrIds) {
                user_is_changed.put(usrId, false);
            }
            for (String usrId : usrIds) {
                //如果当前用户在当前时刻已经换过，或者之前交换的次数已达到上限，则直接查看下一个用户
                if (changeTimes.get(usrId) == changeTimeLimit)
                    continue;
                if (user_is_changed.get(usrId))
                    continue;
                Trajectory trajectory_of_thisUser = whoseTrajectory.get(usrId);
               
                if (trajectory_of_thisUser == null)
                    continue;
                int lastCheckedIndex=-1;
                if(lastCheckedIndices.containsKey(trajectory_of_thisUser)){
                	lastCheckedIndex=lastCheckedIndices.get(trajectory_of_thisUser);
                }
                DataPoint dp_thisUser_now = trajectory_of_thisUser.getDataPoint_of_given_time(ts,lastCheckedIndex+1);
                lastCheckedIndices.put(trajectory_of_thisUser, lastCheckedIndex+1);
                
                if (dp_thisUser_now == null)//当前时刻该用户无数据点
                    continue;
                /*if(trajectory_of_thisUser.getDataPoints().indexOf(dp_thisUser_now)==0)//轨迹的第一数据点不参与交换，否则无法记录初始用户名
                    continue;*/
                StayArea stayArea_containThisUser_now = PrivacyUtils.pass_by_which_StayArea(dp_thisUser_now, stayAreas);
                if (stayArea_containThisUser_now == null)//当前未经过停留区
                    continue;
                else //当前在某个停留区
                {
//                    boolean is_Contained_by_any_StayArea = PrivacyUtils.is_Contained_by_any_StayArea(usrId, previous_timestamp, stayAreas);
                    DataPoint dp_thisUser_prev = trajectory_of_thisUser.getDataPoint_of_given_time(previous_timestamp,lastCheckedIndex);
                    boolean is_Contained_by_any_StayArea;
                    if (dp_thisUser_prev != null) {
                        is_Contained_by_any_StayArea = PrivacyUtils.is_in_any_StayArea(dp_thisUser_prev, stayAreas);
                    } else is_Contained_by_any_StayArea = false;
                    if (ts.equals(t0))
                        is_Contained_by_any_StayArea = false;
                    if (!is_Contained_by_any_StayArea) //前一时刻未在停留区，当前时刻在停留区域，说明刚进入停留区,换名
                    {
                        TempStayArea tempStayArea = PrivacyUtils.contain_by_which_TempStayArea(ts,stayArea_containThisUser_now);
                        
                        Set<String> users_in_same_area_now = new HashSet<>();
                        
                        //将经过此停留区的所有用户加入当前用户集中
                        //提高换名率的折衷，将引入较大统计误差
                        /*for(Trajectory trajectory:trajectories){
                        	if(lastCheckedIndices.containsKey(trajectory)){
                        		lastCheckedIndex=lastCheckedIndices.get(trajectory);
                        	}else{
                        		lastCheckedIndex=-1;
                        	}
                        	DataPoint dp_now=trajectory.getDataPoint_of_given_time(ts,lastCheckedIndex+1);
                        	if(dp_now!=null&&stayArea_containThisUser_now.equals(PrivacyUtils.pass_by_which_StayArea(dp_now, stayAreas)))
                        		users_in_same_area_now.add(dp_now.getUserId());
                        }*/
                        
                        if (tempStayArea != null) {
                            List<StayPoint> sps_in_same_area_now = tempStayArea.getStayPoints();
                            for (Iterator sp_it = sps_in_same_area_now.iterator(); sp_it.hasNext(); ) {
                                StayPoint sp=(StayPoint) sp_it.next();
                                Trajectory trajectory=null;
                                if(whoseStayPoint.containsKey(sp)){
                                	trajectory=whoseStayPoint.get(sp);
                                	if(lastCheckedIndices.containsKey(trajectory)){
                                		lastCheckedIndex=lastCheckedIndices.get(trajectory);
                                	}else{
                                		lastCheckedIndex=-1;
                                	}
                                	DataPoint dataPoint=trajectory.getDataPoint_of_given_time(ts,lastCheckedIndex+1);
                                	if(dataPoint!=null){
                                		if(changeTimes.get(dataPoint.getUserId())<changeTimeLimit){
                                			if(!dataPoint.getUserId().equals(usrId)){
                                				users_in_same_area_now.add(dataPoint.getUserId());
                                			}
                                		}
                                			
                                	}
                                		
                                }else{
                                	String user=sp.getCurrentUserId(ts);
                                	if(user!=null){
                                		whoseStayPoint.put(sp, whoseTrajectory.get(user));
                                		if(changeTimes.get(user)<changeTimeLimit){
                                			if(!user.equals(usrId)){
                                				users_in_same_area_now.add(user);
                                			}
                                		}
                                	}
                                }
                                
                            	
                            }
                        }
                        if (users_in_same_area_now.size() != 0) {
                        	//记录当前停留区未换名用户，换名时优先选择他们
                        	Set<String> unChangedUsers_sameArea=new HashSet<>();
                        	for(Iterator it=users_in_same_area_now.iterator();it.hasNext();){
                        		String u=(String)it.next();
                        		if(changeTimes.get(u)==0){
                        			unChangedUsers_sameArea.add(u);
                        		}
                        	}
                        	
                            int pickCount = 0;
                            String user_to_change_with;
                            if(unChangedUsers_sameArea.size()!=0){
                            	user_to_change_with= PrivacyUtils.randomPick(unChangedUsers_sameArea);
                            }else
                            	user_to_change_with=PrivacyUtils.randomPick(users_in_same_area_now);
                            pickCount++;
                            
                            //如果对象已经换过了(为了提高换名率，去掉了此项限制)或者超过交换上限,或者换名对象是自己，重新寻找一个换名对象
                            while (changeTimes.get(user_to_change_with) == changeTimeLimit
                            		|| user_to_change_with.equals(usrId)) {
                            	if(changeTimes.get(user_to_change_with) == changeTimeLimit) 
                            		users_in_same_area_now.remove(user_to_change_with);
                                if (pickCount > 2 * users_in_same_area_now.size()) break;
                                if(unChangedUsers_sameArea.size()==0) break;
                                user_to_change_with = PrivacyUtils.randomPick(unChangedUsers_sameArea);
                                pickCount++;
                            }
                            
                            //如果在为交换用户集中未找到，则继续在区域内所有用户集中寻找。
                            while (changeTimes.get(user_to_change_with) == changeTimeLimit|| user_to_change_with.equals(usrId)) {
                            	if(changeTimes.get(user_to_change_with) == changeTimeLimit) 
                            		users_in_same_area_now.remove(user_to_change_with);
                                if (pickCount > 2 * users_in_same_area_now.size()) break;
                                if(unChangedUsers_sameArea.size()==0) break;
                                user_to_change_with = PrivacyUtils.randomPick(users_in_same_area_now);
                                pickCount++;
                            }
                            
                            //如果依然未找到一个有效的换名对象，则放弃此次换名
                            if ( changeTimes.get(user_to_change_with) == changeTimeLimit
                                    || user_to_change_with.equals(usrId))
                                continue;
                            //System.out.println(ts + ": " + usrId + "与" + user_to_change_with + "换名");
                            
                            ///////////////////////////////////////////////////////////////
                            //这里面开始我的计算熵值得函数了
                            Trajectory thisTra = whoseTrajectory.get(usrId);
                            Trajectory thatTra = whoseTrajectory.get(user_to_change_with);
                            Map<String,Double> thisMap=thisTra.getProbabilities();
                            Map<String,Double> thatMap=thatTra.getProbabilities();
                            Set<String> thisStr=thisMap.keySet();
                            Set<String> thatStr=thatMap.keySet();
                            Map<String,Integer> flag=new HashMap<String,Integer>();
                           	Iterator<String> id1=thisStr.iterator();
                           
                           	while(id1.hasNext()){
                           		String id2=id1.next();
                           		Double a=thisMap.get(id2);
                           		//对于第一个map还说，如果第二个map包含这个id，则把对应值相加
                           		if(thatMap.containsKey(id2)){
                           			Double b=thatMap.get(id2);
                           			Double sum=a*alf+b*alf;
                           			thisTra.setProbabilities(id2, sum);
                           			thatTra.setProbabilities(id2, sum);
                           		}
                           		else{
                           			Double sum=a*alf;
                           			thisTra.setProbabilities(id2, sum);
                           			thatTra.setProbabilities(id2, sum);
                           		}
                           		flag.put(id2, 1);
                            }
                        	Iterator<String> id3=thatStr.iterator();
                         	while(id3.hasNext()){
                           		String id4=id3.next();
                           		Double a=thatMap.get(id4);
                           		//对于第二个map还说，如果第一个map包含这个id，则把对应值相加
                           		if(!flag.containsKey(id4)){
                           			Double sum=a*alf;
                           			thisTra.setProbabilities(id4, sum);
                           			thatTra.setProbabilities(id4, sum);
                           		}
                            }
                            flag.clear();
                            
                            ///////////////////////////////////////////////////////////////
                            
                            
                            //更新换名用户当前时间点往后的所有轨迹数据
                            Trajectory trajectory_tochangenamewith=whoseTrajectory.get(user_to_change_with);
                            Set<Trajectory> trajectories_toupdate=new HashSet<>();
                            trajectories_toupdate.add(trajectory_of_thisUser);
                            trajectories_toupdate.add(trajectory_tochangenamewith);
                            
                            Iterator<Trajectory> traj_it = trajectories_toupdate.iterator();
                            
                            while (traj_it.hasNext()) {
                                Trajectory traj = traj_it.next();
                                if(lastCheckedIndices.containsKey(traj)){
                                	lastCheckedIndex=lastCheckedIndices.get(traj);
                                }else{
                                	lastCheckedIndex=-1;
                                }
                                DataPoint current_user = traj.getDataPoint_of_given_time(ts,lastCheckedIndex+1);
                                lastCheckedIndices.put(traj, lastCheckedIndex+1);
                                
                                if (current_user == null)//寻找到的是给定时间戳的下一个数据点，若为空，说明该条轨迹到达结尾
                                    continue;
                                String current_userId = current_user.getUserId();
                                //将usrId换为user_to_change_with
                                if (current_userId.equals(usrId)) {
                                    List<DataPoint> dataPoints = traj.getDataPoints();
                                    for (int i = lastCheckedIndex+1; i < traj.getDataPoints().size(); i++) {
                                        DataPoint dp = dataPoints.get(i);
                                            dp.setUserId(user_to_change_with);
                                    }
                                }
                                
                                //将user_to_change_with换为usrid
                                else if (current_userId.equals(user_to_change_with)) {
                                    List<DataPoint> dataPoints = traj.getDataPoints();
                                    for (int i = lastCheckedIndex+1; i < dataPoints.size(); i++) {
                                        DataPoint dp = dataPoints.get(i);
                                        dp.setUserId(usrId);
                                    }
                                }
                            }

                            
                            //将换名的用户的已交换标志设置为true
                            user_is_changed.put(usrId, true);
                            user_is_changed.put(user_to_change_with, true);
                            //更新交换次数
                            int oldchangeTime = changeTimes.get(usrId);
                            changeTimes.put(usrId, oldchangeTime + 1);
                            oldchangeTime = changeTimes.get(user_to_change_with);
                            changeTimes.put(user_to_change_with, oldchangeTime + 1);
                            
                            //update whoseTrajectory map
                            whoseTrajectory.put(usrId, trajectory_tochangenamewith);
                            whoseTrajectory.put(user_to_change_with, trajectory_of_thisUser);
                        }
                    }
                }
            }
        }
        double changeUserNumber = 0;
        double totalchangeTime = 0;
        double changeRate = 0.0;
        double changePerUser = 0.0;
        for (Map.Entry<String, Integer> changeTime : changeTimes.entrySet()) {
            if (changeTime.getValue() != 0) changeUserNumber++;
            totalchangeTime += changeTime.getValue();
        }
        changeRate = changeUserNumber / changeTimes.size();
        changePerUser = totalchangeTime / changeUserNumber;
        System.out.println("用户总数：" + changeTimes.size() + "\t换名用户数：" + changeUserNumber + "\t换名率：" + String.format("%.2f",changeRate*100)+"%");
        LogUtils.getInstance().append("用户总数：" + changeTimes.size() + "\t换名用户数：" + changeUserNumber + "\t换名率：" + String.format("%.2f",changeRate*100)+"%");
        System.out.println("换名总次数：" + totalchangeTime + "\t换名用户数：" + changeUserNumber + "\t平均换名次数：" + String.format("%.2f",changePerUser));
        LogUtils.getInstance().append("换名总次数：" + totalchangeTime + "\t换名用户数：" + changeUserNumber + "\t平均换名次数：" + String.format("%.2f",changePerUser));
       
        ////////////////////////////////////////////////////////////////
        //计算熵的输出
        int i=0;
        Double entropymax=0.0;
        int sum=trajectories.size();
      //  System.out.println("轨迹数"+(1.0/sum)*Math.log(sum));
        for(int k=0;k<sum;k++)
        {
        	entropymax-=(1.0/sum)*Math.log(1.0/sum);
        }
        System.out.println("熵值"+entropymax);
        Double ave=0.0;
        for(Trajectory traj:trajectories){
        	 Double tempans=0.0;
             Map<String,Double> temp=traj.getProbabilities();
             Set<String> tempStr=temp.keySet();
             Iterator<String> id5=tempStr.iterator();
             while(id5.hasNext()){
            	 String id6=id5.next();
            	 tempans-=temp.get(id6)*Math.log(temp.get(id6));
             }
             i++;
             tempans=(tempans)/entropymax*100;
             System.out.println("轨迹"+i+" 交换后信息熵="+(tempans)+"%");
             ave+=tempans;
             entropy.add(tempans);
        }
       
        System.out.println("轨迹总的交换后信息熵="+ave/sum+"%");
        
        return trajectories;
    }
}
