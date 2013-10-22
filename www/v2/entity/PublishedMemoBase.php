<?php

class PublishedMemoBase {
	
	public $PublishedMemoBaseId;
	public $FacebookUserId;
	public $MemoBaseId;
	public $MemoBaseGenreId;
	public $Description;
	public $Downloads;
	public $AdminsScore;
	public $UsersScore;
	public $Created;
	public $PrimaryLanguageAIso639;
	public $PrimaryLanguageBIso639;
	
	public $FacebookUser;
	public $MemoBase;
	public $MemoBaseGenre;
	
	public $MemosCount;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->PublishedMemoBaseId = isset($obj->publishedMemoBaseId)?$obj->publishedMemoBaseId:null;
		$this->FacebookUserId = isset($obj->facebookUserId)?$obj->facebookUserId:null;
		$this->MemoBaseId = isset($obj->memoBaseId)?$obj->memoBaseId:null;
		$this->MemoBaseGenreId = isset($obj->memoBaseGenreId)?$obj->memoBaseGenreId:null;
		$this->Description = isset($obj->description)?$obj->description:null;
		$this->Downloads = isset($obj->downloads)?$obj->downloads:null;
		$this->AdminsScore = isset($obj->adminsScore)?$obj->adminsScore:null;
		$this->UsersScore = isset($obj->usersScore)?$obj->usersScore:null;
		$this->Created = isset($obj->created)?$obj->created:null;
		$this->PrimaryLanguageAIso639 = isset($obj->primaryLanguageAIso639)?$obj->primaryLanguageAIso639:null;
		$this->PrimaryLanguageBIso639 = isset($obj->primaryLanguageBIso639)?$obj->primaryLanguageBIso639:null;
		
		if(isset($obj->facebookUser)) {
			$inner = new FacebookUser();
			$inner->decode($obj->facebookUser);
			$this->FacebookUser = $inner;
		}
		
		if(isset($obj->memoBase)) {
			$inner = new MemoBase();
			$inner->decode($obj->memoBase);
			$this->MemoBase = $inner;
			
			if($this->PrimaryLanguageAIso639 == null) {
				$this->getPrimaryLanguages();
			}
		}
		
		if(isset($obj->memoBaseGenre)) {
			$inner = new MemoBaseGenre();
			$inner->decode($obj->memoBaseGenre);
			$this->MemoBaseGenre = $inner;
		}
	}
	
	public function encode() {
		
		$builder = new JsonBuilder();
		
		$builder->put("publishedMemoBaseId", $this->PublishedMemoBaseId);
		$builder->put("memoBaseGenreId", $this->MemoBaseGenreId);
		$builder->put("memoBase", $this->MemoBase);
		$builder->put("description", $this->Description);
		$builder->put("downloads", $this->Downloads);
		$builder->put("primaryLanguageAIso639", $this->PrimaryLanguageAIso639,true);
		$builder->put("primaryLanguageBIso639", $this->PrimaryLanguageBIso639,true);
		$builder->put("memosCount", $this->MemosCount);
				
		return $builder->__toString();
	}
	
	private function getPrimaryLanguages() {
		$languageA = "EN";
		$languageB = "EN";
		
		$langArray = array();
		foreach($this->MemoBase->Memos as $memo) {
			$langA = $memo->WordA->LanguageIso639;
			$langB = $memo->WordB->LanguageIso639;
			
			if(strcmp($langA,$langB) > 0) {
				$tmp = $langA;
				$langA = $langB;
				$langB = $tmp;
			}

			$key = $langA . "," . $langB;
			$exists = array_key_exists($key, $langArray);
			if($exists) {
				$langArray[$key] = $langArray[$key] + 1;
			} else {
				$langArray[$key] = 0;
			}
		}
		
		$maxKey = "";
		$maxValue = -1;
		foreach($langArray as $key=>$value) {
			if($maxValue < $value) {
				$maxKey = $key;
				$maxValue = $value;
			}
		}
		
		if($maxValue != -1) {
			$langs = explode(",", $maxKey);
			$languageA = $langs[0];
			$languageB = $langs[1];
		}
		
		$this->PrimaryLanguageAIso639 = $languageA;
		$this->PrimaryLanguageBIso639 = $languageB;
	}
}

?>