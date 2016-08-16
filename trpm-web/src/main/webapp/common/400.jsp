<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0,minimum-scale=1.0,user-scalable=0"/>
	<meta name="apple-touch-fullscreen" content="YES"/>
	<meta name="apple-mobile-web-app-status-bar-style" content="black"/>
 	<title>VIPKID-400</title>
	<link href="<%=request.getContextPath()%>/common/style/error404_500.css" rel="stylesheet" type="text/css">
	<script>
		if (/Android (\d+\.\d+)/.test(navigator.userAgent)) {
			var version = parseFloat(RegExp.$1);
			if (version > 2.3) {
				var phoneScale = parseInt(window.screen.width) / 640;
				document.write('<meta name="viewport" content="width=640, minimum-scale = ' + phoneScale + ', maximum-scale = ' + phoneScale + ', target-densitydpi=device-dpi"/>');
			} else {
				document.write('<meta name="viewport" content="width=640, target-densitydpi=device-dpi"/>');
			}
		} else {
			document.write('<meta name="viewport" content="width=640, user-scalable=no, target-densitydpi=device-dpi"/>');
		}
	</script>
</head>
<body class="bg404">
   <div class="content">
       <div class="main">
           <P class="sorrymessage404">Sorry,HTTP request parameter is not legitimate, the error code 400!</P>
           <a href="javascript:history.back()" class="backhome500" >BACK</a>
           <span id="rocket"></span>
           <p class="anball rotateball" id="anball"><img src="<%=request.getContextPath()%>/common/images/404_ball.png"></p>
       </div>
   </div>
</body>
</html>