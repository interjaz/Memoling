<?php ob_start(); ?>

<!-- <link rel="stylesheet" type="text/css" href="website/content/css/"/>
<script type="text/javascript" src="website/content/js/"></script> -->

<link rel="stylesheet" type="text/css" href="website/content/css/app.css"/>

<?php $header = ob_get_clean(); ?>
<?php ob_start(); ?>

<div ng-view></div>



<?php $content = ob_get_clean(); ?>