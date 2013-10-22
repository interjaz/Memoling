<?php

class Content {
	
	
	public static function img($src) {
		return Content::getBase() . "website/content/img/" . $src;
	}
	
	public static function css($src) {
		return Content::getBase() . "website/content/css/" . $src;
	}
	
	public static function js($src) {
		return Content::getBase() . "website/content/js/" . $src;
	}
	
	
	public static function getBase() {
		$server = $_SERVER["DOCUMENT_URI"];
		$slashes = substr_count($server, "/");
		
		$base = "";
		for($i=0;$i<$slashes-2;$i++) {
			$base .= "../";
		}
		
		return $base;
	}
	
}

?>