package cn.edu.xjtu.hadoop;

import com.google.common.base.Charsets;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

/**
 * 
 * @author zhangchen
 *
 */
public class FileNameAsKeyLineInputFormat extends FileInputFormat<Text, Text> {

	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException,
			InterruptedException {
		String delimiter = context.getConfiguration().get(
		        "textinputformat.record.delimiter");
		    byte[] recordDelimiterBytes = null;
		    if (null != delimiter)
		      recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
		    MyLineRecordReader recordReader= new MyLineRecordReader(recordDelimiterBytes);
		    recordReader.initialize(split, context);
		    return recordReader;
	}

}
