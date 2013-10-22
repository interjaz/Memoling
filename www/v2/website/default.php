
<!DOCTYPE html>
<html>
	<head>
		<title>Memoling</title>
		<link rel="shortcut icon" href="<?php echo Content::img("favicon.ico"); ?>">
		
		<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300' rel='stylesheet' type='text/css'>
		<script type="text/javascript" src="<?php echo Content::js("jquery-1.10.2.min.js"); ?>"></script>
		
		<style type="text/css">
				body {
					background: url(<?php echo Content::img("bkg.png"); ?>);
				}
				span { 
					font-family: 'Open Sans Condensed', sans-serif;
					color: #ffffff;
					font-size: 1.8em;
				}
				
				.border {
					position:absolute;
					top:0px;
					left:0px;
					width:100%;
					height:100%;
					background:#ffffff;
					z-index:0;
					opacity:0.5;
					-webkit-border-radius: 20px;
					-moz-border-radius: 20px;
					border-radius: 20px;
				}
				
				.borderWrapper {
					position:relative;
					display:inline-block;
					cursor: pointer;
				}
				
		</style>
		<script type="text/javascript">
			$(document).ready(function() {
			
				$(".borderWrapper").each(function() {
				
					$(this).on("mouseover", function() {
						var hover = $($(this).children(0).get(0));
						hover.animate({
							opacity: 0.9
						});
					});
				
					$(this).on("mouseout", function() {
						var hover = $($(this).children(0).get(0));
						hover.animate({
							opacity: 0.5
						});
					});
					
					$(this).on("click", function() {
						var url = $(this).attr("url");
						window.location.href = url;
					});
				
				});
				
				
			});
		</script>
	</head>
<body>

	<div style="margin-top:150px;text-align:center;">

		<div style="position:relative;display:inline-block;margin:10px;">
			<div class="borderWrapper" url="http://goo.gl/xxK6BR">
				<div class="border"></div>				
				<img style="position:relative;z-index:2;width:200px" src="<?php echo Content::img("qrcode_play.png"); ?>" />
				
			</div>
			<br/>
			<span>PLAY STORE</span>
		</div>
		
		
		<div style="position:relative;display:inline-block;margin:10px;">
			<div class="borderWrapper" url="http://goo.gl/4F1h5g">
				<div class="border"></div>
				<img style="position:relative;z-index:2;width:200px" src="<?php echo Content::img("qrcode_facebook.png"); ?>" />
			</div>
			<br/>
			<span>FACEBOOK</span>
		</div>
		
	</div>
	
<img style="position:fixed;bottom:0px;right:0px;height:25%;z-index:-1" src="<?php echo Content::img("footer.png"); ?>" />

</body>
</html>