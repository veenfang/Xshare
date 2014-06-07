<?php

include_once('./ERROR_CODE.php');

session_start();
$username = $_SESSION['username'];
$filename = $_REQUEST['filename'];

if(is_dir("../documents/$username/$filename/complete")){
	echo "done";
}
else{
	echo "doing";
}

?>
