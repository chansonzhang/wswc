package cn.edu.xjtu.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * 
 * @author zhangchen
 *
 */
public class LogUtils {
	private static LogUtils ourInstance=new LogUtils();
	public static LogUtils getInstance(){
		return ourInstance;
	}
	private File logFile;
	private BufferedWriter writer;
	private LogUtils(){
		String logDir=PrivacyConfig.getInstance().getLogDir();
		logFile=new File(logDir);
		File parents=new File(logFile.getParent());
		if(!parents.canWrite()){
			try {
				throw new IOException("You have no access to write the log dir!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!parents.exists())parents.mkdirs();
		if(!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			writer=new BufferedWriter(new FileWriter(logDir,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void append(String str){
		
		try {
			writer.write(str);
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		writer.close();
		super.finalize();
	}

}
