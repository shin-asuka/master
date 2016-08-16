var depends = [ "function", "jquery-pagin", "avalon", "moment-timezone", "jquery-load",
		"jquery-bootstrap", "tools" ];
define(depends, function() {

	/* 视图模型 */
	var _model = null;

	/* 时区变量 */
	var _tz = null;

	/* 月份偏移量变量 */
	var _month = 0;

	/* 初始化函数 */
	var init = function(tz, month) {
		/* 时区设置 */
		_tz = tz;
		/* 月份偏移量设置 */
		_month = month;

		/* 绑定月份切换函数 */
		$("button.next").bind("click", nextMonth);
		$("button.curr").bind("click", currMonth);
		$("button.prev").bind("click", prevMonth);

		/* 自适应内容 */
		resizeContent();
		$(window).resize(function() {
			resizeContent();
		});
	};

	/* 自适应内容函数 */
	var resizeContent = function() {
		var height = $(window).height() - 300;
		$("#comments-content").attr(
				"style",
				"overflow-y:socll;height:" + height
						+ "px;min-height:350px;overflow-x:auto;border-bottom:1px solid #eee;");
	}

	/* 格式化时间毫秒 */
	avalon.filters.formatDate = function(val) {
		var moment = require('moment');
		return moment(val).tz(_tz).format('MMM DD YYYY, hh:mmA');
	};

	avalon.filters.formatHtml = function(val){
		if(val == null || val == undefined || val == "" ){
			return "";
		}
		val = val.replace(/\&gt;/gi,">");
		val = val.replace(/\&lt;/gi,"<");
		val = val.replace(/\&amp;/gi,"&");
		val = val.replace(/\&quot;/gi,"\"");
		val = val.replace(/\&nbsp;/gi," ");
		return val;
	};
	
	/* comments分页函数 */
	var commentsList = function(page) {
		Portal.loading("open");
		var url = webPath + "/commentsList.json";

		var data = Tools.newMap();
		data.put("offsetOfMonth", $("input[name=offsetOfMonth]").val());
		data.put("curPage", page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				if (null === _model) {
					_model = avalon.define({
						$id : "comments-list-controller",
						dataList : datas.dataList
					});
				} else {
					_model.dataList = datas.dataList;
				}
				/* 模板渲染 */
				avalon.scan();
			}
		});
	};

	/* 当前月份 */
	var currMonth = function() {
		$("input[name=offsetOfMonth]").val(0);
		$("#commentsForm").submit();
	};

	/* 下一月份 */
	var nextMonth = function() {
		_month += 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#commentsForm").submit();
	};

	/* 上一月份 */
	var prevMonth = function() {
		_month -= 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#commentsForm").submit();
	};

	return {
		init : init,
		commentsList : commentsList
	};

});