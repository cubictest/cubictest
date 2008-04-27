function updateForm(selectBox) {
	if (selectBox.value == "cubics") {
		document.getElementById("customCriteriaLabel").innerHTML = 
			'<label for="includeUnreleased">Include unreleased cubes</label>';
		document.getElementById("customCriteria").innerHTML = 
			'<input type="checkbox" id="includeUnreleased"/>'; 
	}
	else if (selectBox.value == "reviews") {
		document.getElementById("customCriteriaLabel").innerHTML = ''; 
		document.getElementById("customCriteria").innerHTML = ''; 
	}
	else {
		document.getElementById("customCriteriaLabel").innerHTML = ''; 
		document.getElementById("customCriteria").innerHTML = ''; 
	} 
}

function showHelpfulGreet() {
	document.getElementById("helpfulGreet").innerHTML = "Nice!";
}
function hideHelpfulGreet() {
	document.getElementById("helpfulGreet").innerHTML = "";
}
