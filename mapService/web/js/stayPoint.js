/**
 * Created by zhangchen on 2015/10/5.
 */
/**
 *
 * @param data stayPoint list to show
 */



function showStayPoint(data)
{
    if(data.length==0)
    {
        var point="西安";
    }
    else var point=new BMap.Point(data[0].location.longitude,data[0].location.latitude);
    mp.centerAndZoom(point, 11);
    mp.addControl(new BMap.NavigationControl());
    mp.addControl(new BMap.ScaleControl());
    mp.addControl(new BMap.OverviewMapControl());
    mp.enableScrollWheelZoom();
    mp.enableAutoResize();

    for (var i = 0; i < data.length; i++) {
        var point = new BMap.Point(data[i].location.longitude, data[i].location.latitude);
        var marker = new BMap.Marker(point);
        mp.addOverlay(marker);
        marker.setTitle("用户名:"+data[i].userId+" 坐标:("+data[i].location.latitude+","+data[i].location.longitude+")");
    }
}

function showTrajectory(data)
{
    for(var i=0;i<data.length;i++)
    {
        var points=new Array();
        for(var j=0;j<data[i].dataPoints.length;j++)
        {
            var point=new BMap.Point(data[i].dataPoints[j].location.longitude,data[i].dataPoints[j].location.latitude)
            points.push(point);
        }
        var curve = new BMapLib.CurveLine(points, {strokeColor:"blue", strokeWeight:3, strokeOpacity:0.5}); //创建弧线对象
        mp.addOverlay(curve); //添加到地图中
    }
}