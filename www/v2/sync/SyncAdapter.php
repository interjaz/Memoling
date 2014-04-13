<?php 

interface SyncAdapter {
	
	public function decodeEntity($json);
	public function getEntity($primaryKey);
	public function insertEntity($dbTran, $object, $syncAction);
	public function deleteEntity($dbTran, $primaryKey, $syncAction);
	public function updateEntity($dbTran, $object, $syncAction);
	
}

?>