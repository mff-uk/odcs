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
 */

if ( typeof baseUrl == 'undefined' ) {
	var baseUrl = "."; // TenderStats || .
}

/*
if ( typeof wsUrl == 'undefined' ) {
	var wsUrl = baseUrl + "/ws";
}*/

// for IE10 cache
$.ajaxSetup({ cache: false });

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
}

function displayError(message){
	$('#progressbar').hide();		
	var alert = $('<div class="alert alert-error">');
	alert.append("<strong>Error</strong> "+(message)?message:"");
	alert.insertAfter('#progressbar');
}

function userPreferences() {

	$.getJSON(baseUrl+"/SystemManager?action=getUserPreferences", function(data) {
		if (sessionStorage.username != undefined) {
			$("#username").append(sessionStorage.username);
		}
		if (data == null || data.length == 0) {
			sessionStorage.clear();
			window.location.href = "./";
		} else {		
			
			$("#username").html(data.username);
			sessionStorage.username = data.username;
			
			if (data.userHelper && data.userHelper == "on") {
				$("#userHelper").slideDown().removeClass("hide");
			}
			
			if (data.userStats && data.userStats == "true") {
				if ( typeof showStats != 'undefined' ) showStats();				
			}
		}
	}).fail(function( jqxhr, textStatus, error ) {
		
		var err = textStatus + ', ' + error;
		console.log( "Request Failed: " + err);
		console.log(jqxhr);
		
		if ( jqxhr.status == 403 ) {
			window.location.href = baseUrl+'/?t=error&m='+htmlEncode("You have been logged out. Please log in.");
		} else {
			alert("An error occured. Please reload the page.");
		}
		
		});
	
}

function userHelper(value) {
	$.getJSON(baseUrl+"/SystemManager?action=updateUserPreference&preference=userHelper&value="
					+ value, function(data) {
				if (data != null) {
					if (data.success) {
						if (value == "on") {
							$("#userHelper").slideDown().removeClass("hide");
						} else {
							$("#userHelper").slideUp().addClass("hide");
						}
					}
				}
			});
}

function checkUser() {
	userPreferences();
}

function showTender(title,tender) {		
	sessionStorage.tenderURL =  decodeURIComponent(tender);
	sessionStorage.contractTitle =  decodeURIComponent(title);
}

function showEvent(contract) {		
	sessionStorage.contractURL =  decodeURIComponent(contract);
	sessionStorage.contractURI =  decodeURIComponent(contract);
	sessionStorage.buyerURL = "";	
}

function showEvent(contract,ns) {		
	sessionStorage.contractURL =  decodeURIComponent(contract);
	sessionStorage.contractURI =  decodeURIComponent(contract);
	sessionStorage.buyerURL =  decodeURIComponent(ns);
	
}

function showEntity(entity) {
	sessionStorage.entity = decodeURIComponent(entity);
}

$(document).ready(function(){
    $('a.back').click(function(){
        parent.history.back();
        return false;
    });
});


function documentLink(documentURI,documentName) {
	
	return $('<a href="'+baseUrl+'/pcfapp/document/?documentURI='+documentURI+'">'+documentName+'</a>');
	
}

function documentItem(documentURI,documentName) {
	var item = $('<li>');			
	remove = $('<span class="removeDoc">');
	remove.on('click', function(){ $(this).closest('li').remove(); changedState = true; });
	icon = $('<i class="icon icon-remove-sign">');
	link = documentLink(documentURI,documentName); //$('<a href="'+baseUrl+'/pcfapp/document/?documentURI='+documentURI+'">').append(documentName);
	input = $('<input type="hidden" name="docs[]" value="'+documentURI+'">');			
	item.append(remove.append(icon)).append(' ').append(link).append(input);
	return item;
}



$(window).ready(function() {
	userPreferences();
});