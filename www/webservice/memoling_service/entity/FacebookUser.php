<?php
require_once("../Init.php");

class FacebookUser {
	
	public $FacebookUserId;
	public $Name;
	public $FirstName;
	public $LastName;
	public $Link;
	public $Username;
	public $HometownId;
	public $LocationId;
	public $Gender;
	public $Timezone;
	public $Locale;
	public $Verified;
	public $UpdatedTime;
	public $Created;
	
	public $Hometown;
	public $Location;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->FacebookUserId= isset($obj->facebookUserId)?$obj->facebookUserId:null;
		$this->Name= isset($obj->name)?$obj->name:null;
		$this->FirstName= isset($obj->firstName)?$obj->firstName:null;
		$this->LastName= isset($obj->lastName)?$obj->lastName:null;
		$this->Link= isset($obj->link)?$obj->link:null;
		$this->Username= isset($obj->username)?$obj->username:null;
		$this->HometownId= isset($obj->hometown)?$obj->hometown:null;
		$this->LocationId= isset($obj->locationId)?$obj->locationId:null;
		$this->Gender= isset($obj->gender)?$obj->gender:null;
		$this->Timezone= isset($obj->timezone)?$obj->timezone:null;
		$this->Locale= isset($obj->locale)?$obj->locale:null;
		$this->Verified= isset($obj->verified)?$obj->verified:null;
		$this->UpdatedTime= isset($obj->updatedTime)?substr($obj->updatedTime,0,19):null;
		$this->Created= isset($obj->created)?$obj->created:null;
		
		if(isset($obj->location)) 
		{
			$location = new FacebookLocation();
			$location->decode($obj->location);
			$this->Location = $location;	
		}
		
		if(isset($obj->hometown)) 
		{
			$hometown = new FacebookLocation();
			$hometown->decode($obj->hometown);
			$this->Hometown = $hometown;	
		}
	}
}

?>