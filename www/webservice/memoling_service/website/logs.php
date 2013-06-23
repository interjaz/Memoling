<?php
require_once("../Init.php");

class Logs {
	
	
	public function __construct() {
		
		
	}	
	
	public function render() {
		$rawLines = $this->readLogs();
		
		$htmlLines = "";
		// Last line is empty
		for($i=sizeof($rawLines)-2;$i>=0;$i--) {
			$htmlLines .= $this->renderLine($rawLines[$i], $i);
		}
		
		$html = $this->renderHeader();
		$html .= $htmlLines;
		$html .= $this->renderFooter();
		
		return $html;
	}
	
	private function readLogs() {
		
		$handle = fopen(Log::LOG_FILE, "r");
		$filesize = filesize(Log::LOG_FILE);
		
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
						background: yellow;
					}
					.header_default {
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
		if(sizeof($content) < 4) {
			return "Error reading line!";
		}
		
		$date = $content[0];
		$prio = strtolower(substr($content[1], 2));
		$msg = str_replace('\n', "<br /", substr($content[2], 4));
		$ex = str_replace('\n', "<br />", substr($content[3], 3));
		
		$html = "";
		$html .= '<div class="header_'.$prio.'" onclick="toggle(\'c_'. $id .'\')">' . $date . '</div>';
		$html .= '<div class="content" id="c_'. $id .'">' . $msg . '<br />'.$ex.'</div>';
		
		return $html;
	}
};

$page = new Logs();
echo $page->render();


?>