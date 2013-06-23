<?php

class JsonException extends Exception {
	
	public $JsonLastError;
	public $JsonLastErrorMessage;
	
	public function __construct($message=null,$code=null,$previous=null) {
		parent::__construct($message, $code, $previous);
		$this->JsonLastError = json_last_error();
		
		switch ($this->JsonLastError) {
			case JSON_ERROR_NONE:
				$this->JsonLastErrorMessage = 'No errors';
				break;
			case JSON_ERROR_DEPTH:
				$this->JsonLastErrorMessage = 'Maximum stack depth exceeded';
				break;
			case JSON_ERROR_STATE_MISMATCH:
				$this->JsonLastErrorMessage =  'Underflow or the modes mismatch';
				break;
			case JSON_ERROR_CTRL_CHAR:
				$this->JsonLastErrorMessage =  'Unexpected control character found';
				break;
			case JSON_ERROR_SYNTAX:
				$this->JsonLastErrorMessage =  'Syntax error, malformed JSON';
				break;
			case JSON_ERROR_UTF8:
				$this->JsonLastErrorMessage =  'Malformed UTF-8 characters, possibly incorrectly encoded';
				break;
			default:
				$this->JsonLastErrorMessage =  'Unknown error';
				break;
		}
	}
	
	public function __toString() {
		$str = parent::__toString();
		$str .= "\JsonLastError:" . var_export($this->JsonLastError, true);
		$str .= "\JsonLastErrorMessage:" . var_export($this->JsonLastErrorMessage, true);
	
		return $str;
	}
}

?>