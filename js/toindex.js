var websocket, lectureInfo;
var currentDisplayNum;
var zoomFactor;
var isMark = false;
var frameRecord = new Array();
var paintColor = "rgb(0,0,0)";

$(document).ready(function(){
    initialize();
});

var initialize = function(){
	webview.JloadUrl();
}	

window.onunload = function(){
	websocket.close();
}

var loadLectureInformation = function(lecture){
	lectureInfo = eval('(' + lecture + ')');
	initWebsocket();
	initRecord();
	currentDisplayNum = 0;
	render(lectureInfo['currentpage'], true);
	turnToPage(lectureInfo['currentpage']);
}

var initRecord = function(){
	var i;
	for(i = 1; i <= lectureInfo['pageNum']; i++){
		frameRecord[i] = false;
	}
}

var initWebsocket = function(){
	websocket = new WebSocket("ws://" + lectureInfo['ip']  + ":8888");
	websocket.close();
	websocket.onopen = function(e){
		alert("open");
		websocket.send("host;veen");
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

var generateUrl = function(pageNum){
	var url = 'http://' + lectureInfo['ip'] + '/islider-mobile/documents/' 
		+ lectureInfo['roomname'] + '/' + lectureInfo['filename'] + '/'
		+ lectureInfo['filename'] + pageNum + '.html';
	return url;
}

var preRender = function(pageNum, num){
	for(var i = pageNum-num; i <= pageNum+num; i++){
		if(i > 0 && i <= lectureInfo['pageNum'] && frameRecord[i] == false){
			render(i, false);
		}
	}
}

var render = function(pageNum, isok){
	var content = document.getElementById('content');
	var canvas = document.createElement('canvas');
	var frame = document.createElement('iframe');
	var url = generateUrl(pageNum);
	frameRecord[pageNum] = true;
	frame.setAttribute('frameborder', '0');
	frame.setAttribute('src', url);
	frame.setAttribute('id', 'frame'+pageNum);
	canvas.setAttribute('id', 'canvas'+pageNum);
	canvas.style.display = 'none';
	frame.style.display = 'none';
	content.appendChild(canvas);
	content.appendChild(frame);
	resize(pageNum);
}

var resize = function(pageNum){
	document.getElementById('frame'+pageNum).onload = function(){
		var frame = $("#frame"+pageNum);
		var canvas = $("#canvas"+pageNum);
		width = window.screen.width;
		height = window.screen.height;
		frame.css("width", width);
		frame.css("height", height);
		canvas.css("height", frame.css("height"));
		canvas.css("width", frame.css("width"));
		canvas.css("position", "absolute");
		document.getElementById('canvas'+pageNum).height = 1.5*height;
		document.getElementById('canvas'+pageNum).width = 1.5*width;

		var wFrame = document.getElementById('frame'+pageNum).contentWindow;
		var wratio = width/wFrame.$('.w1:first').width();
		var hratio = height/wFrame.$('.w1:first').height();
		wFrame.$('.w1').css("margin", "0");
		wFrame.$('.w1').css("padding", "0");
		//wFrame.$('.w1').css("-o-transform","scale("+wratio+","+hratio+")");
		//wFrame.$('.w1').css("-o-transform-origin","0% 0%");
		wFrame.$('.w1').css("-webkit-transform","scale("+wratio+","+hratio+")");
		wFrame.$('.w1').css("-webkit-transform-origin","0% 0%");
		//wFrame.$('.w1').css("-ms-transform","scale("+wratio+","+hratio+")");
		//wFrame.$('.w1').css("-ms-transform-origin","0% 0%");
		//wFrame.$('.w1').css("-moz-transform","scale("+wratio+","+hratio+")");
		//wFrame.$('.w1').css("-moz-transform-origin","0% 0%");
		//wFrame.$('.w1').css("transform","scale("+wratio+","+hratio+")");
		//wFrame.$('.w1').css("transform-origin","0% 0%");
		canvas.css({
			"left": frame.position().left,
			"top": frame.position().top,
		});
		frame.css('zIndex', 0);
		canvas.css('zIndex', 1000);
	}
}

var rescale = function(pageNum){
	var frame = $("#frame"+pageNum);
	width = window.screen.width;
	height = window.screen.height;
	frame.css("width", width);
	frame.css("height", height);
	var wFrame = document.getElementById('frame'+pageNum).contentWindow;
	var wratio = width/(wFrame.$('.w1:first').width());
	var hratio = height/(wFrame.$('.w1:first').height());
	wFrame.$('.w1').css("margin", "0");
	wFrame.$('.w1').css("padding", "0");
	//wFrame.$('.w1').css("-o-transform","scale("+wratio+","+hratio+")");
	//wFrame.$('.w1').css("-o-transform-origin","0% 0%");
	//wFrame.$('.w1').css("-webkit-transform","scale("+wratio+","+hratio+")");
	//wFrame.$('.w1').css("-webkit-transform-origin","0% 0%");
	//wFrame.$('.w1').css("-ms-transform","scale("+wratio+","+hratio+")");
	//wFrame.$('.w1').css("-ms-transform-origin","0% 0%");
	//wFrame.$('.w1').css("-moz-transform","scale("+wratio+","+hratio+")");
	//wFrame.$('.w1').css("-moz-transform-origin","0% 0%");
	//wFrame.$('.w1').css("transform","scale("+wratio+","+hratio+")");
	//wFrame.$('.w1').css("transform-origin","0% 0%");
}


var turnToPage = function(pageNum){
	if(currentDisplayNum != 0){
		var tempCanvas = document.getElementById('canvas'+currentDisplayNum);
		var tempFrame = document.getElementById('frame'+currentDisplayNum);
		tempCanvas.style.display = 'none';
		tempFrame.style.display = 'none';
	}
	var canvas = document.getElementById('canvas'+pageNum);
	var frame = document.getElementById('frame'+pageNum);
	canvas.style.display = 'block';
	frame.style.display = 'block';
	$(frame).css('zIndex', 0);
	$(canvas).css('zIndex', 1000);
	currentDisplayNum = pageNum;
	setSingleTouchListener(pageNum);
	preRender(pageNum, 2);
	rescale(pageNum);
}

var pageForward = function(){
	if(lectureInfo['currentpage'] < lectureInfo['pageNum'] && frameRecord[lectureInfo['currentpage']] == true){
		lectureInfo['currentpage']++;
		turnToPage(lectureInfo['currentpage']);
		websocket.send("33,"+lectureInfo['currentpage']);
	}
	else{
		alert("已经是最后一页了!");
	}
}

var pageBackward = function(){
	/*if(lectureInfo['currentpage'] > 1){
		lectureInfo['currentpage']--;
		turnToPage(lectureInfo['currentpage']);
		websocket.send('33,'+lectureInfo['currentpage']);
	}
	else{
		alert("已经是第一页了!");
	}*/
	ColorPicker(

        document.getElementById('slider'),
        document.getElementById('picker'),

        function(hex, hsv, rgb) {
          alert(rgb);
          document.body.style.backgroundColor = hex;        // #HEX
        });
}

var closeWebscoket = function(){   //nonoononnnnnooo
	alert("close");
	websocket.close();
}

var changeColor = function(color){
	paintColor = color;
}

var setSingleTouchListener = function(pageNum) {
	var canvas = document.getElementById('canvas'+pageNum);
	var ctx = canvas.getContext('2d');
	ctx.linewidth = 60;
	ctx.strokeStyle = "rgb(0, 0, 0)";
	var width = window.screen.width*1.5;
	var height = window.screen.height*1.5;
	var started = false;
	var canvasLeft = $(canvas).position().left;
	var canvasTop = $(canvas).position().top;

	canvas.ontouchstart = function(e){
		ctx.strokeStyle = paintColor;
		ctx.linewidth = 80/2/zoomFactor;
		if(isMark){
			e.preventDefault();
			var touch = e.touches[0];
			var x = touch.pageX;
			var y = touch.pageY;
			ctx.beginPath();
			var realX = (x-canvasLeft)*1.5;; 
			var realY = (y-canvasTop)*1.5;;
			ctx.moveTo(realX, realY);
			websocket.send("1,"+realX/width+","+realY/height);
			started = true;
		}
	}
	canvas.ontouchmove = function(e){
		ctx.linewidth = 80/2/zoomFactor;
		if(started && isMark){
			e.preventDefault();
			var touch = e.touches[0];
			var x = touch.pageX;
			var y = touch.pageY;
			var realX = (x-canvasLeft)*1.5; 
			var realY = (y-canvasTop)*1.5;;
			ctx.lineTo(realX, realY);
			ctx.stroke();
			websocket.send("2,"+realX/width+","+realY/height);
		}	
	}
	canvas.ontouchend = function(e){
		if(isMark){
			e.preventDefault();
		}
	}
}


var getZoomFactor = function(factor){
	zoomFactor = factor;
}

var mark = function(){
	if(isMark == false){
		isMark = true;
		webview.preventZoom();
	}
	else if(isMark == true){
		isMark = false;
		webview.permitZoom();
		enablePageTurn = true;
	}
	alert(isMark);
}

