package cn.edu.xjtu.hadoop;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by zhangchen on 2016/4/14.
 * Contact Me: chansonzhang@163.com
 * 输入key是文件名，value是行号+"\t"+"行"
 * 只将行写入对应的文件
 */
public class PureLineOutputFormat extends FileOutputFormat<Text, Text> {
    @Override
    public RecordWriter<Text, Text> getRecordWriter(
            TaskAttemptContext job) throws IOException, InterruptedException {
        Path outputPath = getOutputPath(job);
        return new PureLineWriter(job, outputPath);
    }
}