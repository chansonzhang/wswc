package cn.edu.xjtu.utils;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;

import java.util.*;

/**
 * Created by zhangchen on 2015/12/16.
 */
public class TrajectoryGenerator {
    private static final String DATA_FILE = "E:\\Document\\项目\\15年华为\\demo\\hwPrivacy\\data\\00020081023025304.plt"; //鏁版嵁鏂囦欢
    private static final String DATA_PATH = "E:\\Document\\项目\\15年华为\\数据\\Geolife Trajectories 1.2\\Geolife Trajectories 1.2\\Data";
    private static final int USR_COUNT = 178;
    /*public ArrayList<Trajectory> getTrajectories() {
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        String[] dates = FileUtils.findDays(1);
        Scanner scanner = null;
        //结果中用户数的上限，设置为0则获取所有拥有数据的用户的数据
        int resultUsrCountLimit = 0;

        int resultUsrCount = 0;
        for (int i = 0; i < USR_COUNT; i++) {
            String userId = String.format("%03d", i);
            File userFolder = new File(DATA_PATH, userId + "/Trajectory");
            File[] files = userFolder.listFiles();
            boolean thisUsrHasTrajectory = false;
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                String filename = file.getName();
                for (String date : dates) {
                    if (filename.substring(0, filename.length() - 10).equals(date)) {
                        try {
                            scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        for (int k = 0; k < 6; k++)//无效数据
                        {
                            scanner.nextLine();
                        }
                        Trajectory trajectory = new Trajectory();
                        do {
                            String currentLine = scanner.nextLine();
                            if (currentLine != null) {
                                String[] values = currentLine.split(",");
                                DataPoint dataPoint = new DataPoint(userId
                                        , new Location(Double.parseDouble(values[1]), Double.parseDouble(values[0]))
                                        , Timestamp.valueOf(values[5] + " " + values[6]));
                                trajectory.addDataPoint(dataPoint);
                            }
                        }
                        while (scanner.hasNextLine());
                        trajectories.add(trajectory);
                        thisUsrHasTrajectory = true;
                    }
                }
            }
            if (thisUsrHasTrajectory) resultUsrCount++;
            if (resultUsrCountLimit != 0 && resultUsrCount == resultUsrCountLimit)
                break;
        }
        return trajectories;
    }
*/
    /**
     * 采用的数据源不同
     *
     * @return
     */
    /*public ArrayList<Trajectory> getTrajectories1() {
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        Scanner scanner = null;
        File userFolder = new File("E:\\Document\\项目\\15年华为\\demo\\数据\\2008-11-16");
        File[] files = userFolder.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            String filename = file.getName();
            try {
                scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int k = 0; k < 6; k++)//无效数据
            {
                scanner.nextLine();
            }
            Trajectory trajectory = new Trajectory();
            do {
                String currentLine = scanner.nextLine();
                if (currentLine != null) {
                    String[] values = currentLine.split("\t");
                    DataPoint dataPoint = new DataPoint(filename.substring(0, 3)
                            , new Location(Double.parseDouble(values[1]), Double.parseDouble(values[0]))
                            , Timestamp.valueOf(values[5].replace("/", "-") + " " + values[6]));
                    trajectory.addDataPoint(dataPoint);
                }
            }
            while (scanner.hasNextLine());
            trajectories.add(trajectory);
        }
        return trajectories;
    }
*/
    /*public ArrayList<Trajectory> getTrajectories_dhj() {
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        Scanner scanner = null;
        File userFolder = new File("E:\\Document\\项目\\15年华为\\demo\\数据\\dhj");
        File[] files = userFolder.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            String filename = file.getName();
            try {
                scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int k = 0; k < 6; k++)//无效数据
            {
                scanner.nextLine();
            }
            Trajectory trajectory = new Trajectory();
            do {
                String currentLine = scanner.nextLine();
                if (currentLine != null) {
                    String[] values = currentLine.split(",");
                    DataPoint dataPoint = new DataPoint(values[0]
                            , new Location(Double.parseDouble(values[2]), Double.parseDouble(values[1]))
                            , Timestamp.valueOf(values[6] + " " + values[7]));
                    trajectory.addDataPoint(dataPoint);
                }
            }
            while (scanner.hasNextLine());
            trajectories.add(trajectory);
        }
        return trajectories;
    }
*/
    /**
     * 找出指定日期的轨迹
     *
     * @param date
     * @return
     */
    /*public ArrayList<Trajectory> getTrajectories(String date) {
        String[] strs = date.split("-");
        date = strs[0] + strs[1] + strs[2];
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        Scanner scanner = null;
        //结果中用户数的上限，设置为0则获取所有拥有数据的用户的数据
        int resultUsrCountLimit = 0;

        int resultUsrCount = 0;
        for (int i = 0; i < USR_COUNT; i++) {
            String userId = String.format("%03d", i);
            File userFolder = new File(DATA_PATH, userId + "/Trajectory");
            File[] files = userFolder.listFiles();
            boolean thisUsrHasTrajectory = false;
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                String filename = file.getName();
                if (filename.substring(0, filename.length() - 10).equals(date)) {
                    try {
                        scanner = new Scanner(new BufferedInputStream(new FileInputStream(userFolder + "/" + filename)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    for (int k = 0; k < 6; k++)//无效数据
                    {
                        scanner.nextLine();
                    }
                    Trajectory trajectory = new Trajectory();
                    do {
                        String currentLine = scanner.nextLine();
                        if (currentLine != null) {
                            String[] values = currentLine.split(",");
                            DataPoint dataPoint = new DataPoint(userId
                                    , new Location(Double.parseDouble(values[1]), Double.parseDouble(values[0]))
                                    , Timestamp.valueOf(values[5] + " " + values[6]));
                            trajectory.addDataPoint(dataPoint);
                        }
                    }
                    while (scanner.hasNextLine());
                    trajectories.add(trajectory);
                    thisUsrHasTrajectory = true;
                }
            }
            if (thisUsrHasTrajectory) resultUsrCount++;
            if (resultUsrCountLimit != 0 && resultUsrCount == resultUsrCountLimit)
                break;
        }
        return trajectories;
    }
*/
    /**
     * 使用E:\Document\项目\15年华为\demo\数据\zc下的文件，每一行第一个属性为用户id
     *
     * @param date
     * @return
     */
    /*public ArrayList<Trajectory> getTrajectories1(String date) {
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        final int[] resultPointNum = {0};
        int resultUsrCount = 0;
        File folder = new File("E:\\Document\\项目\\15年华为\\demo\\数据\\zc");
        List<Thread> threads = new ArrayList<>();
        for (int tn = 0; tn < 1; tn++) {
            final int finalTn = tn;
            final String finalDate = date;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        String userId = String.format("%03d", finalTn * 100 + i);
                        File[] files = folder.listFiles();
                        for (int j = 0; j < files.length; j++) {
                            File file = files[j];
                            String filename = file.getName();
                            Scanner scanner = null;
                            try {
                                scanner = new Scanner(new BufferedInputStream(new FileInputStream(folder + "/" + filename)));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Trajectory trajectory = new Trajectory();
                            do {
                                String currentLine = scanner.nextLine();
                                if (currentLine != null) {
                                    String[] values = currentLine.split(",");
                                    if (values[0].equals(userId) && values[6].equals(finalDate)) {
                                        DataPoint dataPoint = new DataPoint(values[0]
                                                , new Location(Double.parseDouble(values[2]), Double.parseDouble(values[1]))
                                                , Timestamp.valueOf(values[6] + " " + values[7]));
                                        trajectory.addDataPoint(dataPoint);
                                    }
                                }
                            }
                            while (scanner.hasNextLine());
                            if (trajectory.getDataPoints().size() != 0) {
                                //20090403011657.plt文件存在问题，未完全按照时间戳排序，这样的问题也许存在于其他文件内，用排序解决
                                List<DataPoint> dataPoints = trajectory.getDataPoints();
                                dataPoints.sort(new Comparator<DataPoint>() {
                                    @Override
                                    public int compare(DataPoint o1, DataPoint o2) {
                                        if (o1.getTime().before(o2.getTime())) return -1;
                                        else if (o1.getTime().after(o2.getTime())) return 1;
                                        else return 0;
                                    }
                                });
                                trajectory.setDataPoints(dataPoints);
                                trajectories.add(trajectory);
                                resultPointNum[0] += trajectory.getDataPoints().size();
//                                System.out.println("线程" + finalTn + ":当前读取了" + resultPointNum[0] + "个原始数据点");
                            }
                        }
                    }
                }
            });
            thread.start();
            threads.add(thread);
            System.out.println("读取线程" + finalTn + "：开始运行");
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print("一共读取了" + resultPointNum[0] + "个原始数据点,");
        Comparator<Trajectory> comparator = new Comparator<Trajectory>() {
            @Override
            public int compare(Trajectory o1, Trajectory o2) {
                return o1.getDataPoints().get(0).getUserId().compareTo(o2.getDataPoints().get(0).getUserId());
            }
        };
        trajectories.sort(comparator);
        return trajectories;
    }
*/
    /**
     * 从dataPoints中提取轨迹
     *
     * @param dataPoints
     * @return
     */
    public ArrayList<Trajectory> getTrajectories2(List<DataPoint> dataPoints) {
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        Map<String,Trajectory> trajectoryMap=new HashMap<>();

        for (int i = 0; i < dataPoints.size(); i++) {
        	DataPoint dataPoint=dataPoints.get(i);
        	String userId=dataPoint.getUserId();
        	if(trajectoryMap.containsKey(userId)){
        		trajectoryMap.get(userId).addDataPoint(dataPoint);
        	}else{
        		Trajectory trajectory = new Trajectory();
        		trajectory.addDataPoint(dataPoint);
        		trajectoryMap.put(userId, trajectory);
        	}

        }
        
        for(Trajectory trajectory:trajectoryMap.values()){
        	trajectory.getDataPoints().sort(new Comparator<DataPoint>() {

				@Override
				public int compare(DataPoint o1, DataPoint o2) {
					if(o1.getTime().before(o2.getTime())) return -1;
					else if(o1.getTime().after(o2.getTime())) return 1;
					else return 0;
				}
			});
        	trajectories.add(trajectory);
        }
        trajectories.sort(new Comparator<Trajectory>() {

			@Override
			public int compare(Trajectory o1, Trajectory o2) {
				return o1.getDataPoints().get(0).getUserId().compareTo(o2.getDataPoints().get(0).getUserId());
			}
		});
        return trajectories;
    }
}
