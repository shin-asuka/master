define([ "jquery-form", "jquery-bootstrap", "jquery-load", "tools" ], function() {

	var _timeout = 60 * 1000;

	/** 定义错误函数处理 */
	var feedbackError = function(reponse, status, info) {
		Portal.loading("close");

		if ("timeout" === reponse.status) {
			$.alert("error", {
				title : " Sorry！",
				content : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : " Error！",
				content : "Feedback submission failed."
			});
		}
	};

	/** 事件监听，在feedback加载成功后需要执行 */
	var listener = function() {
		var w_width = ($(window).width()-$("div.trailFeedbackContainer").width())/2;
		$("div.trailFeedbackContainer").css({"left":w_width+"px"})
		/** ** 单击左边箭头关闭 */
		$("div.hideFeedbackButton").click(function() {
			/** 提醒保存
			$.alert('confirm', {
				title : "Prompt",
				content : "Do you want to save?",
				button : "Yes",
				style:{
					"margin-top":"10%",
					"width":"400px"
				},
				callback : function() {
					trailCommentSubmit();
				}
			});
			**/
			hideFeedBack();
		});

		/** * level 选则进入事件 */
		$(".ng-hide-feedback").click(function() {
			$("div#" + $(this).attr("name")).show().siblings().hide();
		});

		/** ** 教室页面右侧DemoReport数据回选 */
		$("select").each(function() {
			var value = $(this).attr("field");
			$(this).find("option").each(function() {
				if ($(this).attr("value") == value) {
					$(this).attr("selected", true);
				} else {
					$(this).removeAttr("selected");
				}
			});
		});

		$("input[name='trialLevelResult']").click(function(){
			if($(this).prop('checked')){
				$("#result_suggest").html($(this).next().text());
			}
		});
		
		$("input[type='radio']").each(function() {
			var field = $(this).attr("field");
			var value = $(this).attr("value");
			if (field == value) {
				$("#result_suggest").html($(this).next().text());
				$(this).prop('checked', true);
			} else {
				$(this).prop('checked', false);
			}
			//
			if($("#teacherFeedbackText").val() != ""){
				$(this).attr("disabled","disabled");
			}
		});
	};

	/** **public 请求页面 */
	var openFeedback = function(isRequire, onlineClassId, studentId) {
		/** * 已经打开直接返回 */
		if ($("div.trailFeedbackContainer").children().length > 0) {
			openShow(isRequire);
			return false;
		}
		/** * 未打开，从后台请求页面,再打开返回 */
		var data = {
			"type" : "required",
			"onlineClassId" : onlineClassId,
			"studentId" : studentId
		};
		var url = webPath + "/feedback.shtml";
		Portal.loading("open");
		$.ajaxRequest({
			url : url,
			data : data,
			success : function(datas) {
				$("div.trailFeedbackContainer").html(datas);
				openShow(isRequire);
				listener();
				openSessionStorage(onlineClassId);
				Portal.loading("close");
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				feedbackError(reponse, status, info);
			}
		});
	};

	/** *public 提交保存整个feedback表单 */
	var trailCommentSubmit = function() {
		/** * feedback页面表单提交函数及验证 */
		var teacherFeedback = $("#teacherFeedbackText").val();
		var lessonDiff = $("#lessonDiff").val();

		if (Tools.isEmpty(teacherFeedback) || Tools.isEmpty(lessonDiff) || 0 == lessonDiff) {
			$.alert("confirm", {
				title : "Prompt",
				content : "Please fill in the required fields in the feedback form!",
				cancel:"OK",
				cancelClass:"primary",
				style : {
					"margin-top" : "10%",
					"width" : "400px"
				}
			});
		} else {
			//TODO : 关闭按钮同样需要提示
			$.alert('confirm', {
				title : "Prompt",
				content : "Do you want to submit?",
				cancel:"No",
				cancelClass:"gray",
				style:{
					"margin-top":"10%",
					"width":"400px"
				},
				button : "Submit",
				callback : function() {
					executeSubmit();
				}
			});
		}
	};
	
	var executeSubmit = function(){
		/** 提交函数 */
		$("#trailFeedbackForm").ajaxSubmit({
			beforeSubmit : function() {
				Portal.loading("open");
				$("button.btn-primary").attr("disabled", "disabled");
			},
			dataType:"json",
			success : function(data) {
				Portal.loading("close");
				$("button.btn-primary").removeAttr("disabled");
				if(data.result){
					$("#" + $("#onlineClassId").val() + " a").html("Feedback");
					/* 重新检查Feedback */
					var id = $("input[name=id]").val();
					checkFeedback(id);
				}else if(data.msg){
					$.alert("error", {
						title : data.msg
					});
					reomveFeedBack();
				}else{
					$.alert("error", {
						title : "Sorry, save failed. Please try again."
					});
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				$("button.btn-primary").removeAttr("disabled");
				feedbackError(reponse, status, info);
			},
			statusCode : $.statusCode
		});
	}

	var checkFeedback = function(id){
		var url = webPath + "/getComment.json";
		Portal.loading("open");
		$.ajaxRequest({
			url : url,
			data : {"id": id},
			success : function(datas) {
				Portal.loading("close");
				datas = jQuery.parseJSON(datas);
				if(datas.status){
					$.alert("confirm", {
						title : "Feedback submitted successfully",
						content : "Submitted content: '" + datas.teacherComment.teacherFeedback + "'",
						cancel:"OK",
						cancelClass:"primary",
						style : {
							"margin-top" : "10%",
							"width" : "600px"
						}
					});
					reomveFeedBack();
				}else{
					$.alert("error", {
						title : "Feedback submitted failed, please try again later!"
					});
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				feedbackError(reponse, status, info);
			}
		});
	};
	
	/** 提交后处理，关闭feedback，清空feedback */
	var reomveFeedBack = function() {
		hideFeedBack();
		$("div.trailFeedbackContainer").empty();
	}

	/** *public 单击Cancel函数 隐藏feedback */
	var hideFeedBack = function() {
		$(".divload").hide();
		$("body").removeClass("modal-open");
		$("div.trailFeedbackContainer").animate({
			top : '-700px',
			opacity : 'hide'
		}, 300);
		$("div.edgingsForTrail > div").removeClass("selected");
	};

	/** *public 单击右侧feedback选项卡单击切换事件 */
	var parentTab = function() {
		$("div.edgingsForTrail > div").click(function() {
			$(this).addClass("selected").siblings().removeClass("selected");
			$("div#" + $(this).attr("name")).show().siblings().hide();
		});
	};

	/** 不请求后台，直接打开并选中feedback，isRequire是需要选中的div ID */
	var openShow = function(isRequire) {
		var obj = $("div." + isRequire);
		$(obj).addClass("selected").siblings().removeClass("selected");
		$("div#" + isRequire).show().siblings().hide();
		$(".divload").show();
		$("body").addClass("modal-open");
		var w_height = ($(window).height()-$("div.trailFeedbackContainer").height())/2;
		w_height = w_height - 10;
		$("div.trailFeedbackContainer").animate({
			top:w_height+"px",
			opacity : 'show'
		}, 300);
	};
	
	var openSessionStorage = function(onlineClassId){
		if(window.sessionStorage){
			var feedbackText = window.sessionStorage.getItem("teacherFeedbackText"+onlineClassId);
			var tipsForOtherTeachers = window.sessionStorage.getItem("tipsForOtherTeachers"+onlineClassId);
			var trialLevelResult = window.sessionStorage.getItem("trialLevelResult"+onlineClassId);
			if(feedbackText){
				$("#teacherFeedbackText").val(feedbackText);
			}
			if(tipsForOtherTeachers){
				$("textarea[name='tipsForOtherTeachers']").val(tipsForOtherTeachers);
			}
			if(trialLevelResult){
				$("input[type='radio']").each(function(){
					var field = trialLevelResult;
					var value = $(this).attr("value");
					if (field == value) {
						$("#result_suggest").html($(this).next().text());
						$(this).prop('checked', true);
					} else {
						$(this).prop('checked', false);
					}
				});
			}
			//清除所有
			window.sessionStorage.removeItem("teacherFeedbackText"+onlineClassId);
			window.sessionStorage.removeItem("tipsForOtherTeachers"+onlineClassId);
			window.sessionStorage.removeItem("trialLevelResult"+onlineClassId);
		}
	};

	return {
		trailCommentClose : hideFeedBack,
		parentTab : parentTab,
		trailCommentSubmit : trailCommentSubmit,
		openFeedback : openFeedback
	}

});