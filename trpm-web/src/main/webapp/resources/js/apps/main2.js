require.config({

	/* JS根目录 */
	baseUrl : jsPath + "/resources/js/",

	/* JS路径定义 */
	paths : {
		/* 插件JS */
		"jquery" : "libs/jquery.1.11.3.min",
		"jquery-datetime" : "libs/wdatepicker/wdatepicker.min",
		"bootstrap" : "libs/bootstrap.min",
		"jquery-form" : "libs/jquery.form.min",
		"jquery-validator" : "libs/jquery.form.validator.min",
		"jquery-pagin" : "libs/jquery.paging.min",
		"jquery-cookie" : "libs/jquery.cookie.min",
		"tools" : "libs/tools.min",
		"jquery-load" : "libs/loading/jquery.loading.min",
		"windowfocus" : "libs/jquery.windowfocus",
		"function" : "apps/function",
		"avalon" : "libs/avalon.modern.shim.min",
		"moment-timezone" : "libs/moment-timezone-2020.min",
		"moment" : "libs/moment.min",
		"jquery-sort" : "libs/jquery.table.sort.min",
		"jquery-bootstrap" : "libs/jquery.bootstrap",
		"select2" : "libs/select2/select2.min",
		/* 模块JS */
		"index" : "apps/common/index",
		"schedule" : "apps/portal/schedule",
		"classrooms" : "apps/portal/classrooms",
		"personal" : "apps/portal/personal/personal",
		"personal-basicinfo" : "apps/portal/personal/basicinfo",
		"personal-bankinfo" : "apps/portal/personal/bankinfo",
		"personal-password" : "apps/portal/personal/password",
		"personal-location" : "apps/portal/personal/location",
		"report-ua" : "apps/portal/report.ua",
		"report-demo" : "apps/portal/report.demo",
		"room-demo" : "apps/portal/report.demo.room",
		"report-practicum":"apps/portal/practicum.report",
		"feedback" : "apps/portal/feedback2",
		"comments" : "apps/portal/comments2",
		"classmajor" : "apps/portal/class.major",
		"classopen" : "apps/portal/class.open",
		"classpracticum" : "apps/portal/class.practicum",
		"checkalive" : "apps/portal/check.alive",
		"duobeiyun" : "libs/duobeiyun-js-sdk-0.1.1-min",
		"dbyproxy" : "apps/portal/dby.proxy",
		"xdyproxy" : "apps/portal/xdy.proxy",
		"messenger" : "libs/messenger",
		"countdown" : "libs/jquery.countdown",
		"signup":"apps/passport/signup",
		"signin":"apps/passport/signin",
		"email":"apps/passport/passport.email",
		"modifypwd":"apps/passport/modifypwd",
		"resetpwd":"apps/passport/resetpwd",
		"validator":"apps/passport/validator",
		"utils":"apps/passport/validator.utils",
		"privacy":"apps/passport/privacy",
		"changepwd":"apps/portal/change.password",
		"payroll":"apps/portal/payroll",
		"pe":"apps/pe/pe"
	},

	/* JS模块加载超时，默认7秒 */
	waitSeconds : 0,

	/* 更新JS缓存 */
	urlArgs : "t=201608151500",

	/* 加载非AMD规范的JS */
	shim : {
		"bootstrap" : {
			deps : [ "jquery" ]
		},
		"jquery-validator" : {
			deps : [ "jquery" ]
		},
		"jquery-pagin" : {
			deps : [ "jquery" ]
		},
		"jquery-cookie" : {
			deps : [ "jquery" ]
		},
		'jquery-datetime' : {
			deps : [ 'jquery' ]
		},
		"tools" : {
			deps : [ "jquery" ]
		},
		"function" : {
			deps : [ "jquery" ]
		},
		"jquery-load" : {
			deps : [ "jquery" ]
		},
		"moment-timezone" : {
			deps : [ "moment" ]
		},
		"moment" : {
			deps : [ "jquery" ]
		},
		"jquery-bootstrap" : {
			deps : [ "bootstrap" ]
		},
		"jquery-sort" : {
			deps : [ "jquery" ]
		},
		"duobeiyun" : {
			deps : [ "jquery" ]
		},
		"messenger" : {
			deps : [ "jquery" ]
		},
		"windowfocus":{
			deps : [ "jquery" ]
		},
		"countdown" : {
			deps : [ "jquery","windowfocus"]
		},
		"validator":{
			deps : [ "jquery" ]
		},
		"select2":{
			deps : [ "jquery" ]
		}
	}

});
