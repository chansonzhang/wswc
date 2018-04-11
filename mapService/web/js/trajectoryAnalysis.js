/**
 * Created by zhangchen on 2015/10/16.
 */

function showTrajectories(data, color, lineKind) {
    if (!color) {
        for (var i = 0; i < data.length; i++) {
            var points = new Array();
            for (var j = 0; j < data[i].dataPoints.length; j++) {
                var point = new BMap.Point(data[i].dataPoints[j].location.longitude, data[i].dataPoints[j].location.latitude)
                points.push(point);
            }
            var polyline = new BMap.Polyline(points, {strokeColor: "blue", strokeWeight: 3, strokeOpacity: 0}); //创建弧线对象
            mp.addOverlay(polyline); //添加到地图中
        }
    }
    else {
        var colorMap = new Map();
        for (var i = 0; i < data.length; i++) {
            var color = "";
            var usrId = data[i].dataPoints[0].userId;
            if (colorMap.has(usrId)) {
                color = getcolor(colorMap.get(usrId));
            }
            else {
                var lenth = colorMap.size;
                colorMap.set(usrId, lenth % 7);
                color = getcolor(colorMap.get(usrId));
            }

            var points = new Array();
            for (var j = 0; j < data[i].dataPoints.length; j++) {
                var point = new BMap.Point(data[i].dataPoints[j].location.longitude, data[i].dataPoints[j].location.latitude)
                points.push(point);
                if(points.length%50==0&&j < data[i].dataPoints.length-5)
                {
                    var currentPoint=point;
                    var nextPoint=new BMap.Point(data[i].dataPoints[j+5].location.longitude, data[i].dataPoints[j+5].location.latitude)
                    var rotation=calRot(currentPoint,nextPoint);
                    /**
                     * arcsin的取值为-90+90,如果在y轴右侧，从上方顺时针旋转90-rotation，否则从下方顺时针旋转90+rotation
                     */
                    var direction=(nextPoint.lng>currentPoint.lng)?"up":"down";
                    //alert("nextPoint.longitude"+nextPoint.longitude+"\ncurrentPoint.longitude"+currentPoint.longitude);
                    if(direction=="up")
                    {
                        var vectorFOArrow = new BMap.Marker(point, {
                            //  初始化方向向上的开放式箭头
                            icon: new BMap.Symbol(BMap_Symbol_SHAPE_FORWARD_OPEN_ARROW, {
                                scale: 1,
                                strokeWeight: 1,
                                rotation: 90-rotation,
                                fillColor: color,
                                fillOpacity: 0.8
                            })
                        });
                        mp.addOverlay(vectorFOArrow);
                        vectorFOArrow.setTitle(usrId);
                    }
                    else
                    {
                        var vectorBOArrow = new BMap.Marker(point, {
                            // 初始化方向向下的开放式箭头
                            icon: new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
                                scale: 1,
                                strokeWeight: 1,
                                rotation: 90+rotation,
                                fillColor: color,
                                fillOpacity: 0.8
                            })
                        });
                        mp.addOverlay(vectorBOArrow);
                        vectorBOArrow.setTitle(usrId);
                    }
                }
            }
            var polyline = new BMap.Polyline(points, {strokeColor: color, strokeWeight: 3, strokeOpacity: 0}); //创建弧线对象
            mp.addOverlay(polyline); //添加到地图中
        }
    }
}

function getcolor(key) {
    switch (key) {
        case 0:
            return "black";
        case 1:
            return "red";
        case 2:
            return "maroon";
        case 3:
            return "purple";
        case 4:
            return "green";
        case 5:
            return "yellow";
        case 6:
            return "blue";
    }
}

/**
 * 计算currentPoint与nextPoint连线相对于精度增长方向的偏移角度，区分正负。
 * @param currentPoint
 * @param nextPoint
 * @returns {number}
 */
function calRot(currentPoint,nextPoint)
{
    var rightCornerPoint=new BMap.Point(nextPoint.lng,currentPoint.lat);
    var verticalDistance=mp.getDistance(nextPoint,rightCornerPoint);
    if(nextPoint.lat<currentPoint.lat) verticalDistance=-verticalDistance;
    var bevelDistance=mp.getDistance(currentPoint,nextPoint);
    var rotFromHorizon=(Math.asin(verticalDistance/bevelDistance)/Math.PI)*180;
    return rotFromHorizon;
}