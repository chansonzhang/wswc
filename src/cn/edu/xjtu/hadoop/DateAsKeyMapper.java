package cn.edu.xjtu.hadoop;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;

/**
 * 
 * @author zhangchen
 *         把文件名为key，文件内容为value
 *         转化为日期时间为key，LongLine为value传递给reduce，
 *         LongLine中包含了fileName，lineNumber，和行内容
 *
 */
public class DateAsKeyMapper extends Mapper<Text, Text, Text, LongLine> {

	@Override
	protected void map(Text key, Text value,
			Mapper<Text, Text, Text, LongLine>.Context context)
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		PrivacyConfig.initialize(conf.get("cnfFile"));
		StringTokenizer lineItor = new StringTokenizer(value.toString(), "\n");
		int lineNumber = -1;
		int howManyHoursPerSection=PrivacyConfig.getInstance().getHowManyHoursPerSection();
		while (lineItor.hasMoreTokens()) {
			String currentLine = lineItor.nextToken().toString();
			lineNumber++;
			if (lineNumber < 1)//不读第一行
				continue;
			if (currentLine != null) {
				String ts=currentLine.split("\t")[0];
				String date = ts.split(" ")[0];
				String time = ts.split(" ")[1].substring(0,2);
				time=DateUtils.floorHour(time, howManyHoursPerSection);
				String dateTime=date+time;
				LongLine texts=new LongLine(key, new Text(String.valueOf(lineNumber)), new Text(currentLine));
				context.write(new Text(dateTime),texts);
			}
		}

		
	}
}
