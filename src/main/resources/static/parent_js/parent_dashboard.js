var request;
var timer;

$(document).ready(function(){
	loadingStart();
	loadStudentsSummary();
});


function loadStudentsSummary(){
	
	request = $.ajax({
			url: "/parents/dashboard/children/ehpsummary", 
			success: function(result){
			
				$.each( result , function(index, summary){
					
					var totalCompleted = summary.totalCompleted;
					var totalCount = summary.totalCount;
					
					var $liElement = $("#"+ summary.type + "-" + summary.studentId);
					var $iconElement = $liElement.find("i");
					
					if($liElement){
						
						var summaryText = totalCompleted + " out of " + totalCount + " Completed";
						
							if(summary.type == "P"){
								$liElement.html(
										"<i class='icon-book3 mr-1'></i> Project: " + summaryText
								);
								$liElement.addClass("loaded");
							}
							
							
							if(summary.type == "H"){
								$liElement.html(
										"<i class='icon-file-spreadsheet2 mr-1'></i> Homework: " + summaryText
								);
								$liElement.addClass("loaded");
							}
							
							
							if(summary.type == "E"){
								$liElement.html(
										"<i class='icon-file-text3 mr-1'></i> Exam: " + summaryText
								);
								$liElement.addClass("loaded");
							}
					}
					
				});
			
				
				renderNoExamHomeworkProject();
				clearTimeout(timer);
				
			}
	});
	
}


function renderNoExamHomeworkProject(){
	
	$("li[id^=P-]").not(".loaded").html(
			"<i class='icon-book3 mr-1'></i> No project assign yet to student"
	);
	
	$("li[id^=H-]").not(".loaded").html(
			"<i class='icon-file-spreadsheet2 mr-1'></i> No homework assign yet to student"
	);
	
	$("li[id^=E-]").not(".loaded").html(
			"<i class='icon-file-text3 mr-1'></i> No exam assign yet to student"
	);
	
}


function loadingStart(){
	$(".student-summary-item").html(
			"<i class='spinner-border spinner-border-sm mr-1' role='status' aria-hidden='true'></i> Loading..."
	);
	
	timer = setTimeout(loadingEnd, 4000);
}


function loadingEnd(){
	
	request.abort();
	
	$("li[id^=P-]").not(".loaded").html(
			"<i class='icon-book3 mr-1'></i> Request timeout..."
	);
	
	$("li[id^=H-]").not(".loaded").html(
			"<i class='icon-file-spreadsheet2 mr-1'></i> Request timeout..."
	);
	
	$("li[id^=E-]").not(".loaded").html(
			"<i class='icon-file-text3 mr-1'></i> Request timeout..."
	);
	
}

