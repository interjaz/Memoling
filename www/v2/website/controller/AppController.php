<?php

class AppController extends Controller {
    
 
    public function index() {
        return $this->view(null, "index", "App");
    }
    
}

$appControler = new AppController();


?>
