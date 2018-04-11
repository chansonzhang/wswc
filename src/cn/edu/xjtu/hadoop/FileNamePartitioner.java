package cn.edu.xjtu.hadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

/**
 * 
 * @author zhangchen
 *
 */
public class FileNamePartitioner extends Partitioner<FileNameAndLineNumber, Text> {

	@Override
	public int getPartition(FileNameAndLineNumber key, Text value, int numPartitions) {
		// TODO Auto-generated method stub
		HashPartitioner<Text, Text> hashPartitioner=new HashPartitioner<Text, Text>();
		//根据文件名进行划分
		return hashPartitioner.getPartition(key.getFileName(), value, numPartitions);
	}

}
