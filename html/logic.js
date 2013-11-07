var MINIMUM = 1;
var MAXIMUM = 10;
var imageNum = 3;
var tag = "news";
$(document).ready(function(){
	$("#imageNum").html(imageNum);
	
	$("#num-plus").click(function(){
		if(imageNum < MAXIMUM){
			imageNum++;
			$("#imageNum").html(imageNum);
		}
	});
	
	$("#num-minus").click(function(){
		if(imageNum > MINIMUM){
			imageNum--;
			$("#imageNum").html(imageNum);
		}
	});
	
	$("#btnGo").click(function(){
		if(!$(this).hasClass("disabled")){
			$("#result").css("visibility","visible");
			clearResult();
			$(this).addClass("disabled");
			setLoader("show");
			$("#feedback").html("Searching...");
			getResult();
		}
	});
	
	$("#keys").keyup(function(event){
		if(event.keyCode == 13){
			$("#btnGo").click();
		}
	});
	
	$("#tags li").click(function(){
		clearTag();
		$("#keys").val("");
		$(this).addClass("active");
		tag = $(this).prop("id");
	});
})

function clearTag(){
	$("#tags li").each(function(){
		$(this).removeClass("active");
	});
}

function setLoader(status){
	if(status == "hide"){
		$("#loader").css("visibility","hidden");
	}
	else if(status == "show"){
		$("#loader").css("visibility","visible");
	}
	else{
	}
}

function getResult(){
	var keys = $("#keys").val();
	var keywords = keys.split(" ").join("+");
	var data = keywords + "." + tag + ":" + imageNum;
	$.ajax({
		type: "GET",
		url: "",
		data: data,
		cache: false,
		success: function(jsonString){
			displayResult(jsonString);
			setLoader("hide");
			$("#btnGo").removeClass("disabled");
		}
	});
	//displayResult("http://www.mit.edu/img/BackImage.jpg");
}
function clearResult(){
	$("#result").html("");
}
function displayResult(jsonString){
	console.log(jsonString);
	var data = eval('(' + jsonString + ')');
	for(var i=0;i<data.images.length;i++){	
		var imageUrl = data.images[i].imageUrl;
		var siteUrl = data.images[i].siteUrl;
		var result ="<div class='result'>";
		result += "<a target='_blank' title='view full size image' href='" + imageUrl + "'>" 
		result += "<img src='" + imageUrl + "'></a>"
		result += "<a class='source' title='view original website' target='_blank' href='" + siteUrl +"'>source</a>"
		result += "</div>";
		$(result).appendTo("#result");
		$("#feedback").html("Time used: " + data.timeUsed + "s");
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