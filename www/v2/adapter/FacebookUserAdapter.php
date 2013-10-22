<?php

class FacebookUserAdapter extends DbAdapter {
	
	public function getAll() {
		
		$db = parent::connect();
		$stm = $db->prepare("SELECT * FROM memoling_FacebookUsers");
		$result = $stm->execute();
		
		$list = array();
		while($row = $stm->fetch()) {
			$obj = new FacebookUser();
			$obj->FacebookUserId = $row["FacebookUserId"];
			$obj->FirstName = $row["FirstName"];
			$obj->LastName = $row["LastName"];
			$obj->CreatedAt = $row["CreatedAt"];
			$obj->UpdatedAt = $row["UpdatedAt"];
			$obj->Link = $row["Link"];
			$obj->Locale = $row["Locale"];
			$obj->HometownId = $row["HometownId"];
			$obj->LocationId = $row["LocationId"];
			$obj->Name = $row["Name"];
			$obj->Timezone = $row["Timezone"];
			$obj->UpdatedTime = $row["UpdatedTime"];
			$obj->Username = $row["Username"];
			$obj->Verified  =$row["Verified"];
			
			$list[] = $obj;
		}
		
		return $list;
	}
	
	public function update($facebookUser) {
		return self::updateDb(parent::transConnect(), $facebookUser);
	}
	
	public static function updateDb($db, $facebookUser) {
		
		$updatedTime = null;
		try {
			$updatedTime = self::userUpdatedTimeDb($db,$facebookUser);
		}
		catch(Exception $ex) {
			Log::save("Exception", $ex, Log::PRIO_HIGH);
			return false;
		}
								
		if($updatedTime == null) {
			// Create user
			
			$db->beginTransaction();
			
			try {

				// Check if hometown exists
				if(!self::locationExistsDb($db, $facebookUser->Hometown)) {
					// Create hometown
					self::locationCreateDb($db, $facebookUser->Hometown);
				}
				
				// Check if location exists
				if(!self::locationExistsDb($db, $facebookUser->Location)) {
					// Create location
					self::locationCreateDb($db, $facebookUser->Location);
				}
				
				// Create user
				self::userCreateDb($db, $facebookUser);
				
				$db->commit();
				
			} catch(Exception $ex) {
				$db->rollBack();
				Log::save("Exception", $ex, Log::PRIO_HIGH);
				return false;
			}
						
		} else {
						
			$utc = new DateTimeZone("UTC");
			$lastUpdate = new DateTime($updatedTime, $utc);
			$nowUpdate = new DateTime($facebookUser->UpdatedTime, $utc);
			
			if($nowUpdate->diff($lastUpdate)->format('%R') == "-") {
				// Update user
				
				$db->beginTransaction();
				
				try {

					// Check if hometown exists
					if(!self::locationExistsDb($db, $facebookUser->Hometown)) {
						// Create hometown
						self::locationCreateDb($db, $facebookUser->Hometown);
					}
					
					// Check if location exists
					if(!self::locationExistsDb($db, $facebookUser->Location)) {
						// Create location
						self::locationCreateDb($db, $facebookUser->Location);
					}
					
					// Update user
					self::userUpdateDb($db, $facebookUser);
					
					$db->commit();
					
				} catch (Exception $ex) {
					$db->rollBack();
					Log::save("Exception", $ex, Log::PRIO_HIGH);
					return false;
				}
			}
		}
		return true;
	}

	private static function locationExistsDb($db, $facebookLocation) {
		$query = "SELECT
					FacebookLocationId
				  FROM 
					memoling_FacebookLocations
				  WHERE
					FacebookLocationId = :FLid
				";
				
		$stm = $db->prepare($query);
		$stm->bindParam(":FLid", $facebookLocation->FacebookLocationId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to check if FacebookLocation exists", $db);
		}
		$row = $stm->fetch();		
				
		return $row != null;
	}
	
