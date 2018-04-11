package cn.edu.xjtu.hadoop;
import cn.edu.xjtu.utils.LogUtils;
import cn.edu.xjtu.utils.PrivacyConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 
 * @author zhangchen
 *
 */
public class HadoopMain {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException
	{
		if(args.length<2){
			throw new IllegalArgumentException("Arguments: <input dir> <output dir> <config file>");
		}
		String cnfFile="config/privacy.properties";
		if(args.length==3){
			cnfFile=args[2];
		}
		//用于存储中间结果
				String interPath="/hwPrivacy/interResult/";
				Random random=new Random();
				String key=Integer.toString(random.nextInt());
		Configuration conf=new Configuration();
		PrivacyConfig.initialize(cnfFile);
		conf.set("cnfFile", cnfFile);
		conf.set("inputPath",args[0]);
		conf.set("outputPath", args[1]);
		conf.set("key", key);

	    Configuration conf1=new Configuration();
		conf1.set("cnfFile", cnfFile);
	    conf1.set("inputPath",args[0]);
	    conf1.set("outputPath", args[1]);
	    conf1.set("key", key);
		
		Path interResultPath=new Path(interPath);
		FileSystem fs=interResultPath.getFileSystem(conf);
		if(fs.exists(interResultPath)){
			fs.delete(interResultPath);
			System.out.println(interResultPath.getName()+"存在，已删除！");
		}
		
		LogUtils.getInstance().append("-------begin------");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now=new Date();
        LogUtils.getInstance().append("Time:\t"+format.format(now));
        LogUtils.getInstance().append("输入目录：\t"+args[0]);
        LogUtils.getInstance().append("输出目录：\t"+args[1]);
        LogUtils.getInstance().append("配置文件：\t"+cnfFile);
		long time0=System.currentTimeMillis();

		boolean resultSort=PrivacyConfig.getInstance().getResultSort()==1?true:false;
        
		Job job=Job.getInstance(conf, "hwPrivacy");
	    job.setJarByClass(HadoopMain.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
		if(resultSort) {
			//做排序，先将结果写到中间目录
	    	FileOutputFormat.setOutputPath(job, interResultPath);
		}else{
			//不做排序，直接将结果写到最终输出目录
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
		}
	    job.setInputFormatClass(FileNameAsKeyLineInputFormat.class);
		if(resultSort){
			job.setOutputFormatClass(TextOutputFormat.class);
		}else {
			job.setOutputFormatClass(PureLineOutputFormat.class);
		}
		if(PrivacyConfig.getInstance().getTimeInLine()==1){
			job.setMapperClass(DateTimeAsKeyMapper.class);
		}else{
			job.setMapperClass(DateTimeInFileNameAsKeyMapper.class);

		}
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(LongLine.class);
		job.setPartitionerClass(LongLinePartitioner.class);
	    job.setReducerClass(TimeSectionReducer.class);
	    job.setNumReduceTasks(24/PrivacyConfig.getInstance().getHowManyHoursPerSection());
	    //job结束后才能进行下一步
	    job.waitForCompletion(true);
	    
	    if(PrivacyConfig.getInstance().getResultSort()==1) {
			Job job1 = Job.getInstance(conf1, "hwPrivacy1");
			job1.setJarByClass(HadoopMain.class);
			FileInputFormat.addInputPath(job1, interResultPath);
			FileOutputFormat.setOutputPath(job1, new Path(args[1]));
			job1.setInputFormatClass(KeyValueTextInputFormat.class);
			job1.setOutputFormatClass(WholeFileOutputFormat.class);
			job1.setMapperClass(LineToFileMapper.class);
			job1.setMapOutputKeyClass(FileNameAndLineNumber.class);
			job1.setPartitionerClass(FileNamePartitioner.class);
			job1.setSortComparatorClass(myKeyComparator.class);
			job1.setGroupingComparatorClass(FileGroupingComparator.class);
			job1.setReducerClass(myReducer.class);
			job1.setNumReduceTasks(23);
			job1.waitForCompletion(true);
		}
	    long time1=System.currentTimeMillis();
        System.out.println("结束，一共耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("结束，一共耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("-------end------\n");
	}
}
