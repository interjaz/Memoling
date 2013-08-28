<?php

class JsonBuilder {
	
	private $json = "";
	
	public function put($key, $val, $allowNull=false) {
		if(!$allowNull && $val == null) {
			return;
		}
		
		if(is_object($val)) {
			$this->json .= '"' . $key . '":' . $val->encode()  . ',';
		} else if(is_array($val)) {
			$this->json .= '"' . $key . '":' . $this->arrayToJson($val) . ',';
		} else if(is_numeric($val)) {
			$this->json .= '"' . $key . '":' . $val . ',';
		} else {
			$this->json .= '"' . $key . '":"' . addslashes($val) . '",';
		}
		
	}
	
	public function __toString() {
		
		$string = "{" . $this->json;
		
		if(strlen($string) > 2) {
			$string = substr($string, 0, strlen($string)-1);
		}
		
		$string .= '}';
		
		return $string;
	}

	public static function arrayToJson($array) {
		$string = '[';
		
		foreach($array as $key=>$val) {
			if(is_object($val)) {
				$string .=  $val->encode() . ",";
			} else if(is_numeric($val)) {
				$string .= $val . ',';
			} else {
				$string .= '"' . addslashes($val) . '",';
			}
		}
		
		if(strlen($string) > 1) {
			$string = substr($string, 0, strlen($string)-1);
		}
		$string .= ']';
		
		return $string;
	}
	
}

?>