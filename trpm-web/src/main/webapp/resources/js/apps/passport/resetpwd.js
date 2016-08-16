define(["validator","utils","jquery-load","jquery-bootstrap"], function(validator,util) {

	var _timeout = 60 * 1000;
	
	var init = function(token){
		$("#signupbtn").on("click",listen);
		$("#username").keyup(function(){
			checkinput("username");
		}).blur(function(){
			checkinput("username");
		});
		$(".resetpasswordform").find("input").each(function(){
			var element = this;
			$(element).blur(function(){
				validator.core(element);
			}).keyup(function(){
				validator.core(element);
			});
		});
	};
	
	var listen = function(){
		if(util.checkNotNull("resetpasswordform")){
			var email = $.trim($("#username").val());
			if(checkinput("username")){
				$(window).loading({action:"open",id:"window"});
				$.ajaxRequest({
					url:webPath+"/resetPasswordAction.json",
					data:{"email":email},
					dataType:"json",
					success:function(data){
						ajaxsuccess(data);
					},
					timeout : _timeout,
					error : function(reponse, status, info) {
						ajaxerror(reponse, status, info);
					}
				});
			}
		}
	};
	
	var checkinput = function(id){
		if(validator.core($("#"+id))){
			return true;
		}
		return false;
	}
	
	/**定义成功函数处理*/
	var ajaxsuccess = function(data){
		$(window).loading({action:"close",id:"window"});
		if(data.info == "error-locked"){
			$(".writeinfo").hide();
			$(".locked").hide().fadeIn();
			util.errorShow("username");
		}else if(data.info == "user-error"){
			$(".writeinfo").hide();
			$(".usererror").hide().fadeIn();
			util.errorShow("username");
		}else if(data.info == "quit-locked"){
			$(".writeinfo").hide();
			$(".quitinfo").hide().fadeIn();
			util.errorShow("username");
		}else{
			$(".writeinfo,.resetpasswordform .signinbtn").hide();
			util.passShow("username");
			$("#disabledbtn").html("30s").show();
			countdown("disabledbtn",29,"signupbtn");
			$.alert('confirm', {
				title : "Success",
				content : "Your password reset request has been sent to "+$("#username").val(),
				style:{
					"margin-top":"10%",
					"width":"300px",
					"text-align":"left"
				},
				cancel:'&nbsp;&nbsp;OK&nbsp;&nbsp;'
			});
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
	
	var countdown = function(currId,time,afterId){
		setTimeout(function(){
			if(time > 0){
				time--;
				$("#"+currId).html(time+"s");
				setTimeout(arguments.callee, 1000);
			}else{
				$(".writeinfo,.resetpasswordform .signinbtn").hide();
				$("#"+afterId).html("Try again").show();
			}
		}, 1000);
	};
	
	return {
		init:init
	}
	
});