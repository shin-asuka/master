var depends = [ "personal", "function", "tools", "jquery-form", "jquery-validator",
		"jquery-bootstrap", "jquery-load", "jquery-datetime", "select2" ];
define(depends, function(personal) {
	
	var ajaxErrorfunction = function(reponse, status, info) {
		Portal.loading("close");
		$("#setBasicInfoSubmit").removeAttr("disabled");
		if (reponse.statusText == "timeout") {
			$.alert("error", {
				title : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : "Basic information update failed!"
			});
		}
	};

	var init = function(timezone, codeId) {
		/* 初始化属性设置 */
		$("#timezone").val(timezone);

		$("input[type=radio]").each(function() {
			if ($(this).val() === $(this).attr("field")) {
				$(this).prop("checked", 'checked');
			} else {
				$(this).removeProp("checked");
			}
		});
		
		$("#skype,#mobile").keydown(function(){
			if (event.keyCode == 32) {
				return false;
			}
		}).blur(function(){
			$(this).val($(this).val().replace(/\s/gi,""));
		});

		/* 绑定upload avatar函数 */
		$("#changeAvatar").bind("click", showUploadAvatar);

		$("#birthday").keydown(function(){
			if (event.keyCode == 8) {
				return false;
			}
		});
		
		/* 初始化日期控件 */
		$("#birthday").click(function() {
			WdatePicker({
				qsEnabled : false,
				isShowClear : false,
				isShowOK : false,
				isShowToday : false
			});
		});

		/* 初始化下拉框样式 */
		//$("#timezone,.select2").select2();

		$("#mobilePix ul li").click(function() {
			var o = $(this).children("a").clone();
			o.children("span:eq(0)").remove();
			$("input[name=phoneNationCode]").val(o.children("span.code").text());
			$("input[name=phoneNationId]").val(o.children("span.code").prop("id"));
			$("#mobilePix button .value").html(o.html());
		});

		/* 初始化code选择 */
		if (codeId) {
			$("li[name='" + codeId + "']").click();
		}
		
		/* 初始化输入字符统计 */
		var initValue = $('#introduction').val();
		//initValue = initValue.replace(/\s/gi,"");
		$('#introduction-left').html(800-initValue.length);
		$('#introduction').bind('input propertychange', function() {
			var value = $(this).val();
			//value = value.replace(/\s/gi,"");
		    $('#introduction-left').html(800-value.length);
		});
		
		/* 初始化基本信息表单 */
		setBasicInfoForm();
		$("#setBasicInfoForm").fadeIn();
	};

	/* 基本信息表单 */
	var setBasicInfoForm = function() {
		$("#setBasicInfoSubmit").formValidator({
			scope : "#setBasicInfoForm",
			silent : false
		});

		$("#setBasicInfoForm").ajaxForm({
			beforeSubmit : function() {
				$(".has-error").hide();
				if (vaildFrom()) {
					Portal.loading('open');
					$("#setBasicInfoSubmit").attr("disabled", "disabled");
					return true;
				}
				return false;
			},
			success : function(datas) {
				setBasicInfoSucceed(datas);
			},
			timeout:15000,
			error:function(reponse, status, info){
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};

	var vaildFrom = function() {
		
		var flag = true;
		
		if($("input[name=gender]:checked").size()==0){
			$("#gender-tip").show();
			flag =  false;
		}
		
		if($("input[name=country]:checked").size()==0){
			$("#country-tip").show();
			flag = false;
		}
		
		if($("input[name=phoneNationId]").val()==0){
			$("#mobilePix-tip").show();
			flag = false;
		}
		
		if($("#countryId option:selected").val()=="0"){
			$("#verifyCountryAddress-tip").show();
			flag = false;
		}else if($("#stateId option:selected").val()=="0" && $("#city option:selected").val()=="0"){
			$("#verifyCountryAddress-tip").show();
			flag = false;
		}

		return flag;
	}

	/* 基本信息修改成功函数 */
	var setBasicInfoSucceed = function(datas) {
		datas = $.parseJSON(datas);
		if (datas.action) {
			$.alert("info", {
				title : "Basic information update successful！"
			});
			personal.loadingPage(webPath + "/personal/basicinfo.shtml");
		} else {
			Portal.loading("close");
			$.alert("error", {
				title : "Basic information update failed！"
			});
			$("#setBasicInfoSubmit").removeAttr("disabled");
			$("#teacherName").html(datas.user.name);
		}
	};

	/* 头像上传函数 */
	var uploadAvatarForm = function() {
		$("#uploadAvatarForm").ajaxForm({
			beforeSubmit : function() {
				var file = $("#avatarFile").val();

				/* 文件选择判断 */
				if (Tools.isEmpty(file)) {
					$.alert("error", {
						title : "Please attach your file first!"
					});
					return false;
				}
				Portal.loading("open");
			},
			success : function(datas) {
				Portal.loading("close");
				uploadAvatarSucceed(datas);
			},
			timeout:15000,
			error:function(reponse, status, info){
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};

	/* 头像上传成功函数 */
	var uploadAvatarSucceed = function(datas) {
		datas = $.parseJSON(datas);

		if (datas.uploadResult.result) {
			var url = datas.uploadResult.encodeUrl;//url

			if (!url.startsWith("http://")) {
				url = "http://resource.vipkid.com.cn/" + url;
			}
			$("img.avatarImg").attr("src", url);

			$.alert("info", {
				title : "Avatar change successful！"
			});
		} else {
			$.alert("error", {
				title : "Avatar change failed！"
			});
		}

		$('#teacher_avatar_modal').modal('hide');
	};

	/* 打开上传对话框 */
	var showUploadAvatar = function() {
		Portal.loading("open");
		var url = webPath + "/uploadAvatar.shtml";

		$.ajaxRequest({
			url : url,
			data : {},
			success : function(datas) {
				Portal.loading("close");
				$('#teacher_avatar_modal').remove();

				$("body").append(datas);
				$('#teacher_avatar_modal').modal({
					backdrop : "static",
					show : true
				});
			},
			timeout:15000,
			error:function(reponse, status, info){
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};

	return {
		init : init,
		uploadAvatarForm : uploadAvatarForm
	}
});
