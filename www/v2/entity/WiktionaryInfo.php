<?php

class WiktionaryInfo {

	public $WiktionaryInfoId;
	public $Name;
	public $Description;
	public $Language;
	public $Version;
	public $DownloadUrl;
	public $DownloadSize;
	public $RealSize;
	
	public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("wiktionaryInfoId", $this->WiktionaryInfoId);
		$builder->put("name", $this->Name);
		$builder->put("description", $this->Description);
		$builder->put("language", $this->Language);
		$builder->put("version", $this->Version);
		$builder->put("downloadUrl", $this->DownloadUrl);
		$builder->put("downloadSize", $this->DownloadSize);
		$builder->put("realSize", $this->RealSize);
	
		return $builder->__toString();
	}
	
}