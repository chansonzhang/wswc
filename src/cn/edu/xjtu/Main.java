package cn.edu.xjtu;

import cn.edu.xjtu.models.DataPoint;
import cn.edu.xjtu.models.Trajectory;
import cn.edu.xjtu.utils.FileUtils;
import cn.edu.xjtu.utils.LogUtils;
import cn.edu.xjtu.utils.PrivacyConfig;
import cn.edu.xjtu.utils.PrivacyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
    	if(args.length<3)
    		throw new IllegalArgumentException("Argument(s):<Input Dir> <Output Dir> <Configure File>");
//        String date = FileUtils.findDays(1)[0];
        //String date=PrivacyConfig.getInstance().getDate();
        //date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        PrivacyConfig.initialize(args[2]);
    	PrivacyConfig.getInstance().setSourceDir(args[0]);
        PrivacyConfig.getInstance().setResultDir(args[1]);
        LogUtils.getInstance().append("-------begin------");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now=new Date();
        LogUtils.getInstance().append("Time:\t"+format.format(now));
        LogUtils.getInstance().append("输入目录：\t"+args[0]);
        LogUtils.getInstance().append("输出目录：\t"+args[1]);
        LogUtils.getInstance().append("配置文件：\t"+args[2]);
    	System.out.println("开始读取原始轨迹……");
        long time0 = System.currentTimeMillis();
        List<DataPoint> dataPoints=FileUtils.getDataPoints();
        List<Trajectory> originTs = PrivacyUtils.getTrajectories(dataPoints);
        long time1 = System.currentTimeMillis();
        System.out.println("生成了" + originTs.size() + "条原始轨迹");
        LogUtils.getInstance().append("生成了" + originTs.size() + "条原始轨迹");
        System.out.println("读取数据耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("读取数据耗时：" + (time1 - time0) / 1000 / 60 + "min " + ((time1 - time0) / 1000) % 60 + "s");
        System.out.println("开始隐私保护……");
        List<Trajectory> resultTrajectories=PrivacyUtils.protectPrivacy(originTs);
        System.out.println("隐私保护后轨迹数："+resultTrajectories.size());
        long time2 = System.currentTimeMillis();
        System.out.println("隐私保护耗时：" + (time2 - time1) / 1000 / 60 + "min " + ((time2 - time1) / 1000) % 60 + "s");
        LogUtils.getInstance().append("隐私保护耗时：" + (time2 - time1) / 1000 / 60 + "min " + ((time2 - time1) / 1000) % 60 + "s");
        System.out.println("开始写结果……");
        FileUtils.writeResult(PrivacyConfig.getInstance().getResultDir(),resultTrajectories);
        long time3=System.currentTimeMillis();
        System.out.println("写结果耗时：" + (time3 - time2) / 1000 / 60 + "min " + ((time3 - time2) / 1000) % 60 + "s");
        LogUtils.getInstance().append("写结果耗时：" + (time3 - time2) / 1000 / 60 + "min " + ((time3 - time2) / 1000) % 60 + "s");
        System.out.println("结束，一共耗时：" + (time3 - time0) / 1000 / 60 + "min " + ((time3 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("结束，一共耗时：" + (time3 - time0) / 1000 / 60 + "min " + ((time3 - time0) / 1000) % 60 + "s");
        LogUtils.getInstance().append("-------end------");
    }
}
