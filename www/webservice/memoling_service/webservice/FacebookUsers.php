<?php
require_once("../Init.php");

$dump = "POST: " . var_export($_POST, true) . "\r\nGET: " . var_export($_GET, true);
Log::save($dump, null, Log::PRIO_FLOW);

class FacebookUsers extends Webservice {
	
	private $m_facebookUserAdapter;
	
	public function __construct() {
		$this->m_facebookUserAdapter = new FacebookUserAdapter();
	}

	public function index() {
		$description = "FacebookUsers:<br />Actions:<br/>&nbsp;&nbsp;index()<br />&nbsp;&nbsp;login(FacebookUser user)";
		return $description;
	}
	
	public function login() {

		$user = isset($_POST["user"])?$_POST['user']:null;
		if($user == null) {
			return json_encode(false);
		}

		$fUser = new FacebookUser();
		$fUser->decode($user);
		
		// Update user, create if necessary
		$result = $this->m_facebookUserAdapter->update($fUser);
		
		// Return ok
		return json_encode($result);
	}
	
}

$handle = new FacebookUsers();
echo $handle->action();

?>