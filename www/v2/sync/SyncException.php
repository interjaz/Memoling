<?php

class SyncException extends Exception {
	
	public $SyncAction;

	public function __construct($message, $action) {
		$this->message = $message;
		$this->SyncAction = $action;	
	}
	
	public function __toString() {
		$str = parent::__toString();
		$str .= "\nSync Action:" . var_export($this->SyncAction, true);
	
		return $str;
	}
}