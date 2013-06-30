<?php
require_once("../Init.php");

class Memo {
	// Database columns
	public $MemoId;
	public $MemoBaseId;
	public $WordAId;
	public $WordBId;
	public $Created;
	public $LastReviewed;
	public $Displayed;
	public $CorrectAnsweredWordA;
	public $CorrectAnsweredWordB;
	public $Active;
	// Extra columns
	public $WordA;
	public $WordB;
	public $MemoBase;

	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);

		if($obj == null) {
			throw new JsonException();
		}

		$this->MemoId= isset($obj->memoId)?$obj->memoId:null;
		$this->MemoBaseId= isset($obj->memoBaseId)?$obj->memoBaseId:null;
		$this->WordAId= isset($obj->wordAId)?$obj->wordAId:null;
		$this->WordBId= isset($obj->wordBId)?$obj->wordBId:null;
		$this->Created= isset($obj->created)?$obj->created:null;
		$this->LastReviewed= isset($obj->lastReviewed)?$obj->lastReviewed:null;
		$this->Displayed= isset($obj->displayed)?$obj->displayed:null;
		$this->CorrectAnsweredWordA= isset($obj->correctAnsweredWordA)?$obj->correctAnsweredWordA:null;
		$this->CorrectAnsweredWordB= isset($obj->correctAnsweredWordB)?$obj->correctAnsweredWordB:null;
		$this->Active= isset($obj->active)?$obj->active:null;

		if(isset($obj->wordA)) {
			$inner = new Word();
			$inner->decode($obj->wordA);
			$this->WordA = $inner;
		}

		if(isset($obj->wordB)) {
			$inner = new Word();
			$inner->decode($obj->wordB);
			$this->WordB = $inner;
		}

		if(isset($obj->memoBase)) {
			$inner = new MemoBase();
			$inner->decode($obj->memoBase);
			$this->memoBase = $inner;
		}

	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("wordA", $this->WordA);
		$builder->put("wordB", $this->WordB);
		
		return $builder->__toString();
	}
}