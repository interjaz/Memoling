<?php

class SqlException extends Exception {

    public $SqlState;
	public $SqlErrorCode;
	public $SqlErrorMessage;
	
	public function __construct($message=null,$stm=null,$code=null,$previous=null) {
		parent::__construct($message, $code, $previous);
		if($db != null) {
            $err = $stm->errorInfo();
			$this->SqlState = $err[0];
			$this->SqlErrorCode = $err[1];
			$this->SqlErrorMessage = $err[2];
		}
	}
	
	public function __toString() {
		$str = parent::__toString();
		$str .= "\nSqlErrorState:" . var_export($this->SqlState, true); 
		$str .= "\nSqlErrorCode:" . var_export($this->SqlErrorCode, true); 
		$str .= "\nSqlErrorMessage:" . var_export($this->SqlErrorMessage, true);
		
		return $str;
	}
	
} 

?>