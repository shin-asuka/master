var depends = [ "function", "jquery-pagin", "avalon", "moment-timezone", "jquery-load",
		"jquery-sort", "jquery-bootstrap", "tools", "jquery-cookie" ];
var default_dateFormat = "yyyy-MM-dd HH:mm";
define(depends, function() {

	/* 视图模型 */
	var _model = null;

	/* 时区变量 */
	var _tz = null;

	/* 月份偏移量变量 */
	var _month = 0;

	/* 初始分页变量 */
	var _initPage = 1;


	//顶部下拉事件
	$("#comments-content").find(".dropdown-toggle").click(function(){
		var hrefId = jQuery(this).attr("href");
		console.info("href = "+hrefId);
		if(jQuery(hrefId).hasClass("in")){
			$(this).children("b").removeClass('upCaret').addClass('downCaret');
		}else{
			$(this).children("b").removeClass('downCaret').addClass('upCaret');
		}
	});

	/**
	 * 初始化函数
	 * 
	 * @tz timezone
	 * @month 月份偏移量
	 */
	var init = function(tz, month) {
		/* 时区设置 */
		_tz = tz;
		/* 月份偏移量设置 */
		_month = month;
	

		/* 绑定tab切换函数 */
		$("#salary").bind("click", salary);
		$("#priceList").bind("click", price);

		/* 绑定月份切换函数 */
		$("button.next").bind("click", nextMonth);
		$("button.curr").bind("click", currMonth);
		$("button.prev").bind("click", prevMonth);
		
		
		//处理Vipkid 日期
		
		var dateObj = jQuery('[showType="date"]');
		dateObj.each(function(){
			var dateFormat = default_dateFormat;
			var time = jQuery(this).attr("value");
			var fm = jQuery(this).attr("format");
			if(fm !=null  && fm !=''){
				dateFormat = fm;
			}
			var dateStr = "";
			if(time!=null && time!=''){
				time = Number(time);
				dateStr = DateFormat(new Date(time),dateFormat);
				jQuery(this).html(dateStr);
			}
		});
		
		
	};

	

	/* 格式化时间毫秒 */
	avalon.filters.formatDate = function(val) {
		var moment = require('moment');
		return moment(val).tz(_tz).format('MMM DD YYYY, hh:mmA');
	};

	

	/* 设置分页列表请求参数 */
	var listParams = function(page) {
		var data = Tools.newMap();
		data.put("offsetOfMonth", $("input[name=offsetOfMonth]").val());

		if (1 !== _initPage) {
			data.put("pageNo", _initPage);
		} else {
			data.put("pageNo", page);
		}

		return data;
	};

	/*  */
	var preSalaryList = function(page) {
		Portal.loading("open");

		var url = webPath + "/salaryList.json";
		var data = listParams(page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				loadPage(".paginationCo", datas.courseTotal, 15, function(page) {
					salaryList(page);
				});
				loadPage(".paginationDe", datas.deTotal, 15, function(page) {
					
				});
				render("course-list-controller", datas);
			}
		});
		
		/**
		 * 分页函数
		 * 
		 * @selector jquery选择器
		 * @totalLines 总行数
		 * @linePerPage 每页行数
		 * @callback 回调函数
		 * @require ["jquery", "jquery-pagin"]
		 */
		var loadPage = function(selector, totalLines, linePerPage, callback) {
			if (0 !== totalLines) {
				Portal._pagin = $(selector).paging(totalLines, {
					format : '< (qq-) nncn (-pp) >',
					perpage : linePerPage,
					page : 1,
					onSelect : function(page) {
						callback(page);
					},
					onFormat : function(type) {
						return Portal.pageFormat.call(this, type);
					}
				});
			}
		};
	};
	
	var doSourceData = function(dataList){
		var dateFormat = default_dateFormat;
		if(dataList == null){
			return dataList;
		}
		for(var i = 0 ; i < dataList.length; i++){
			var data = dataList[i];
			var scheduledTime = data["scheduledTime"];
			if(scheduledTime!=null){
				var scheduledTimeData = new Date(scheduledTime);
				data["scheduledTime"] = DateFormat(scheduledTimeData,dateFormat);
			}
			if(data["totalSalary"]!=null && data["totalSalary"]!=''){
				data["totalSalary"] = data["totalSalary"]/100.0;
			}
			data["shortNotice"] = data["shortNotice"]==1?"Yes":"No";
		}
		return dataList;
	};
	
	var salaryList = function(page) {
		Portal.loading("open");

		var url = webPath + "/salaryList.json";
		var data = listParams(page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				if(datas!=null){
					doSourceData(datas.dataList);
					doSourceData(datas.deDataList);
				}
				//do records 
				if(datas!=null && datas.courseTotal!=null && datas.courseTotal!=''){
					jQuery('#courses_records_id').html(datas.courseTotal);
				}else{
					jQuery('#courses_records_id').html(0);
				}
				if(datas!=null && datas.deTotal!=null && datas.deTotal!=''){
					jQuery('#Course_deductions_records_id').html(datas.deTotal);
				}else{
					jQuery('#Course_deductions_records_id').html(0);
				}
				render("course-list-controller", datas);
			}
		});
	};
	
	
	/*  */
	var priceList = function(page) {
		Portal.loading("open");

		var url = webPath + "/priceList.json";
		var data = listParams(page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				render("price-list-controller", datas);
			}
		});
	};

	/* 呈现模板 */
	var render = function(id, datas) {
		if (null === _model) {
			_model = avalon.define({
				$id : id,
				dataList : datas.dataList,
				deDataList : datas.deDataList
				
			});
		} else {
			_model.dataList = datas.dataList;
			_model.deDataList = datas.deDataList;
		}
		/*设置显示标题 */
		//setTitle(datas.monthOfYear);
		/*模板渲染 */
		avalon.scan();
		Portal.loading("close");
	};

	/* 设置classrooms显示的月份标题 */
	var setTitle = function(title) {
		$("#yearMonthTitle").html(title);
	};

	/* 显示salary */
	var salary = function() {
		$("input[name=payrollType]").val("SALARY");
		$("input[name=offsetOfMonth]").val(0);
		$("#payrollForm").submit();
	};

	/* 显示 priceList */
	var price = function() {
		$("input[name=payrollType]").val("PRICELIST");
		$("input[name=offsetOfMonth]").val(0);
		$("#payrollForm").submit();
	};

	/* 当前月份 */
	var currMonth = function() {
		$("input[name=offsetOfMonth]").val(0);
		$("#payrollForm").submit();
	};

	/* 下一月份 */
	var nextMonth = function() {
		_month += 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#payrollForm").submit();
	};

	/* 上一月份 */
	var prevMonth = function() {
		_month -= 1;
		$("input[name=offsetOfMonth]").val(_month);
		$("#payrollForm").submit();
	};
	
	var showDetials = function(obj){
		$('#popWindow').show();
		$('#maskLayer').show();
		$('.detailInfo').html($(obj).attr("content")) ;
	
	};
	var closeDetials = function(details){
		$('#popWindow').hide();
		$('#maskLayer').hide();
		
	
	};

	return {
		init : init,
		salaryList : salaryList,
		preSalaryList : preSalaryList,
		priceList : priceList,
		showDetials : showDetials,
		closeDetials:closeDetials,
	};

});

function DateFormat(date , fmt){
	  var o = {
		"M+" : date.getMonth() + 1, //月份   
		"d+" : date.getDate(), //日   
		"h+" : date.getHours(), //小时   
		"H+" : date.getHours(), //小时  
		"m+" : date.getMinutes(), //分   
		"s+" : date.getSeconds(), //秒   
		"q+" : Math.floor((date.getMonth() + 3) / 3), //季度   
		"S" : date.getMilliseconds() //毫秒   
	};
	if (/(y+)/.test(fmt)){
		fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for ( var k in o){
		if (new RegExp("(" + k + ")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]): (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
	return fmt;   
}


