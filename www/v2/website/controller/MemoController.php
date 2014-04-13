<?php

class MemoController extends Controller {
	
    private $memoAdapter;
    private $syncClientAdapter;
    
    
    public function __construct() {
        $this->memoAdapter = new MemoAdapter();
        $this->syncClientAdapter = new SyncClientAdapter();
        
        parent::__construct();
    }

    public function getClientId() {
        $syncClient = $this->syncClientAdapter->getByFacebookUserId(parent::facebookUserId());
        return $syncClient->SyncClientId;
    }
    
	public function listAll() {
		$memoBaseId = $_GET['id'];
        $memos = $this->memoAdapter->getAllDeep($memoBaseId);
		echo JsonBuilder::arrayToJson($memos);
	}
	
    public function update() {
        $model = new Memo();
        $model->decode($_POST['model']);
        $clientId = $this->getClientId();
        
        try {
            $this->memoAdapter->update($model, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
    
    public function insert() {
        $model = new Memo();
        $model->decode($_POST['model']);
        $clientId = $this->getClientId();
        
        try {
            $this->memoAdapter->insert($model, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
    
    public function delete() {
        $id = $_POST['id'];
        $clientId = $this->getClientId();
        
        try {
            $this->memoAdapter->delete($id, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
    
}

new MemoController();

?>