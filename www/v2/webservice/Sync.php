<?php
require_once("../Init.php");

class Sync extends Webservice {

	private $m_sentenceAdapter;

	public function __construct() {
		$this->m_sentenceAdapter = new TatoebaSentenceAdapter();
	}
	
    public function register() {
        
        try {
            $jsonSyncClient = $_POST['syncClient'];
            $syncClient = new SyncClient();
            $syncClient->decode($jsonSyncClient);
            
            // Check if exists
			$syncClientAdapter = new SyncClientAdapter();
			$client = $syncClientAdapter->getByFacebookUserId($syncClient->FacebookUserId);
	
			if($client == null) {
                // Generate new client id
                $syncClient->SyncClientId = Helper::newGuid();
                
				// Register
                $syncClientAdapter->register($syncClient);
			} else {
                $syncClient = $client;
            }
            
            echo $syncClient->encode();
            
        } catch(Exception $ex) {
			Log::save("Sync.register Exception", $ex, Log::PRIO_HIGH);
            echo "false";
        }
    }
    
    public function syncBase64() {
        try {
            
			// Decode
			$baseSyncPackage = $_POST['syncPackage'];
            $gzipSyncPackage = base64_decode($baseSyncPackage);
            $jsonSyncPackage = gzdecode($gzipSyncPackage);
                        
            Log::save("Sync.SyncBase64 message: " . $jsonSyncPackage);
            
            $jsonServerPackage = $this->sync($jsonSyncPackage);
            $gzipSyncPackage = gzencode($jsonServerPackage);
            $baseSyncPackage =  base64_encode($gzipSyncPackage);
            
            echo $baseSyncPackage;
        } catch(Exception $ex) {
			Log::save("Sync.SyncBase64 Exception", $ex, Log::PRIO_HIGH);
            echo "false";
        }
    }
    
	private function sync($jsonSyncPackage) {
		
		try {
            
			$clientPackage = new SyncPackage();
			$clientPackage->decode($jsonSyncPackage);
			//var_dump($clientPackage);
	
			// Authorize
			$syncClientAdapter = new SyncClientAdapter();
			$client = $syncClientAdapter->get($clientPackage->SyncClientId);
	
			if($client == null) {
				throw new Exception("Not authorized");
			}
            
			// Create Adapter
			$syncAdapter = new SyncActionAdapter();
			
			// Get Server Changes
			$serverActions = $syncAdapter->get($clientPackage->SyncClientId, $clientPackage->ServerTimestamp);
			
			//var_dump($serverActions);
			//exit;
			
			// Resolve
			$syncResolver = new SyncResolver();
			$syncResolver->resolveEngine($clientPackage->SyncActions, $serverActions);
			
			// Enforce foreign key policy
			$this->foreignKeyPolicy($syncResolver->PendingClientActions);
			$this->foreignKeyPolicy($syncResolver->PendingServerActions);
	
			//var_dump($syncResolver->PendingClientActions);
			//var_dump($syncResolver->PendingServerActions);
            //exit;
			
			// Update server
			$syncAdapter->syncServer($syncResolver->PendingServerActions);
			
			// Update Client
			$serverPackage = $syncAdapter->syncClient($clientPackage->SyncClientId, $syncResolver->PendingClientActions);
			
			return $serverPackage->encode();
			
		} catch(Exception $ex) {
			Log::save("Sync.sync Exception", $ex, Log::PRIO_HIGH);
			return null;
		}
	}
	
	private function foreignKeyPolicy(&$array) {
		// This is a little bit hacky
		usort($array, array("Sync", "foreignKeyPolicyComparator"));
		return $array;
	}
	
	public static function foreignKeyPolicyComparator($syncActionA, $syncActionB) {
			
        $tableA = $syncActionA->Table;
        $tableB = $syncActionB->Table;

        if($tableA == $tableB) {
            // DESC
            return $syncActionA->ServerTimestamp < $syncActionB->ServerTimestamp ? 1 : -1;
        }

        $orderA = $tableA == "MemoBases" ? 3 : ($tableA == "Words" ? 2 : ($tableA == "Memos" ? 1 : 0));
        $orderB = $tableB == "MemoBases" ? 3 : ($tableB == "Words" ? 2 : ($tableB == "Memos" ? 1 : 0));

        return $orderA < $orderB ? 1 : -1;
	}
}

$handle = new Sync();
echo $handle->action();

?>