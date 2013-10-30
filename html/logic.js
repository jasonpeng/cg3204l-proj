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
	//displayResult("http://www.mit.edu/img/BackImage.jpg");
}
function clearResult(){
	$("#result").html("");
}
function displayResult(data){
	console.log(data);
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
		$(this).load(function(){
			var height = $(this).height();
			var width = $(this).width();	
			var parent = $(this).parents(".result").get(0);			
			if(width >= height){
				$(this).addClass("wide");
				if(width > $(parent).width()){
					var width_view = $(parent).width();
					var height_view = height*width_view/width;
					var top = $(parent).height()/2 - height_view/2;
					$(this).css("margin-top",top+"px");
				}
				else{
					var top = $(parent).height()/2 - height/2;
					$(this).css("margin-top",top+"px");
				}
			}
			else{
				$(this).addClass("tall");
				if(height > $(parent).height()){
					//adjusted by css
				}
				else{
					var top = $(parent).height()/2 - height/2;
					$(this).css("margin-top",top+"px");
				}
			}
			/*if($(parent).width() > width){
				var left = $(parent).width()/2 - width/2;
				$(this).css("margin-left",left+"px");
			}*/
		});
		
	});
}