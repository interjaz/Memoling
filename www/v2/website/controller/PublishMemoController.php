<?php

class PublishMemoController extends Controller {
	
	public function index() {	
		$adapter = new PublishedMemoAdapter();
		$model = $adapter->get($_GET['id']);
		
		$this->view($model);
	}
	
}

new PublishMemoController();

?>