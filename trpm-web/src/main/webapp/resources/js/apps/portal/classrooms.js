var depends = [ "function", "jquery-pagin", "avalon", "moment-timezone", "jquery-load",
		"jquery-sort", "jquery-bootstrap", "tools", "jquery-cookie" ];
define(depends, function() {

	/* 视图模型 */
	var _model = null;

	/* 时区变量 */
	var _tz = null;

	/* 月份偏移量变量 */
	var _month = 0;

	/* 初始分页变量 */
	var _initPage = 1;

	var _finishType = {
		"STUDENT_NO_SHOW" : "CANDIDATE_NO_SHOW",
		"STUDENT_IT_PROBLEM" : "CANDIDATE_IT_PROBLEM",
		"SYSTEM_PROBLEM" : "SYSTEM_PROBLEM",
		"TEACHER_NO_SHOW" : "PRACTICUM_GIVER_NO_SHOW",
		"TEACHER_IT_PROBLEM" : "PRACTICUM_GIVER_IT_PROBLEM",
		"TEACHER_CANCELLATION" : "PRACTICUM_GIVER_CANCELLATION"
	};

	/**
	 * 初始化函数
	 * 
	 * @tz timezone
	 * @month 月份偏移量
	 */
	var init = function(tz, month, initPage, showLayer) {
		/* 时区设置 */
		_tz = tz;
		/* 月份偏移量设置 */
		_month = month;
		/* 初始分页设置 */
		_initPage = initPage;

		/* 绑定tab切换函数 */
		$("#courses").bind("click", courses);
		$("#practicum").bind("click", practicum);

		/* 绑定月份切换函数 */
		$("button.next").bind("click", nextMonth);
		$("button.curr").bind("click", currMonth);
		$("button.prev").bind("click", prevMonth);

		/* 是否显示提示层 */
		tipsLayer(showLayer);
		
		//js 替换统计名称~
		$(".count-title").each(function(){
			var html = _finishType[$.trim($(this).html())];
			if(html != undefined ){
				$(this).html(html);
			}
		});
		
	};

	/* 提示层函数 */
	var tipsLayer = function(showLayer) {
		var url = webPath + "/disableLayer.json", classroomsType = 0;

		if (showLayer) {
			$("div.schedule-guide").show();
			$("body").addClass("modal-open");
			
			$("div.classroom-guide-img > a").click(function() {
				$.ajaxRequest({
					url : url,
					dataType : "json",
					data : {
						"loginType" : classroomsType
					},
					success : function(datas) {
						$("div.schedule-guide").hide();
						$("body").removeClass("modal-open");
						if (undefined !== console) {
							console.log("disable classrooms layer.");
						}
					}
				});
			});
		}
	};

	/* 格式化时间毫秒 */
	avalon.filters.formatDate = function(val) {
		var moment = require('moment');
		return moment(val).tz(_tz).format('MMM DD YYYY, hh:mmA');
	};

	/* 格式化shortNotice显示 */
	avalon.filters.formatShortNotice = function(val) {
		if (val) {
			return "Yes";
		} else {
			return "No";
		}
	};

	/* 格式化status显示 */
	avalon.filters.formatStatus = function(val) {
		if (val !== 'INVALID') {
			return val;
		} else {
			return "FINISHED";
		}
	};

	/* 格式化isPaidTrail显示 */
	avalon.filters.formatIsPaidTrail = function(val) {
		if (val) {
			return "| New VIPKID!";
		}
	};

	avalon.filters.formatType = function(val) {
		var type = _finishType[val];
		return type ? type : val;
	};

	/* 格式化classroom显示 */
	avalon.filters.formatClassroom = function(val) {
		if (val.startsWith("OPEN")) {
			return "Open-Class";
		} else {
			return "Classroom";
		}
	};

	/* 显示Report链接 */
	avalon.filters.showReport = function(el) {
		var url = webPath + "/showReport.shtml";

		var data = Tools.newMap();
		data.put("serialNumber", el.serialNumber);
		data.put("onlineClassId", el.id);
		data.put("lessonId", el.lessonId);
		data.put("studentId", el.studentId);
		data.put("scheduledTime", el.scheduledDateTime);

		$.ajaxRequest({
			url : url,
			data : data.toParam(),
			success : function(datas) {
				$("#" + el.id).html(datas);
			}
		});
	};

	/* 设置分页列表请求参数 */
	var listParams = function(page) {
		var data = Tools.newMap();
		data.put("offsetOfMonth", $("input[name=offsetOfMonth]").val());

		if (1 !== _initPage) {
			data.put("curPage", _initPage);
		} else {
			data.put("curPage", page);
		}

		return data;
	};

	/* 获取MAJOR课程分页列表 */
	var majorList = function(page) {
		Portal.loading("open");

		var url = webPath + "/majorList.json";
		var data = listParams(page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				render("major-list-controller", datas);
			}
		});
	};

	/* 获取PRACTICUM课程分页列表 */
	var practicumList = function(page) {
		Portal.loading("open");

		var url = webPath + "/practicumList.json";
		var data = listParams(page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				render("practicum-list-controller", datas);
			}
		});
	};

	/* 呈现模板 */
	var render = function(id, datas) {
		if (null === _model) {
			_model = avalon.define({
				$id : id,
				dataList : datas.dataList,
				materials : showMaterials
			});
		} else {
			_model.dataList = datas.dataList;
		}
		/* 设置显示标题 */
		setTitle(datas.monthOfYear);
		/* 模板渲染 */
		avalon.scan();
		/* 设置表格排序 */
		$("#roomsTable").sort();

		/* 处理初始化分页定位 */
		if (1 !== _initPage) {
			Portal._pagin.setPage(_initPage, true);
			_initPage = 1;
		}
	};

	/* 设置classrooms显示的月份标题 */
	var setTitle = function(title) {
		$("#yearMonthTitle").html(title);
	};

	/* 显示courses classrooms */
	var courses = function() {
		$("input[name=courseType]").val("MAJOR");
		$("input[name=offsetOfMonth]").val(0);
		$("#classroomsForm").submit();
	};

	/* 显示practicum classrooms */
	var practicum = function() {
		$("input[name=courseType]").val("PRACTICUM");
		$("input[name=offsetOfMonth]").val(0);
		$("#classroomsForm").submit();
	};

	/* 显示materials */
	var showMaterials = function(lessonId, serialNumber) {
		Portal.loading("open");
		var url = webPath + "/showMaterials.shtml";

		var data = Tools.newMap();
		data.put("lessonId", lessonId);
		data.put("serialNumber", serialNumber);

		$.ajaxRequest({
			url : url,
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				$("body").append(datas).addClass("modal-open");
				setMaterials();
			}
		});
	};

	/* 设置materials分页 */
	var setMaterials = function() {
		var prev = $("div.aside-body:visible").prev().attr("id");
		var next = $("div.aside-body:visible").next().attr("id");

		if (undefined === prev) {
			prev = $("div.aside-body:first").attr("id");
		}

		if (undefined === next) {
			next = $("div.aside-body:last").attr("id");
		}

		$("#prev-sn").html("Prev: <span>" + prev + "</span>");
		$("#next-sn").html("Next: <span>" + next + "</span>");
	};

	/* 关闭materials课程列表 */
	var closeMaterials = function() {
		$("#materials-content").remove();
		$("body").removeClass("modal-open");
	};

	/* 显示设置materials课程列表分页 */
	var changeMaterials = function(el) {
		var sn = $(el).children("span").html();
		$("#" + sn).show().siblings("div.aside-body").hide();
		setMaterials();
	};

	/* 当前月份 */
	var currMonth = function() {
		$("input[name=offsetOfMonth]").val(0);
		$("#classroomsForm").submit();
	};

	/* 下一月份 */
	var nextMonth = function() {
		_month += 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#classroomsForm").submit();
	};

	/* 上一月份 */
	var prevMonth = function() {
		_month -= 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#classroomsForm").submit();
	};

	return {
		init : init,
		majorList : majorList,
		practicumList : practicumList,
		closeMaterials : closeMaterials,
		changeMaterials : changeMaterials
	};

});
