<?php
session_start();
include_once("Init.php");

class Index {
	
	function __construct() {
		
		$controller = isset($_GET['controller'])?$_GET['controller']:null;
		$controller = explode('/', $controller);
        $controller = $controller[0];
				
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