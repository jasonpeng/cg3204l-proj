$(document).ready(function(){
	
	$("#btnGo").click(function(){
			$("#result").css("visibility","visible");
			clearResult();
			getResult();
	});
	
	$("#keys").keyup(function(event){
		if(event.keyCode == 13){
			$("#btnGo").click();
		}
	});
})

function getResult(){
	var keys = $("#keys").val();
	var keywords = keys.split(" ").join("+");
	$.ajax({
		type: "GET",
		url: "",
		data: keywords,
		cache: false,
		success: function(data){
			displayResult(data);
		}
	});
	
}
function clearResult(){
	$("#result").html("");
}
function displayResult(data){
	var urls = data.split(" ");
	for(var i=0;i<urls.length;i++){
		
		var result ="<div class='result'>";
		result += "<a target='_blank' href='" + urls[i] + "'>" 
		result += "<img src='" + urls[i] + "'></a></div>";
		$(result).appendTo("#result");
	}
	adjustSize();
}
function adjustSize(){
	$("#result").find("img").each(function(){
		var imgClass = ($(this).width()/$(this).height() > 1) ? 'wide' : 'tall';
		$(this).addClass(imgClass);
		var parent = $(this).parents(".result").get(0);
		if( $(parent).height() > $(this).height()){
		    var h = $(parent).height()/2;
			h -= $(this).height()/2;
			$(this).css("margin-top",h+"px");
		}
		if($(parent).width() > $(this).width()){
			var w = $(parent).width()/2;
			w -= $(this).width()/2;
			$(this).css("margin-left",w+"px");
		}
	});
}