	private static function locationCreateDb($db, $facebookLocation) {
		$query = "INSERT INTO 
					memoling_FacebookLocations
				  VALUES(:Fid,:Name,UTC_TIMESTAMP())";
		
		$stm = $db->prepare($query);
		$stm->bindParam(":Fid", $facebookLocation->FacebookLocationId);
		$stm->bindParam(":Name", $facebookLocation->Name);
		if(!$stm->execute()) {			
			throw new SqlException("Failed to create FacebookLocation", $db);
		}
	}

	private static function userUpdatedTimeDb($db, $facebookUser) {
		$query = "SELECT
					FacebookUserId, UpdatedTime
				  FROM
					memoling_FacebookUsers
				  WHERE
					FacebookUserId = :FacebookUserId";
		
		$stm = $db->prepare($query);
		$stm->bindParam(":FacebookUserId", $facebookUser->FacebookUserId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to check if FacebookUser exists", $db);
		}
		
		$row = $stm->fetch();
		
		if($row == null) {
			return null;
		}
		
		return $row["UpdatedTime"];
	}
	
	private static function userCreateDb($db, $facebookUser) {

		$query = "INSERT INTO
						memoling_FacebookUsers
					  VALUES(:Fid,:Name,:FirstName,:LastName,:Link,:Username,:Hid,:Lid,:Gender,:Timezone,:Locale,:Verified,:UpdatedTime,CURRENT_TIMESTAMP)";
		$stm = $db->prepare($query);
				
		$stm->bindParam(":Fid", $facebookUser->FacebookUserId);
		$stm->bindParam(":Name", $facebookUser->Name);
		$stm->bindParam(":FirstName", $facebookUser->FirstName);
		$stm->bindParam(":LastName", $facebookUser->LastName);
		$stm->bindParam(":Link", $facebookUser->Link);
		$stm->bindParam(":Username", $facebookUser->Username);
		$stm->bindParam(":Hid", $facebookUser->Hometown->FacebookLocationId);
		$stm->bindParam(":Lid", $facebookUser->Location->FacebookLocationId);
		$stm->bindParam(":Gender", $facebookUser->Gender);
		$stm->bindParam(":Timezone", $facebookUser->Timezone);
		$stm->bindParam(":Locale", $facebookUser->Locale);
		$stm->bindParam(":Verified", $facebookUser->Verified, PDO::PARAM_BOOL);
		$stm->bindParam(":UpdatedTime", $facebookUser->UpdatedTime);
		if(!$stm->execute()) {			
			throw new SqlException("Failed to create FacebookUser", $db);
		}
		
	}
	
	private static function userUpdateDb($db, $facebookUser) {
		
		// Update user
		$query = "UPDATE
					memoling_FacebookUsers
				  SET
					Name = :Name,
					FirstName = :FirstName,
					LastName = :LastName,
					Link = :Link,
					Username = :Username,
					HometownId = :Hid,
					LocationId = :Lid,
					Gender = :Gender,
					Timezone = :Timezone,
					Locale = :Locale,
					Verified = :Verified,
					UpdatedTime = :UpdatedTime
				  WHERE
					FacebookUserId = :Fid
				";
		
		$stm = $db->prepare($query);
		$stm->bindParam(":Fid", $facebookUser->FacebookUserId);
		$stm->bindParam(":Name", $facebookUser->Name);
		$stm->bindParam(":FirstName", $facebookUser->FirstName);
		$stm->bindParam(":LastName", $facebookUser->LastName);
		$stm->bindParam(":Link", $facebookUser->Link);
		$stm->bindParam(":Username", $facebookUser->Username);
		$stm->bindParam(":Hid", $facebookUser->Hometown->FacebookLocationId);
		$stm->bindParam(":Lid", $facebookUser->Location->FacebookLocationId);
		$stm->bindParam(":Gender", $facebookUser->Gender);
		$stm->bindParam(":Timezone", $facebookUser->Timezone);
		$stm->bindParam(":Locale", $facebookUser->Locale);
		$stm->bindParam(":Verified", $facebookUser->Verified, PDO::PARAM_BOOL);
		$stm->bindParam(":UpdatedTime", $facebookUser->UpdatedTime);
		if(!$stm->execute()) {
			throw new SqlException("Failed to update FacebookUser", $db);
		}
	}
}