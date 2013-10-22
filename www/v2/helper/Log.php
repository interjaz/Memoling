<?php

class Log {
	
	const LOG_FILE = "logs/log.txt";
	const LOG_FILE_BAK = "logs/log.txt.bak";
	const LOG_FILE_ERR = "logs/log.err.txt";
	const PRIO_FLOW = "FLOW";
	const PRIO_DEFAULT = "DEFAULT";
	const PRIO_HIGH = "HIGH";
	const MAX_FILE_SIZE = 1048576; // 1 MB
	
	public static function save($msg, $ex=null, $priority=Log::PRIO_DEFAULT) {
		
		$server = $_SERVER["DOCUMENT_URI"];
		$slashes = substr_count($server, "/");
		
		$basePath = Helper::getRoot();
		
				
		// Replace new lines to \n sign
		$ex = str_replace(array("\r", "\n"), array('', '\n'), $ex);
		$msg = str_replace(array("\r", "\n"), array('', '\n'), $msg);
		
		$log = date("d-m-y H:i:s") . "¦IP:" . $_SERVER['REMOTE_ADDR'] . "¦P:" . $priority . "¦MSG:" . $msg . "¦EX:" . $ex . "\r\n";
		
		$handle = fopen($basePath . Log::LOG_FILE, "a+");
		fwrite($handle, $log);
		fclose($handle);
		
		if(filesize($basePath . Log::LOG_FILE) > Log::MAX_FILE_SIZE) {
			if(file_exists($basePath . Log::LOG_FILE_BAK)) {
				unlink($basePath . Log::LOG_FILE_BAK);
			}
			copy($basePath . Log::LOG_FILE, $basePath . Log::LOG_FILE_BAK);
			unlink($basePath . Log::LOG_FILE);
		}
		
		if($priority == Log::PRIO_DEFAULT || $priority == Log::PRIO_HIGH) {
			$handle = fopen($basePath . Log::LOG_FILE_ERR, "a+");
			fwrite($handle, $log);
			fclose($handle);
		}
	}
	
	
}


?>