<?php

	if(isset($_GET['go']) && $_GET['go'] == 'true') {
		
		 header( 'Location: test.php?go=false' ) ;
		
	}
	

?>

<a href="test.php?go=true" >Go</a>

