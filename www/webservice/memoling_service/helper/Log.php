<?php
require_once("../Init.php");

class Log {
	
	const LOG_FILE = "../log.txt";
	const PRIO_FLOW = "FLOW";
	const PRIO_DEFAULT = "Default";
	
	public static function save($msg, $ex=null, $priority=Log::PRIO_DEFAULT) {
		
		// Replace new lines to \n sign
		$ex = str_replace(array("\r", "\n"), array('', '\n'), $ex);
		$msg = str_replace(array("\r", "\n"), array('', '\n'), $msg);
		
		$log = date("d-m-y H:i:s") . "¦P:" . $priority . "¦MSG:" . $msg . "¦EX:" . $ex . "\r\n";
		
		$handle = fopen(Log::LOG_FILE, "a+");
		fwrite($handle, $log);
		fclose($handle);
	}
	
	
}


?>