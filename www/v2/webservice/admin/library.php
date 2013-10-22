<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
<meta charset="UTF-8" />
</head>
<body>
<a href="libraries.php">Back</a> <br />
<?php
include_once("../../Init.php");

$publishedAdapter = new PublishedMemoBaseAdapter();



if(isset($_GET['delete']) && isset($_GET['id']) && $_GET['delete'] == 'true') {

	if(isset($_GET['password']) && strcmp(md5($_GET['password']), 'e82af5820966236e4e72f28613938b97') == 0) {
		$result = $publishedAdapter->delete($_GET['id']);
		
		echo 'deleted: ' + ($result?"true":"false");
		exit;
	} else {
		echo '<b>Password required</b>';
	}
}

$published = $publishedAdapter->get($_GET['id']);

?>

<a href="?id=<?php echo $_GET['id']; ?>&delete=true">Delete?</a>

<table>
	<tr>
		<td>Name:</td>
		<td>[<?php echo $published->MemoBase->Name; ?>]
		</td>
	</tr>
	<tr>
		<td>Description:</td>
		<td>[<?php echo $published->Description; ?>]
		</td>
	</tr>
	<tr>
		<td>Memos:</td>
		<td>[<?php echo $published->MemosCount; ?>]
		</td>
	</tr>
	<tr>
		<td>Downloads:</td>
		<td>[<?php echo $published->Downloads; ?>]
		</td>
	</tr>
	<tr>
		<td>Genre:</td>
		<td>[<?php echo $published->MemoBaseGenre->Genre; ?>]
		</td>
	</tr>
	<tr>
		<td>Created:</td>
		<td>[<?php echo $published->Created; ?>]
		</td>
	</tr>
	<tr>
		<td>Lang 1:</td>
		<td>[<?php echo $published->PrimaryLanguageAIso639; ?>]
		</td>
	</tr>
	<tr>
		<td>Lang 2:</td>
		<td>[<?php echo $published->PrimaryLanguageBIso639; ?>]
		</td>
	</tr>
</table>

<table>
	<?php 
	foreach($published->MemoBase->Memos as $memo) {
		?>
	<tr>
		<td><?php echo $memo->WordA->Word ?></td>
		<td><?php echo $memo->WordA->LanguageIso639 ?></td>
		<td><?php echo $memo->WordB->Word ?></td>
		<td><?php echo $memo->WordB->LanguageIso639 ?></td>
	</tr>
	<?php 
	}
	?>
</table>

</body>
</html>