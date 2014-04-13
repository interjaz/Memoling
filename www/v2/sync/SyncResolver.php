<?php 
//
// Think twice before chaning anthing here.
// There is a series of unit tests associated 
// with this file. See webservice/synctest.php
//

class SyncResolver {
	
	public $PendingServerActions;
	public $PendingClientActions;
	
	private $groupedServerActions;
	private $groupedClientActions;
	
	public function __construct() {
		$this->PendingServerActions = array();
		$this->PendingClientActions = array();
		
		$this->groupedServerActions = array();
		$this->groupedClientActions = array();
	}
	
	public function resolveEngine($clientActions, $serverActions) {

		// Group client actions
		$this->groupedClientActions = $this->groupActions($clientActions);
		
		// Group server actions
		$this->groupedServerActions = $this->groupActions($serverActions);
		
		// Merge client actions
		foreach($this->groupedClientActions as $key=>$grouped) {
			$this->mergeClient($key);
		}
		
		// Merge server actions
		foreach($this->groupedServerActions as $key=>$grouped) {
			$this->mergeServer($key);
		}
	}
	
	// Leaves groupedClientActions unchanged
	// Modifies groupedserverActions accordingly
	private function mergeClient($key) {
		$hasServerActions = array_key_exists($key, $this->groupedServerActions);

		$clientActions = &$this->groupedClientActions[$key];
		$serverActions = null;
		
		if(!$hasServerActions) {
			// Check if first is deleted
			if($clientActions[0]->Action == SyncAction::ACTION_DELETE) {
				$this->PendingServerActions[] = $clientActions[0];
				return;
			}
			
			// No server actions, apply all from client
			foreach($clientActions as $clientAction) {
				$this->PendingServerActions[] = $clientAction;
			}

			return;
		}
		
		// Merge & Resolve Conflicts
		// If the single last action was delete - ignore rest actions
		// Otherwise ignore delete
		$serverActions = &$this->groupedServerActions[$key];
		
		// We assume operations are already order by timestamp desc
		$finished = $this->mergeClient_ResolveDeletes($key, $clientActions, $serverActions);
		
		if($finished) {
			return;
		}

		$finished = $this->mergeClient_ResolveUpdates($key, $clientActions, $serverActions);
		
		if($finished) {
			return;
		}
		
		$finished = $this->mergeClient_ResolveInserts($key, $clientActions, $serverActions);
		
		// At this point all Client Action for this $key should have been processed
		// We cannot discard them however, since we are in foreach loop
	}


	private function mergeClient_ResolveDeletes($key, &$clientActions, &$serverActions) {
	
		$isDeletedClient = $clientActions[0]->Action == SyncAction::ACTION_DELETE;
		$isDeletedServer = $serverActions[0]->Action == SyncAction::ACTION_DELETE;
		
		if($isDeletedClient || $isDeletedServer) {
				
			$isLatterClient = $clientActions[0]->ServerTimestamp > $serverActions[0]->ServerTimestamp;
			$isLatterServer = $clientActions[0]->ServerTimestamp < $serverActions[0]->ServerTimestamp;
			$isAtSameTime = !$isLatterClient && !$isLatterServer;
				
			$updateClient = ($isLatterServer && $isDeletedServer);
			$updateServer = ($isLatterClient && $isDeletedClient);
				
			$toDelete = $updateClient ||
			$updateServer ||
			($isAtSameTime && $isDeletedClient && $isDeletedServer);
				
			if($updateClient) {
				$this->PendingClientActions[] = $serverActions[0];
			}
				
			if($updateServer) {
				$this->PendingServerActions[] = $clientActions[0];
			}
				
				
			// If deleted only on server OR client (not both)
			if(!$toDelete) {
				// Not deleted - recreate object using conflicted one
				if($isDeletedClient) {
					$serverActions[0]->Action = SyncAction::ACTION_INSERT;
					$this->PendingClientActions[] = $serverActions[0];
				}
		
				if($isDeletedServer) {
					$clientActions[0]->Action = SyncAction::ACTION_INSERT;
					$this->PendingServerActions[] = $clientActions[0];
				}
			}
				
			// Discard servers rest
			unset($this->groupedServerActions[$key]);
			return true;
		}
		
		return false;
	}
	
