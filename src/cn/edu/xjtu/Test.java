package cn.edu.xjtu;

import cn.edu.xjtu.models.Location;
import cn.edu.xjtu.utils.TimeUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangchen on 2015/11/6.
 */
class MY
{
    public void setList(List<String> list) {
        this.list = list;
    }

    List<String> list;
};
public class Test {

    public static void main(String[] args) throws ParseException
    {
        String zhang="zhang  chen";
        String[] values=zhang.split(" ");
        String tab="\\t".toString();
        System.out.println(org.apache.commons.lang.StringEscapeUtils.unescapeJava(tab));
        double a=1.000000001;
    	List<String> strs1=new ArrayList<String>();
    	String str="zhangchen";
    	strs1.add(str);
    	List<String> strs2=new ArrayList<String>();
    	strs2.add(strs1.get(0));
    	strs1.remove(str);
    	System.out.println("Strs2:"+strs2.get(0));
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date=format.parse("1992-08-21 09:24:06.34");
        System.out.println("Origin date:"+format.format(date));
        
        Timestamp ts=TimeUtils.Date2TimeStamp(date);
        System.out.println("Timestamp:"+ts.toString());
        
        Date date1=TimeUtils.TimeStamp2Date(ts);
        System.out.println("date after process:"+format.format(date1));
        
        
        Timestamp ts1=Timestamp.valueOf("1992-08-21 09:24:06.3");
        System.out.println(ts1.getNanos());
        System.out.println(format.format(TimeUtils.TimeStamp2Date(ts1)));
       List<String> ss=new ArrayList<>();
        ss.add("zhangchen");
        String s=ss.get(0);
        ss.add("redw");
        s=ss.get(1);
        for(String s1 :ss)
        {
            System.out.println(s);
        }
        List<MY> list1=new ArrayList<>();

        MY my=new MY();
        List<String> list11=new ArrayList<>();
        list11.add("zhangchne");
        my.setList(list11);

        list1.add(my);

//        List<MY> list2=new ArrayList<>(Arrays.asList(new MY[list1.size()]));
//        Collections.copy(list2,list1);
//        List<MY> list2=new ArrayList<>();
//        list2.addAll(list1);
        List<MY> list2=new ArrayList<>();


        double changeRate = 0.0;
        double changePerUser = 0.0;

        changeRate = 52.0 / 400;
        System.out.println("用户总数：" + 400 + "\t换名用户数：" + 52 + "\t换名率：" +changeRate);
        List<String> sts=new ArrayList<>();
        sts.add("zhangchen");
        sts.add("redw");
        System.out.println(sts.size());
        List<String> sts1=Arrays.asList(new String[sts.size()]);
        System.out.println(sts1.size());

        Collections.copy(sts1,sts);
        System.out.println(sts1.size());

        sts1.add("ygz");
        System.out.println(sts.size());

        System.out.println(sts1.size());

        
    }

}
class MyClass
{
    Timestamp time;
    List<String> strs=new ArrayList<>();
    List<String> strs1;
    Location location;
    String str;
    public MyClass(Timestamp t,List<String> strs,List<String> strs1,Location loc,String str)
    {
        this.time=t;
        this.strs=strs;
        this.strs1=new ArrayList<>(strs1);
        this.location=loc;
        this.str=str;
    }
    public void setStr(List<String> strs)
    {
        this.strs=strs;
    }
    public  void show()
    {
        System.out.println("time:"+this.time);
        System.out.println("strs:");
        for(String s:strs)
        {
            System.out.print(s+"\t");
        }
        System.out.println("\nsts1:");
        for(String s1:strs1)
        {
            System.out.print(s1+"\t");
        }
        System.out.println();
        System.out.println("location:" + this.location.getLongitude() + "," + this.location.getLatitude());
        System.out.println("str:"+this.str);
    }
}
