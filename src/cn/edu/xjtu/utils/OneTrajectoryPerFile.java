package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.models.Trajectory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchen on 2015/12/18.
 */
public class OneTrajectoryPerFile extends ResultWriteStrategy {
    @Override
    public void write(String directory, List<Trajectory> trajectories) {
        String date = trajectories.get(0).getDataPoints().get(0).getTime().toString().substring(0, 10);
        File resultDirectory = new File(directory, date);
        if (!resultDirectory.exists())
            resultDirectory.mkdirs();
        List<Thread> threads = new ArrayList<>();
        for (Trajectory trajectory : trajectories) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = trajectory.getDataPoints().get(0).getUserId();
                    File resultFile = new File(resultDirectory, userId);
                    if (!resultFile.exists()) {
                        try {
                            resultFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(resultFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    List<DataPoint> dataPoints = trajectory.getDataPoints();
                    for (DataPoint dataPoint : dataPoints) {
                        String uid = dataPoint.getUserId();
                        Location location = dataPoint.getLocation();
                        String time = dataPoint.getTime().toString();
                        try {
                            writer.write(uid + "," + location.getLatitude() + "," + location.getLongitude() + "," + time + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
