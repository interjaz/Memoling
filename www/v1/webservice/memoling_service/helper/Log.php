<?php
require_once("../Init.php");

class Log {
	
	const LOG_FILE = "../log.txt";
	const LOG_FILE_BAK = "../log.txt.bak";
	const LOG_FILE_ERR = "../log.err.txt";
	const PRIO_FLOW = "FLOW";
	const PRIO_DEFAULT = "DEFAULT";
	const PRIO_HIGH = "HIGH";
	const MAX_FILE_SIZE = 1048576; // 1 MB
	
	public static function save($msg, $ex=null, $priority=Log::PRIO_DEFAULT) {
		
		// Replace new lines to \n sign
		$ex = str_replace(array("\r", "\n"), array('', '\n'), $ex);
		$msg = str_replace(array("\r", "\n"), array('', '\n'), $msg);
		
		$log = date("d-m-y H:i:s") . "¦IP:" . $_SERVER['REMOTE_ADDR'] . "¦P:" . $priority . "¦MSG:" . $msg . "¦EX:" . $ex . "\r\n";
		
		$handle = fopen(Log::LOG_FILE, "a+");
		fwrite($handle, $log);
		fclose($handle);
		
		if(filesize(Log::LOG_FILE) > Log::MAX_FILE_SIZE) {
			if(file_exists(Log::LOG_FILE_BAK)) {
				unlink(Log::LOG_FILE_BAK);
			}
			copy(Log::LOG_FILE, Log::LOG_FILE_BAK);
			unlink(Log::LOG_FILE);
		}
		
		if($priority == Log::PRIO_DEFAULT || $priority == Log::PRIO_HIGH) {
			$handle = fopen(Log::LOG_FILE_ERR, "a+");
			fwrite($handle, $log);
			fclose($handle);
		}
	}
	
	
}


?>