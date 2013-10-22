<?php

class TestController extends Controller {
	
	
	public function index() {
		$a = new MemoAdapter();
		
		var_dump($a->get("0079e18b-da35-438b-8eaf-97093c703869"));
	}
	
}

new TestController();

?>