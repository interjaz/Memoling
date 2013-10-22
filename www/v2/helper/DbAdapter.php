<?php

class DbAdapter {

	// Warning!
	// Persistant cannot be true when using SafePDO
	
	private $m_connectionString;
	private $m_connectDbh;
	private $m_transConnectDbh;
	
	public function __construct() {
		$this->m_connectionString = sprintf(
				"mysql:host=%s;dbname=%s",
				Config::$Db_Host,
				Config::$Db_Database
		);		
	}
	
	
	// Do not use this connection when using transactions
	protected function connect() {

		if($this->m_connectDbh != null) {
			return $this->m_connectDbh;
		}
		
		$dbh = new PDO($this->m_connectionString, Config::$Db_Username, Config::$Db_Password, array(PDO::ATTR_PERSISTENT => true));
		$dbh->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );
		$dbh->exec("set names utf8");
		$this->m_connectDbh = $dbh;
		
		return $dbh;
	}
		
	// Do not use this connection for selects
	protected function transConnect() {

		if($this->m_transConnectDbh != null) {
			return $this->m_transConnectDbh;
		}
		
		$dbh = new SafePDO($this->m_connectionString, Config::$Db_Username, Config::$Db_Password, array(PDO::ATTR_PERSISTENT => false));
		$dbh->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );
		$dbh->exec("set names utf8");
		
		$this->m_transConnectDbh = $dbh;
		
		return $dbh;
	}
	
}

?>