	private function mergeClient_ResolveUpdates($key, &$clientActions, &$serverActions) {

		// Assume one operation per column
		foreach($clientActions as $clientKey=>$clientAction) {
		
			// Ignore deletes and inserts
			if($clientAction->Action == SyncAction::ACTION_DELETE ||
				$clientAction->Action == SyncAction::ACTION_INSERT) {
				continue;
			}
				
			$conflictAction = null;
			$conflictKey = null;
				
			$serverActionsToDiscard = array();
			foreach($serverActions as $serverKey=>$serverAction) {
				// Discard deletes
				if($serverAction->Action == SyncAction::ACTION_DELETE) {
					$serverActionsToDiscard[$serverKey] = $serverAction;
					continue;
				}
					
				// Ignore inserts
				if($serverAction->Action == SyncAction::ACTION_INSERT) {
					continue;
				}
		
				if($clientAction->UpdateColumn == $serverAction->UpdateColumn) {
					$conflictAction = $serverAction;
					$conflictKey = $serverKey;
					break;
				}
			}
				
			// Clear ones to discard
			foreach($serverActionsToDiscard as $serverKey=>$serverAction) {
				unset($serverActions[$serverKey]);
			}
				
			// Conflict
			if($conflictAction != null) {
				// Check timestamps to get the most recent one
				if($conflictAction->ServerTimestamp > $clientAction->ServerTimestamp) {
					// Server wins
					$this->PendingClientActions[] = $conflictAction;
				} else {
					// Client wins
					$this->PendingServerActions[] = $clientAction;
				}
		
				unset($serverActions[$conflictKey]);
			} else {
				// Client wins
				$this->PendingServerActions[] = $clientAction;
			}
		}
		
		if(count($serverActions) == 0) {
			unset($this->groupedServerActions[$key]);
		}
		
		return false;
	}
	
	private function mergeClient_ResolveInserts($key, &$clientActions, &$serverActions) {
		
		foreach($clientActions as $clientAction) {

			if($clientAction->Action != SyncAction::ACTION_INSERT) {
				continue;	
			}
			
			$skipInsert = false;
			
			$serverActionsToDiscard = array();
			// Add only if not on the server
			foreach($serverActions as $serverAction) {
				if($serverAction->Action != SyncAction::ACTION_INSERT) {
					continue;
				}
				
				if($clientAction->Table == $serverAction->Table &&
				   $clientAction->PrimaryKey == $serverAction->PrimaryKey) {
					$serverActionsToDiscard[] = $serverAction;
					$skipInsert = true;
					break;
				}
			}

			// Clear ones to discard
			foreach($serverActionsToDiscard as $serverKey=>$serverAction) {
				unset($serverActions[$serverKey]);
			}
			
			if(!$skipInsert) {
				$this->PendingServerActions[] = $clientAction;
			}
		}

		if(count($serverActions) == 0) {
			unset($this->groupedServerActions[$key]);
		}
		
		return false;
	}
	
	private function mergeServer($key) {
		// Assume that all client actions has been already processed and only these have left
		// Also we assume, that groupedServerActions has been filtered by the merge clients, so there is no conflicts
		$serverActions = &$this->groupedServerActions[$key];

		// Check if first is deleted
		if($serverActions[0]->Action == SyncAction::ACTION_DELETE) {
			$this->PendingClientActions[] = $serverActions[0];
			return;
		}
		
		foreach($serverActions as $serverAction) {
			$this->PendingClientActions[] = $serverAction;
		}
	}
	
	private function groupActions($actions) {
		$grouped = array();
		foreach($actions as $action) {
			$key = (string)$action->PrimaryKey .'#' . $action->Table;
			if(!array_key_exists($key, $grouped)) {
				$grouped[$key] = array();
			}

			$grouped[$key][] = $action;
		}
		
		return $grouped;
	}
	
	private function filterActions($serverActionObjects, $clientAction) {

		foreach($serverActionObjects as $serverActionObject) {
			if($serverActionObject->Action->Table == $clientAction->Table && 
			   $serverActionObject->Action->PrimaryKey == $clientAction->PrimaryKey) {
				return $serverActionObject;
			}
		}
		
		return null;
	}
	
	
	
}

?>