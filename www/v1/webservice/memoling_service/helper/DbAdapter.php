<?php
require_once("../Init.php");

class DbAdapter {

	protected static function connect() {

		$connectionString = sprintf(
				"mysql:host=%s;dbname=%s",
				Config::$Db_Host,
				Config::$Db_Database
		);


		$dbh = new PDO($connectionString, Config::$Db_Username, Config::$Db_Password, array(PDO::ATTR_PERSISTENT => true));
		$dbh->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );
		$dbh->exec("set names utf8");
		return $dbh;
	}

}

?>