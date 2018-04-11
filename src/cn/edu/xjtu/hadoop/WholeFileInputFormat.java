package cn.edu.xjtu.hadoop;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author zhangchen
 *
 */
public class WholeFileInputFormat extends FileInputFormat<Text, Text> {

	@Override
	public RecordReader<Text, Text> createRecordReader(
			org.apache.hadoop.mapreduce.InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		RecordReader<Text, Text> recordReader= new WholeFileRecordReader();
		recordReader.initialize(split, context);
		return recordReader;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.lib.input.FileInputFormat#isSplitable(org.apache.hadoop.mapreduce.JobContext, org.apache.hadoop.fs.Path)
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Generate the list of files and make them into FileSplits.
	 *
	 * @param job the job context
	 * @throws IOException
	 */
	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		return super.getSplits(job);
	}

}
