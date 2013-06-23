<?php
require_once("../Init.php");

class PublishedLibraries extends WebService {
	
	private $m_publishedAdapter;
	private $m_genreAdapter;
	
	public function __construct() {
		$this->m_pubishedAdapter = new PublishedMemoBaseAdapter();
		$this->m_genreAdapter = new MemoBaseGenreAdapter();		
	}
	
	public function index() {
		$page = isset($_GET["page"])?$_GET["page"]:0;
		$perPage = 20;
		$from = $page * $perPage;
		
		$result  = $this->m_pubishedAdapter->getAll($from, $perPage);
		
		return json_encode($result);
	}
	
	public function search() {
		$page = isset($_GET["page"])?$_GET["page"]:0;
		$perPage = 20;
		$from = $page * $perPage;
		
		$keyword = isset($_GET["keyword"])?$_GET["keyword"]:"";
		$genreId = isset($_GET["genreId"])?$_GET["genreId"]:null;
		$languageA = isset($_GET["languageAIso639"])?$_GET["languageAIso639"]:null;
		$languageB = isset($_GET["languageBIso639"])?$_GET["languageBIso639"]:null;
		
		$result = $this->m_pubishedAdapter->search($keyword, $genreId, $languageA, $languageB, $from, $perPage);
		
		return JsonBuilder::arrayToJson($result);
	}
	
	public function preview() {
		$result = $this->m_pubishedAdapter->getSample($_GET["id"], 10);
		return json_encode($result);
	}
	
	public function download() {
		$result = $this->m_pubishedAdapter->getDownload($_GET["id"]);
		return json_encode($result);
	}
	
	public function upload() {
		$library = $_POST['library'];
		$published = new PublishedMemoBase();
		$published->decode($library);
		$result = $this->m_pubishedAdapter->upload($published);
		return json_encode($result);
	}
	
}

$handle = new PublishedLibraries();
echo $handle->action();


?>