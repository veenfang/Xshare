// JavaScript Document

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
        };
    };
    return control;
})();
	  