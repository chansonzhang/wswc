package cn.edu.xjtu.hadoop;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchen on 2016/4/14.
 * Contact Me: chansonzhang@163.com
 * 输入key是文件名，value是行号+"\t"+"行"
 * 只将行写入对应的文件
 */
public class PureLineWriter extends RecordWriter<Text, Text> {
    private TaskAttemptContext job;
    private Map<Text,PrintWriter> writers=new HashMap();
    private Path outputPath;
    private String inputPathStr;
    private String outputPathStr;
    public PureLineWriter(TaskAttemptContext job,Path outputPath) {
        this.job=job;
        this.outputPath=outputPath;
        this.inputPathStr=job.getConfiguration().get("inputPath");
        this.outputPathStr=job.getConfiguration().get("outputPath");
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
            Path path=new Path(key.toString().replace(inputPathStr, outputPathStr));
            FSDataOutputStream fileOut=path.getFileSystem(job.getConfiguration()).create(path);
            this.writers.put(key, new PrintWriter(fileOut));
        }
        PrintWriter writer=this.writers.get(key);
        StringBuilder line=new StringBuilder();
        String[] values=value.toString().split("\t");
        for(int i=1;i<values.length-1;i++){
            line.append(values[i]+"\t");
        }
        line.append(values[values.length-1]);
        writer.println(line);
    }

}
