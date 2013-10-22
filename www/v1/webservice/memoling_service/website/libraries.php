<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
<meta charset="UTF-8" />
</head>
<body>

<?php
require_once("../Init.php");

class PageAdapter extends DbAdapter {
	
	private $db;
	
	public function __construct() {
		$this->db = parent::connect();		
	}
	
	public function getLibraries() {
		$stm = $this->db->query("SELECT P.PublishedMemoBaseId AS Id, M.Name AS Name FROM memoling_PublishedMemoBases AS P INNER JOIN memoling_MemoBases AS M ON P.MemoBaseId = M.MemoBaseId");
		$stm->execute();
		
		$array = array();
		while($row = $stm->fetch()) {
			$array[$row[0]] = $row[1];
		}
		
		return $array;
	}
	
	public function getLanguages() {
		$stm = $this->db->query("SELECT DISTINCT LanguageIso639 FROM memoling_Words ORDER BY LanguageIso639");
		$stm->execute();
		
		$count = $stm->rowCount();
		$lang = '(' . $count . ') '; 
		while($row = $stm->fetch()) {
			$lang .= $row[0] . ', ';
		}
		
		return $lang;
	}
	
	public function getUsers() {
		$stm = $this->db->query("SELECT COUNT(*) FROM memoling_FacebookUsers");
		$stm->execute();
		$row = $stm->fetch();
		return $row[0];
	}
	
	public function getWords() {
		$stm = $this->db->query("SELECT COUNT(*) FROM memoling_Words");
		$stm->execute();
		$row = $stm->fetch();
		return $row[0];
	}

	public function getDownloads() {
		$stm = $this->db->query("SELECT SUM(Downloads) FROM memoling_PublishedMemoBases");
		$stm->execute();
		$row = $stm->fetch();
		return $row[0]==null?0:$row[0];
	}
	
	public function getLinks() {
		$stm = $this->db->query("SELECT Name, Link FROM memoling_FacebookUsers ORDER BY Name");
		$stm->execute();

		$link = "";
		while($row = $stm->fetch()) {
			$link = '<a href="'.$row[1].'">'.$row[0].'</a><br />';
		}
		
		return $link;
	}
}

$pageAdapter = new PageAdapter();
$libs = $pageAdapter->getLibraries();
$langs = $pageAdapter->getLanguages();
$users = $pageAdapter->getUsers();
$words = $pageAdapter->getWords();
$downloads = $pageAdapter->getDownloads();

if(isset($_GET['deletePublishedId'])) {
	$pageAdapter->deletePublished($_GET['deletePublishedId']);
}

$links = "";
if(isset($_GET['password']) && strcmp(md5($_GET['password']), 'e82af5820966236e4e72f28613938b97') == 0) {
	$links = '<br />Facebook links <br />';
	$links .= $pageAdapter->getLinks();
}

?>
<a href="index.php">Back</a> <br />&nbsp;<br />
<form method="get" action="library.php">
Total libraries: <?php echo sizeof($libs); ?> 

	<select name="id">
		<?php
		foreach($libs as $key=>$value) {
			echo "<option value='" . $key . "'>" . $value . "</otpion>";
		}
		
		?>
	</select>
	
	<input type="submit" title="Go" />
</form>

Languages: <?php echo $langs; ?> <br />
Users: <?php echo $users; ?><br />
Words: <?php echo $words; ?> <br />
Downloads: <?php echo $downloads; ?> <br />
<?php echo $links; ?>

</body>
</html>
