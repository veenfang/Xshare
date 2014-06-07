<?php

include_once("./model/mDatabase.php");
$ip = $_SERVER["SERVER_ADDR"];
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
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <link rel="stylesheet" type="text/css" href="css/toshow.css" >
            <script>
                var isMark = false;
						</script>
						<script src="js/websocket.js"></script>
            <script src="js/ts.js"></script>
            <script src="js/fs.js"></script>
            <title>$lectureInfo[filename]</title>
            <script>
                var lectureInfo =  $lectureInfoStr;
                var websocket;
                var currentDisplayNum;
                var canvasPaintColor = "rgb(0,0,0)";
                var newColor = '#000000';
                var zoomFactor=1;
								var touchslide;
                
								var mark = function(){
										if(isMark){
												isMark = false;
										}else{
												isMark = true;
										}
								}

								var stopSocket = function(){
                    websocket.close();
								}

                var initWebsocket = function(){
                        websocket = new WebSocket("ws://" + lectureInfo['ip']  + ":8888");
												console.log(lectureInfo['ip']);
                        websocket.onopen = function(e){
                                alert("open");
                                websocket.send("host;"+lectureInfo['roomname']);
                        }
                        websocket.onclose = function(e){
                        }
                        websocket.onmessage = function(e){
                        }
                        websocket.onerror = function(e){
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
                    touchslide.slideTo(pageNum - 1, false);//no callback
                    updatePage(pageNum);
                }
                
                var slideCallBack=function(pageNum){
                    render(pageNum);
                    updatePage(pageNum);
                }
                
				//保留
                var pageForward = function(){
                        turnToPage(lectureInfo['currentpage']+1);
                }
                //保留
                var pageBackward = function(){
                         turnToPage(lectureInfo['currentpage']-1);
                }

                

                var changeColor = function(color){
                        newColor = color;
                        websocket.send('50,'+color);
                        //websocket.flush();
                }
                var updatePage=function(pageNum){
				currentDisplayNum = lectureInfo['currentpage'];
                if(lectureInfo['currentpage']==pageNum) return;
                    currentDisplayNum = lectureInfo['currentpage']=pageNum;
                    if(websocket.readyState == 1)//connected
                       websocket.send("33,"+lectureInfo['currentpage']);
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
				window.onload = function(){
        initWebsocket();
            touchslide = TouchSlide({
                slideCell: "#content",
                titCell: ".hd ul",
                mainCell: "#framelist",
                effect: "left",
                autoPlay: false, //自动播放
                autoPage: true,
                delayTime: 2000,
                interTime: 4000,
                slideCallBack:slideCallBack
            });
                
                currentDisplayNum = lectureInfo['currentpage'];
								turnToPage(lectureInfo['currentpage']);
				}
</script>
        </body>
    </html>
EOT;
?>
