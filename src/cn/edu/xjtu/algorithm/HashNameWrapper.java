package cn.edu.xjtu.algorithm;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.StringProtectUtils;

import java.util.List;

public class HashNameWrapper extends PrivacyStrategy{
	PrivacyStrategy basePrivacyStrategy;
	
	public HashNameWrapper(PrivacyStrategy baseStratety){
		this.basePrivacyStrategy=baseStratety;
	}

	@Override
	public List<Trajectory> protectPrivacy(List<Trajectory> trajectories) {
		List<Trajectory> trajectories1=basePrivacyStrategy.protectPrivacy(trajectories);
		this.hashName(trajectories1);
		return trajectories1;
	}
	
	private void hashName(List<Trajectory> trajectories){
		for(Trajectory trajectory:trajectories){
			for(DataPoint dp:trajectory.getDataPoints()){
				String userId=dp.getUserId();
				String hashedUid=StringProtectUtils.getMd5(userId);
				dp.setUserId(hashedUid);
			}
		}
	}
	
}
