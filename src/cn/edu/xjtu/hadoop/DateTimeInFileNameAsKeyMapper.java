package cn.edu.xjtu.hadoop;

import cn.edu.xjtu.utils.PrivacyConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by zhangchen on 2016/4/14.
 * Contact Me: chansonzhang@163.com
 * 输入key为文件名，value为行号+"\t"+currentLine
 * 输出datetime为key，LongLine为value传递给reduce，
 * LongLine中包含了fileName，lineNumber，和行内容
 */
public class DateTimeInFileNameAsKeyMapper extends Mapper<Text, Text, Text, LongLine> {

    @Override
    protected void map(Text key, Text value,
                       Mapper<Text, Text, Text, LongLine>.Context context)
            throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        PrivacyConfig.initialize(conf.get("cnfFile"));
        int howManyHoursPerSection = PrivacyConfig.getInstance()
                .getHowManyHoursPerSection();
        String ts = key.toString().split("_")[3];
        String date = ts.substring(0,8);
        String time = ts.substring(8,10);
        time = DateUtils.floorHour(time, howManyHoursPerSection);
        String dateTime = date + time;
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
        if (currentLine != null) {
            LongLine texts = new LongLine(key, new Text(
                    String.valueOf(lineNumber)), new Text(currentLine));
            context.write(new Text(dateTime), texts);
        }
    }

}

