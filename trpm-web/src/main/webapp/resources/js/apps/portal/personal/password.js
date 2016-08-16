var depends = ["validator","utils","jquery-form","function", "tools","jquery-bootstrap","jquery-load"];
define(depends, function(validator,util) {
	var init = function() {
		//密码修改初始化
		$("#userpassword").blur(function(){
			$(".hoverpromptbox").hide();
		}).focus(function(){
			$(".hoverpromptbox").show();
		});		
		$("#changePasswordForm").find("input").each(function(){
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
		/* 初始化修改密码表单 */
		$("#changePasswordSubmit").on("click",changePasswordForm);
	};

	/* 修改密码表单 */
	var changePasswordForm = function() {		
		var flag = true;
		$("#changePasswordForm").find("input").each(function(){
			var element = this;
			if(!validator.core(element)){
				flag = false;
			}
		});
		
		if(flag){
			var u = $("#userpassword").val();
			$("#userpassword,#repassword").val(util.encode($.trim($("#userpassword").val())));
			$("#changePasswordForm").ajaxSubmit({
				beforeSubmit : function() {
					$("#changePasswordSubmit").addClass("disabled");
					Portal.loading("open");
				},
				success : function(datas) {
					Portal.loading("close");
					$("#userpassword,#repassword").val(u);
					changePasswordSucceed(datas);
				},
				error:function(){
					Portal.loading("close");
					$("#userpassword,#repassword").val(u);
					$("#changePasswordSubmit").removeClass("disabled");
				}
			});
		}
	};

	/* 修改密码成功函数 */
	var changePasswordSucceed = function(datas) {
		datas = $.parseJSON(datas);

		if (datas.action) {
			$.alert("info", {
				title : "Password change successful！"
			});
		} else if (datas.originalPasswordErr) {
			$.alert("error", {
				title : "The original password you typed is wrong！"
			});
		} else if (datas.patternErr) {
			$.alert("error", {
				title : "Your new password should be a combination of 6-12 letters, numbers and/or underscores！"
			});
		}
		$("#changePasswordSubmit").removeClass("disabled");
		$("#changePasswordForm").find("input[type='password']").each(function(){
			$(this).val("");
		});
		$("#password1,#password2,#password3").attr("class","epbgico");
		
	};

	return {
		init : init
	}

});
