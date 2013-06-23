<?php
__autoload("DbConnector");
__autoload("Helper");

function __autoload($class_name) {
	
	$server = $_SERVER["DOCUMENT_URI"];
	$slashes = substr_count($server, "/");
	
	$base = "";
	for($i=0;$i<$slashes-1;$i++) {
		$base .= "../";
	}
	
	$dirs = array(
			$base."entity",
			$base."adapter",
			$base."webservice",
			$base."webservice/entity",
			$base."helper");

	foreach($dirs as $dir) {
		$path = $dir."/".$class_name.".php";
		
		if(file_exists($path)) {
			require_once($path);
			return;
		}
	}

}

date_default_timezone_set("UTC");

?>