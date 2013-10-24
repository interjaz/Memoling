<?php ob_start(); ?>
<?php $header = ob_get_clean(); ?>
<?php ob_start(); ?>

	<style type="text/css">
	
		#notice {
			background: #151515;
			padding:10px;
			-webkit-border-radius: 10px;
			-moz-border-radius: 10px;
			border-radius: 10px;
			font-size: 12px;
			margin-bottom: 10px;
			font-family: Oxygen;
			min-height: 35px;
		}
		
		#notice a {
			color: #fff;
			font-weight: bold;
			font-size: 1.1em;
		}
		
		#translation_left {
				text-align:left;
				vertical-align: top;
				padding:10px;
				background: #151515;
			}
			
			#translation_right {
				text-align:left;
				vertical-align: top;
				margin-left:-0px;
				padding:10px;
				background: #fff;
				color:#111
			}
	
		@media all and (max-width: 320px)
		{
			#translation_left {
				padding:10px;
				-webkit-border-radius: 10px 10px 0px 0px;
				-moz-border-radius: 10px 10px 0px 0px;
				border-radius: 10px 10px 0px 0px;
				display: block;
			}
			
			#translation_right {
				margin-top: 5px;
				margin-bottom: 10px;
				margin-left:-0px;
				-webkit-border-radius: 0px 0px 10px 10px;
				-moz-border-radius: 0px 0px 10px 10px;
				border-radius: 0px 0px 10px 10px;
				display: block;
			}
		}
	
		@media all and (min-width: 321px)
		{
	
			#translation_left {
				padding:10px;
				display:inline-block;
				-webkit-border-radius: 10px 0px 0px 10px;
				-moz-border-radius: 10px 0px 0px 10px;
				border-radius: 10px 0px 0px 10px;
				width:45%;
			}
			
			#translation_right {
				margin-left:-0px;
				display:inline-block;
				-webkit-border-radius: 0px 10px 10px 0px;
				-moz-border-radius: 0px 10px 10px 0px;
				border-radius: 0px 10px 10px 0px;
				width:45%;
			}
		}
		
		@media all and (min-width: 1200px) {
			#translation_left {
				width: 600px;
			}
			
			#translation_right {
				width: 600px;
			}
		}
		
	</style>

	<div id="notice">
		<div style="font-size: 40px;float:left;margin-top:-10px;font-family:Verdana;">!</div>
		<div style="">
		You are seeing this page because <a href="<?php echo $model->FacebookUser->Link; ?>"><?php echo $model->FacebookUser->Name; ?></a> shared expression from Memoling with you and
		you do not have installed Memoling application. Check the application on Google play.
		</div>
	</div>
	
	
	<div id="translation_left">
	 <span style="font-family:Oxygen;font-size:11px;color:#aaa">FROM</span>
	 <span style="font-size:14px;color:#eee"><?php echo Helper::onEmptyString(Language::parse($model->Memo->WordA->LanguageIso639)->Name,"-"); ?></span>
	 <hr style="height: 1px;border: 0px;background: #333"/>
		<p>
			<span style="display:block;font-family:Oxygen;font-size:11px;color:#aaa">WORD</span>
			<span style="color:#eee"><?php echo Helper::onEmptyString($model->Memo->WordA->Word, "-"); ?></span>
		</p>
		<p>
			<span style="display:block;font-family:Oxygen;font-size:11px;color:#aaa">DEFINITION</span>
			<span style="color:#eee"><?php echo Helper::onEmptyString($model->Memo->WordA->Description, "-"); ?></span>
		</p>
	</div>
	
	<div id="translation_right">
	 <span style="font-family:Oxygen;font-size:11px;color:#888">TO</span>
	 <span style="font-size:14px;color:#222"> <?php echo Helper::onEmptyString(Language::parse($model->Memo->WordB->LanguageIso639)->Name, "-"); ?> </span>
	 <hr style="height: 1px;border: 0px;background: #777"/>
		<p>
			<span style="display:block;font-family:Oxygen;font-size:11px;color:#888">WORD</span>
			<span style="color:#000"><?php echo Helper::onEmptyString($model->Memo->WordB->Word, "-"); ?></span>
		</p>
		<p>
			<span style="display:block;font-family:Oxygen;font-size:11px;color:#888">DEFINITION</span>
			<span style="color:#000"><?php echo Helper::onEmptyString($model->Memo->WordB->Description, "-"); ?></span>
		</p>
	</div>
<?php $content = ob_get_clean(); ?>