<?php

include_once("../model/mDatabase.php");
//$ip = $_SERVER["REMOTE_ADDR"];
$ip = '192.168.0.1';
$lectureInfo = array('ip' => $ip);

$roomname = $_REQUEST['roomname'];
$db = new mDatabase();
$roomInfo = $db->queryRoom($roomname);

/*
$roomInfo = array(
    "roomname" => "fw",
    "file" => "document.txt",
    "currentpage" => 1,
    "pagenum" => 10
);
*/

$lectureInfo['roomname'] = $roomInfo['roomname'];
$lectureInfo['filename'] = $roomInfo['file'];
$lectureInfo['currentpage'] = $roomInfo['currentpage'];
$lectureInfo['pageNum'] = $roomInfo['pagenum'];
$lectureInfo['lecturer'] = $roomInfo['roomname'];
$lectureInfoStr = json_encode($lectureInfo);

print <<<EOT
    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="utf-8">
            <link rel="stylesheet" href="css/toshow.css" >
            <script src="js/ts.js"></script>
            <script src="js/fs.js"></script>
            <title>$lectureInfo[filename]</title>
            <script>
                var lectureInfo =  $lectureInfoStr;
                var websocket;
                var currentDisplayNum;
                var canvasPaintColor = "rgb(0,0,0)";
								var width, height;


                var initWebsocket = function() {
                    websocket = new WebSocket("ws://" + lectureInfo['ip'] + ":8888");
                    websocket.onopen = function(e) {
												alert("open!");
                        websocket.send("guest;" + lectureInfo['roomname']);
                    }
                    websocket.onclose = function(e) {
                        alert("websocket close");
                    }
                    websocket.onmessage = function(e) {
                        var data = e.data.split(",");   //here, we can also get the termination information
                        switch (data[0]) {
                            case "1":
                            case "2":
                            case "3":
                                draw(data);
                                break;
                            case "33":
                                turnToPage(data[1]);
                                break;
                            case "50":
                                canvasPaintColor = data[1];
                                break;
                            case "80":
                                break;
                            case "99":
                                websocket.close();
                                break;
                        }

                    }
                    websocket.onerror = function(e) {
                        alert("websocket error");
                    }
                }

                var draw = function(data) {
                    var ctx = document.getElementById("canvas" + currentDisplayNum).getContext("2d");
                    ctx.strokeStyle = canvasPaintColor;
                    switch (data[0]) {
                        case "1":
                            ctx.beginPath();
                            ctx.moveTo(width * parseFloat(data[1]), height * parseFloat(data[2]));
                            break;
                        case "2":
                            ctx.lineTo(width * parseFloat(data[1]), height * parseFloat(data[2]));
                            ctx.stroke();
                            break;
                    }
                }

                var render = function(pageNum, isok) {
                    var frame = document.getElementById('iframe' + pageNum);
                    if(frame.getAttribute("src")!= ""){
                    	return;
                    }

                    var url = generateUrl(pageNum);
                    frame.setAttribute('src', url);
                    control.rescale(frame);
                }

                var generateUrl = function(pageNum) {
                    var url = 'http://' + lectureInfo['ip'] + '/islider-mobile/documents/'
                            + lectureInfo['roomname'] + '/' + lectureInfo['filename'] + '/'
                            + lectureInfo['filename'] + pageNum + '.html';
                    
                    //var url = "test"+pageNum+".html";
                    return url;
                }

                var turnToPage = function(pageNum) {
                    var frame = document.getElementById('iframe' + pageNum);
                    render(pageNum,true);
                    touchslide.slideTo(pageNum - 1);
                }
                
            </script>

        </head>
        <body>
            <div id="content">
            <div class="bd">
                <ul id="framelist">
EOT;
for ($i = 1; $i <= $lectureInfo['pageNum']; $i++) {
    print <<<EOT


                    <li><iframe id="iframe$i" src=""></iframe>
                        <canvas id="canvas$i">canvas$i</canvas>
                    </li>

EOT;
}
print <<<EOT

                </ul>
            </div>
            <div class="hd">
                <ul>

                </ul>
            </div>

        </div>
        <script>
         var touchslide = TouchSlide({
                slideCell: "#content",
                titCell: ".hd ul",
                mainCell: "#framelist",
                effect: "left",
                autoPlay: false, //自动播放
                autoPage: true,
                delayTime: 2000,
                interTime: 4000,
                slideCallBack:render
						});
						window.onload = function(){
                initWebsocket();
								currentDisplayNum = 1;
								//-------------------------------------------------------modify
								width = document.body.scrollWidth;
								height = document.body.scrolltHeight;
								turnToPage(lectureInfo['currentpage']);
						}
</script>
        </body>
    </html>
EOT;
?>
