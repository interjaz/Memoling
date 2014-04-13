<?php

class SessionController extends Controller {
    
    public function login() {
        $_SESSION['FacebookUserId'] = $_GET['facebookUserId'];
    }
    
    public function logout() {
        session_destroy();
    }
}

new SessionController();

?>