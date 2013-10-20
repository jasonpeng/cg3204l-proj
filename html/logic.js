$(document).ready(function(){
	
	$("#btnGo").click(function(){
			$("#result").css("visibility","visible");
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

function displayResult(data){
	var urls = data.split(" ");
	for(var i=0;i<urls.length;i++){
		var result = "<div class='result'>";
		result += "<img src='" + urls[i] + "'></div>";
		$(result).appendTo("#result");
	}
}