

function renderDataTable(){
	
	var url = window.location.pathname + "/notassign/studentlist";
	
	$("#dataTables-studentList").DataTable({
		"pageLength" : 5,
		"lengthMenu": [ 5, 10, 15, 25, 50 ],
		"columnDefs": [
		    { "width": "5%", "targets": 1 }
		],
		"ajax" : {
			url: url,
			dataType: "json",
            contentType: "application/json",
            dataSrc: ""
		},
		"columns" : [
			{  
				data: 'lastName',
				render: function(data, type, row, meta) {
					return row.firstName + " " + data;
				}	
			},
			{
				data: "studentId",
				render: function(data, type, row, meta) {								
					return ("<div class='text-right'>" +
							"<button type='button' class='btn btn-primary' onclick='clickAssign("+data+");'>Assign</button>" +
							"</div>");														
				}
			}
		]
		
		
	});
	
}

function clickAssign(studentId) {
	
	var url = window.location.pathname;
	
	swal({
	    title: "Are you sure?",
	    text: "Assign this student to parent",
	    icon: "warning",
	    buttons: true,
	    dangerMode: true,
	})
	.then( (isConfirm) => {
		if( !isConfirm ) {
			return;
		
		} else {
			
			$.post(url, { studentId: studentId },
			function(){
				location.reload(true);
			});
			
			
		}
	});
}



