/* 定义Portal模块的公共函数 */
var Portal = Portal || {};

/* 分页对象 */
Portal._pagin = {};

/* Loading对象id */
Portal._loading = new Date().getTime();

/**
 * Loading函数
 * 
 * @action 动作指令："open","close"
 * @require ["jquery", "loading"]
 */
Portal.loading = function(action) {
	$(window).loading({
		action : action,
		id : Portal._loading
	});
};

/* 分页样式定义 */
Portal.pageFormat = function(type) {
	switch (type) {
	case 'block':
		if (this.value == this.page)
			return '<li class="active"> <a href="javascript:;">' + this.value + '</a> </li>';
		else
			return '<li> <a href="javascript:;">' + this.value + '</a> </li>';
	case 'next':
		if (this.active) {
			return '<li> <a href="javascript:;"> <span>»</span> </a> </li>';
		}
		return '<li class="active"> <a href="javascript:;"> <span>»</span> </a> </li>';
	case 'prev':
		if (this.active) {
			return '<li> <a href="javascript:;"> <span>«</span> </a> </li>';
		}
		return '<li class="active"> <a href="javascript:;"> <span>«</span> </a> </li>';
	case 'first':
		return '<li> <a href="javascript:;"> <span>First</span> </a> </li>';
	case 'last':
		return '<li> <a href="javascript:;"> <span>Last</span> </a> </li>';
	case 'fill':
		if (this.active) {
			return '<li> <span class="text">...</span> </li>';
		}
		return '';
	case 'left':
		if (this.active) {
			return '<li> <a href="#' + this.value + '">' + this.value + '</a> </li>';
		}
		return '';
	case 'right':
		if (this.active) {
			return '<li> <a href="#' + this.value + '">' + this.value + '</a> </li>';
		}
		return '';
	}
};

/**
 * 分页函数
 * 
 * @selector jquery选择器
 * @totalLines 总行数
 * @linePerPage 每页行数
 * @callback 回调函数
 * @require ["jquery", "jquery-pagin"]
 */
Portal.paging = function(selector, totalLines, linePerPage, callback) {
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
	} else {
		$.alert("error", {
			title : "No more data！"
		});
	}
};
