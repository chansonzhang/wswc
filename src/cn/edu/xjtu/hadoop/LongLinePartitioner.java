package cn.edu.xjtu.hadoop;

import cn.edu.xjtu.utils.PrivacyConfig;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Created by zhangchen on 2016/4/12.
 * Contact Me: chansonzhang@163.com
 */
public class LongLinePartitioner extends Partitioner<Text, LongLine> {
    @Override
    public int getPartition(Text text, LongLine longLine, int numPartitions) {
        String dateTime=text.toString();
        String time;
        if(PrivacyConfig.getInstance().getTimeInLine()==1){
             time=dateTime.substring(10,12);
        }else{//文件名中的时间
             time=dateTime.substring(8,10);
        }

        int t=Integer.parseInt(time);
        int howManyHoursPerSection= PrivacyConfig.getInstance().getHowManyHoursPerSection();
        return ((t/howManyHoursPerSection)%numPartitions);
    }
}
