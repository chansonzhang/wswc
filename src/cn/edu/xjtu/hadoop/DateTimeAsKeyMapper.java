package cn.edu.xjtu.hadoop;

import cn.edu.xjtu.utils.PrivacyConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 
 * @author zhangchen
 * key为文件名，value为行号+"\t"+currentLine
 * 转化为日期时间为key，LongLine为value传递给reduce，
 *         LongLine中包含了fileName，lineNumber，和行内容
 *
 */
public class DateTimeAsKeyMapper extends Mapper<Text, Text, Text, LongLine> {

	@Override
	protected void map(Text key, Text value,
			Mapper<Text, Text, Text, LongLine>.Context context)
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		PrivacyConfig.initialize(conf.get("cnfFile"));
		final String dataSeparator=PrivacyConfig.getInstance().getDataSeparator();
		final int tsIndex=PrivacyConfig.getInstance().getTsIndex();
		final int userIndex=PrivacyConfig.getInstance().getUserIndex();
		final int lonIndex=PrivacyConfig.getInstance().getLonIndex();
		final int latIndex=PrivacyConfig.getInstance().getLatIndex();
		int howManyHoursPerSection = PrivacyConfig.getInstance()
				.getHowManyHoursPerSection();
		String lineNumAndcurrentLine = value.toString();
		String[] values = lineNumAndcurrentLine.split("\t");
		int lineNumber = Integer.parseInt(values[0]);
		StringBuilder linebuilder = new StringBuilder();
		for (int i = 1; i < values.length - 1; i++)
			linebuilder.append(values[i] + "\t");
		linebuilder.append(values[values.length - 1]);
		String currentLine = linebuilder.toString();
		if (lineNumber < 1)// 不读第一行
			currentLine = null;
		//将数据的有效性验证移动到MyLineRecordReader中
		/*if(currentLine!=null){
			String[] cols = currentLine.split("\t");
			if ((!DataUtils.isValidLongitude(cols[10]))|| (!DataUtils.isValidLatitude(cols[11])))
				currentLine=null;
		}*/
		if (currentLine != null) {
			String ts = currentLine.split(dataSeparator)[tsIndex];
			String date = ts.split(" ")[0];
			String time = ts.split(" ")[1].substring(0, 2);
			time = DateUtils.floorHour(time, howManyHoursPerSection);
			String dateTime = date + time;
			LongLine texts = new LongLine(key, new Text(
					String.valueOf(lineNumber)), new Text(currentLine));
			context.write(new Text(dateTime), texts);
		}
	}

}
