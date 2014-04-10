<?php

class SafePDO extends PDO {

	private $m_tranCounter;
	private $m_tranSuccessful;

	public function __constructor($dsn, $username, $password, $driver_options) {		
		parent::_construct($dsn, $username, $password, $driver_options);
		$this->m_tranCounter = 0;
	}

	// Override default behaviour
	public function beginTransaction() {
		if($this->m_tranCounter == 0) {
			parent::beginTransaction();
			$this->m_tranSuccessful = true;
		}
		
		$this->m_tranCounter++;
	}

	// Override default behaviour
	public function commit() {
		$this->m_tranCounter--;

		if($this->m_tranCounter < 0) {
			throw new Exception("Number of Commits and RollBack (" . $this->m_tranCounter . ") does not match number of BeginTransaction calls");
		}
		
		if($this->m_tranCounter == 0) {
			if(!$this->m_tranSuccessful) {
				parent::rollBack();
				throw new Exception("Some transactions were unsuccessful");
			} else {
				parent::commit();
			}
		}
	}

	// Override default behaviour
	public function rollBack() {
		$this->m_tranCounter--;
		
		if($this->m_tranCounter < 0) {
			throw new Exception("Number of Commits and RollBack (" . $this->m_tranCounter . ") does not match number of BeginTransaction calls");
		}
		
		$this->m_tranSuccessful = false;
		
		if($this->m_tranCounter == 0) {
			parent::rollBack();
		} 
	}
}