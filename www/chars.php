<?php


echo "træk";

$bytes = array(116,114,-61,-90,107);
$output = '';
for ($i = 0, $j = count($bytes); $i < $j; ++$i) {
    $output .= chr($bytes[$i]);
}
echo $output;

?>