package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangchen on 2015/9/28.
 * 文件操作类
 */
public class FileUtils {
    private static final String sourceDir=PrivacyConfig.getInstance().getSourceDir();
    private static final String resultDir=PrivacyConfig.getInstance().getResultDir();
    private static final String DATA_PATH = "E:\\Document\\项目\\15年华为\\数据\\Geolife Trajectories 1.2\\Geolife Trajectories 1.2\\Data";
    private static final int USR_COUNT = 178;

    public static void main(String[] args) {
        FileUtils.generate_a_holy_big_day();
    }

    public static void writeResult(String resultDir, List<Trajectory> trajectories) {
        ResultWriter writer = new ResultWriter(new WriteWithIndex());
        writer.write(resultDir, trajectories);
    }

    /**
     * 不用指定日期
     * @return
     */
    public static List<DataPoint> getDataPoints(){
    	List<DataPoint> dataPoints = new ArrayList<>();
        File source_dir_file=new File(sourceDir);
        readDataPointFromFile(source_dir_file,dataPoints);
        return dataPoints; 
    }
    
    /**
     * 获取指定日期的所有数据点
     *
     * @param date
     * @return
     */
    public static List<DataPoint> getDataPoints(String date) {
        List<DataPoint> dataPoints = new ArrayList<>();
        File source_dir_file=new File(sourceDir);
        readDataPointFromFile(date,source_dir_file,dataPoints);
        return dataPoints;
        }
    
    public static void readDataPointFromFile(String date,File file,List<DataPoint> dataPoints){
    	if(file.isDirectory()){
    		for(File f:file.listFiles())
    			readDataPointFromFile(date, f, dataPoints);
    	}else if(file.isFile()){
    		Scanner scanner=null;
    		int lineNumber=-1;
    		try{
    			scanner=new Scanner(new BufferedInputStream(new FileInputStream(file)));
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}
    		while(scanner.hasNextLine()){
    			String currentLine = scanner.nextLine();
    			lineNumber++;
    			if(lineNumber<1) continue;
                if (currentLine != null) {
                    String[] values = currentLine.split("\t");
                    if (values[10].equals("-999")||values[11].equals("-999")) continue;
                    if(values[0].split(" ")[0].equals(date)){
                        DataPoint dataPoint = new DataPoint(values[1]
                                , new Location(Double.parseDouble(values[10]), Double.parseDouble(values[11]))
                                , Timestamp.valueOf(values[0]),file.getPath(),lineNumber);
                        dataPoints.add(dataPoint);
                    }
                }
    		}
    		scanner.close();
    	}
        
        
    }


    /**
     * 不用指定日期
     * @param file
     * @param dataPoints
     */
    public static void readDataPointFromFile(File file,List<DataPoint> dataPoints){
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final String dataSeparator=PrivacyConfig.getInstance().getDataSeparator();
        final int tsIndex=PrivacyConfig.getInstance().getTsIndex();
        final int userIndex=PrivacyConfig.getInstance().getUserIndex();
        final int lonIndex=PrivacyConfig.getInstance().getLonIndex();
        final int latIndex=PrivacyConfig.getInstance().getLatIndex();
    	if(file.isDirectory()){
    		for(File f:file.listFiles())
    			readDataPointFromFile(f, dataPoints);
    	}else if(file.isFile()){
    		Scanner scanner=null;
    		int lineNumber=-1;
    		try{
    			scanner=new Scanner(new BufferedInputStream(new FileInputStream(file)));
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}
    		while(scanner.hasNextLine()){
    			String currentLine = scanner.nextLine();
    			lineNumber++;
    			if(lineNumber<1) continue;
                if(lineNumber==1316179){
                    System.out.print("hello world");
                }
                if (currentLine != null) {
                	String[] values = currentLine.split(dataSeparator);
                	Date date=null;
    				try {
                        if (values[tsIndex].indexOf(".") == -1)
                            date = format.parse(values[tsIndex] + ".000");
                        else
                            date = format.parse(values[tsIndex]);
                    } catch (ParseException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                    
    				if ((!DataUtils.isValidLongitude(values[lonIndex]))|| (!DataUtils.isValidLatitude(values[latIndex])))
    					continue;
                        DataPoint dataPoint = new DataPoint(values[userIndex]
                                , new Location(Double.parseDouble(values[lonIndex]), Double.parseDouble(values[latIndex]))
                                , TimeUtils.Date2TimeStamp(date),file.getPath(),lineNumber);
                        dataPoints.add(dataPoint);
                }
    		}
    		scanner.close();
    	}
        
        
    }


    /**
     * @param howMany
     * @return
     * @author zhangchen
     */
    public static String[] findDays(int howMany) {
        Map<String, Integer> dateCount = new HashMap<String, Integer>();
        for (int i = 0; i < USR_COUNT; i++) {
            String userId = String.format("%03d", i);
            File userFolder = new File(DATA_PATH, userId + "/Trajectory");
            File[] files = userFolder.listFiles();
            for (int j = 0; j < files.length; j++) {
                String filename = files[j].getName();
                String date = filename.substring(0, filename.length() - 10);
                int value = 0;
                if (dateCount.containsKey(date))
                    value = dateCount.get(date);
                dateCount.put(date, value + 1);
            }
        }

        Set<String> topNDateCount = new HashSet<>();
        int[] counts = new int[howMany];
        String[] dates = new String[howMany];
        for (int i = 0; i < howMany; i++) {
            Iterator<Map.Entry<String, Integer>> it = dateCount.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                if (!topNDateCount.contains(entry.getKey()) && entry.getValue() > counts[i]) {
                    dates[i] = entry.getKey();
                    counts[i] = entry.getValue();
                }
            }
            topNDateCount.add(dates[i]);
        }
        return dates;
    }

    public static void generate_a_holy_big_day() {
        File output_file = new File("E:\\Document\\项目\\15年华为\\demo\\数据\\zc\\a_holy_big_day.zc");
        FileWriter writer = null;
        try {
            writer = new FileWriter(output_file, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] dates = findDays(1);
        String date = dates[0];
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        Scanner scanner = null;
        //结果中用户数的上限，设置为0则获取所有拥有数据的用户的数据
        int resultUsrCountLimit = 1000;
        int resultUsrCount = 0;
        for (int i = 0; i < USR_COUNT; i++) {
            if (resultUsrCountLimit != 0 && resultUsrCount == resultUsrCountLimit)
                break;
            File userFolder = new File(DATA_PATH, String.format("%03d", i) + "/Trajectory");
            File[] files = userFolder.listFiles();
//            File file = files[0];
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                String filename = file.getName();
                try {
                    scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                for (int k = 0; k < 6; k++)//无效数据
                {
                    scanner.nextLine();
                }
                do {
                    String currentLine = scanner.nextLine();
                    if (currentLine != null) {
                        String userId = String.format("%03d", resultUsrCount);
//                        if(userId.equals("047"))
//                            System.out.println("047对应的文件名："+filename);
                        currentLine = userId + "," + currentLine;
                        if (!currentLine.split(",")[6].equals(date)) {
                            String new_currentLine = "";
                            for (int m = 0; m < 6; m++) {
                                new_currentLine += currentLine.split(",")[m] + ",";
                            }
                            new_currentLine += date + "," + currentLine.split(",")[7];
                            currentLine = new_currentLine;
                        }
                        try {
                            writer.write(currentLine + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                while (scanner.hasNextLine());
                resultUsrCount++;
                scanner.close();
                if (resultUsrCountLimit != 0 && resultUsrCount == resultUsrCountLimit)
                    break;
            }

        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
