<?php

class Controller {

	private $m_action;
	private $m_master;
	
	const RESPONSE_OK = 200;
	const RESPONSE_NOT_FOUND = 404;

	public function __construct() {
		$this->m_master = "master";
		
		$this->action();
	}

	public function setMaster($master) {
		$this->m_master = $master;
	}
	
	public function getMaster($master) {
		return $this->m_master;
	}
	
	public function view($model=null,$action=null, $controller=null,$master=null) {
		
		if(isset($_GET['format']) && $_GET['format'] == "json") {
			echo $model->encode();
			return;
		}
		
		if($action == null) {
			$action = $this->m_action;
		}
		
		if($controller == null) {
			$controller = $_GET['controller'];
		}

		$base = Controller::getBase();

		$master = null;		
		if($this->m_master != null) {
			$master = $base . "website/view/" . $this->m_master . ".php";
		}
		
		$view = $base . "website/view/" . $controller .  "/" . $action . ".php";
		
		include_once($view);
		if($master != null) {
			include_once($master);
		}
	}
	
    protected function facebookUserId() {
        if(!isset($_SESSION['FacebookUserId'])) {
            return null;
        }
        
        return $_SESSION['FacebookUserId'];
    }
    
    public static function isAuthorized() {
        return isset($_SESSION['FacebookUserId']) && $_SESSION['FacebookUserId'] != '';
    }
    
	private function httpResponse($code = NULL) {
		if ($code !== NULL) {
			switch ($code) {
				case 100: $text = 'Continue'; break;
				case 101: $text = 'Switching Protocols'; break;
				case 200: $text = 'OK'; break;
				case 201: $text = 'Created'; break;
				case 202: $text = 'Accepted'; break;
				case 203: $text = 'Non-Authoritative Information'; break;
				case 204: $text = 'No Content'; break;
				case 205: $text = 'Reset Content'; break;
				case 206: $text = 'Partial Content'; break;
				case 300: $text = 'Multiple Choices'; break;
				case 301: $text = 'Moved Permanently'; break;
				case 302: $text = 'Moved Temporarily'; break;
				case 303: $text = 'See Other'; break;
				case 304: $text = 'Not Modified'; break;
				case 305: $text = 'Use Proxy'; break;
				case 400: $text = 'Bad Request'; break;
				case 401: $text = 'Unauthorized'; break;
				case 402: $text = 'Payment Required'; break;
				case 403: $text = 'Forbidden'; break;
				case 404: $text = 'Not Found'; break;
				case 405: $text = 'Method Not Allowed'; break;
				case 406: $text = 'Not Acceptable'; break;
				case 407: $text = 'Proxy Authentication Required'; break;
				case 408: $text = 'Request Time-out'; break;
				case 409: $text = 'Conflict'; break;
				case 410: $text = 'Gone'; break;
				case 411: $text = 'Length Required'; break;
				case 412: $text = 'Precondition Failed'; break;
				case 413: $text = 'Request Entity Too Large'; break;
				case 414: $text = 'Request-URI Too Large'; break;
				case 415: $text = 'Unsupported Media Type'; break;
				case 500: $text = 'Internal Server Error'; break;
				case 501: $text = 'Not Implemented'; break;
				case 502: $text = 'Bad Gateway'; break;
				case 503: $text = 'Service Unavailable'; break;
				case 504: $text = 'Gateway Time-out'; break;
				case 505: $text = 'HTTP Version not supported'; break;
				default:
					exit('Unknown http status code "' . htmlentities($code) . '"');
					break;
			}

			$protocol = (isset($_SERVER['SERVER_PROTOCOL']) ? $_SERVER['SERVER_PROTOCOL'] : 'HTTP/1.0');
			header($protocol . ' ' . $code . ' ' . $text);
			$GLOBALS['http_response_code'] = $code;

		} else {
			$code = (isset($GLOBALS['http_response_code']) ? $GLOBALS['http_response_code'] : 200);
		}

		return $code;
	}

	private function action() {

		$action = isset($_GET["action"])?$_GET["action"]:"index";
        $this->m_action = $action;
        
		$reflectionMethod = null;

		try {
			$reflectionMethod = new ReflectionMethod($this, $this->m_action);
		} catch(ReflectionException $ex) {
			Log::save("Invoking unknown method", $ex);
			$this->httpResponse(Controller::RESPONSE_NOT_FOUND);
			return;
		}

		$this->httpResponse(Controller::RESPONSE_OK);
		try {
			return $reflectionMethod->invoke($this);
		} catch(Exception $ex) {
			Log::save("Uncaught Exception", $ex, Log::PRIO_HIGH);
			throw $ex;
		}
	}

	private static function getBase() {
		$server = $_SERVER["DOCUMENT_URI"];
		$slashes = substr_count($server, "/");
	
		$base = "";
		for($i=0;$i<$slashes-2;$i++) {
			$base .= "../";
		}
	
		return $base;
	}

}

?>