/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/* 
 * author: Ivan Kosdy
 * based on previous versions of M. Snoha's work on tables 
 */

var tableData;

function loadTable(init) {

	if (currentPage < 1) {
		currentPage = 1;
	} else if (currentPage > pagesTotal && pagesTotal > 0) {
		currentPage = pagesTotal;
	}

	var address = tableAddress;
	var page = currentPage;
	//address += "?page=" + (page - 1);
	//address += "&items=" + (tableItemsPerPage);		
	
	if (init) {
		tableParams.reload = true; 
		tableData = new Array();
	}	
	else {
		tableParams.reload = undefined;
	}
	
	tableParams.page = currentPage-1;
	tableParams.items = tableItemsPerPage;
	
	$.getJSON(address, tableParams, function(data) {
		
		$('#progressbar').hide();
		
		if (data.success) {
			if (data.pages != undefined)
				pagesTotal = data.pages;
			if (init)
				paging();
			togglePageButtons();
			tableData[page] = data.data;
			fillTable();
			} else {
				$('#progressbar').hide();		
				var alert = $('<div class="alert alert-error">');
				alert.append("<strong>Error:</strong> Unable to load table. "+(data.message)?data.message:"");
				alert.insertAfter('#progressbar');
		}
	}).fail(function( jqxhr, textStatus, error ) {
		
		var err = textStatus + ', ' + error;
		console.log( "Request Failed: " + err);
		console.log(jqxhr);
		
		$('#progressbar').hide();		
		var alert = $('<div class="alert alert-error">');
		alert.append("<strong>Error:</strong> Unable to load table");
		alert.insertAfter('#progressbar');
		
		if ( jqxhr.status == 403 ) {
			window.location.href = baseUrl+'/?t=error&m='+htmlEncode("You have been logged out. Please log in.");
		}
		
		});
}


function sortTable(link,column,asc) {		
	$(link).closest('tr').find('i').remove();	
	if ( tableParams.order == column ) {
		tableParams.desc = ( tableParams.desc ) ? false : true;
	}
	else
	{		
		tableParams.desc = (tableParams.order) ? false : true;
	}	
	$('<i class="icon-chevron-'+(( tableParams.desc )?'down':'up')+'">').insertBefore($(link));	
	tableParams.order=column;
	
	loadTable(true);
	 
}

function CPVs(cpv1, cpvAll) {

	var cpv = new Array();

	if (cpv1 != undefined) {
		cpv.push(cpv1.replace("http://purl.org/weso/cpv/2008/", "", "g"));						
	}

	if (cpvAll != undefined) {
		cpv_a = (cpvAll.replace("http://purl.org/weso/cpv/2008/", "", "g")).split(" ");
		$.each(cpv_a,function(i,val){ cpv.push(val);});				
	}	
	var list = $("<ul>");	
	
	$.each(cpv,function(index){
		list.append( $('<li>').append(cpvs["cpv"+cpv[index]]));
	});
	
	list.addClass("table_cpvs");
	
	return list;
}

function paging() {

	if (pagesTotal <= 1)
		return;

	if (pagesTotal > windowSize + 2) {
		$('#showAllPages').removeClass("hide");
	}

	$("#pages").append(
			'<li><a href="#" id="loadPreviousContracts">Prev</a></li>');
	$("#loadPreviousContracts").click(function() {
		if ($(this).parent().hasClass("disabled")) {
			return false;
		}
		;
		currentPage--;
		loadTable(true); // TODO
	});

	for ( var i = 1; i <= pagesTotal; i++) {
		$("#pages").append(
				'<li><a href="#" id="loadContractsPage' + i + '">' + (i)
						+ '</a></li>');
		$("#loadContractsPage" + i).on('click', function() {
			if ($(this).parent().hasClass("active")) {
				return false;
			}
			;
			currentPage = parseInt($(this).attr("id").match(/(\d+)$/)[0], 10);
			loadTable();
		});
	}

	$("#pages").append('<li><a href="#" id="loadNextContracts">Next</a></li>');
	$("#loadNextContracts").click(function() {
		if ($(this).parent().hasClass("disabled")) {
			return false;
		}
		;
		currentPage++;
		loadTable();
	});

}

function editEvent(contract) {
	sessionStorage.editContractURI = contract;
}

function formatDate(date) {
	var d = new Date(date);	
	return dateFormat(d,"yyyy-mm-dd");
	
}

function togglePageButtons() {
	$("#pages li").addClass("reallyhide");
	$("#loadPreviousContracts").parent().removeClass("reallyhide");
	$("#loadNextContracts").parent().removeClass("reallyhide");
	$("#loadContractsPage" + 1).parent().removeClass("reallyhide");
	$("#loadContractsPage" + pagesTotal).parent().removeClass("reallyhide");

	$("#pages li").removeClass("active");
	if (currentPage == 1) {
		$("#loadPreviousContracts").parent().addClass("disabled");
	} else {
		$("#loadPreviousContracts").parent().removeClass("disabled");
	}
	;
	if (currentPage == pagesTotal) {
		$("#loadNextContracts").parent().addClass("disabled");
	} else {
		$("#loadNextContracts").parent().removeClass("disabled");
	}
	;
	$("#loadContractsPage" + currentPage).parent().addClass("active");

	for ( var i = currentPage - windowSize; i <= currentPage + windowSize; i++) {
		if (i > 1 && i < pagesTotal) {
			$("#loadContractsPage" + i).parent().removeClass("reallyhide");
		}
	}

	$(".3dots").remove();
	if ((currentPage - windowSize - 1 >= 1)
			&& $("#loadContractsPage" + (currentPage - windowSize - 1))
					.parent().hasClass('reallyhide')) {
		$("#loadContractsPage" + (currentPage - windowSize - 1)).parent()
				.after('<li class="3dots disabled"><a href="#">...</a></li>');
	}
	if ((currentPage + windowSize + 1 <= pagesTotal)
			&& $("#loadContractsPage" + (currentPage + windowSize + 1))
					.parent().hasClass('reallyhide')) {
		$("#loadContractsPage" + (currentPage + windowSize + 1)).parent()
				.before('<li class="3dots disabled"><a href="#">...</a></li>');
	}
}

function saveEventInfo(contractURI, title, description, price, currency, cpvString, place) {
	sessionStorage.contractURI = decodeURIComponent(contractURI);
	sessionStorage.contractTitle = decodeURIComponent(title);
	sessionStorage.contractDescription = decodeURIComponent(description);
	sessionStorage.contractPrice = decodeURIComponent(price);
	sessionStorage.contractCurrency = decodeURIComponent(currency);
	sessionStorage.contractCpvString = decodeURIComponent(cpvString);
	sessionStorage.contractPlace = decodeURIComponent(place);
}

function saveEventInfo(i) {
	sessionStorage.contractURI = tableData[currentPage][i].contractURI;
	sessionStorage.contractTitle = tableData[currentPage][i].title;
	sessionStorage.contractDescription = tableData[currentPage][i].description;
	sessionStorage.contractPrice = tableData[currentPage][i].price;
	sessionStorage.contractCurrency = tableData[currentPage][i].currency;
	sessionStorage.contractCpvString = tableData[currentPage][i].cpv1URL.replace("http://purl.org/weso/cpv/2008/","");
	sessionStorage.contractPlace = tableData[currentPage][i].place;
}

function editEvent(contract) {
	sessionStorage.editContractURL = contract;
}