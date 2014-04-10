<!DOCTYPE html>
<html ng-app="app">
	<head>
		<title><?php echo !isset($title)?"Memoling.com":$title; ?></title>
		
		<meta charset="utf-8"> 
		<meta name="viewport" content="width=320, height=480">
		
		<link rel="shortcut icon" href="<?php echo Content::img("favicon.ico"); ?>" />
		
		<link rel="stylesheet" type="text/css" href="<?php echo Content::css("master.css"); ?>" />
		<link href='http://fonts.googleapis.com/css?family=Oxygen' rel='stylesheet' type='text/css' />
		<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300&subset=latin,latin-ext,cyrillic-ext,cyrillic,vietnamese,greek,greek-ext' rel='stylesheet' type='text/css'>
		
		<script type="text/javascript" src="<?php echo Content::js("jquery-1.10.2.min.js"); ?>"></script>
		<script type="text/javascript" src="<?php echo Content::js("master.js"); ?>"></script>		
		
		<script type="text/javascript" src="http://code.angularjs.org/1.2.15/angular.min.js"></script>
		<script type="text/javascript" src="http://code.angularjs.org/1.2.15/angular-route.min.js"></script>
		
		<script type="text/javascript" src="<?php echo Content::js("app.angular.js"); ?>"></script>
		<script type="text/javascript" src="<?php echo Content::js("app.service.angular.js"); ?>"></script>
		<script type="text/javascript" src="<?php echo Content::js("app.directive.angular.js"); ?>"></script>
		<script type="text/javascript" src="<?php echo Content::js("app.model.angular.js"); ?>"></script>
		
		
		<?php echo $header; ?>
	</head>
	<body>

        
<div id="fb-root"></div>
<script>
    
  var facebookLoggedIn = false;
    
  window.fbAsyncInit = function() {
  FB.init({
    appId      : 620366261326135,
    status     : true, // check login status
    cookie     : true, // enable cookies to allow the server to access the session
    xfbml      : true  // parse XFBML
  });

  FB.Event.subscribe('auth.authResponseChange', function(response) {
    if (response.status === 'connected') {
      facebookSessionLogin();
    } else if (response.status === 'not_authorized') {
      facebookSessionLogout();
      FB.login();
    } else {
      facebookSessionLogout();
      FB.login();
    }
  });
  };

  // Load the SDK asynchronously
  (function(d){
   var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
   if (d.getElementById(id)) {return;}
   js = d.createElement('script'); js.id = id; js.async = true;
   js.src = "//connect.facebook.net/en_US/all.js";
   ref.parentNode.insertBefore(js, ref);
  }(document));

  // Here we run a very simple test of the Graph API after login is successful. 
  // This testAPI() function is only called in those cases. 
  function facebookSessionLogin() {

        FB.api('/me', function(response) {
          $('#fb-name').html(response.name);
            // Login user
            $.ajax({
               method: 'GET', 
               url: 'index.php?controller=Session&action=login&facebookUserId=' + response.id,
                success: function() {
                    if(!facebookLoggedIn) {
                        facebookLoggedIn = true;
                        setFacebookUserId(response.id);
                    }
                }
            });
        });
      
  }
    
  function facebookSessionLogout() {
    // Logout user
    $.ajax({
       method: 'GET', 
       url: 'index.php?controller=Session&action=logout',
        success: function() {
            $('#fb-name').html("");
            setFacebookUserId(null);
            facebookLoggedIn = false;
        }
    });
  }
    
    
  <?php if(Controller::isAuthorized()) { ?>
        if(!facebookLoggedIn) {
            facebookLoggedIn = true;
            setFacebookUserId('<?php echo Controller::facebookUserId() ?>');
        }
  <?php } ?>
    
</script>
		<div id="wrapper">
		<div id="top">
			<div id="top_content">
				<a id="memoling_link" href="http://memoling.com">
					<img style="height:30px;vertical-align:middle" src="<?php echo Content::img("logo.png"); ?>" />
					<span style="font-size:12px;font-family: Oxygen;color:#ddd;">MEMOLING</span>
					<span style="font-size:10px;font-family: Oxygen;color:#999;margin-left:-5px;">.COM</span>
				</a>
				
				<div style="position:absolute;top:9px;right:0px;display:inline-block;padding-right:10px;">

                <span id="fb-name"></span>
                <div class="fb-login-button" data-max-rows="1" data-size="small" data-show-faces="false" data-auto-logout-link="true"></div>
					
				</div>
				
			</div>
		</div>
	
		<div id="main">
			<div id="main_content" ng-controller="AppCtrl">
			
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

        

<div id="toast">
	<h2>Title</h2>
	<h3>Description</h3>
</div>
        
        
	</body>
</html>