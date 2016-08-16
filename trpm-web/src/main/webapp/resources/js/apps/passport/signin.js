define([ "utils", "jquery-load", "jquery-bootstrap", "jquery-cookie" ],function(util) {
			var remode = Math.random().toString(36).substr(2);
			var _timeout = 60 * 1000;
			var setCookie = function() {
				var data = $.cookie("TRPM_PASSPORT_NEW");
				if (undefined === data || data === "") {
					$("#remember").removeClass("selboxhook");
					$("#username,#password").val("");
				} else {
					$("#remember").addClass("selboxhook");
					data = $.parseJSON(util.decode(data));
					$("#username").val($.trim(util.decode(data.email)));
					$("#password").val(remode);
				}
				$("#username").focus();
			};
			var init = function() {
				removeCookie("TRPM_PASSPORT");
				$(".teachersigninform").find(".selbox").click(function() {
					if ($(this).children('i').hasClass("selboxhook")) {
						var ip = $.cookie("TRPM_TOKEN_ID");
						if(ip){
							removeCookie("TRPM_TOKEN_ID"+ip);
							removeCookie("TRPM_TOKEN_ID");
							removeCookie("TRPM_PASSPORT_NEW");
							$("#password").val("");
						}
						$(this).children('i').removeClass("selboxhook");
					} else {
						$(this).children('i').addClass("selboxhook");
					}
				});
				$("#signinbtn").on("click", listen);
				$("body").keydown(function() {
					if (event.keyCode == "13") {
						listen();
					}
				});
				
				$("#password").keyup(function(){
					var ip = $.cookie("TRPM_TOKEN_ID");
					if(ip){
						removeCookie("TRPM_TOKEN_ID"+ip);
						removeCookie("TRPM_TOKEN_ID");
						removeCookie("TRPM_PASSPORT_NEW");
					}
				});
				
				/* Cookie value 设置 */
				setCookie();
			};
			
			var removeCookie = function(key){
				$.removeCookie(key, {
					expires : 365,
					path : '/'
				});
			};
			
			var listen = function() {
				var dataform = {
					email : util.encode($.trim($("#username").val())),
					passwd : util.encode($.trim($("#password").val()))
				};
				if ($("#remember").hasClass("selboxhook")) {
					dataform.remember = true;
				} else {
					dataform.remember = false;
				}				
				if (util.checkNotNull("teachersigninform")) {
					$(window).loading({
						action : "open",
						id : "window"
					});
					$.ajaxRequest({
						url : webPath + "/signInAction.json",
						data : dataform,
						dataType : "json",
						success : function(data) {
							ajaxsuccess(data);
						},
						timeout : _timeout,
						error : function(reponse, status, info) {
							ajaxerror(reponse, status, info);
						}
					});
				}
			};
			/** 定义成功函数处理 */
			var ajaxsuccess = function(data) {
				$(window).loading({
					action : "close",
					id : "window"
				});
				util.errorShow('username');
				util.errorShow('password');
				if (data.info == "success-pass") {
					$(window).loading({
						action : "open",
						id : "window"
					});
					window.location.href = webPath + "/index.shtml?uuid="
							+ data.uuid;
					$(".writeinfo").hide();
					util.passShow('username');
					util.passShow('password');
				} else if (data.info == "error-locked") {
					$(".writeinfo").hide();
					$(".locked").hide().fadeIn();
				} else if (data.info == "user-error") {
					$(".writeinfo").hide();
					$(".usererror").hide().fadeIn();
				} else if (data.info == "quit-locked") {
					$(".writeinfo").hide();
					$(".quitinfo").hide().fadeIn();
				} else {
					$(".writeinfo").hide();
					$(".otherinfo").hide().fadeIn();
				}
			};
			/** 定义错误函数处理 */
			var ajaxerror = function(reponse, status, info) {
				$(window).loading({
					action : "close",
					id : "window"
				});
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
				init : init
			}
		});