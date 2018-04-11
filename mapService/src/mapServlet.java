import cn.edu.xjtu.utils.PrivacyUtils;
import cn.edu.xjtu.algorithm.kanonymity.Kanony;
import cn.edu.xjtu.algorithm.kanonymity.KanonyUtils;
import cn.edu.xjtu.models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchen on 2015/9/28.
 */
@WebServlet(name = "mapServlet")
public class mapServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int cmd=Integer.parseInt(request.getParameter("cmd"));
        ObjectToJson o2j=new ObjectToJson();
        PrintWriter out = response.getWriter();
        String json;
        switch (cmd)
        {
            //获取所有stayPoint
            case 0:
                List<StayPoint> stayPoints= PrivacyUtils.getStayPoints();
                response.setContentType("text/plain");
                json= o2j.objectToJson(stayPoints).toString();//������json����
                System.out.println(json);
                out.write(json);
                break;
            //获取指定用户的轨迹
            case 1:
                String[] usrIds= (String[]) o2j.jsonToModel(request.getParameter("usrIds"), String[].class);
                List<Trajectory> trajectories=PrivacyUtils.getTrajectories(usrIds);
                //缩减数据规模
//                System.out.println(trajectories.get(0).getDataPoints().size());
                for(Trajectory t:trajectories)
                {
                    List<DataPoint> dataPoints=t.getDataPoints();
                    List<DataPoint> dpToRemoves=new ArrayList<>();
                    for(int i=0;i<dataPoints.size();i++)
                    {
                        if (i%100!=0)
                        {
                            dpToRemoves.add(dataPoints.get(i));
                        }
                    }
                    for(int j=0;j<dpToRemoves.size();j++)
                    {
                        t.removeDataPoint(dpToRemoves.get(j));
                    }
                }
//                System.out.println(trajectories.get(0).getDataPoints().size());
                response.setContentType("text/plain");
                json=o2j.objectToJson(trajectories).toString();
                out.write(json);
                break;
            //获取聚类结果
            case 2:
                ArrayList<SPCluster> spcs=PrivacyUtils.getSpcs();
                response.setContentType("text/plain");
                json=o2j.objectToJson(spcs).toString();
                out.write(json);
                break;
            //获取所有轨迹
            case 3:
                List<Trajectory> trajectories1=PrivacyUtils.getTrajectories();
                response.setContentType("text/plain");
                json=o2j.objectToJson(trajectories1).toString();
                out.write(json);
                break;
            //获取格子结果
            case 4:
                long startTime = System.currentTimeMillis();   //获取当前时间
                ArrayList<DataPoint> dp = KanonyUtils.setDataPoints();
                ArrayList<Grid> kanonies= Kanony.getkGrid(dp, 3, 116.28, 39.96, 0.18 / 20, 0.12 / 20, 19, 19);
                long endTime = System.currentTimeMillis();
                System.out.println("run time:"+(endTime-startTime)+"ms");
                double num = 0;
                for (int i = 0;i<kanonies.size();i++){
                    num = num + kanonies.get(i).getDataPoints().size();
                }
                System.out.println("K-grid:" + kanonies.size() + ", k-point:" + num);
                response.setContentType("text/plain");
                json=o2j.objectToJson(kanonies).toString();
                out.write(json);
                break;
            //格子初始化
            case 5:
                ArrayList<Grid> grids=Kanony.start(116.28,39.96,0.18/20,0.12/20,19,19);
                response.setContentType("text/plain");
                json=o2j.objectToJson(grids).toString();
                out.write(json);
                break;
            case 6:
                List<DataPoint> dataPoints= KanonyUtils.setDataPoints();
                response.setContentType("text/plain");
                json= o2j.objectToJson(dataPoints).toString();//������json����
                System.out.println(json);
                out.write(json);
                break;
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
