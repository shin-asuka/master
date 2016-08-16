var depends = [ "function", "tools", "jquery-validator", "jquery-cookie", "jquery-bootstrap" ];
define(depends, function() {

	/* COOKIE键值 */
	var _TRPM_USER = "TRPM_USER";
	
	var _message = {
		badCredentials : {
			title : "Login failed！ ",
			content : "Check your username and password. If you still cannot login, contact support."
		},
		disabled : {
			title : "Login failed！",
			content : "Your account is locked, contact support."
		}
	};
	
	var init = function() {
		showTips();
		bindForm();
		remember();
	};
	
	/* 保存用户名 */
	var remember = function(){
		var value = $.cookie(_TRPM_USER);
			
		if (undefined === value) {
			$("#rememberUsername").removeProp("checked");
		} else {
			$("#rememberUsername").prop("checked", "checked");
			$("#j_username").val(value);
		}
		
		$("#rememberUsername").click(function(){
			if (!$(this).prop("checked")) {
				$.removeCookie(_TRPM_USER, {
					expires : 365,
					path : '/'
				});
			}
		});
		
		$("#loginSubmit").click(function() {
			if ($("#rememberUsername").prop("checked")) {
				var username = $("#j_username").val();
				$.cookie(_TRPM_USER, username, {
					expires : 365,
					path : '/'
				});
			}
		});
	};

	/* 显示登录提示信息 */
	var showTips = function() {
		var type = Tools.getUrlParam("t");
		if (null != type) {
			$.alert("error", _message[type]);
		}
	};

	/* 绑定表单验证 */
	var bindForm = function() {
		$("#loginSubmit").formValidator({
			scope : "#loginForm",
			silent : true
		});
	};

	return {
		init : init
	};

});