define([ "function", "jquery-bootstrap", "jquery-form", "jquery-load" ], function(report) {

	var _timeout = 60 * 1000;
	
	/**定义错误函数处理*/
	var demoReportError = function(reponse, status, info) {
		Portal.loading("close");

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
	
	/** 一般单选框回选 */
	var listener = function() {
		$("#report-Modal input[type='radio']").each(function() {
			var field = $(this).attr("field");
			var value = $(this).attr("value");
			if (field == value) {
				$(this).prop('checked', true);
			} else {
				$(this).prop('checked', false);
			}
		});
	};
	
	/**demoReport打开成功后初始化函数*/
	var openSuccess = function(datas){
		Portal.loading("close");
		if ($('#report-Modal').html()) {
			$('#report-Modal').remove();
		}
		$("body").append(datas);
		$('#report-Modal').modal('show');
	};

	/** DemoReport页面打开事件 */
	var showDemoReport = function(serialNumber, onlineClassId, studentId) {
		Portal.loading("open");
		var url = webPath + "/demoReport.shtml";

		$.ajaxRequest({
			url : url,
			data : {"serialNumber" : serialNumber,"onlineClassId" : onlineClassId,"studentId" : studentId},
			success : function(datas) {
				openSuccess(datas);
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				demoReportError(reponse, status, info);
			}
		});
	};

	/** DemoReport Score等级选择效果函数 */
	var selectLevel = function(id, index) {
		$("span.histogram-box > a").each(function(i) {
			var css = $(this).attr("name");
			if (i < index) {
				$(this).addClass(css);
			} else {
				$(this).removeClass(css);
			}
		});

		$("div#" + id).show().siblings("div.ng-scope").hide();
	};

	/** DemoReport Score等级回选函数 */
	var setLevel = function(level) {
		/** 设置level */
		var inputLevel = $("input[name=level][value=" + level + "]");
		inputLevel.click();
		var parentId = inputLevel.closest("div.ng-scope").attr("id");
		$("a[title=" + parentId + "]").click();
	};

	/** demoReport页面表单提交/保存函数 isSubmited= true 表示提交  false表示仅保存*/
	var reportSubmit = function(isSubmited) {
		if (isSubmited) {
			/** DemoReport等级下的unit选择检查 */
			if (!levelCheck())
				return false;

			$.alert('confirm', {
				title : "Prompt",
				//content : "After submission will not be changed!",
				content : "Are you sure you want to submit?",
				button : "Yes",
				style:{
					"margin-top":"10%",
					"width":"400px"
				},
				callback : function() {
					executeSubmit(isSubmited);
				}
			});
		} else {
			executeSubmit(isSubmited);
		}
	};
	
	/** 表单提交实现函数 */
	var executeSubmit = function(isSubmited) {
		$("#reportForm").ajaxSubmit({
			dataType : "json",
			data : {"isSubmited" : isSubmited},
			beforeSubmit : function() {
				Portal.loading("open");
			},
			success : function(datas) {
				demoReportSucess(datas,isSubmited);
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				demoReportError(reponse, status, info);
			},
			statusCode : $.statusCode
		});
	};

	/** DemoReport等级必选检查，验证 */
	var levelCheck = function() {
		var ng = $("#histogram-container").find("div[style='display: block;']");
		var hts = ng.size();
		if (hts == 0) {
			ng = $("#histogram-container").find("div[style='']");
			hts = ng.size();
		}
		var score = ng.find("input:checked").size();
		if (0 != hts && 0 == score) {
			$.alert("confirm", {
				title : "Prompt",
				content : "Please select the level and unit under the score tab before submitting."
			});
			return false;
		}
		return true;
	};
	
	/**demoReport 提交成功后处理函数*/
	var demoReportSucess = function(datas,isSubmited){
		Portal.loading("close");
		if (datas.result) {
			$('#report-Modal').modal('hide');
			if (isSubmited) {
				/** 修改feedback按钮提交后的状态文本 */
				var classid = $("#onlineClassId").val();
				$("#" + classid + " a").html("Feedback(submitted)");
				$.alert("info", {
					title : " Submitted successfully!"
				});
			} else {
				$.alert("info", {
					title : " Saved successfully!"
				});
			}
			try{
				require("room-demo").reomveReportRoom();
			}catch(e){
				
			}
		} else {
			$.alert("error", {
				title : datas.msg
			});
		}
	};

	return {
		listener:listener,
		showDemoReport : showDemoReport,
		selectLevel : selectLevel,
		setLevel : setLevel,
		reportSubmit : reportSubmit
	}
	
});