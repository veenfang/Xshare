//----------------------socket------------------------
var websocket;
var initWebsocket = function(){
	websocket = new WebSocket("ws://192.168.4.103:8888");
	websocket.onopen = function(e){
		alert("open");
		websocket.send("host;fw");
	}
	websocket.onclose = function(e){
		alert("close");
	}
	websocket.onmessage = function(e){
		alert(e.data);
	}
	websocket.onerror = function(e){
		alert("error");
	}
}
			
//initWebsocket();

var exit = function(){
	websocket.send("99,-1,-1");  //99 is the signal to exit
	websocket.close();
}

var urls;
var urlsSize;
var urlScan = -1;


var loadUrls = function(array){
	urls = array.split(",");   //to be modified
	alert("call");
	//urlSize = urls.length();
	alert("result"+urls[0]);
	urlScan = -1;
	var mFrame = document.getElementById("frame");
	mFrame.src = urls[0];
}

var pageTurning = function(){
	urlScan++;
	alert(urls[urlScan]);
	var frame = $("#frame");
	frame.attr("src", urls[urlScan]);
	websocket.send("33,"+urls[urlScan]);  //33 is the signal to turn a page
}
//----------------------socket------------------------
var isMark = false;
var enablePageTurn = true;


var mark = function(){
	if(isMark == false){
		isMark = true;
	}
	else if(isMark == true){
		isMark = false;
		enablePageTurn = true;
	}
	alert(isMark);
}

var wFrame;
var width, height, oHeight, oWidth;
var initialize = function(){

	//webview.JloadUrl();
	urls = new Array();
        urls.push("test.html");
	var frame = $("#frame");
        frame.attr("src",urls[0]);
	var canvas = $("#canvas");
	width = window.screen.width;
	height = document.body.scrollHeight;
	oHeight = canvas.css("height");
	oWidth = canvas.css("width");
	var mFrame = window.frames["frame"].document;
	 wFrame = document.getElementById('frame').contentWindow;
        document.getElementById('frame').onload = alert(("123"+document.getElementById('frame').$));
	frame.css("width", 200);
	frame.css("height", 200);
        
	/*
	canvas.css("position", "absolute");
	canvas.css("height", frame.css("height"));
	canvas.css("width", frame.css("width"));
	canvas.css({
		"left": frame.position().left,
		"top": frame.position().top,
	});
	frame.css('zIndex', 1000);
	canvas.css('zIndex', 0);
    */
}	

var isDoubleTouch = false;
	 
var setSingleTouchListener = function() {
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext('2d');
	ctx.strokeStyle = "rgb(0, 0, 0)";
	var started = false;
	var canvasLeft = $(canvas).position().left;
	var canvasTop = $(canvas).position().top;

	var hFactor = height/parseInt(oHeight);
	var wFactor = width/parseInt(oWidth);
	
	var pageTurnOriginX = 0;

	canvas.ontouchstart = function(e){
		if(isMark){
			//e.preventDefault();
			var touch = e.touches[0];
			//alert(touch);
			var x = touch.clientX;
			var y = touch.clientY;
			//alert(x);
			//alert(y);
			ctx.beginPath();
			var realX = (x-canvasLeft)/wFactor; 
			var realY = (y-canvasTop)/hFactor;
			ctx.moveTo(realX, realY);
			websocket.send("1,"+realX/parseInt(oWidth)+","+realY/parseInt(oHeight));
			started = true;
		}
		else{
			var touch = e.touches[0];
			pageTurnOriginX = touch.clientX;
			started = true;
		}
	}
	canvas.ontouchmove = function(e){
		//e.preventDefault();
		var touch = e.touches[0];
		//alert(touch);
		var x = touch.clientX;
		var y = touch.clientY;
		if(started && isMark){
			e.preventDefault();
			var realX = (x-canvasLeft)/wFactor; 
			var realY = (y-canvasTop)/hFactor;
			ctx.lineTo(realX, realY);
			ctx.stroke();
			websocket.send("2,"+realX/parseInt(oWidth)+","+realY/parseInt(oHeight));
		}
		else{
			/*if(Math.abs(pageTurnOriginX-x) >= window.screen.width/2.5){
				if(enablePageTurn){
					//alert("pageTurn");
					pageTurning();
					enablePageTurn = false;
				}
			}*/
		}
	}
	canvas.ontouchend = function(e){
		//e.preventDefault();
		//figureNum--;
		//alert(figureNum);
		//var touch = e.touches[0];
		//alert(touch);
		//var x = touch.clientX;
		//var y = touch.clientY;
		//if(started){
			//var realX = (x-canvasLeft)/wFactor; 
			//var realY = (y-canvasTop)/hFactor;
			//ctx.lineTo(realX, realY);
			//ctx.stroke();
			//websocket.send("3,"+realX/parseInt(oWidth)+","+realY/parseInt(oHeight));
			//started = false;
		//}
	}
}

$(document).ready(function(){
    initialize();
	//setSingleTouchListener();
});


