define([ "function", "tools", "jquery-bootstrap", "jquery-cookie", "jquery-load" ], function() {

	var _timeout = 60 * 1000;

	/* 分页变量 */
	var _offsetOfWeek = 0;
	
	var _show24HoursInfo = false;

	/* 当前星期 */
	var currWeek = function() {
		$("input[name=offsetOfWeek]").val(0);
		$("#scheduleForm").submit();
	};

	/* 下一星期 */
	var nextWeek = function() {
		_offsetOfWeek += 1;
		$("input[name=offsetOfWeek]").val(_offsetOfWeek);
		$("#scheduleForm").submit();
	};

	/* 上一星期 */
	var prevWeek = function() {
		_offsetOfWeek -= 1;
		$("input[name=offsetOfWeek]").val(_offsetOfWeek);
		$("#scheduleForm").submit();
	};

	/** 定义错误函数处理 */
	var backError = function(reponse, status, info) {
		$(".divload").remove();
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

	var init = function(offsetOfWeek, showPracticum,showAdminQuiz,show24HoursInfo) {
		$.removeCookie("from", {
			path : '/'
		});

		if ($.cookie("TRPM_CHANGE_WINDOW")) {
			$("#password-Modal").remove();
			$.ajaxRequest({
				url : webPath + "/changePassword.shtml",
				success : function(datas) {
					$("#dialog").append(datas);
					$('#password-Modal').modal({
						backdrop : "static",
						show : true,
						keyboard : false
					});
					$(".divload").fadeOut(100).remove();
				},
				timeout : _timeout,
				error : function(reponse, status, info) {
					backError(reponse, status, info);
				}
			});
		} else {
			$(".divload").remove();
		}

		_offsetOfWeek = offsetOfWeek;
		/* 初始化tooltip工具 */
		initTooltip();
		/* 初始化popover工具 */
		initPopover();

		/* 绑定tab切换函数 */
		$("#courses").bind("click", courses);
		$("#practicum").bind("click", practicum);

		/* 绑定doBook函数 */
		$("div[data-schedule]").bind("click", function(event) {
			doBook($(this));
			event.stopPropagation();
		});

		/* 绑定do24Hour */
		$("input.24hours").bind("click", function(event) {
			do24Hour($(this), event);
		});
		_show24HoursInfo = show24HoursInfo;

		/* 绑定星期切换函数 */
		$("button.next").bind("click", nextWeek);
		$("button.curr").bind("click", currWeek);
		$("button.prev").bind("click", prevWeek);

		/* 是否显示提示层 */
		tipsLayer(showAdminQuiz,showPracticum);
	};

	/* 提示层函数 */
	var tipsLayer = function(showAdminQuiz,showPracticum) {
		if(showAdminQuiz){
			showAdminQuizFun(showAdminQuiz,showPracticum);
		}else{
			if(showPracticum){
				showPracticumFun(showPracticum);
			}
		}
	};
	
	var showAdminQuizFun = function(showAdminQuiz,showPracticum){
		if (showAdminQuiz) {
			$("div.admin-quiz-tips").show();
			$("body").addClass("modal-open");
			$("div.sure-btn").click(function() {
				$.ajaxRequest({
					url : webPath + "/disableLayer.json",
					dataType : "json",
					data : {
						"loginType" : 2
					},
					success : function(datas) {
						$("div.admin-quiz-tips").hide();
						$("body").removeClass("modal-open");
						showPracticumFun(showPracticum);
						if (undefined !== console) {
							console.log("disable adminquiz layer.");
						}
					}
				});
			});
		}	
	};
	
	var showPracticumFun = function(showPracticum){
		if (showPracticum) {
			$("div.schedule-guide").show();
			$("body").addClass("modal-open");
			$("div.schedule-guide-img > a").click(function() {
				$.ajaxRequest({
					url : webPath + "/disableLayer.json",
					dataType : "json",
					data : {
						"loginType" : 1
					},
					success : function(datas) {
						$("div.schedule-guide").hide();
						$("body").removeClass("modal-open");
						if (undefined !== console) {
							console.log("disable schedule layer.");
						}
					}
				});
			});
		}	
	};

	/* 查找PRACTICUM的单元格 */
	var getPracticumCell = function(selector) {
		var cellHeaderTD = $(selector).closest("div.schedule_td");
		var cellHeaderTR = cellHeaderTD.closest("div.schedule_tr");

		var headerTDIdx = parseInt(cellHeaderTD.attr("index"));
		var footerTRIdx = parseInt(cellHeaderTR.attr("index")) + 1;

		var cellFooterTR = $("div.schedule_tr[index=" + footerTRIdx + "]");
		var cellFooterTD = null;
		if (null !== cellFooterTR) {
			cellFooterTD = cellFooterTR.find("div.schedule_td[index=" + headerTDIdx + "]");
		}

		return {
			header : cellHeaderTD,
			footer : cellFooterTD
		};
	};

	var initTooltip = function() {
		$('[data-toggle="tooltip"]').tooltip({
			container : 'body',
			delay : {
				"show" : 300,
				"hide" : 10
			},
			title : function() {
				return $(this).children("div.timeInfo").html();
			}
		});
	};

	var initPopover = function() {
		$('[data-toggle="popover"]').popover({
			container : 'body',
			delay : {
				"show" : 10,
				"hide" : 10
			},
			trigger : 'click',
			content : function() {
				var id = "#" + $(this).prop("id") + "-info";
				return $(this).find(id).html();
			}
		}).click(function(event) {
			event.stopPropagation();
		});

		$('[data-toggle="popover"]').on("show.bs.popover", function() {
			var that = $(this);
			$('[data-toggle="popover"]').each(function() {
				if (that.attr("id") !== $(this).attr("id")) {
					$(this).popover("hide");
				}
			});
			/* 销毁tooltip特效 */
			$('[data-toggle="tooltip"]').tooltip('destroy');
		});

		$('[data-toggle="popover"]').on("hide.bs.popover", function() {
			/* 重新初始化tooltip特效 */
			initTooltip();
		});

		$(document).click(function() {
			$('[data-toggle="popover"]').popover("hide");
		});
	};

	/* 显示courses schedule */
	var courses = function() {
		$("input[name=courseType]").val("MAJOR");
		$("input[name=offsetOfWeek]").val(0);
		$("#scheduleForm").submit();
	};

	/* 显示practicum schedule */
	var practicum = function() {
		$("input[name=courseType]").val("PRACTICUM");
		$("input[name=offsetOfWeek]").val(0);
		$("#scheduleForm").submit();
	};

	/* 处理timeSlot函数 */
	var doBook = function(currObj) {
		var scheduleTime = currObj.attr("data-schedule");
		var onlineClassId = currObj.attr("data-id");

		if (currObj.hasClass("empty") || currObj.hasClass("peakTime")) {
			doCreate(currObj, scheduleTime);
		} else if (currObj.hasClass("available") || !Tools.isUndefined(onlineClassid)) {
			doCancel(currObj, scheduleTime, onlineClassId);
		}
	};

	/* 显示加载中函数 */
	var showMask = function(currObj, height) {
		var width = $(currObj).width();
		var position = $(currObj).closest("div.schedule_td").position();
		/* 生产随机id */
		var _id = new Date().getTime();

		var _div = $("<div>", {
			class : "schedule_mask",
			id : _id
		}).css({
			"left" : position.left,
			"top" : position.top
		});

		_div.width(width).height(height).appendTo("body");

		return _id;
	};

	/* 不能放置Practicum错误提示 */
	var disabledPlaceError = function() {
		$.alert("error", {
			title : "Sorry！ ",
			content : "You can't place a practicum time slot here."
		});
	};

	/* 验证单元格是否可以放置PRACTICUM的TimeSlot */
	var validatePracticum = function(currObj, courseType) {
		if ("PRACTICUM" === courseType) {
			var practicumCell = getPracticumCell(currObj);

			if (null === practicumCell.footer
					|| 0 === $(practicumCell.footer).find("div.empty[data-schedule]").size()) {
				disabledPlaceError();
				return false;
			}
		}

		return true;
	};

	/* 处理TimeSlot的创建 */
	var doCreate = function(currObj, scheduleTime) {
		var url = webPath + "/createTimeSlot.json", courseType = $("#courseType").val();

		/* 验证单元格是否可以放置PRACTICUM的TimeSlot */
		if (!validatePracticum(currObj, courseType)) {
			return;
		}

		/* 获取当前对象的高度 */
		var height = $(currObj).height() + 1;
		if ("PRACTICUM" === courseType) {
			height *= 2;
		}
		/* 显示加载中 */
		var _id = showMask(currObj, height);

		var params = Tools.newMap();
		params.put("scheduleTime", scheduleTime);
		params.put("courseType", courseType);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : params.toParam(),
			success : function(datas) {
				if (datas.action) {
					if (currObj.hasClass("peakTime")) {
						currObj.wrap("<div class='peakTime'></div>");
					}
					currObj.removeClass().addClass("available");
					currObj.attr("data-id", datas.onlineClassId);

					var span = $("<span>" + datas.timePoint + "&nbsp;</span>");
					
					if ("MAJOR" === courseType) {
						var checkbox = $("<input type='checkbox' class='24hours' value='"
								+ datas.onlineClassId + "' />");
						checkbox.bind("click", function(event) {
							do24Hour($(this), event);
						});
						span.append(checkbox);
					}
					
					currObj.append(span);

					if ("PRACTICUM" === courseType) {
						currObj.addClass("practicumSlot").css({
							"z-index" : "999",
							"position" : "relative"
						});
					}
				} else if (datas.disabledPlaceErr) {
					disabledPlaceError();
				}

				/* 移除加载中 */
				$("#" + _id).remove();
			}
		});
	};

	var do24Hour = function(curObj, event) {
		event.stopPropagation();
		if (curObj.prop("checked") == true) {
			set24Hour(curObj);
		} else {
			delete24Hour(curObj);
		}
	};

	var set24Hour = function(curObj) {
		if (_show24HoursInfo) {
			_show24HoursInfo = false;
			
			alert("NOTICE: Only make a timeslot available to be booked within 24 hours if you are able to teach this class as there will be no further reminder to you when booked. If this class is booked and you are not present, this will be a TEACHER_NO_SHOW.");
		} 
		add24Hour(curObj);
	};
	
	var add24Hour = function(curObj){
		var url = webPath + "/set24Hour.json";

		var params = Tools.newMap();
		params.put("onlineClassId", curObj.val());
		params.put("offsetOfWeek", _offsetOfWeek);
		
		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : params.toParam(),
			success : function(datas) {
				if (datas.result) {
					curObj.prop("checked", true);
					$.alert("info", {
						title : "This timeslot is now able to be booked within 24 hours with no further reminder."
					});
				} else if(datas.less15Error) {
					alert("You can make a timeslot available to be booked within 24 hours after you have opened no less than 15 timeslots.");
					curObj.prop("checked", false);
				} else if (datas.lessOneHourError) {
					alert("The scheduled start time is too soon! Please select another timeslot.");
					curObj.prop("checked", false);
				} else {
					$.alert("error", {
						title : "Server request error, please try again later."
					});
					curObj.prop("checked", false);
				}
			},
			error : function(){
				alert("Server request error, please try again later.");
				curObj.prop("checked", false);
			}
		});
	};

	var delete24Hour = function(curObj) {
		var url = webPath + "/delete24Hour.json";

		var params = Tools.newMap();
		params.put("onlineClassId", curObj.val());

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : params.toParam(),
			success : function(datas) {
				if (datas.result) {
					curObj.prop("checked", false);
					$.alert("info", {
						title : "This timeslot will not be booked within 24 hours without your permission."
					});
				} else {
					$.alert("error", {
						title : "Server request error, please try again later."
					});
					curObj.prop("checked", true);
				}
			},
			error : function(){
				alert("Server request error, please try again later.");
				curObj.prop("checked", true);
			}
		});
	};

	/* 处理TimeSlot的取消 */
	var doCancel = function(currObj, scheduleTime, onlineClassId) {
		var url = webPath + "/cancelTimeSlot.json";
		var courseType = $("#courseType").val();

		var params = Tools.newMap();
		params.put("scheduleTime", scheduleTime);
		params.put("onlineClassId", onlineClassId);
		params.put("courseType", courseType);

		/* 获取当前对象的高度 */
		var height = $(currObj).height() + 1;
		/* 显示加载中 */
		var _id = showMask(currObj, height);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : params.toParam(),
			success : function(datas) {
				if (datas.action) {
					currObj.removeClass().addClass("empty");
					currObj.removeAttr("data-id");
					currObj.children("span").remove();

					if ("PRACTICUM" === courseType) {
						$('[data-toggle="tooltip"]').tooltip('hide');
						currObj.css({
							"z-index" : "1",
							"position" : "inherit"
						});
					}
				} else if (datas.peakTimeErr) {
					/* PeakTime错误提示 */
					$.alert("error", {
						title : "Please schedule at least 15 time slots in peak time."
					});
				} else if (datas.statusErr) {
					/* TimeSlot状态错误提示 */
					$.alert("error", {
						title : "Sorry！ ",
						content : "You can't remove this time slot. Please refresh your page."
					});
				}

				/* 移除加载中 */
				$("#" + _id).remove();
			}
		});
	};

	return {
		init : init
	};

});