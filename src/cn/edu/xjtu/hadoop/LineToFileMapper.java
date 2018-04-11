package cn.edu.xjtu.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
/**
 * 
 * @author zhangchen
 *将行号从value中提取出来，放入key中，这样可以利用自定义的keyComparator进行排序
 *value中还应保留行号，因为后面会用到
 */
public class LineToFileMapper extends Mapper<Text, Text, FileNameAndLineNumber, Text> {

	@Override
	protected void map(Text key, Text value,
			Mapper<Text, Text, FileNameAndLineNumber, Text>.Context context)
			throws IOException, InterruptedException {
		String[] values=value.toString().split("\t");
		int lineNumber=Integer.parseInt(values[0]);
		FileNameAndLineNumber fileNameAndLineNumber=new FileNameAndLineNumber(key,new Text(String.valueOf(lineNumber)));
		context.write(fileNameAndLineNumber, value);
	}
	
}
