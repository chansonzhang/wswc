package cn.edu.xjtu.hadoop;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author zhangchen
 *
 */
public class myMapper extends Mapper<Text, Text, Text, Text> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object,
	 * java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(Text key, Text value,
			Mapper<Text, Text, Text, Text>.Context context) throws IOException,
			InterruptedException {
		Configuration conf = context.getConfiguration();
		PrivacyConfig.initialize(conf.get("cnfFile"));
		StringTokenizer lineItor = new StringTokenizer(value.toString(), "\n");
		int lineNumber = -1;
		List<DataPoint> dataPoints = new ArrayList<>();
		/*
		 * String date="20090220"; date = date.substring(0, 4) + "-" +
		 * date.substring(4, 6) + "-" + date.substring(6, 8);
		 */
		// System.out.println(value.toString());
		// System.out.println(date);
		while (lineItor.hasMoreTokens()) {
			String currentLine = lineItor.nextToken().toString();
			lineNumber++;
			if (lineNumber < 1)
				continue;
			if (currentLine != null) {
				String[] values = currentLine.split("\t");
				if (values[10].equals("-999") || values[11].equals("-999"))
					continue;
				DataPoint dataPoint = new DataPoint(values[1], new Location(
						Double.parseDouble(values[10]),
						Double.parseDouble(values[11])),
						Timestamp.valueOf(values[0]), key.toString(),
						lineNumber);
				dataPoints.add(dataPoint);
			}
		}
		List<Trajectory> originTs = PrivacyUtils.getTrajectories(dataPoints);
		List<Trajectory> resultTrajectories = PrivacyUtils
				.protectPrivacy(originTs);
		for (Trajectory t : resultTrajectories) {
			List<DataPoint> dps = t.getDataPoints();
			for (DataPoint dataPoint : dps) {
				String uid = dataPoint.getUserId();
				Location location = dataPoint.getLocation();
				String time = dataPoint.getTime().toString();
				context.write(key, new Text(uid + "," + location.getLatitude()
						+ "," + location.getLongitude() + "," + time));
			}
		}

	}

}
