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
		
		/** room ** 单击左边箭头关闭 */
		$("div.hideReportButton").click(function() {
			hideReportRoom();
		});
		
		$("div.reportContainer input[type='radio']").each(function() {
			var field = $(this).attr("field");
			var value = $(this).attr("value");
			if (field == value) {
				$(this).prop('checked', true);
			} else {
				$(this).prop('checked', false);
			}
		});
		$("div.reportContainer select").each(function(){
			var value = $(this).attr("field");
			$(this).find("option").each(function(){
				if($(this).attr("value") == value){
					$(this).attr("selected",true);
				}else{
					$(this).removeAttr("selected");
				}
			});
		});
	};
	
	/** 提交后处理，关闭demoReport，清空demoReport */
	var reomveReportRoom = function() {
		hideReportRoom();
		$("div.reportContainer").empty();
	};

	/** *public 单击Cancel函数 隐藏demoReport */
	var hideReportRoom = function() {
		$("div.reportContainer").animate({
			right : '-450px',
			opacity : 'hide'
		}, 100);
		$("div.edgings > div").removeClass("selected");
	};
	
	/**打开DemoReport*/
	var openShowRoom = function(isRequire){
		var obj = $("div." + isRequire);
		$(obj).addClass("selected").siblings().removeClass("selected");
		$("div#" + isRequire).show().siblings().hide();
		$("div.reportContainer").animate({
			right : '70px',
			opacity : 'show'
		}, 100);
	};
	
	/** 教室页面右侧DemoReport切换效果 */
	var parentTab = function() {
		$("div.edgings > div").click(function() {
			$("div.reportContainer").show();
			$(this).addClass("selected").siblings().removeClass("selected");
			$("div#" + $(this).attr("name")).show().siblings().hide();
		});
	};
	
	/** DemoReport页面打开事件 form room*/
	var openDemoReportRoom = function(serialNumber, onlineClassId, studentId,isRequire) {
		/** * 已经打开直接返回 */
		if($("div.reportContainer").children().length > 0) {
			openShowRoom(isRequire);
			return false;
		}
		
		Portal.loading("open");
		var url = webPath + "/demoReportRoom.shtml";
		$.ajaxRequest({
			url : url,
			data : {"serialNumber" : serialNumber,"onlineClassId" : onlineClassId,"studentId" : studentId},
			success : function(datas) {
				Portal.loading("close");
				$("div.reportContainer").html(datas);
				openShowRoom(isRequire);
				listener();
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				demoReportError(reponse, status, info);
			}
		});
	};
	
	/** DemoReport等级必选检查，验证 */
	var levelCheck = function() {
		var level = $("#level").val();
		if (level == "" || level == 0) {
			$.alert("confirm", {
				title : "Prompt",
				content : "Please select the level and unit under the score tab before submitting."
			});
			return false;
		}
		return true;
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
		$("#reportContainerForm").ajaxSubmit({
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
	
	/**demoReport 提交成功后处理函数*/
	var demoReportSucess = function(datas,isSubmited){
		Portal.loading("close");
		if (datas.result) {
			reomveReportRoom();
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
		} else {
			$.alert("error", {
				title : datas.msg
			});
		}
	};
	
	return {
		hideReportRoom : hideReportRoom,
		reomveReportRoom:reomveReportRoom,
		reportSubmit:reportSubmit,
		parentTab:parentTab,
		openDemoReportRoom : openDemoReportRoom
	}
	
});