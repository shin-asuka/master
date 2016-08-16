define([ "function", "jquery-bootstrap", "jquery-form", "jquery-load", "tools" ], function(report) {

	var _timeout = 60 * 1000;

	/* 上传大小限制，单位KB */
	var _maxsize = 1024;

	var _serialNumber, _studentId, _onlineClassId;
		
	/**定义错误函数处理*/
	var uaReportError = function(reponse, status, info) {
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
	
	/**页面请求成功*/
	var uaReportSucceed = function(datas) {
		if ($('#report-Modal').html()) {
			$('#report-Modal').remove();
		}
		$("body").append(datas);
		$('#report-Modal').modal('show');
		$(".file-title").sizes({size:25});
		listener();
		Portal.loading("close");
	};
	
	/**页面按钮动画1*/
	var uaReportAnimateOver = function() {
		$(this).next().css({
			"background" : "#eee",
			"color" : "#f00",
			"text-decoration" : "none",
			"border" : "#ddd 1px solid"
		});
	};
	
	/**页面按钮动画2*/
	var uaReportAnimateOut = function() {
		$(this).next().removeAttr("style");
	};

	/**页面请求开始*/
	var showUAReport = function(serialNumber, studentId, onlineClassId) {
		Portal.loading("open");
		var url = webPath + "/uploadPage.shtml";

		var data = Tools.newMap();
		data.put("name", serialNumber);
		data.put("studentId", studentId);
		data.put("onlineClassId", onlineClassId);

		_serialNumber = serialNumber;
		_studentId = studentId;
		_onlineClassId = onlineClassId;

		$.ajaxRequest({
			url : url,
			data : data.toParam(),
			success : function(datas) {
				uaReportSucceed(datas);
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				uaReportError(reponse, status, info);
			}
		});
	};
	
	/**Report上传处理错误*/
	var uaReportUploadError = function(reponse, status, info) {
		var msg = "Upload Failed！";
		if (reponse.statusText == "timeout") {
			msg = "Sorry！Server request timed out.";
		}

		Portal.loading("close");
		$(".modal-body").hide();

		$("#failed .msg").html(msg);
		$("#failed").show();
	};
	
	/**Report上传进度处理*/
	var uaReportUploadProgress = function(event, position, total, percentComplete) {
		if (percentComplete < 100) {
			$("#uploadReport").hide();
			$("#text-file").html(percentComplete + "%");
		} else {
			$("#uploadReport").show();
			$("#text-file").html("Attach File");
		}
	};
	
	/**Report上传成功处理*/
	var uaReportUploadSucceed = function(datas) {
		Portal.loading("close");
		$(".modal-body").hide();

		if (datas.result) {
			$("#successful .msg").html(datas.msg);
			$("#successful").show();
			/* 上传成功后更新列表显示状态 */
			$("#" + _onlineClassId + " a").html("UA Report");
		} else {
			$("#failed .msg").html(datas.msg);
			$("#failed").show();
		}
	};

	/**Report上传提交之前*/
	var uaReportBeforeSubmit = function() {
		Portal.loading("open");

		$(".msg").html("");
		$("#setBasicInfoSubmit").attr("disabled", "disabled");
	};

	/**Report上传提交*/
	var uaReportSubmit = function() {
		var filepath = $("#uploadReport").val();
		
		var message = "";		
		/** 文件未选择 */
		if (Tools.isEmpty(filepath)) {
			message += "Please attach your file first!<br/>";
		}else{
			filepath = filepath.substring(filepath.lastIndexOf("."), filepath.length);
			/** 文件格式不正确 */
			var accept = $("#uploadReport").attr("accept");
			if ((accept + ",").indexOf(filepath + ",") < 0) {
				message += "We only support \".docx\" files！<br/>";
			}else{
				/** 文件大小不正确 */
				var fileSize = $("#uploadReport")[0].files[0].size / 1024;
				if (fileSize > _maxsize) {
					message += "The file size can not exceed 1 MB！<br/>";
				}
			}
		}
		
		/**不允许输入0或者空格*/
		var score = $.trim($("#score").val());
		if(score == "" || score == "0"){
			message += "Please enter the assessment score first!<br/>";
		}else{
			/**输入整数判断*/
			var d = /^[0-9]\d*$/i;
			if(!d.test(score) || score.indexOf("0") == 0){
				message += "Report score format incorrect!<br/>";
			}else if(score > 100000){
				message += "Report score format incorrect!<br/>";
			}
		}
		if(message != ""){
			$.alert("error", {title:message});
			return false;
		}
		

		var data = {
			"name" : _serialNumber,
			"studentId" : _studentId
		};

		/** 开始上传 */
		$("#uploadReportForm").ajaxSubmit({
			dataType : "json",
			data : data,
			beforeSubmit : uaReportBeforeSubmit,
			success : function(datas) {
				uaReportUploadSucceed(datas);
			},
			timeout : _timeout,
			uploadProgress : function(event, position, total, percentComplete) {
				uaReportUploadProgress(event, position, total, percentComplete);
			},
			error : function(reponse, status, info) {
				uaReportUploadError(reponse, status, info);
			},
			statusCode : $.statusCode
		});
	};

	var listener = function() {
		/** 文件特效 */
		$("#uploadReport").hover(uaReportAnimateOver, uaReportAnimateOut);

		/* 文件选择 */
		$("#uploadReport").change(function() {
			var name = $(this).val().substring($(this).val().lastIndexOf("\\") + 1);
			$(".file-title").attr("title", name);
			$(".file-title").html(name);
			$(".file-title").sizes({size:25});
		});

		/* 返回上传页面 */
		$("#returnUpload").click(function() {
			$(".modal-body").hide();
			$("#upload").show();
		});

		/* 关闭弹出层，去掉正在进行的Ajax */
		$(".closebutton").click(function() {
			$('#report-Modal').modal('hide');
		});

		
		// 当报告已被审核，前台提示不能修改报告*/
		$("#uploadChangeReoprt").click(function() {
			$.alert("confirm", {
				title : "Error",
				content : "This report has been sent to the parent already so you cannot make changes to it now.",
				style:{
					"width":"450px",
					"top":"50px"
				}
			});	
		});

		/* 上传逻辑实现 */
		$("#uploadReportFormSubmit").click(uaReportSubmit);
	};

	return {
		showUAReport : showUAReport
	}

});