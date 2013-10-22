<?php
require_once("../../Init.php");

class Logs {


	public function __construct() {


	}

	public function render() {
		$filePath = Helper::getRoot() . Log::LOG_FILE;
		
		if(isset($_GET['file'])) {
			if($_GET['file'] == 'err') {
				$filePath = Helper::getRoot() . Log::LOG_FILE_ERR;
			} else if($_GET['file'] == 'bak') {
				$filePath = Helper::getRoot() . Log::LOG_FILE_BAK;
			}
		}

		$rawLines = $this->readLogs($filePath);

		$back = '<a href="index.php">Back</a><br />';
		
		$htmlFiles = '<p><a href="?file">Original</a>&nbsp;';
		$htmlFiles .= '<a href="?file=bak">Backup file</a>&nbsp;';
		$htmlFiles .= '<a href="?file=err">Erros only</a><br /></p>';

		$htmlLines = "";
		
		if($rawLines == null) {
			$htmlLines .= "No file found.";
		} else {
			// Last line is empty
			for($i=sizeof($rawLines)-2;$i>=0;$i--) {
				$htmlLines .= $this->renderLine($rawLines[$i], $i);
			}
		}

		$html = $this->renderHeader();
		$html .= $back;
		$html .= $htmlFiles;
		$html .= $htmlLines;
		$html .= $this->renderFooter();

		return $html;
	}

	private function readLogs($file) {

		if(!file_exists($file)) {
			return null;
		}

		$handle = fopen($file, "r");
		$filesize = filesize($file);

		$content = fread($handle, $filesize);
		$lines = explode("\n" ,$content);

		fclose($handle);

		return $lines;
	}

	private function renderHeader() {
		return
		'
				<html>
				<head>
				<style type="text/css">
				.content {
				display: none;
	}
				.header_flow {
				background: green;
	}
				.header_default {
				background: yellow;
	}
				.header_high {
				background: red;
	}
				</style>
				<script type="text/javascript">
				function toggle(id) {
				var div = document.getElementById(id);

				if(div.style.display != "block") {
				div.style.display = "block";
	} else {
				div.style.display = "none";
	}
	}
				</script>
				</head>
				<body>
					
				';
	}

	private function renderFooter() {
		return
		"
				</body>
				</html>";

	}


	private function renderLine($string, $id) {

		$content = explode("¦", $string);
		if(sizeof($content) < 5) {
			return "Error reading line!<br />";
		}

		$date = $content[0];
		$ip = substr($content[1], 3);
		$prio = strtolower(substr($content[2], 2));
		$msg = str_replace('\n', "<br /", substr($content[3], 4));
		$ex = str_replace('\n', "<br />", substr($content[4], 3));

		$html = "";
		$html .= '<div class="header_'.$prio.'" onclick="toggle(\'c_'. $id .'\')">' . $date . '</div>';
		$html .= '<div class="content" id="c_'. $id .'">IP: ' . $ip . '<br />' . $msg . '<br />'.$ex.'</div>';

		return $html;
	}
};

$page = new Logs();
echo $page->render();

?>