package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author zhangchen
 *
 */
public class WriteWithIndex extends ResultWriteStrategy{

	@Override
	public void write(String directory, List<Trajectory> trajectories) {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Random random=new Random();
		String key=Integer.toString(random.nextInt());
		FileWriter writer=null;
		Scanner scanner=null;
		String srcDir=PrivacyConfig.getInstance().getSourceDir();
		String desDir=PrivacyConfig.getInstance().getResultDir();
		copyAllFileStructureWithoutContent(srcDir,desDir);
		List<DataPoint> dataPoints=new ArrayList();
		for(Trajectory trajectory:trajectories){
			dataPoints.addAll(trajectory.getDataPoints());
		}
		dataPoints.sort(new Comparator<DataPoint>() {

			@Override
			public int compare(DataPoint dp1, DataPoint dp2) {
				if(dp1.getFileName().compareTo(dp2.getFileName())<0) return -1;
				else if(dp1.getFileName().compareTo(dp2.getFileName())>0) return 1;
				else{
					if(dp1.getLineNumber()<dp2.getLineNumber()) return -1;
					else if(dp1.getLineNumber()>dp2.getLineNumber()) return 1;
					else return 0; 
				}
			}
			
		});
		
		Set<List<DataPoint>> dataPointss=new HashSet<>();
		String currentFileName=dataPoints.get(0).getFileName();
		List<DataPoint> dataPoints_sameFile=new ArrayList<>();
		
		for(int i=0;i<dataPoints.size();i++){
			DataPoint dp=dataPoints.get(i);
			if(!dp.getFileName().equals(currentFileName)){
				dataPointss.add(dataPoints_sameFile);
				dataPoints_sameFile=new ArrayList<>();
				currentFileName=dp.getFileName();
			}else if(i==dataPoints.size()-1){
				dataPointss.add(dataPoints_sameFile);
			}
			dataPoints_sameFile.add(dp);
		}
		
		for(List<DataPoint> dps:dataPointss){
			String fileName=dps.get(0).getFileName();
			File srcFile=new File(fileName);
			File desFile=new File(fileName.replace(srcDir, desDir));
			try{
				scanner=new Scanner(new FileInputStream(srcFile));
				writer=new FileWriter(desFile);
				int lineNumber=-1;
				int dpsIndex=0;
				while(scanner.hasNextLine()){
					if(dpsIndex>dps.size()-1) break; //数据点已读完，剩下的数据原样写入即可
					String line=scanner.nextLine();
					lineNumber++;
					String[] attributes=line.split("\t");
					DataPoint dataPoint=dps.get(dpsIndex);
					if(dataPoint.getLineNumber()==lineNumber){
						dpsIndex++;
						attributes[0]=format.format(TimeUtils.TimeStamp2Date(dataPoint.getTime()));
						attributes[1]=StringProtectUtils.encryptHMAC(dataPoint.getUserId(),key);
						attributes[10]=String.valueOf(dataPoint.getLocation().getLongitude());
						attributes[11]=String.valueOf(dataPoint.getLocation().getLatitude());
					}else{//针对未读取的无效数据
						if(lineNumber!=0&&PrivacyConfig.getInstance().getTimeInterval()!=0){
							continue;
						}
						String userId=attributes[1];
						String hashedUserId=StringProtectUtils.encryptHMAC(userId,key);
						if (lineNumber == 0)
							hashedUserId = userId;// 第一行是列名，不需要hash
						attributes[1]=hashedUserId;
					}
					line=String.join("\t", attributes);
					writer.write(line+"\n");
				}
				while(scanner.hasNextLine()){
					if(PrivacyConfig.getInstance().getTimeInterval()!=0)
						break;
					String line=scanner.nextLine();
					lineNumber++;
					String[] attributes=line.split("\t");
					String userId=attributes[1];
					String hashedUserId=StringProtectUtils.encryptHMAC(userId,key);
					if (lineNumber == 0)
						hashedUserId = userId;// 第一行是列名，不需要hash
					attributes[1]=hashedUserId;
					line=String.join("\t", attributes);
					writer.write(line+"\n");
				}
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				scanner.close();
				try{
					writer.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 只拷贝文件结构，不拷贝内容
	 * @param srcDir
	 * @param desDir
	 */
	public void copyAllFileStructureWithoutContent(String srcDir,String desDir){
		File srcDirFile=new File(srcDir);
		for(File file:srcDirFile.listFiles()){
			String oldPath=file.getPath();
			String newPath=oldPath.replace(srcDir, desDir);
			File newFile=new File(newPath);
			if(file.isDirectory()){
				if(!newFile.exists()){
					newFile.mkdir();
				}
				copyAllFileStructureWithoutContent(oldPath, newPath);
			}else if(file.isFile()){
				if(!newFile.exists()){
					try{
						newFile.createNewFile();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
