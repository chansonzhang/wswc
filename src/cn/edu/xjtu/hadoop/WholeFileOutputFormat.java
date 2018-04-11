package cn.edu.xjtu.hadoop;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 
 * @author zhangchen
 *
 */
public class WholeFileOutputFormat extends FileOutputFormat<Text, Text>{
	@Override
	public RecordWriter<Text, Text> getRecordWriter(
			TaskAttemptContext job) throws IOException, InterruptedException {
		Path outputPath=getOutputPath(job);
		return new NoTabWriter(job,outputPath);
	}

}
