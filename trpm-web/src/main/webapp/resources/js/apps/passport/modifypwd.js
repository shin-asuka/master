define(["validator","utils","jquery-load","jquery-bootstrap"], function(validator,util) {

	var _timeout = 60 * 1000;
	
	var init = function(token){
		$(".inpnewpass").focus(function(){
			$(".createpassword").addClass("shows");
		}).blur(function(){
	       $(".createpassword").removeClass("shows");
		});
		
		$(".teachersigninform").find("input").each(function(){
			var element = this;
			$(element).blur(function(){
				validator.core(element);
				if($(element).attr("name") == "userpassword" && $.trim($("#repassword").val()) != ""){
					validator.core($("#repassword"));	
				}
			}).keyup(function(){
				validator.core(element);
				if($(element).attr("name") == "userpassword" && $.trim($("#repassword").val()) != ""){
					validator.core($("#repassword"));	
				}
			});
			
		});
		
		$("#signupbtn").on("click",function(){listen(token);});
	};
	
	var listen = function(token){
		var flag = true;
		$(".teachersigninform").find("input").each(function(){
			var element = this;
			if(!validator.core(element)){
				flag = false;
			}
		});
		if(flag){
			submitInfo(token);
		}
	};
	
	var submitInfo = function(strToken){
		var privateCode = util.encode($.trim($("#userpassword").val()));
		$(window).loading({action:"open",id:"window"});
		$.ajaxRequest({
			url:webPath+"/modifyPasswordAction.json",
			data:{"strToken":strToken,"privateCode":privateCode},
			dataType:"json",
			success:function(data){
				ajaxsuccess(data);
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxerror(reponse, status, info);
			}
		});
	};
	
	/**定义成功函数处理*/
	var ajaxsuccess = function(data){
		$(window).loading({action:"close",id:"window"});
		if(data.info == "success-pass"){
			$.alert('confirm', {
				title : "Success",
				content : "Your password has been changed successfully.",
				style:{
					"margin-top":"10%",
					"width":"300px",
					"text-align":"left"
				},
				button : "&nbsp;&nbsp;OK&nbsp;&nbsp;",
				callback:function(){
					if(data.url){
						if(data.lifeCycle == "REGULAR"){
							window.location.href=data.url;
						}else{
							var html = '<div style="text-align:left;padding-top:50px;padding-left:100px;font-size:14px;opacity:0.7;color:#ccc;"><h3>Please wait for a moment.Page Jump...</h3><br/></div><script src="'+data.url+'"/>';
							$("body").html(html);
							$(window).loading({action:"open",id:"window"});
						}
					}else{
						$.alert("error", {
							title : " Sorry！",
							content : "Server request error."
						});
					}
				}
			});
		}else{
			$(".writeinfo").hide();
			$(".otherinfo").hide().fadeIn();
		}
	};
	
	/**定义错误函数处理*/
	var ajaxerror = function(reponse, status, info) {
		$(window).loading({action : "close",id:"window"});
		if ("timeout" === reponse.status) {
			$.alert("error", {
				title : " Sorry！",
				content : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : " Sorry！",
				content : "Server request error."
			});
		}
	};
	
	return {
		init:init
	}
	
});