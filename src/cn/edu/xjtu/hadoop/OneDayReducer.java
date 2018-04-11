package cn.edu.xjtu.hadoop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapreduce.Reducer;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;
import cn.edu.xjtu.utils.StringProtectUtils;

/**
 * 
 * @author zhanchen 
 * 输入key是日期，value是包含文件名、行号、行的数组
 * 输出key是文件名，value是行
 *
 */
public class OneDayReducer extends Reducer<Text, Text[], Text, Text> {

	@Override
	protected void reduce(Text arg0, Iterable<Text[]> values,
			Reducer<Text, Text[], Text, Text>.Context context)
			throws IOException, InterruptedException {
		Iterator<Text[]> valueItor = values.iterator();
		List<DataPoint> dataPoints = new ArrayList<>();
		while (valueItor.hasNext()) {
			Text[] texts = valueItor.next();
			Text fileName = texts[0];
			int lineNumber = Integer.parseInt(texts[1].toString());
			String currentLine = texts[2].toString();

			if (currentLine != null) {
				String[] cols = currentLine.split("\t");
				if (cols[10].equals("-999") || cols[11].equals("-999"))
					continue;
				DataPoint dataPoint = new DataPoint(cols[1], new Location(
						Double.parseDouble(cols[10]),
						Double.parseDouble(cols[11])),
						Timestamp.valueOf(cols[0]), fileName.toString(),
						lineNumber);
				dataPoints.add(dataPoint);
			}
		}
		List<Trajectory> originTs = PrivacyUtils.getTrajectories(dataPoints);
		List<Trajectory> resultTrajectories = PrivacyUtils
				.protectPrivacy(originTs);

		Map<String, Scanner> scanners = new HashMap<>();

		String srcDir = PrivacyConfig.getInstance().getSourceDir();
		String desDir = PrivacyConfig.getInstance().getResultDir();
		copyAllFileStructureWithoutContent(srcDir, desDir);

		dataPoints = new ArrayList<DataPoint>();
		for (Trajectory trajectory : resultTrajectories) {
			dataPoints.addAll(trajectory.getDataPoints());
		}
		dataPoints.sort(new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint dp1, DataPoint dp2) {
				if (dp1.getFileName().compareTo(dp2.getFileName()) < 0)
					return -1;
				else if (dp1.getFileName().compareTo(dp2.getFileName()) > 0)
					return 1;
				else {
					if (dp1.getLineNumber() < dp2.getLineNumber())
						return -1;
					else if (dp1.getLineNumber() > dp2.getLineNumber())
						return 1;
					else
						return 0;
				}
			}

		});

		Set<List<DataPoint>> dataPointss = new HashSet<>();
		String currentFileName = dataPoints.get(0).getFileName();
		List<DataPoint> dataPoints_sameFile = new ArrayList<>();

		for (int i = 0; i < dataPoints.size(); i++) {
			DataPoint dp = dataPoints.get(i);
			if (!dp.getFileName().equals(currentFileName)) {
				dataPointss.add(dataPoints_sameFile);
				dataPoints_sameFile = new ArrayList<>();
				currentFileName = dp.getFileName();
			} else if (i == dataPoints.size() - 1) {
				dataPointss.add(dataPoints_sameFile);
			}
			dataPoints_sameFile.add(dp);
		}

		for (List<DataPoint> dps : dataPointss) {
			String fileName = dps.get(0).getFileName();
			Scanner scanner = null;
			if (scanners.get(fileName) == null) {
				String inputPath = context.getConfiguration().get("inputPath");
				Path path = new Path(inputPath);
				FileSystem fs = path.getFileSystem(context.getConfiguration());
				FSDataInputStream in = fs.open(path);
				scanners.put(fileName, new Scanner(in));
			}
			scanner = scanners.get(fileName);
			try {
				int lineNumber = -1;
				int dpsIndex = 0;
				while (scanner.hasNextLine()) {
					if (dpsIndex > dps.size() - 1)
						break; // 数据点已读完，剩下的数据原样写入即可
					String line = scanner.nextLine();
					lineNumber++;
					String[] attributes = line.split("\t");
					DataPoint dataPoint = dps.get(dpsIndex);
					if (dataPoint.getLineNumber() == lineNumber) {
						dpsIndex++;
						attributes[0] = dataPoint.getTime().toString();
						attributes[1] = dataPoint.getUserId();
						attributes[10] = String.valueOf(dataPoint.getLocation()
								.getLongitude());
						attributes[11] = String.valueOf(dataPoint.getLocation()
								.getLatitude());
					} else {// 针对未读取的无效数据
						String userId = attributes[1];
						String hashedUserId = StringProtectUtils.getMd5(userId);
						if(lineNumber==0) hashedUserId=userId;//第一行是列名，不需要hash
						attributes[1] = hashedUserId;
					}
					line = String.join("\t", attributes);
					context.write(new Text(fileName), new Text(line));
				}
				while (scanner.hasNextLine()) {
					if (dpsIndex > dps.size() - 1)
						break; // 数据点已读完，剩下的原样写入即可。
					String line = scanner.nextLine();
					lineNumber++;
					context.write(new Text(fileName), new Text(line + "\n"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				scanner.close();
			}
		}
	}

	/**
	 * 只拷贝文件结构，不拷贝内容
	 * 
	 * @param srcDir
	 * @param desDir
	 */
	public void copyAllFileStructureWithoutContent(String srcDir, String desDir) {
		File srcDirFile = new File(srcDir);
		for (File file : srcDirFile.listFiles()) {
			String oldPath = file.getPath();
			String newPath = oldPath.replace(srcDir, desDir);
			File newFile = new File(newPath);
			if (file.isDirectory()) {
				if (!newFile.exists()) {
					newFile.mkdir();
				}
				copyAllFileStructureWithoutContent(oldPath, newPath);
			} else if (file.isFile()) {
				if (!newFile.exists()) {
					try {
						newFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
