$(document).ready(function(){
	initialize();
	
	$("#btnGo").click(function(){
			$("#result").css("visibility","visible");
			getResult();
	});
	
	$("#keys").keyup(function(event){
		if(event.keyCode == 13){
			$("#btnGo").click();
		}
	});
}

function getResult(){
	var keys = $("#keys").val();
	$.ajax({
		type: "GET",
		url: "",
		data: keys,
		cache: false,
		success: function(data){
			displayResult(data);
		}
	});
}

function displayResult(){
}