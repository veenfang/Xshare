// JavaScript Document

//使用全局的  isMark 是不是正在做标记
//使用全局 newColor

var control = (function() {
    var control = new Object();
    control.rescale = function(iframe) {
        iframe._ = iframe;
        iframe.onload = function() {
            var iframe = this._;
            var fwindow = iframe.contentWindow;
            var fwidth = iframe.offsetWidth;
            var fheight = iframe.offsetHeight;
            var fdoc = fwindow.document;
            var ele = fdoc.getElementsByClassName("w1")[0];
            var eleContainer = ele.parentNode;
            var wratio = fwidth / ele.offsetWidth;
            var hratio = fheight / ele.offsetHeight;
            ele.style.margin = "0";
            ele.style.padding = "0";

            /* Safari 和 Chrome */
            ele.style.webkitTransformOrigin = "0% 0%";
            ele.style.webkitTransform = "scale(" + wratio + ")";
            /* IE 9 */
            ele.style.msTransformOrigin = "0% 0%";
            ele.style.msTransform = "scale(" + wratio + ")";
            /* Firefox */
            ele.style.MozTransformOrigin = "0% 0%";
            ele.style.MozTransform = "scale(" + wratio + ")";
            /* Opera */
            ele.style.OTransformOrigin = "0% 0%";
            ele.style.OTransform = "scale(" + wratio + ")";



            iframe.style.height = ele.offsetHeight * wratio + 20 + "px";
            iframe.style.width = fwidth + "px";
            iframe.scrolling = "no";
            var canvas = iframe.nextElementSibling;
            canvas.style.position = "absolute";
            canvas.style.left = iframe.offsetLeft + "px";
            canvas.style.width = iframe.offsetWidth + "px";
            canvas.width = canvas.offsetWidth;
            canvas.height = canvas.offsetHeight;
            setSingleTouchListener(canvas, iframe);    //addListener
        };
    };

    var setSingleTouchListener = function(canvas, iframe) {
        var ctx = canvas.getContext('2d');
        ctx.linewidth = 60;
        ctx.strokeStyle = "rgb(0, 0, 0)";
        
        var width = iframe.offsetWidth;
        var height = iframe.offsetHeight;
        var started = false;
        var canvasLeft = canvas.offsetLeft;
        var canvasTop = canvas.offsetTop;


        var hasTouch = 'ontouchstart' in window;
        var touchStart = hasTouch ? 'touchstart' : 'mousedown';
        var touchMove = hasTouch ? 'touchmove' : 'mousemove';
        //var touchMove = hasTouch ? 'touchmove' : '';
        var touchEnd = hasTouch ? 'touchend' : 'mouseup';

        

        //触摸开始函数
        var ctStart = function(e) {
            ctx.strokeStyle = newColor;
            ctx.linewidth = 80 / 2 / zoomFactor;
            if (isMark !== true)
                return;


            if (e.stopPropagation) {
                // W3C standard variant
                e.stopPropagation()
            } else {
                // IE variant
                e.cancelBubble = true
            }


            console.log("50," + newColor);
            websocket.send("50," + newColor);
            var touch = hasTouch ? e.touches[0] : e;
            var x = touch.pageX;
            var y = touch.pageY;
            //console.log(x);
            //console.log(y);
            ctx.beginPath();
            var realX = (x) * 1;
            var realY = (y) * 1;
            ctx.moveTo(realX, realY);
            //console.log("1," + realX / width + "," + realY / height);
            websocket.send("1," + realX / width + "," + realY / height);
            started = true;
            //添加“触摸移动”事件监听
            canvas.addEventListener(touchMove, ctMove, false);
            //添加“触摸结束”事件监听
            canvas.addEventListener(touchEnd, ctEnd, false);
        }

        //触摸移动函数
        var ctMove = function(e) {

            if (e.stopPropagation) {
                // W3C standard variant
                e.stopPropagation()
            } else {
                // IE variant
                e.cancelBubble = true
            }


            if (hasTouch) {
                if (e.touches.length > 1 || e.scale && e.scale !== 1)
                    return
            }


            var touch = hasTouch ? e.touches[0] : e;

            ctx.linewidth = 80 / 2 / zoomFactor;
            if (started && isMark) {
                e.preventDefault();
                var x = touch.pageX;
                var y = touch.pageY;
                var realX = (x ) * 1;
                var realY = (y ) * 1;

                ctx.lineTo(realX, realY);
                ctx.stroke();
                //console.log("2," + realX / width + "," + realY / height);
                websocket.send("2," + realX / width + "," + realY / height);
                //websocket.flush();
            }

        }

        //触摸结束函数
        var ctEnd = function(e) {
            if (e.stopPropagation) {
                // W3C standard variant
                e.stopPropagation()
            } else {
                // IE variant
                e.cancelBubble = true
            }
            canvas.removeEventListener(touchMove, ctMove, false);
		        canvas.removeEventListener(touchEnd, ctEnd, false);
        }
				alert("ready to set");
        canvas.addEventListener(touchStart, ctStart, false);

    }
    return control;
})();
	  
