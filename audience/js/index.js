----------------------socket------------------------
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
	//alert("call");
	urlSize = urls.length;
	alert("result"+array);
	var frame = $("#frame");
    //frame.attr("src",urls[0]);
	urlScan = 0;
	//var mFrame = document.getElementById("frame");
	//mFrame.src = urls[0];
}

var pageTurning = function(){
	if(urlScan < urlSize-1){
	  urlScan++;
	  alert(urls[urlScan]);
	  var frame = $("#frame");
	  frame.attr("src", urls[urlScan]);
	  websocket.send("33,"+urls[urlScan]);  //33 is the signal to turn a page
	}
}
//----------------------socket------------------------
var isMark = false;
var enablePageTurn = true;
var zoomFactor = 1.0;

var getZoomFactor = function(factor){
	zoomFactor = factor;
	alert(zoomFactor);
}


var mark = function(){
	if(isMark == false){
		isMark = true;
		webview.preventZoom();
		//alert(zoomFactor);
	}
	else if(isMark == true){
		isMark = false;
		webview.permitZoom();
		enablePageTurn = true;
	}
	alert(isMark);
}

var wFrame;
var width, height, oHeight, oWidth;
var initialize = function(){

	webview.JloadUrl();
	urls = new Array();
    urls.push("test.html");
	var frame = $("#frame");
    frame.attr("src",urls[0]);
	var canvas = $("#canvas");
	var mFrame = window.frames["frame"].document;
	width = window.screen.width;
	height = document.body.scrollHeight;
	oHeight = canvas.css("height");
	oWidth = canvas.css("width");
	wFrame = document.getElementById('frame').contentWindow;
	document.getElementById('frame').onload = function(){
        //alert(document.getElementById('frame').contentWindow.$);
		//alert(wFrame.$('.w1:first').width());
		var ratio = frame.width()/wFrame.$('.w1:first').width();
		wFrame.$('.w1').css("-webkit-transform","scale("+ratio+")");
		wFrame.$('.w1').css("-webkit-transform-origin","0% 0%");
	}
    //alert(document.getElementById('frame').contentWindow.$);
	frame.css("width", width);
	frame.css("height", height);
        

	canvas.css("position", "absolute");
	canvas.css("height", frame.css("height"));
	canvas.css("width", frame.css("width"));
	canvas.css({
		"left": frame.position().left,
		"top": frame.position().top,
	});
	frame.css('zIndex', 0);
	canvas.css('zIndex', 1000);
    
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
			e.preventDefault();
			var touch = e.touches[0];
			//alert(touch);
			var x = touch.pageX;
			var y = touch.pageY;
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
		var x = touch.pageX;
		var y = touch.pageY;
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
		e.preventDefault();
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
			websocket.send("3,"+realX/parseInt(oWidth)+","+realY/parseInt(oHeight));
			//started = false;
		//}
	}
}

$(document).ready(function(){
    initialize();
	setSingleTouchListener();
});


