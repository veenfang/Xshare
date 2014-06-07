<?php

define('UNIX_DOMAIN', '/tmp/UNIX.d');

include_once("../control/ERROR_CODE.php");

session_start();
$username = "";
if(isset($_SESSION["username"])){
	$username = $_SESSION["username"];
}

$filename = $_FILES["uploadedFile"]["name"];
$file = $_FILES["uploadedFile"]["tmp_name"];

$tmp_filename = "{$username}_{$filename}";
if($file){
	move_uploaded_file($file, "./raw_files_to_be_changed/$tmp_filename");
}

$obj = array('name'=>$username, 'file'=>$tmp_filename);

$json = json_encode($obj);
echo $json;

$mSocket = socket_create(AF_UNIX, SOCK_STREAM, SOL_SOCKET);
$isConnected = socket_connect($mSocket, UNIX_DOMAIN);

if($isConnected){
	socket_write($mSocket, $json, strlen($json));
	print('done');
}

?>
