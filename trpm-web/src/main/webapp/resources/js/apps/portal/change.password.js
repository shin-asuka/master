define(["validator","utils","function", "tools", "jquery-bootstrap", "jquery-form",  "jquery-cookie", "jquery-load" ], function(validator,util) {
	
	var init = function(){		
		
		$("#userpassword").blur(function(){
			$(".hoverpromptbox").css({"border":"1px solid #ccc"});
			$(".hoverpromptbox").removeClass('change');
		});
		$("#userpassword").focus(function(){
			$(".hoverpromptbox").css({"border":"1px solid #66afe9"});
			$(".hoverpromptbox").addClass('change');
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
			$.removeCookie("changeWindow");
			$('#password-Modal').modal('hide');
			$('#password-Modal').on('hidden.bs.modal', function () {
				var datas = $("#dialog").attr("data");
				if(datas != "false,false"){
					$("body").addClass("modal-open");
				}
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
	};
	
	return {
		init : init
	};

});