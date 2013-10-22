<?php
include_once("Init.php");

class Index {
	
	function __construct() {
		
		$controller = isset($_GET['controller'])?$_GET['controller']:null;
				
		if($controller == null) {
			// Not found
			require_once("website/default.php");
		} else {
			require_once("website/controller/" . $controller . "Controller.php");
		}
		
	}  

}

new Index();

?>