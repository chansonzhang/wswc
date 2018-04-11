package cn.edu.xjtu.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 
 * @author zhangchen
 *
 */
public class HDFSUtils {
	/**
	 * 只拷贝文件结构，不拷贝内容
	 * HDFS版本
	 * @param srcDir
	 * @param desDir
	 * @throws IOException 
	 */
	public static void copyAllFileStructureWithoutContent(String srcDir, String desDir) throws IOException {
		FileSystem fs=FileSystem.get(new Configuration());
		Path srcPath=new Path(srcDir);
		for (FileStatus file : fs.listStatus(srcPath)) {
			String oldPathStr = file.getPath().toString();
			String newPathStr = oldPathStr.replace(srcDir, desDir);
			Path newPath=new Path(newPathStr);
			if (file.isDirectory()) {
				if (!fs.exists(newPath)){
					fs.mkdirs(newPath);
				}
				copyAllFileStructureWithoutContent(oldPathStr, newPathStr);
			} else if (file.isFile()) {
				if (!fs.exists(newPath)) {
					try {
						fs.create(newPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
