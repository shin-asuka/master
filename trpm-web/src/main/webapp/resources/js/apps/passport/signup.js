define(["validator","utils","jquery-load","jquery-bootstrap"], function(validator,util) {

	var _timeout = 60 * 1000;
	
	var init = function(){
		$(".teachersignupform").find(".emailaddressinp").focus(function(){
			$(this).parent().addClass("borccc");
			$(".emailaddress").addClass("shows");	
		}).blur(function(){
			$(this).parent().removeClass("borccc");
			$(".emailaddress").removeClass("shows");	
		});
		$(".passwordinptxt input").focus(function(){
			$(this).parent().addClass("borccc");				
			$(".createpassword").addClass("top189");
			$(".createpassword").addClass("shows");
		}).blur(function(){
			$(this).parent().removeClass("borccc");
			$(".createpassword").removeClass("top189");
			$(".createpassword").removeClass("shows");
		});
		
		$(".teachersignupform").find("input").each(function(){
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
		
		$("#signupbtn").on("click",listen);
		
		$("body").keydown(function() {
		    if (event.keyCode == "13") {//keyCode=13是回车键
		    	listen();
		    }
		});  
	};
	
	var listen = function(){
		var flag = true;
		$(".teachersignupform").find("input").each(function(){
			var element = this;
			if(!validator.core(element)){
				flag = false;
			}
		});
		if(flag){
			submitInfo();
		}
	};
	
	var submitInfo = function(){
		var email = $.trim($("#email").val());
		var privateCode = util.encode($.trim($("#userpassword").val()));
		$(window).loading({action:"open",id:"window"});
		$.ajaxRequest({
			url:webPath+"/signUpAction.json",
			data:{"email":email,"privateCode":privateCode},
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
			if(data.uuid){
				$(window).loading({action:"open",id:"window"});
				facebookcode();//执行fackbook跟踪代码
				window.location.href = webPath + "/signupSuccess.shtml?uuid="+data.uuid;
			}else{
				$.alert("error", {
					title : " Sorry！",
					content : "Server request error."
				});
			}
			// window.location.href = webPath+"/index.shtml?uuid="+data.uuid;
		}else if(data.info == "error-account"){
			$(".writeinfo").hide();
			$(".otherinfo").hide().fadeIn();
			util.errorShow('username');
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