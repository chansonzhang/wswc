/**
 * Created by zhangchen on 2015/10/8.
 */
function showSPClusters(data)
{
    for(var i=0;i<data.length;i++)
    {
        var pStart = data[i].southWest;
        var pEnd = data[i].northEast;
        var rectangle = new BMap.Polygon([
            new BMap.Point(pStart.longitude,pStart.latitude),
            new BMap.Point(pStart.longitude,pEnd.latitude),
            new BMap.Point(pEnd.longitude,pEnd.latitude),
            new BMap.Point(pEnd.longitude,pStart.latitude)
        ], {strokeColor:"blue", strokeWeight:3, strokeOpacity:0,FillOpacity:1});  //创建矩形

        mp.addOverlay(rectangle);         //增加矩形
    }


}
function showGrid(data)
{
    for(var i=0;i<data.length;i++)
    {
        var pStart = data[i].southWest;
        var pEnd = data[i].northEast;
        var rectangle = new BMap.Polygon([
            new BMap.Point(pStart.longitude,pStart.latitude),
            new BMap.Point(pStart.longitude,pEnd.latitude),
            new BMap.Point(pEnd.longitude,pEnd.latitude),
            new BMap.Point(pEnd.longitude,pStart.latitude)
        ], {strokeColor:"yellow", strokeWeight:8, strokeOpacity:0});  //创建矩形

        mp.addOverlay(rectangle);         //增加矩形
    }


}