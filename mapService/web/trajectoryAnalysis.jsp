<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
    <title>轨迹分析</title>
    <script type="text/javascript" src="<%=basePath%>js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>js/map.js"></script>
    <script type="text/javascript" src="<%=basePath%>js/stayPoint.js"></script>
    <script type="text/javascript" src="<%=basePath%>js/trajectoryAnalysis.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/library/CurveLine/1.5/src/CurveLine.min.js"></script>
</head>
<body>
<script type="text/javascript">
    function initialize() {
        loadMap();
        $.getJSON("<%=basePath%>mapServlet?cmd=0",function (data) {
            showStayPoint(data);
            $.getJSON("<%=basePath%>mapServlet?cmd=3",function(data){
                showTrajectories(data,true,1);
            });
        });

    }
    function loadScript() {
        var script = document.createElement("script");
        script.src = "http://api.map.baidu.com/api?v=2.0&ak=FXmQCEnVoHuSR5nYfX40C7Sp&callback=initialize";//此为v1.5版本的引用方式
        document.body.appendChild(script);
    }
    window.onload = loadScript;
</script>
<br>
<div class="panel panel-success">
    <div class="panel-heading">
        地图显示
    </div>
    <div class="panel-body" id="mapPanel">
        <div id="map" style="width:1000px;height:900px"></div>
    </div>
</div>
<script>
    adaptMap();
    window.addEventListener("resize", function () {
        adaptMap();
    }, true);
</script>
</body>
</html>