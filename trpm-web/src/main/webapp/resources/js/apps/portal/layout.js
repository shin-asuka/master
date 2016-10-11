
$(document).ready(function() {
	loadAnnouce();
});

function loadAnnouce(){
	var scrollId = "myscroll";
	var myscroll = jQuery("#"+scrollId);
	if(myscroll.children().length>1){
		loadScroll(scrollId,100);
	}
}

function loadScroll(scrollId,scrollheight) {
	if(scrollId == null){
		console.info("scrollId is null");
		return ;
	}
	
	function get(element) {
		if (arguments.length > 1) {
			for (var i = 0, length = arguments.length, elements = []; i < length; i++) {
				elements.push(get(arguments[i]));
			}
			return elements;
		}
		if (typeof element == "string") {
			return document.getElementById(element);
		} else {
			return element;
		}
	}

	var Class = {
		create : function() {
			return function() {
				this.initialize.apply(this, arguments);
			}
		}
	};

	Function.prototype.bind = function(object) {
		var method = this;
		return function() {
			method.apply(object, arguments);
		}
	};
	var Scroll = Class.create();
	Scroll.prototype = {
		initialize : function(element, height) {
			this.element = get(element);
			this.element.innerHTML += this.element.innerHTML;
			this.height = height;
			this.maxHeight = this.element.scrollHeight / 2;
			this.counter = 0;
			this.scroll();
			this.timer = "";
			this.element.onmouseover = this.stop.bind(this);
			this.element.onmouseout = function() {
				this.timer = setTimeout(this.scroll.bind(this), 1000);
			}.bind(this);
		},
		scroll : function() {
			if (this.element.scrollTop < this.maxHeight) {
				this.element.scrollTop++;
				this.counter++;
			} else {
				this.element.scrollTop = 0;
				this.counter = 0;
			}
			if (this.counter < this.height) {
				this.timer = setTimeout(this.scroll.bind(this), 20);
			} else {
				this.counter = 0;
				this.timer = setTimeout(this.scroll.bind(this), 1000);
			}
		},
		stop : function() {
			clearTimeout(this.timer);
		}
	}
	var myscroll = new Scroll(scrollId, scrollheight);
}

function closeAnnounce(obj){
	jQuery(obj).parent().remove();
}

function closeBanner(obj){
	jQuery(obj).parent().remove();
}

function openThirdYearAnniversaryPage(){
	window.open("http://www.baidu.com");
}






