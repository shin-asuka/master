define(["utils","jquery-load","jquery-bootstrap","jquery-cookie"], function(util) {
	
	var _timeout = 60 * 1000;
	
	var init = function(){
		$("#send").on("click",listen);
		$("#mail").keyup(function(){
			checkinput("mail");
		}).blur(function(){
			checkinput("mail");
		});
	};
	
	var listen = function(){
		if(util.checkNotNull("resetpasswordform")){
			var email = $("#mail").val();
			var type = $("#mailType").val();
			if(checkinput("mail") && checkinput("mailType")){
				$(window).loading({action:"open",id:"window"});
				$.ajaxRequest({
					url:webPath+"/searchEmail.json",
					data:{"email":email,"type":type},
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
		var validator = $("#"+id).attr("validator");
		var value = $("#"+id).val();
		if(validator){
			var json = eval("("+validator+")");	
			if(json.pattern){
				var pattern = new RegExp(json.pattern);
				if(!pattern.test(value)){
					util.errorShow(id);
					$("#warnwords-"+id).html(json.patternMsg).hide().fadeIn();
					return false;
				}else{
					util.passShow(id);
					$("#warnwords-"+id).html("").hide();
				}
			}
		}
		return true;
	};
	
	/**定义成功函数处理*/
	var ajaxsuccess = function(data){
		$(window).loading({action:"close",id:"window"});
		if(data.info == "error-locked"){
			$(".writeinfo").hide();
			$(".locked").hide().fadeIn();
			util.errorShow("mail");
		}else if(data.info == "user-error"){
			$(".writeinfo").hide();
			$(".usererror").hide().fadeIn();
			util.errorShow("mail");
		}else if(data.info == "quit-locked"){
			$(".writeinfo").hide();
			$(".quitinfo").hide().fadeIn();
			util.errorShow("mail");
		}else{
			$(".writeinfo,.resetpasswordform .signinbtn").hide();
			util.passShow("mail");
			$("#disabledbtn").html("30s").show();
			countdown("disabledbtn",29,"send");
			$.alert('confirm', {
				title : "Success",
				content : "Your request has been sent to "+$("#mail").val(),
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