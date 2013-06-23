<?php
require_once("../Init.php");

class DbAdapter {
	
	protected static function connect() {
		
		$connectionString = sprintf(
				"mysql:host=%s;dbname=%s",
				Config::$Db_Host,
				Config::$Db_Database
				);
	
		
		$dbh = new PDO($connectionString, Config::$Db_Username, Config::$Db_Password);
		$dbh->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );
		return $dbh;
	} 
	
}

?>