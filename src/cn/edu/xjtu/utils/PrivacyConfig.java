package cn.edu.xjtu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class PrivacyConfig {
	private String date;
    private int protectMethod;
    private int timeInterval;
    private int timeThreshold;
    private int distanceThreshold;
    private int leaveThreshold;
    private double eps;
    private int minp;
    private int SCAN_INTERVAL;
    private int DURATION_THRESHOLD;
    private int CROWD_THRESHOLD;
    private int changeTimeLimit;
    private String sourceDir;
    private String resultDir;
    private String logDir;
    private int K;
    private int Interval;
    private int leng;
    private int bc;
    private int measure;
    private int howManyHoursPerSection;
    private int resultSort;
    private int timeInLine;
    private int threadNum;
    private String dataSeparator;
    private int tsIndex;
    private int userIndex;
    private int lonIndex;
    private int latIndex;
    private static PrivacyConfig ourInstance = null;
    private static Object syncObj=new Object();
    public static PrivacyConfig getInstance(){
    	if(ourInstance==null){
    		throw new NullPointerException("请先使用initialize静态方法初始化配置文件！");
    	}
    	return ourInstance;
    }
    public static PrivacyConfig initialize(String cnf) {
    	if(ourInstance==null){
    		synchronized (syncObj) {
    			if(ourInstance==null){
    				ourInstance=new PrivacyConfig(cnf);
    			}
			}
    	}
        return ourInstance;
    }
    private PrivacyConfig(String cnf) {
        Properties properties=new Properties();
        FileInputStream fileInputStream=null;
        try {
            //fileInputStream=new FileInputStream(System.getProperty("user.dir")+File.separator+"config"+ File.separator+"privacy.properties");
        	File cnf_file=new File(cnf);
        	fileInputStream=new FileInputStream(cnf_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.date=properties.getProperty("date");
        this.changeTimeLimit= Integer.parseInt(properties.getProperty("changeTimeLimit"));
        this.CROWD_THRESHOLD= Integer.parseInt(properties.getProperty("CROWD_THRESHOLD"));
        this.distanceThreshold= Integer.parseInt(properties.getProperty("distanceThreshold"));
        this.DURATION_THRESHOLD= Integer.parseInt(properties.getProperty("DURATION_THRESHOLD"));
        this.eps= Double.parseDouble(properties.getProperty("eps"));
        this.minp= Integer.parseInt(properties.getProperty("minp"));
        this.protectMethod= Integer.parseInt(properties.getProperty("protectMethod"));
        this.timeInterval= Integer.parseInt(properties.getProperty("timeInterval"));
        this.timeThreshold= Integer.parseInt(properties.getProperty("timeThreshold"));
        this.leaveThreshold= Integer.parseInt(properties.getProperty("leaveThreshold"));
        //this.SCAN_INTERVAL= Integer.parseInt(properties.getProperty("SCAN_INTERVAL"));
        //this.sourceDir=properties.getProperty("sourceDir").replace("/",File.separator);
        //this.resultDir=properties.getProperty("resultDir").replace("/", File.separator);
        this.logDir=properties.getProperty("logDir")==null?"/var/log/hwPrivacy/hwPrivacy.log":properties.getProperty("logDir").replace("/", File.separator);
        this.bc=properties.getProperty("bc")==null?500:Integer.parseInt(properties.getProperty("bc"));
        this.K=properties.getProperty("K")==null?10:Integer.parseInt(properties.getProperty("K"));
        this.leng=properties.getProperty("leng")==null?50000:Integer.parseInt(properties.getProperty("leng"));
        this.Interval=properties.getProperty("Interval")==null?60:Integer.parseInt(properties.getProperty("Interval"));
        this.measure=Integer.parseInt(properties.getProperty("measure"));
        this.howManyHoursPerSection=properties.getProperty("howManyHoursPerSection")==null?
        		6:Integer.parseInt(properties.getProperty("howManyHoursPerSection"));
        this.resultSort=properties.getProperty("resultSort")==null?
                0:Integer.parseInt(properties.getProperty("resultSort"));
        this.timeInLine=properties.getProperty("timeInLine")==null?
                0:Integer.parseInt(properties.getProperty("timeInLine"));
        this.threadNum=properties.getProperty("threadNum")==null?
                0:Integer.parseInt(properties.getProperty("threadNum"));
        this.dataSeparator=properties.get("dataSeparator")==null?
                "\t":properties.getProperty("dataSeparator").toString();
        this.tsIndex=properties.getProperty("tsIndex")==null?
                0:Integer.parseInt(properties.getProperty("tsIndex"));
        this.userIndex=properties.getProperty("userIndex")==null?
                1:Integer.parseInt(properties.getProperty("userIndex"));
        this.lonIndex=properties.getProperty("lonIndex")==null?
                10:Integer.parseInt(properties.getProperty("lonIndex"));
        this.latIndex=properties.getProperty("latIndex")==null?
                11:Integer.parseInt(properties.getProperty("latIndex"));

    }

    
    public int getMeasure() {
		return measure;
	}
	public void setMeasure(int measure) {
		this.measure = measure;
	}
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	public void setResultDir(String resultDir) {
		this.resultDir = resultDir;
	}
	public int getProtectMethod() {
        return protectMethod;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public int getTimeThreshold() {
        return timeThreshold;
    }

    public int getDistanceThreshold() {
        return distanceThreshold;
    }

    public int getLeaveThreshold() {
        return leaveThreshold;
    }

    public double getEps() {
        return eps;
    }

    public int getMinp() {
        return minp;
    }

    public int getDURATION_THRESHOLD() {
        return DURATION_THRESHOLD;
    }

    public int getCROWD_THRESHOLD() {
        return CROWD_THRESHOLD;
    }

    public int getChangeTimeLimit() {
        return changeTimeLimit;
    }

    public static PrivacyConfig getOurInstance() {
        return ourInstance;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getLogDir(){
    	return logDir;
    }
    
    public String getResultDir() {
        return resultDir;
    }
	public String getDate() {
		return date;
	}
	public int getK() {
		return K;
	}
	public void setK(int k) {
		K = k;
	}
	public int getBc() {
		return bc;
	}
	public void setBc(int bc) {
		this.bc = bc;
	}
	public int getInterval() {
		return Interval;
	}
	public void setInterval(int interval) {
		Interval = interval;
	}
	public int getLeng() {
		return leng;
	}
	public void setLeng(int leng) {
		this.leng = leng;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getHowManyHoursPerSection() {
		return howManyHoursPerSection;
	}
	public int getResultSort(){
        return resultSort;
    }
    public int getTimeInLine(){
        return timeInLine;
    }
    public int getThreadNum(){return threadNum;}

    public String getDataSeparator() {
        return dataSeparator;
    }

    public int getTsIndex() {
        return tsIndex;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public int getLonIndex() {
        return lonIndex;
    }

    public int getLatIndex() {
        return latIndex;
    }
}
