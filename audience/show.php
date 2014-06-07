<?php
include_once("../model/mDatabase.php");

$ip = $_SERVER["SERVER_ADDR"];
$lectureInfo = array('ip'=>$ip);

$roomname = "veen";
$db = new mDatabase();
$roomInfo = $db->queryRoom($roomname);

$lectureInfo['roomname'] = $roomInfo['roomname'];
$lectureInfo['file'] = $roomInfo['file'];
$lectureInfo['currentpage'] = $roomInfo['currentpage'];
$lectureInfo['pagenum'] = $roomInfo['pagenum'];

$lectureInfo = json_encode($lectureInfo);
?>

<!DOCTYPE html>
<html>
    <head>
        <title>Islider</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<link rel="stylesheet" type="text/css" href="../css/index.css"/>
		<script src="jquery-1.10.2.min.js"></script>
		<script src="json2.js"></script>
		<script>
			var lectureInfo = <?= $lectureInfo ?>;
			var generatePageUrl = function(pageNum){
				var url = 'http://' + lectureInfo['ip'] + '/islider-mobile/documents/' 
						  + lectureInfo['roomname'] + '/' + lectureInfo['file'] + '/'
						  + lectureInfo['file'] + pageNum + '.html';
				return url;
			}
			var currentUrl;
			var width, height, oWidth, oHeight, wratio, hratio, chratio;
			var initialize = function(){
				var frame = $("#frame");
				var canvas = $("#canvas");
				currentUrl = generatePageUrl(lectureInso['currentpage']);
				frame.attr("src", currentUrl);
				width = window.screen.width;
				height = window.screen.height;
				oWidth = parseFloat(canvas.css("width"));
				oHeight = parseFloat(canvas.css("height"));
				frame.css("width", width);
				frame.css("height", height);
				canvas.css("height", frame.css("height"));
				canvas.css("width", frame.css("width"));
				wFrame = document.getElementById('frame').contentWindow;
				document.getElementById('frame').onload = function(){
					wratio = width/wFrame.$('.w1:first').width();
					hratio = height/wFrame.$('.w1:first').height();
					wFrame.$('.w1').css("margin", "0");
					wFrame.$('.w1').css("padding", "0");
					wFrame.$('.w1').css("-webkit-transform","scale("+wratio+","+hratio+")");
					wFrame.$('.w1').css("-webkit-transform-origin","0% 0%");
					canvas.css({
						"position": "absolute",
						"left": frame.position().left,
						"top": frame.position().top,
					});
					frame.css('zIndex', 0);
					canvas.css('zIndex', 1000);
				}
			}
			var draw = function(data){
				var ctx = document.getElementById("canvas").getContext("2d");
				ctx.strokeStyle = "rgb(0, 0, 0)";
				switch(data[0]){
					case "1":
						ctx.beginPath();
						ctx.moveTo(oWidth*parseFloat(data[1]), oHeight*parseFloat(data[2]));
						break;
					case "2":
					//case "3":
						ctx.lineTo(oWidth*parseFloat(data[1]), oHeight*parseFloat(data[2]));
						ctx.stroke();
						break;
				}
			}
			var turnToPage = function(pageNum){
				currentUrl = generatePageUrl(pageNum);
				$('#frame').attr('src', currentUrl);
			}
			$(document).ready(function(){
				initialize();
			});

            //----------------------socket------------------------
			var websocket;	
			var initWebsocket = function(){
				websocket = new WebSocket("ws://" + lectureInfo['ip'] + ":8888");
				//websocket = new WebSocket("ws://10.4.2.147:8888");
				websocket.onopen = function(e){
					alert("open");
					websocket.send("guest;veen");
				}
				websocket.onclose = function(e){
					alert("closed");
				}
				websocket.onmessage = function(e){
					var data = e.data.split(",");   //here, we can also get the termination information
					switch(data[0]){
						case "1":
						case "2":
						case "3":
							draw(data);
							break;
						case "33":
							tureToPage(data[1]);
						case "80":
							//pageTurning(data[1]);
							break;
						case "99":
							websocket.close();
							break;
					}
				}
				websocket.onerror = function(e){
					alert("error");
					console.log("error occur!");
				}
			}
			
			/*var timeIntervalId;
			
			var stopInterval = function(){
				window.clearInterval(timeIntervalId);
			}*/

			$(document).ready(function(){
				initWebsocket();
			});
			
			/*if(websocket.readyState != 1){
				timeIntervalId = setInterval(function(){
					if(websocket.readyState != 1){
						initWebsocket();
					}
					else{
						websocket.send("guest;fw");
						stopInterval();
					}
				}, 100);
			}
			else{
				websocket.send("guest;fw");
			}*/
			//----------------------socket------------------------
 
		</script>
    </head>
	<body>
		<iframe id="frame" src="" frameborder=0></iframe>
		<canvas id="canvas"></canvas>
	</body>
</html>
