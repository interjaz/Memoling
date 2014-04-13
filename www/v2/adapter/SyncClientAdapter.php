<?php

class SyncClientAdapter extends DbAdapter {
	
	public function get($syncClientId) {
		return self::getDb(parent::connect(), $syncClientId);
	}
	
	public static function getDb($db, $syncClientId) {
		
		$query = "SELECT * FROM memoling_SyncClients WHERE SyncClientId = :SyncClientId";
		$stm = $db->prepare($query);
		$stm->bindParam("SyncClientId", $syncClientId);
		$stm->execute();
		
		if($row = $stm->fetch()) {
			$obj = self::bindSyncClient($row);
			return $obj;
		}
		
		return null;
	}
	
	public function register($syncClient) {
		return self::registerDb(parent::transConnect(), $syncClient);
	}
	
	public static function registerDb($db, $syncClient) {
		
        $query = "INSERT INTO 
                  memoling_SyncClients(SyncClientId,FacebookUserId, Description)
                VALUES(:SyncClientId,:FacebookUserId,:Description)";
        
        $stm = $db->prepare($query);
        $stm->bindParam("SyncClientId", $syncClient->SyncClientId);
        $stm->bindParam("FacebookUserId", $syncClient->FacebookUserId);
        $stm->bindParam("Description", $syncClient->Description);
        
		if(!$stm->execute()) {
			throw new SqlException("Failed to create SyncClientId", $dbTran);
		}
	}

	public function getByFacebookUserId($facebookUserId) {
		return SyncClientAdapter::getByFacebookUserIdDb(parent::connect(), $facebookUserId);
	}
	
	public static function getByFacebookUserIdDb($db, $facebookUserId) {
	
		$stm = $db->prepare("SELECT * FROM memoling_SyncClients WHERE FacebookUserId = :FacebookUserId");
		$stm->bindParam("FacebookUserId", $facebookUserId);
		$stm->execute();
	
		if($row = $stm->fetch()) {
			$obj = self::bindSyncClient($row);
            return $obj;
		}
	
		return null;
	}
	
	public function insert($syncClient) {
		return SyncClientAdapter::insertDb(parent::connect(), $syncClient);
	}
	
	public static function insertDb($db, $syncClient) {
	
		$db->beginTransaction();
	
		try {
	
			// Insert Memo
			$memoId = Helper::newGuid();
			$query = "INSERT INTO
					memoling_SyncClients
					VALUES(:SyncClientId,:FacebookId,:Description)";
			$stm = $db->prepare($query);
			$stm->bindParam(":SyncClientId", $syncClient->SyncClientId);
			$stm->bindParam(":FacebookUserId", $syncClient->FacebookUSerId);
			$stm->bindParam(":Description", $syncClient->Description);
	
			if(!$stm->execute()) {
				throw new SqlException("Failed to create new SyncClient", $db);
			}
	
			$db->commit();
	
		} catch(Exception $ex) {
			$db->rollBack();
			throw $ex;
		}
	}
	
	private static function bindSyncClient($row) {
		$client = new SyncClient();
		$client->SyncClientId = $row["SyncClientId"];
		$client->FacebookUserId = $row["FacebookUserId"];
		$client->Description = $row["Description"];
		
		return $client;
	}
	
}

?>