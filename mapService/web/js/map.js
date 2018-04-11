/**
 * Created by zhangchen on 2015/10/8.
 */
function loadMap()
{
    mp = new BMap.Map('map');
}

function adaptMap() {
    var width = $("#mapPanel").width();
    var height = $("#mapPanel").height();
    document.getElementById("map").style.width = width + "px";
    document.getElementById("map").style.height = height + "px";
}