$(document).ready(function(){
	
	var dayuh=$(window).height();
	if(dayuh>650){
		$(".pagefour,.pagefive,.pageseven").find(".wordwhite").css("margin-top","100px");
		$(".pagesix").find(".wavingast").css("bottom","250px");
	}
	var numt = 1;
	
	if($(".micoimg").hasClass('musiczhuan')){
        $(".micoimg").removeClass("musiczhuan");  
	}else{
		$(".micoimg").addClass("musiczhuan");
	}
	
	$(".micoimg").click(function(){
		if($(".micoimg").hasClass('musiczhuan')){
            $(".micoimg").removeClass("musiczhuan");  
		}else{
			$(".micoimg").addClass("musiczhuan");
		}
		
       bf();
	})
	$(".down1").click(function(){
		$(".homepage").removeClass("active");
		$(".pagetwo").addClass("active");
		var wh=$(window).height();
		$("#superContainer").animate({top:-wh});
	})
		$(".down2").click(function(){
		$(".pagetwo").removeClass("active");
		$(".pagethree").addClass("active");
		var wh=$(window).height()*2;
		$("#superContainer").animate({top:-wh});
	})
		$(".down3").click(function(){
		$(".pagethree").removeClass("active");
		$(".pagefour").addClass("active");
		var wh=$(window).height()*3;
		$("#superContainer").animate({top:-wh});
	})
		$(".down4").click(function(){
		$(".pagefour").removeClass("active");
		$(".pagefive").addClass("active");
		var wh=$(window).height()*4;
		$("#superContainer").animate({top:-wh});
	})
		$(".down5").click(function(){
		$(".pagefive").removeClass("active");
		$(".pagesix").addClass("active");
		var wh=$(window).height()*5;
		$("#superContainer").animate({top:-wh});
	})
		$(".down6").click(function(){
		$(".pagesix").removeClass("active");
		$(".pageseven").addClass("active");
		var wh=$(window).height()*6;
		$("#superContainer").animate({top:-wh});
	})
		$(".down7").click(function(){
		$(".pageseven").removeClass("active");
		$(".pageeight").addClass("active");
		var wh=$(window).height()*7;
		$("#superContainer").animate({top:-wh});
	})
	$(".topico").click(function(){
		var topval=$("#superContainer").css('top');
		$(".homepage").addClass("active");
		$("#superContainer").animate({top:"0px"});
})
	/*---------clock-----------*/
              setInterval( function() {	        	  
	              var seconds = new Date().getSeconds();
	              var sdegree = seconds * 6;
	              var srotate = "rotate(" + sdegree + "deg)";
	              
	              $("#sec").css({"-moz-transform" : srotate, "-webkit-transform" : srotate});
                  
              }, 1000 );
	
				setInterval( function() {	 
				  numt = numt+1; 
				  if(numt == 99){
					  bf();
				  }
				  $(".loadtext").html(numt+"%");
				}, 20); 
			
              setInterval( function() {
	              var hours = new Date().getHours();
	              var mins = new Date().getMinutes();
	              var hdegree = hours * 30 + (mins / 2);
	              var hrotate = "rotate(" + hdegree + "deg)";
	              
	              $("#hour").css({"-moz-transform" : hrotate, "-webkit-transform" : hrotate});
                  
              }, 1000 );
        
        
              setInterval( function() {
              var mins = new Date().getMinutes();
              var mdegree = mins * 6;
              var mrotate = "rotate(" + mdegree + "deg)";
              
              $("#min").css({"-moz-transform" : mrotate, "-webkit-transform" : mrotate});
                  
              }, 1000 );


});

setTimeout('funA()', 2000); 
function funA(){
  $(".divload").hide();
}

function bf(){
 var audio = document.getElementById('music1'); 
 if(audio!==null){             
    //检测播放是否已暂停.audio.paused 在播放器播放时返回false.
  if(audio.paused){                 
      audio.play();//audio.play();// 这个就是播放  
  }else{
  
   audio.pause();// 这个就是暂停
  }
 } 
}
