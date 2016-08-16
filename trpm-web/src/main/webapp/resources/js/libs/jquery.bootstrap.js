(function($) {

	/* bootstrap alert扩展 */
	/* alert标题 */
	var _title = $("<strong>");

	/* alert内容 */
	var _content = $("<span>");

	/* alert容器 */
	var _alert = $("<div>", {
		class : "alert fade in hides"
	});

	/* modal容器 */
	var _modal = $("<div>", {
		class : "modal fade",
		role : "dialog"
	});

	/* modal对话框 */
	var _model_dialog = $("<div>", {
		class : "modal-dialog"
	});

	/* modal内容容器 */
	var _model_content = $("<div>", {
		class : "modal-content"
	});

	/* modal头部 */
	var _model_header = $("<div>", {
		class : "modal-header"
	});

	/* modal头部关闭按钮 */
	var _model_header_close = $("<button>", {
		class : "close modal-close",
		type : "button",
		"data-dismiss" : "modal",
		"aria-label" : "Close",
		html : "<span aria-hidden='true'>×</span>"
	});

	/* modal头部标题 */
	var _model_title = $("<h4>", {
		class : "modal-title"
	});

	/* modal内容体 */
	var _model_body = $("<div>", {
		class : "modal-body"
	});

	/* modal尾部 */
	var _model_footer = $("<div>", {
		class : "modal-footer"
	});

	/* modal尾部取消按钮 */
	var _model_footer_cancel = $("<button>", {
		class : "btn btn-warning",
		type : "button",
		"data-dismiss" : "modal",
		html : "Cancel"
	});

	/* modal尾部自定义按钮 */
	var _model_footer_button = $("<button>", {
		class : "btn btn-primary",
		type : "button"
	});

	/* 设置alert标题 */
	var setTitle = function(title, alert) {
		_title.clone().html(title).appendTo(alert);
	};

	/* 设置alert内容 */
	var setContent = function(content, alert) {
		_content.clone().html(content).appendTo(alert);
	};

	/* alert主函数 */
	var alert = function(clazz, title, content) {
		$("div.alert").remove();
		var o = _alert.clone().addClass(clazz);

		setTitle(title, o);
		setContent(content, o);

		o.fadeIn().delay(6000).fadeOut("normal", function() {
			$(this).remove();
		}).appendTo("body");
		
		var width = o.width()/2 + 15;
		o.css({
			"margin-left":"-"+width+"px"
		});
	};

	/* 注册jQuery函数 */
	$.alert = function(type, message) {
		var method = method || {};

		if (undefined === message.title) {
			message.title = "";
		}

		if (undefined === message.content) {
			message.content = "";
		}

		/* alert info函数 */
		method["info"] = function() {
			alert("alert-success", message.title, message.content);
		};

		/* alert error函数 */
		method["error"] = function() {
			alert("alert-danger", message.title, message.content);
		};

		/* alert confirm函数 */
		method["confirm"] = function() {
			/* 设置标题和内容 */
			var _m_title = _model_title.clone().html(message.title);
			var _m_body = _model_body.clone().html(message.content);

			var _m_header = _model_header.clone();
			var _m_content = _model_content.clone();
			var _m_footer = _model_footer.clone();

			var _m = _modal.clone();
			var _m_dialog = _model_dialog.clone();

			var _cancel = _model_footer_cancel.clone();
			if(undefined !== message.cancel){
				_cancel.html(message.cancel);
			}
			if(undefined !== message.cancelClass){
				_cancel.attr("class","btn btn-"+message.cancelClass);
			}
			/* 设置尾部 */
			_m_footer.append(_cancel);

			/* 设置自定义按钮 */
			var _button = _model_footer_button.clone();
			if ("function" === typeof (message.callback)) {
				_button.click(function() {
					message.callback(message.params);
					_m.modal("hide");
				});

				_button.html(message.button);
				_m_footer.append(_button);
			}

			/* 设置头部 */
			_m_header.append(_model_header_close);
			_m_header.append(_m_title);

			/* 设置内容体 */
			_m_content.append(_m_header);
			_m_content.append(_m_body);
			_m_content.append(_m_footer);

			/* 设置对话框 */
			_m_dialog.append(_m_content);
			if (undefined !== message.style) {
				_m_dialog.css(message.style);
			}

			/* 设置modal隐藏事件 */
			_m.on("hidden.bs.modal", function() {
				_m.remove();
			}).append(_m_dialog).appendTo("body").modal({
				backdrop : "static",
				show : true
			});
		};

		method[type]();
	};

	/* jQuery ajax扩展 */
	var _ajax = $.ajax;

	$.statusCode = {
		606 : function() {
			if ("function" === typeof (Portal.loading)) {
				Portal.loading("close");
			}
			$.alert("confirm", {
				title : "Prompt",
				content : "Session expired！Please click 'OK' to refresh this page.",
				button : "OK",
				callback : function() {
					window.top.location.reload(true);
				}
			});
		}
	};

	/* 注册ajax请求函数 */
	$.ajaxRequest = function(options) {

		/* 默认设置 */
		var settings = {
			cache : false,
			dataType : "html",
			type : "POST"
		};

		$.extend(settings, options);

		/* 默认回调函数 */
		var callbacks = {
			error : function(XMLHttpRequest, textStatus, error) {
			},
			success : function(data, textStatus) {
			},
			statusCode : $.statusCode
		};

		$.extend(callbacks, options);

		/* 扩展增强处理 */
		var _opt = $.extend(settings, {
			error : function(XMLHttpRequest, textStatus, error) {
				/* 错误方法增强处理 */
				callbacks.error(XMLHttpRequest, textStatus, error);
			},
			success : function(data, textStatus) {
				/* 成功回调方法增强处理 */
				callbacks.success(data, textStatus);
			},
			statusCode : callbacks.statusCode
		});

		_ajax(_opt);
	};
	
	/*用于文件名显示的插件*/
	$.fn.sizes = function(options) {
		var json = {};
		var dfault = {
			size:15,
			content:'...'
		};
		json = $.extend(json,dfault, options);
		var text = $.trim($(this).html());
		var size = text.length;
		if(size > json.size){
			var jclen = json.content.length;
			text = text.substring(0,(json.size-jclen)) + json.content;
		}
		$(this).html(text);
	}

})(jQuery);