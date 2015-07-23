/* 
 * author: Ivan Kosdy 
 */

var statsLoaded = false;

function userStatsLoad() {

	$.getJSON(baseUrl+"/pcfapp/statsBuyer", function(data) {
		if (data != null) {			
			for ( var type in data.data )
			{				
				$("#stats"+type+" td:last-child").html(data.data[type]);
			}
			statsLoaded = true;
			showStats();
		}
	}).error(function(){
		var divError = $('<div class="alert alert-error box">');
		divError.append('<strong>Error:</strong> Unable to load stats');
		var stats = $('#statsTable').closest('.box');
		stats.hide();
		divError.insertAfter(stats);
	});
}

function showStats() {
	if ( !statsLoaded ) {
		userStatsLoad(); 
	}
	else
	{		
		$("#statsTable").fadeIn('fast').removeClass('hide');
		$("#statsHide i.icon-plus").addClass('hide');
		$("#statsHide i.icon-minus").removeClass('hide');		
	}
}

function hideStats() {
	$("#statsTable").fadeOut('fast').addClass("hide");
	$("#statsHide i.icon-plus").removeClass('hide');
	$("#statsHide i.icon-minus").addClass('hide');	
}

function userStats(value) {
	
	$.getJSON(baseUrl+"/SystemManager?action=updateUserPreference&preference=userStats&value="+ value,
			function(data) {
				if (data != null) {
					if (data.success) {
						if (value == 'true') {
							showStats();
						} else {
							hideStats();
						}
					}
				}
			});	
	
}

function copyEvent(contractURL) {
	sessionStorage.copyContractURL = contractURL;
	return;
	
}

function linkEntity(entityName,entityURI) {
	var link = $('<a>');
	link.attr('href',baseUrl+'/buyer/supplier?q='+entityURI);	
	link.append(entityName);
	return link;
}

function linkBuyer(entityName,entityURI) {
	var link = $('<a>');
	link.attr('href',baseUrl+'/buyer/view?q='+entityURI);	
	link.append(entityName);
	return link;
}

function linkTender(tenderTitle,tenderURI) {
	var link = $('<a>');
	link.attr('href',baseUrl+'/buyer/tender/view?q='+encodeURI(tenderURI));	
	link.append("tender");
	return link;
}

function linkEvent(eventTitle,eventURI) {
	var link = $('<a>');
	link.attr('href',baseUrl+'/buyer/contract/view?q='+encodeURI(eventURI));
	link.append(eventTitle);
	return link;
}

function searchCPVBar() {
	if ( sessionStorage.searchCPV != 'null' ) { $("#CPVcode").val(sessionStorage.searchCPV); }
	sessionStorage.searchCPV = null;
	$("#CPVcode").typeahead({ source: collection });			
	$("#CPVsearch").on('click',function(){						
		$.ajax({
		    url: baseUrl+"/contract/cpvSearch/",
		    data: { CPV: $('#CPVcode').val() } ,
		    type: 'POST',
		    success: function (response, textStatus, jqXHR) {
		    	sessionStorage.contractTitle = response.data.title;			    	
		    	sessionStorage.contractURL = response.data.contractURI;
		    	sessionStorage.suitableEdit = response.data.contractURI;
		    	sessionStorage.searchCPV = $('#CPVcode').val();
		    	showEvent(response.data.contractURI);			    	
		    	document.location.href = baseUrl+"/buyer/suitableSuppliers"; 
		    },
		    error: function (data, textStatus, jqXHR) { alert("An error occured. Please reload the page."); }
		});
	});
}

function newDoc(inputName,name) {
	var newD = $('<li class="addDocLI hide">');
	var newClose = $('<a>').append($('<i>').addClass('icon-remove-sign'));
	newClose.css('cursor','pointer');
	newClose.on('click',function() {				
		$(this).closest('.addDocLI').fadeOut(function(){ $(this).remove();});
	});
	newD.append(newClose);
	newD.append($('<label for="'+inputName+'">').append(name));
	newD.append($('<br>'));
	newD.append($('<input type="file" name="'+inputName+'">'));
	return newD;
} 