(function($) {

	if (!$.windowFocus) {
		$.extend({
			windowFocus : function() {
				var init = true, methods = [];

				if (!$(document).data('windowFocus')) {
					$(document).data('windowFocus', $.windowFocus.init());
				}

				for (x in arguments) {
					if (typeof arguments[x] == "object") {
						if (arguments[x]["blur"]) {
							$.windowFocus.methods.blur.push(arguments[x].blur);
						}
						if (arguments[x]["focus"]) {
							$.windowFocus.methods.focus.push(arguments[x].focus);
						}
						if (arguments[x]["blurFocus"]) {
							$.windowFocus.methods.blurFocus.push(arguments[x].blurFocus);
						}
						if (arguments[x]["initRun"]) {
							init = arguments[x].initRun;
						}
					} else if (typeof arguments[x] == "function") {
						methods.push(arguments[x]);
					} else if (typeof arguments[x] == "boolean") {
						init = arguments[x];
					}
				}

				if (methods) {
					if (methods.length == 1) {
						$.windowFocus.methods.blurFocus.push(methods[0]);
					} else {
						$.windowFocus.methods.blur.push(methods[0]);
						$.windowFocus.methods.focus.push(methods[1]);
					}
				}

				if (init) {
					$.windowFocus.methods.onChange();
				}
			}
		});

		$.windowFocus.init = function() {
			// var document.hidden || document.msHidden || document.webkitHidden
			// || document.mozHidden;

			// Standards:
			if ($.windowFocus.props.hidden in document) { // IE10 | FF20+
				document.addEventListener("visibilitychange", $.windowFocus.methods.onChange);
			} else if (($.windowFocus.props.hidden = "mozHidden") in document) { // Older
				// FF
				// Versions
				// (?)
				document.addEventListener("mozvisibilitychange", $.windowFocus.methods.onChange);
			} else if (($.windowFocus.props.hidden = "webkitHidden") in document) { // Chrome
				document.addEventListener("webkitvisibilitychange", $.windowFocus.methods.onChange);
			} else if (($.windowFocus.props.hidden = "msHidden") in document) { // IE
				// 4-6
				document.addEventListener("msvisibilitychange", $.windowFocus.methods.onChange);
			} else if (($.windowFocus.props.hidden = "onfocusin") in document) { // IE7-9
				document.onfocusin = document.onfocusout = $.windowFocus.methods.onChange;
			} else {
				// All others:
				window.onpageshow = window.onpagehide = window.onfocus = window.onblur = $.windowFocus.methods.onChange;
			}

			return $.windowFocus;
		};

		$.windowFocus.methods = {
			blurFocus : [],
			blur : [],
			focus : [],
			exeCB : function(e) {
				if ($.windowFocus.methods.blurFocus) {
					$.each($.windowFocus.methods.blurFocus, function(k, v) {
						if (typeof this == 'function') {
							this.apply($.windowFocus, [ e, !e.hidden ])
						}
					});
				}
				if (e.hidden && $.windowFocus.methods.blur) {
					$.each($.windowFocus.methods.blur, function(k, v) {
						if (typeof this == 'function') {
							this.apply($.windowFocus, [ e ])
						}
					});
				}
				if (!e.hidden && $.windowFocus.methods.focus) {
					$.each($.windowFocus.methods.focus, function(k, v) {
						if (typeof this == 'function') {
							this.apply($.windowFocus, [ e ])
						}
					});
				}
			},
			onChange : function(e) {
				var eMap = {
					focus : false,
					focusin : false,
					pageshow : false,
					blur : true,
					focusout : true,
					pagehide : true
				};
				e = e || window.event;
				if (e) {
					e.hidden = e.type in eMap ? eMap[e.type] : document[$.windowFocus.props.hidden];
					$(window).data("visible", !e.hidden);
					$.windowFocus.methods.exeCB(e);
				} else {
					try {
						$.windowFocus.methods.onChange
								.call(document, new Event('visibilitychange'));
					} catch (err) {
						if (undefined !== console) {
							console.log(err);
						}
					}
				}
			}
		};

		$.windowFocus.props = {
			hidden : "hidden"
		};
	}

})(jQuery);