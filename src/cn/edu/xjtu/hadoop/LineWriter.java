package cn.edu.xjtu.hadoop;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class LineWriter extends RecordWriter<Text, Text> {
	private TaskAttemptContext job;
	private Map<Text,PrintWriter> writers=new HashMap();
	private Path outputPath;
	public LineWriter(TaskAttemptContext job,Path outputPath) {
		this.job=job;
		this.outputPath=outputPath;
	}
	@Override
	public void close(TaskAttemptContext arg0) throws IOException,
			InterruptedException {
		for(PrintWriter writer:this.writers.values()){
			writer.close();
		}
		
	}

	@Override
	public void write(Text key, Text value) throws IOException,
			InterruptedException {
		if(!this.writers.containsKey(key)){
			Path path=new Path(this.outputPath+"/"+key);
			FSDataOutputStream fileOut=path.getFileSystem(job.getConfiguration()).create(path);
			this.writers.put(key, new PrintWriter(fileOut));
		}
		PrintWriter writer=this.writers.get(key);
		writer.println(value.toString());
	}

}
