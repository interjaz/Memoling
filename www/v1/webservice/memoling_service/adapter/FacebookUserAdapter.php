<?php
require_once("../Init.php");

class FacebookUserAdapter extends DbAdapter {
	
	protected $db;
	
	public function __construct() {
		$this->db = parent::connect();
	}
	
	public function getAll() {
		
		$stm = $this->db->prepare("SELECT * FROM memoling_FacebookUsers");
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
			
		$updatedTime = null;
		try {
			$updatedTime = $this->userUpdatedTime($facebookUser);
		}
		catch(Exception $ex) {
			Log::save("Exception", $ex, Log::PRIO_HIGH);
			return false;
		}
						
		if($updatedTime == null) {
			// Create user
			
			$this->db->beginTransaction();
			
			try {

				// Check if hometown exists
				if(!$this->locationExists($facebookUser->Hometown)) {
					// Create hometown
					$this->locationCreate($facebookUser->Hometown);
				}
				
				// Check if location exists
				if(!$this->locationExists($facebookUser->Location)) {
					// Create location
					$this->locationCreate($facebookUser->Location);
				}
				
				// Create user
				$this->userCreate($facebookUser);
				
				$this->db->commit();
				
			} catch(Exception $ex) {
				$this->db->rollBack();
				Log::save("Exception", $ex, Log::PRIO_HIGH);
				return false;
			}
						
		} else {
						
			$utc = new DateTimeZone("UTC");
			$lastUpdate = new DateTime($updatedTime, $utc);
			$nowUpdate = new DateTime($facebookUser->UpdatedTime, $utc);
			
			if($nowUpdate->diff($lastUpdate)->format('%R') == "-") {
				// Update user
				
				$this->db->beginTransaction();
				
				try {

					// Check if hometown exists
					if(!$this->locationExists($facebookUser->Hometown)) {
						// Create hometown
						$this->locationCreate($facebookUser->Hometown);
					}
					
					// Check if location exists
					if(!$this->locationExists($facebookUser->Location)) {
						// Create location
						$this->locationCreate($facebookUser->Location);
					}
					
					// Update user
					$this->userUpdate($facebookUser);
					
					$this->db->commit();
				} catch (Exception $ex) {
					$this->db->rollBack();
					Log::save("Exception", $ex, Log::PRIO_HIGH);
					return false;
				}
			}
		}
		
		return true;
	}

	private function locationExists($facebookLocation) {
		$query = "SELECT
					FacebookLocationId
				  FROM 
					memoling_FacebookLocations
				  WHERE
					FacebookLocationId = :FLid
				";
				
		$stm = $this->db->prepare($query);
		$stm->bindParam(":FLid", $facebookLocation->FacebookLocationId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to check if FacebookLocation exists", $this->db);
		}
		$row = $stm->fetch();		
				
		return $row != null;
	}
	
	private function locationCreate($facebookLocation) {
		$query = "INSERT INTO 
					memoling_FacebookLocations
				  VALUES(:Fid,:Name,CURRENT_TIMESTAMP)";
		
		$stm = $this->db->prepare($query);
		$stm->bindParam(":Fid", $facebookLocation->FacebookLocationId);
		$stm->bindParam(":Name", $facebookLocation->Name);
		if(!$stm->execute()) {			
			throw new SqlException("Failed to create FacebookLocation", $this->db);
		}
	}

	private function userUpdatedTime($facebookUser) {
		$query = "SELECT
					FacebookUserId, UpdatedTime
				  FROM
					memoling_FacebookUsers
				  WHERE
					FacebookUserId = :FacebookUserId";
		
		$stm = $this->db->prepare($query);
		$stm->bindParam(":FacebookUserId", $facebookUser->FacebookUserId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to check if FacebookUser exists", $this->db);
		}
		
		$row = $stm->fetch();
		
		if($row == null) {
			return null;
		}
		
		return $row["UpdatedTime"];
	}
	
	private function userCreate($facebookUser) {

		$query = "INSERT INTO
						memoling_FacebookUsers
					  VALUES(:Fid,:Name,:FirstName,:LastName,:Link,:Username,:Hid,:Lid,:Gender,:Timezone,:Locale,:Verified,:UpdatedTime,CURRENT_TIMESTAMP)";
		$stm = $this->db->prepare($query);
				
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
			throw new SqlException("Failed to create FacebookUser", $this->db);
		}
		
	}
	
	private function userUpdate($facebookUser) {
		
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
		
		$stm = $this->db->prepare($query);
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
			throw new SqlException("Failed to update FacebookUser", $this->db);
		}
	}
}