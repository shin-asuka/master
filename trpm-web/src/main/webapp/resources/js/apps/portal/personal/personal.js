var depends = ["function","tools","jquery-load","jquery-bootstrap"];
define(depends, function() {	
	
	var _timeout = 15 * 1000;
	
	var ajaxErrorfunction = function(reponse, status, info) {
		Portal.loading("close");
		if (reponse.statusText == "timeout") {
			$.alert("error", {
				title : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : "Server request error!"
			});
		}
	};
	
	var init = function(){
		var index = jQuery('#persion-info').attr('index');
		if(index == 'taxpayer'){
			var cUrl = '/personal/taxpayer.shtml';
			loadingPage(cUrl);
			jQuery('#persion-info').parent().find('li.active[url]').removeClass('active');
			jQuery('[url="'+cUrl+'"]').addClass('active');
		}
		
		/* 初始化tab切换 */
		$("ul.nav-stacked > li").click(function() {
			parentLoadPage($(this).attr("url"),this);
		});
		initButton();
	};
		
	var initButton = function(){
		$("#toCancel,#toEdit").click(function(){
			loadingPage($(this).attr("url"));
		});
		//基本信息
		copyinit();
	};
	
	var copyinit = function(){
		/*copy*/
		$("#urlButton").click(function(){
			$("#copyurl").select();
			document.execCommand("Copy")
			$.alert("info", {
				title : "Copied successfully!"
			});
		});
	};
	
	var parentLoadPage = function(url,obj){
		Portal.loading("open");
		$.ajaxRequest({
			url:url,
			success:function(data){
				if(data != "" && $.trim(data).indexOf("<div") == 0){
					$("#persion-info").html(data);
					initButton();
					//new code
					var index = $(obj).index();
					console.info("index:" + index);
					$(obj).addClass("active").siblings().removeClass("active");
				}else{
					location.href=webPath + "/index.shtml";
				}
			},	
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			},
			timeout:_timeout,
			complete:function(){
				Portal.loading("close");
			}
		});
	};
	
	var loadingPage = function(url){
		Portal.loading("open");
		$.ajaxRequest({
			url:url,
			type:"POST",
			success:function(data){
				if(data != "" && $.trim(data).indexOf("<div") == 0){
					$("#persion-info").html(data);
					initButton();
				}else{
					location.href=webPath + "/index.shtml";
				}
			},
			timeout:_timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			},
			complete:function(){
				Portal.loading("close");
			}
		});
	};
	
	return {
		init : init,
		loadingPage:loadingPage
	}
});