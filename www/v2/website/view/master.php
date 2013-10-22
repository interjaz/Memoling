<!DOCTYPE html>
<html>
	<head>
		<title><?php echo !isset($title)?"Memoling.com":$title; ?></title>
		
		<meta charset="utf-8"> 
		<meta name="viewport" content="width=320, height=480">
		
		<link rel="shortcut icon" href="<?php echo Content::img("favicon.ico"); ?>" />
		
		<link rel="stylesheet" type="text/css" href="<?php echo Content::css("master.css"); ?>" />
		<link href='http://fonts.googleapis.com/css?family=Oxygen' rel='stylesheet' type='text/css' />
		
		<script type="text/javascript" src="<?php echo Content::js("jquery-1.10.2.min.js"); ?>"></script>
		<script type="text/javascript" src="<?php echo Content::js("master.js"); ?>"></script>
		
		<?php echo $header; ?>
	</head>
	<body>

		
		<div id="wrapper">
		<div id="top">
			<div id="top_content">
				<a id="memoling_link" href="http://memoling.com">
					<img style="height:30px;vertical-align:middle" src="<?php echo Content::img("logo.png"); ?>" />
					<span style="font-size:12px;font-family: Oxygen;color:#ddd;">MEMOLING</span>
					<span style="font-size:10px;font-family: Oxygen;color:#999;margin-left:-5px;">.COM</span>
				</a>
				
				<div style="position:absolute;top:9px;right:0px;display:inline-block">
					
				
					
				</div>
				
			</div>
		</div>
	
		<div id="main">
			<div id="main_content">
			
			<?php echo $content; ?>
				
			
			</div>
		</div>
		

		<div id="footer">
			<div id="footer_content">
			
				<div style="display:inline-block;vertical-align:top;padding:10px;">
					<a href="https://play.google.com/store/apps/details?id=app.memoling.android">
						<img alt="Android app on Google Play"
						   src="https://developer.android.com/images/brand/en_app_rgb_wo_60.png" />
					</a>
				</div>

				<div id="footer_links">
					<a href="http://goo.gl/xxK6BR">Android Application</a>
					<a href="http://goo.gl/4F1h5g">Find us on Facebook</a>
				</div>
				
			<img id="footer_logo" src="<?php echo Content::img("ic_footer.png"); ?>" />
			</div>
			
		</div>
		
		</div>

	</body>
</html>