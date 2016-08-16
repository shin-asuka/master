define(["jquery-load","jquery-bootstrap"], function() {
	
	var _timeout = 60 * 1000;
	
	var init = function(){
		var height = $(".privacycontent").height() - 50;
		$(".privacycontent-left").attr("style","height:"+height+"px;");
		resize();
		$(window).resize(function(){
			resize();
		});
		$(".back").click(function(){
			$('body,html').animate({scrollTop:0},500);
		});
		$(".divload").fadeOut(500);
	};
	
	var resize = function(){
		var width = ($(window).width()/2 - 495);
		$(".privacycontent").attr("style","margin-left:"+width+"px;");
	}

	return {
		init:init
	}
	
});