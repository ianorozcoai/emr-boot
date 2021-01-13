$(document).ready(function(){

    $("#withFee").on("change rightnow",function(){
        if ($(this).val() == "N") {
            $("#feeAmount").attr("readonly", true);
        } else {
            $('#feeAmount').attr("readonly", false);
        }
    }).triggerHandler("rightnow");

    eventDateEnabler();

});

function eventDateEnabler() {
    var eventId = $("#eventId").val();
    var $eventDateFrom = $("#eventDateFrom");

    if(eventId == ""){
        console.log("Create Event");
    } else {
        console.log("Update Event");
        var now = new Date();
        var eventStartDate = new Date($eventDateFrom.val());
        console.log("eventStart" + eventStartDate + " NOW " +now);
        if (eventStartDate < now) {
            console.log("Event has already started");
            $(".form-group > :input").not("textarea").attr("readonly", true);
            $(".form-group > :input").not("textarea").attr("title","Event has already stated. You cannot update this field anymore.");
        }

    }
}