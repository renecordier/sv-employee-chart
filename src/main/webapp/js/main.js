var init = false;

$(function(){
	$( "input[name='stats']" ).change(function(){
		var str = $(this).val();
		
	});
	//agesHR = getAgeList(mode);
	
	$("#chartEmployee").bind( "updateChart", function (event, ages) {
		 $( this ).text( "hello" );
	});
	
	$("input[name='submitEmployee']").click( function() {
		$.cookie("ages", "ploppy");
		$("#chartEmployee").trigger("updateChart", ["ages"]);
	});
	
	$("#chartEmployee").text($.cookie("ages"));
});

function getAgeList(mode) {
	
}
