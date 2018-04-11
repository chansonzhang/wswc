package cn.edu.xjtu.hadoop;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.*;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanchen
 *         输入key是日期和起始时间，value是包含文件名、行号、行的对象
 *         输出key是文件名，value是行号+"\t"+"行"
 *         为了解决不同的reduce写同一个文件时彼此覆盖的问题，每个reduce只写自己负责处理的那一部分，
 *         且在这个reduce中不管文件中的排序问题，在后续的job中，再根据行号进行排序
 */
public class TimeSectionReducer extends Reducer<Text, LongLine, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<LongLine> values,
                          Reducer<Text, LongLine, Text, Text>.Context context)
            throws IOException, InterruptedException {
        PrivacyConfig.initialize(context.getConfiguration().get("cnfFile"));
        LogUtils.getInstance().append("Task: " + context.getTaskAttemptID().toString());
        Iterator<LongLine> valueItor = values.iterator();
        List<DataPoint> dataPoints = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final String dataSeparator=PrivacyConfig.getInstance().getDataSeparator();
        final int tsIndex=PrivacyConfig.getInstance().getTsIndex();
        final int userIndex=PrivacyConfig.getInstance().getUserIndex();
        final int lonIndex=PrivacyConfig.getInstance().getLonIndex();
        final int latIndex=PrivacyConfig.getInstance().getLatIndex();
        while (valueItor.hasNext()) {
            LongLine texts = valueItor.next();
            Text fileName = texts.getFileName();
            int lineNumber = Integer.parseInt(texts.getLineNumber().toString());
            String currentLine = texts.getLine().toString();

            if (currentLine != null) {
                String[] cols = currentLine.split(dataSeparator);
                //System.out.println("currentLine:\n"+currentLine);
                //System.out.println("dataSeperator:"+dataSeparator+"End");
                //System.out.println("tsIndex:"+tsIndex);
                //下面的判断已经在map中进行，这样读入的数据已经都是有效数据
                /*if ((!DataUtils.isValidLongitude(cols[10]))|| (!DataUtils.isValidLatitude(cols[11])))
					continue;*/
                Date date = null;
                try {
                    if (cols[tsIndex].indexOf(".") == -1)
                        date = format.parse(cols[tsIndex] + ".000");
                    else
                        date = format.parse(cols[tsIndex]);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                DataPoint dataPoint = new DataPoint(cols[userIndex], new Location(
                        Double.parseDouble(cols[lonIndex]),
                        Double.parseDouble(cols[latIndex])),
                        TimeUtils.Date2TimeStamp(date), fileName.toString(),
                        lineNumber);
                dataPoints.add(dataPoint);
            }
        }
        if (dataPoints.size() == 0) {
            return;
        }
        List<Trajectory> originTs = PrivacyUtils.getTrajectories(dataPoints);
        List<Trajectory> resultTrajectories = PrivacyUtils
                .protectPrivacy(originTs);

        Map<String, Scanner> scanners = new HashMap<>();
        String srcDir = context.getConfiguration().get("inputPath");
        String desDir = context.getConfiguration().get("outputPath");
        String keyHMAC = context.getConfiguration().get("key");
        if (PrivacyConfig.getInstance().getResultSort() != 1) {
            HDFSUtils.copyAllFileStructureWithoutContent(srcDir, desDir);
        }

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

        List<List<DataPoint>> dataPointss = new ArrayList<>();
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

        int threadNum = PrivacyConfig.getInstance().getThreadNum();
        if (threadNum > 1) {
            ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
            List<Thread> writeThreads = new ArrayList<>();
            for(int i=dataPointss.size()-1;i>=0;i--){
                List<DataPoint> dps=dataPointss.get(i);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = dps.get(0).getFileName();
                        Scanner scanner = null;
                        if (scanners.get(fileName) == null) {
                            Path path = new Path(fileName);
                            FileSystem fs = null;
                            try {
                                fs = path.getFileSystem(context.getConfiguration());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            FSDataInputStream in = null;
                            try {
                                in = fs.open(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                                String[] attributes = line.split(dataSeparator);
                                DataPoint dataPoint = dps.get(dpsIndex);
                                if (dataPoint.getLineNumber() == lineNumber) {
                                    dpsIndex++;
                                    attributes[0] = format.format(TimeUtils.TimeStamp2Date(dataPoint.getTime()));
                                    if (PrivacyConfig.getInstance().getResultSort() == 1) {
                                        attributes[1] = dataPoint.getUserId();
                                    } else {
                                        attributes[1] = StringProtectUtils.encryptHMAC(dataPoint.getUserId(), keyHMAC);
                                    }
                                    attributes[10] = String.valueOf(dataPoint.getLocation()
                                            .getLongitude());
                                    attributes[11] = String.valueOf(dataPoint.getLocation()
                                            .getLatitude());
                                    line = String.join("\t", attributes);
                                    line = String.valueOf(lineNumber) + "\t" + line;
                                    context.write(new Text(fileName), new Text(line));
                                } else {// 针对未读取的无效数据
                                    /**
                                     * 未读取的数据不写入，将会在后续的job中写入，这样做的目的是每个reduce只写自己负责的那部分，避免与其他reduce产生写冲突
                                     */
                                    if (PrivacyConfig.getInstance().getResultSort() != 1 && lineNumber == 0) {
                                        line = String.valueOf(lineNumber) + "\t" + line;
                                        context.write(new Text(fileName), new Text(line));
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            scanner.close();
                        }
                    }
                });
                writeThreads.add(thread);

                dataPointss.remove(i);
            }

            for (Thread thread : writeThreads) {
                threadPool.submit(thread);
            }

            //wait for all the threads to complete
            while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) ;
        } else {
            for(int i=dataPointss.size()-1;i>=0;i--){
                List<DataPoint> dps=dataPointss.get(i);

                String fileName = dps.get(0).getFileName();
                Scanner scanner = null;
                if (scanners.get(fileName) == null) {
                    Path path = new Path(fileName);
                    FileSystem fs = null;
                    try {
                        fs = path.getFileSystem(context.getConfiguration());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FSDataInputStream in = null;
                    try {
                        in = fs.open(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                        String[] attributes = line.split(dataSeparator);
                        DataPoint dataPoint = dps.get(dpsIndex);
                        if (dataPoint.getLineNumber() == lineNumber) {
                            dpsIndex++;
                            attributes[tsIndex] = format.format(TimeUtils.TimeStamp2Date(dataPoint.getTime()));
                            if (PrivacyConfig.getInstance().getResultSort() == 1) {
                                attributes[userIndex] = dataPoint.getUserId();
                            } else {
                                attributes[userIndex] = StringProtectUtils.encryptHMAC(dataPoint.getUserId(), keyHMAC);
                            }
                            attributes[lonIndex] = String.valueOf(dataPoint.getLocation()
                                    .getLongitude());
                            attributes[latIndex] = String.valueOf(dataPoint.getLocation()
                                    .getLatitude());
                            line = String.join("\t", attributes);
                            line = String.valueOf(lineNumber) + "\t" + line;
                            context.write(new Text(fileName), new Text(line));
                        } else {// 针对未读取的无效数据
                            /**
                             * 未读取的数据不写入，将会在后续的job中写入，这样做的目的是每个reduce只写自己负责的那部分，避免与其他reduce产生写冲突
                             */
                            if (PrivacyConfig.getInstance().getResultSort() != 1 && lineNumber == 0) {
                                line = String.valueOf(lineNumber) + "\t" + line;
                                context.write(new Text(fileName), new Text(line));
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    scanner.close();
                }

                dataPointss.remove(i);
            }
        }
    }

}
