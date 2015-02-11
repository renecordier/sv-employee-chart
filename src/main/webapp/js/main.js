var barChart = null;
var ctx = null;

$(function(){
	ctx = $("#chartEmployee").get(0).getContext("2d");
	
	var select = $.cookie("selectMode");
	if(select!="") {
		$( "select[name='stats']" ).prop('selectedIndex', select);
	}
	
	displayBarChart();
	
	$( "select[name='stats']" ).change(function(){
		$.cookie("selectMode", $(this).prop('selectedIndex'));
		displayBarChart();
	});
});

function displayBarChart() {
	var mode = $( "select[name='stats']" ).val();
	var _data = [];
	var _label = ["18 - 20", "21 - 30", "31 - 40", "41 - 50", "Over 50"];
	
	var agesHR = [];
	var agesDev = [];
	
	var hrtable = $("#HR");
	var devtable = $("#Dev");
	
	if($(hrtable).length == 0 || $(devtable).length == 0) {
		agesHR = JSON.parse("[" + $.cookie("agesHR") + "]");
		agesDev = JSON.parse("[" + $.cookie("agesDev") + "]");
		if(agesHR=="" || agesDev==""){
			$("#chartEmployee").remove();
			$("#chartArea").append('<div class="alert alert-danger">'
	    			+ '<strong>Not Found!</strong> Data source is not found.'
	    	 	+'</div>');
			return;
		}
	} else {
		agesHR = agesData("#HR tbody tr td:nth-child(7)");
		agesDev = agesData("#Dev tbody tr td:nth-child(7)");
		$.cookie("agesHR", agesHR.toString());
		$.cookie("agesDev", agesDev.toString());
	}
	
	if (mode == "HR") {
		_data = agesHR;
	} else if (mode == "Dev") {
		_data = agesDev;
	} else {
		for (i = 0; i < 5; i++) {
			_data[i] = agesHR[i] + agesDev[i];
		}
	}
	
	var data = {
			labels : _label,
			datasets : [{
				label : "Age statistics",
				fillColor : "rgba(151,187,205,0.5)",
				strokeColor : "rgba(151,187,205,0.8)",
				highlightFill : "rgba(151,187,205,0.75)",
				highlightStroke : "rgba(151,187,205,1)",
				data : _data
			}]
		};
	
	var options = {
			scaleOverride : true,
			scaleStartValue : 0,
			scaleSteps : 5,
			scaleStepWidth : 2,
			xAxisLabel : "Age ranges",
			yAxisLabel : "Number",
			graphTitle : "Age statistics"
	}
	
	barChart = new Chart(ctx).Bar(data,options);
}

function agesData (source) {
	var datas = [0,0,0,0,0];
	$(source).each(function(){
		var age =  parseInt($(this).text());
		if(age < 21) {
			datas[0]++;
		} else if (age < 31) {
			datas[1]++;
		} else if (age < 41) {
			datas[2]++;
		} else if (age < 51) {
			datas[3]++;
		} else {
			datas[4]++;
		}
	});
	
	return datas;
}
