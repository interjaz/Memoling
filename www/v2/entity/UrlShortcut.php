<?php

class UrlShortcut {

	const MAX = 61;
	
	public $Shortcut;
	
	public static function nextShortcut($shortcut) {
		
		$vals = array();
		$len = strlen($shortcut);
		for($i=0;$i<$len;$i++) {
			$vals[$i] = self::charToVal(substr($shortcut, $i, 1));
		}
		
		$next = self::nextVal($vals);
				
		$shortcut = "";
		for($i=0;$i<sizeof($next);$i++) {
			$shortcut .= self::valToChar($next[$i]);
		}
		
		return $shortcut;
	}
	
	public static function nextVal($vals) {
		$len = sizeof($vals);
		for($i=$len-1;$i>=0;$i--) {
			if($vals[$i] < self::MAX) {
				$vals[$i] = $vals[$i]+1;
				for($j=$i+1;$j<$len;$j++) {
					$vals[$j] = 0;
				}
				
				return $vals;
			}
		}
		
		$vals = array_fill(0, $len+1, 0);
		return $vals;
	}
	
	private static function valToChar($val) {
		if($val >= 2*26) {
			// Number
			return chr($val-2*26 + 48);
		} else if($val >= 26) {
			// Capital
			return chr($val-26 + 65);
		} else {
			// Small cap
			return chr($val + 97);
		}
	}
	
	private static function charToVal($char) {
		$ascii = ord($char);
		
		$val = 0;
		if($ascii >= 48 && $ascii <= 57) {
			// Number
			$val = 2*26 + $ascii - 48;
		}
		else if($ascii >= 65 && $ascii <= 90) {
			// Capital
			$val = 26 + $ascii - 65;
		}
		else if($ascii >= 97 && $ascii <= 122) {
			// Small caps
			$val = $ascii - 97;
		}
				
		return $val;
	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("shortcut", $this->Shortcut, true);
		
		return $builder->__toString();
	}

}


?>