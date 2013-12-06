<?php

class PublishedMemo {

	public $PublishedMemoId;
	public $MemoId;
	public $FacebookUserId;
	public $Created;
	
	public $Memo;
	public $FacebookUser;
	

	public function encode() {
	
		$builder = new JsonBuilder();
	
		$builder->put("publishedMemoId", $this->PublishedMemoId);
		$builder->put("memo", $this->Memo);
		
	
		return $builder->__toString();
	}
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->PublishedMemoId =  isset($obj->publishedMemoId)?$obj->publishedMemoId:null;
		$this->MemoId =  isset($obj->memoId)?$obj->memoId:null;
		$this->FacebookUserId =  isset($obj->facebookUserId)?$obj->facebookUserId:null;
		$this->Created =  isset($obj->created)?$obj->created:null;
		
		if(isset($obj->memo)) {
			$this->Memo = new Memo();
			$this->Memo->decode($obj->memo);
		}
		
		if(isset($obj->facebookUser)) {
			$inner = new FacebookUser();
			$inner->decode($obj->facebookUser);
			$this->FacebookUser = $inner;
		}
	}
}
	
?>