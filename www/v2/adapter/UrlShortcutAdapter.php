<?php

class UrlShortcutAdapter extends DbAdapter {

	const MEMOLING_SHORTCUT_BASE = "http://memoling.com/s.php?";
	

	public function getUrl($shortcut) {
		$db = parent::connect();
		
		$stm = $db->prepare("SELECT Url FROM memoling_UrlShortcuts WHERE Shortcut = :Shortcut");
		$stm->bindParam("Shortcut", $shortcut);
		$stm->execute();
		
		if($row = $stm->fetch()) {
			return $row["Url"];
		}
		
		return null;
	}
	
	public function newShortcut($url) {
		$db = parent::transConnect();
		
		$db->exec("LOCK TABLES memoling_UrlShortcuts WRITE");
		
		try {
			$stm = $db->prepare("SELECT SQL_NO_CACHE Shortcut FROM memoling_UrlShortcuts ORDER BY ShortcutId DESC LIMIT 1");
			$stm->execute();
	
			$lastShortcut = "a";
			if($row = $stm->fetch()) {
				$lastShortcut = $row["Shortcut"];
			}
			
			$nextShortcut = UrlShortcut::nextShortcut($lastShortcut);
			
			$stm = $db->prepare("INSERT INTO memoling_UrlShortcuts VALUES (NULL, :Shortcut, :Url, UTC_TIMESTAMP())");
			$stm->bindParam("Shortcut", $nextShortcut);
			$stm->bindParam("Url", $url);
			if(!$stm->execute()) {
				throw new SqlException("Failed to create new shortcut", $db);
			}
			
			$db->exec('UNLOCK TABLES');
			
			$shortcut = new UrlShortcut();
			$shortcut->Shortcut = self::MEMOLING_SHORTCUT_BASE . $nextShortcut;
			
			return $shortcut;
		} catch(Exception $ex) {
			$db->exec('UNLOCK TABLES');
			throw $ex;
		}
	}
	
} 

?>