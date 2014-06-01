<?php

class MemoBaseController extends Controller {
	
    private $memoBaseAdapter;
    private $syncClientAdapter;
    
    public function __construct() {
        $this->memoBaseAdapter = new MemoBaseAdapter();
        $this->syncClientAdapter = new SyncClientAdapter();
        
        parent::__construct();
    }
    
	public function index() {
		$this->view();
	}

    public function getClientId() {
        $syncClient = $this->syncClientAdapter->getByFacebookUserId(parent::facebookUserId());
        return $syncClient->SyncClientId;
    }
	
	public function listAll() {
        $memoBases = $this->memoBaseAdapter->getAllForNavList(parent::facebookUserId());
		echo JsonBuilder::arrayToJson($memoBases);
	}
	
	public function get() {
        $facebookUserId = parent::facebookUserId();
        if($facebookUserId == null) {
            echo "null";
            return;
        }
        
        $model = $this->memoBaseAdapter->get($_GET['id']);
		echo $model->encode();
	}
    
	
    public function update() {
        $model = new MemoBase();
        $model->decode(urldecode ($_POST['model']));
        $clientId = $this->getClientId();
        
        try {
            $this->memoBaseAdapter->update($model, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
    
    public function insert() {
        $model = new MemoBase();
        $model->decode(urldecode ($_POST['model']));
        $clientId = $this->getClientId();
        
        try {
            $this->memoBaseAdapter->insert($model, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
    
    public function delete() {
        $id = $_POST['id'];
        $clientId = $this->getClientId();
        
        try {
            $this->memoBaseAdapter->delete($id, $clientId);
            echo 'true';
        } catch(Exception $ex) {
            echo 'false';
        }
    }
	
}

new MemoBaseController();

?>