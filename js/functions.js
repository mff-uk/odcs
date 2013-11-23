function htmlEncode(value) {
    return String(value)
    		.replace(/undefined/g, '') // for more user friendly messages 
    		.replace(/NaN/g, '0')
    		.replace(/null/g, '')
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
}

function htmlDecode(value){
    return String(value)
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&');
}

function $_GET(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return decodeURIComponent(pair[1]);
		}
	}
}

function formatNumber(value) {
	try {
		return value.toFixed(0).replace(/(\d)(?=(\d{3})+$)/g, "$1 ");
	} catch(e) {
		return value;
	}
}