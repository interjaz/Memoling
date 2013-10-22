<?php

class SqlException extends Exception {

	public $SqlErrorCode;
	public $SqlErrorInfo;
	
	public function __construct($message=null,$db=null,$code=null,$previous=null) {
		parent::__construct($message, $code, $previous);
		if($db != null) {
			$this->SqlErrorCode = $db->errorCode();
			$this->SqlErrorInfo = $db->errorInfo();
		}
	}
	
	public function __toString() {
		$str = parent::__toString();
		$str .= "\nSqlErrorCode:" . var_export($this->SqlErrorCode, true); 
		$str .= "\nSqlErrorInfo:" . var_export($this->SqlErrorInfo, true);
		
		return $str;
	}
	
} 

?>