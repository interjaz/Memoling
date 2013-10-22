<?php
require_once("../Init.php");

class PublishedLibraries extends Webservice {

	const SAMPLE_SIZE = 10;
	private $m_publishedMemoBaseAdapter;
	private $m_publishedMemoAdapter;
	private $m_urlShortcutAdapter;
	private $m_genreAdapter;

	public function __construct() {
		$this->m_publishedMemoBaseAdapter = new PublishedMemoBaseAdapter();
		$this->m_publishedMemoAdapter = new PublishedMemoAdapter();
		$this->m_urlShortcutAdapter = new UrlShortcutAdapter();
		$this->m_genreAdapter = new MemoBaseGenreAdapter();
	}

	public function index() {
		$page = isset($_GET["page"])?$_GET["page"]:0;
		$perPage = 20;
		$from = $page * $perPage;

		$result  = $this->m_publishedMemoBaseAdapter->getAll($from, $perPage);

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

		$result = $this->m_publishedMemoBaseAdapter->search($keyword, $genreId, $languageA, $languageB, $from, $perPage);

		return JsonBuilder::arrayToJson($result);
	}

	public function preview() {
		$result = $this->m_publishedMemoBaseAdapter->get($_GET["id"]);
		
		if(sizeof($result->MemoBase->Memos) > self::SAMPLE_SIZE) {
			$result->MemoBase->Memos = array_splice($result->MemoBase->Memos, 0, self::SAMPLE_SIZE);
		}
		
		return $result->encode();
	}

	public function download() {
		$result = $this->m_publishedMemoBaseAdapter->get($_GET["id"]);
		$this->m_publishedMemoBaseAdapter->updateDownload($_GET["id"]);
		return $result->encode();
	}

	public function upload() {
		$library = $_POST['library'];
		$published = new PublishedMemoBase();
		$published->decode($library);
		$result = $this->m_publishedMemoBaseAdapter->upload($published);
		// bool value
		return json_encode($result);
	}

	public function uploadMemoShare() {

		try {
			$memo = isset($_POST['memo'])?$_POST['memo']:null;
			
			
			$published = new PublishedMemo();
			$published->decode($memo);
			
			$newMemoId = $this->m_publishedMemoAdapter->uploadShare($published);

			
			if($newMemoId != null) {
				$url = "index.php?controller=PublishMemo&id=" . $newMemoId;
				$shortcut = $this->m_urlShortcutAdapter->newShortcut($url);
				
				return $shortcut->encode();
			} else {
				return "null";
			}
		} catch(Exception $ex) {
			var_dump($ex);
			return "null";
		}
	}

}

$handle = new PublishedLibraries();
echo $handle->action();


?>