<?php

include_once("./ERROR_CODE.php");
include_once("../model/mDatabase.php");
$db = new mDatabase();

$controlType = trim($_REQUEST["controlType"]);
$output = array('error_code'=>'');

if($controlType == "signIn"){
	$username = trim($_POST["username"]);
	$password = trim($_POST["password"]);
	$result = $db->queryUser($username);
	if(empty($result) || !isset($result["username"])){
		$output['error_code'] = ERROR_CODE::$SIGNIN_USERNAME_NOT_EXISTED;
		echo json_encode($output);
	}
	else if($password != $result["password"]){
		$output['error_code'] = ERROR_CODE::$SIGNIN_PASSWORD_NOT_MATCHED;
		echo json_encode($output);
	}else{
		$output['error_code'] = ERROR_CODE::$SUCCESS;
		session_start();
		$_SESSION["signin"] = true;
		$_SESSION["username"] = $username;
		$files = array();
		if(file_exists("../documents/$username")){
			foreach(glob("../documents/$username/*") as $fileDir){
				if(is_dir($fileDir)){
					$files[] = basename($fileDir);
				}
			}
		}
		$output['files'] = $files;
		echo json_encode($output);
	}
}
else if($controlType == "signUp"){
	$username = trim($_POST["username"]);
	$password = trim($_POST["password"]);
	$email = trim($_POST["email"]);
	$confirmPassword = trim($_POST["confirmPassword"]);
	$result = $db->queryUser($username);
	if(!empty($result) && isset($result["username"]) && $username == $result["username"]){
		$output['error_code'] = ERROR_CODE::$SIGNUP_USERNAME_EXISTED;
		echo json_encode($output);
	}
	else if($password != $confirmPassword){
		$output['error_code'] = ERROR_CODE::$SIGNUP_PASSWORD_AND_CONFIRM_NOT_MATCHED;
		echo json_encode($output);
	}
	else{
		$output['error_code'] = ERROR_CODE::$SUCCESS;
		$db->addUser($username, $password, $email);
		$db->addRoom($username, "host");
		session_start();
		$_SESSION["signin"] = true;
		$_SESSION["username"] = $username;
		$files = array();
		$output['files'] = $files;
		echo json_encode($output);
	}
}
else if($controlType == "listen"){
	$roomname = trim($_POST["roomname"]);
	$result = $db->queryRoom($roomname);
	if(!empty($result) && isset($result["roomname"]) && $result["roomname"] == $roomname){
		//enter the page for presentation
		$output['error_code'] = ERROR_CODE::$SUCCESS;
		$url = "../audience/toshow.php?roomname=$roomname";
		Header("HTTP/1.1 303 See Other");
		Header("Location: $url");
		exit;
	}	
	else{
		$output['error_code'] = ERROR_CODE::$ROOM_NOT_EXISTED;
		$url = "../audience/selectRoom.php?error_code={$output['error_code']}";
		Header("HTTP/1.1 303 See Other");
		Header("Location: $url");
		exit;
	}
}
else if($controlType == "signout"){
	unset($_SESSION["username"]);
	unset($_SESSION["login"]);
}
else if($controlType == "getLectureInformation"){
	$output["ip"] = $_SERVER["SERVER_ADDR"];
	session_start();
	$output["roomname"] = $_SESSION["username"];
	$output["filename"] = $_REQUEST["filename"];
	$output["pageNum"] = count(glob("../documents/{$_SESSION["username"]}/{$_REQUEST["filename"]}/*.html"));
	$output["currentpage"] = 1;
	$output["result"] = $db->updateRoom($output["roomname"], $output["filename"], $output["currentpage"], $output["pageNum"]);
	/*$pages = array();
	if(file_exists("../documents/$username/$filename")){
		foreach(glob("/var/www/islider-mobile/documents/$username/$filename/*.html") as $pageUrl){
			$pageUrl = substr($pageUrl, 8);
			$pageUrl = "http://".$ip.$pageUrl;
			$pages[] = $pageUrl;
		}
		sort($pages);
	}*/
	echo json_encode($output);
}
else if($controlType == "modifyRoomInformation"){    // safety problem
	$roomname = $_REQUEST["roomname"];
	$filename = $_REQUEST["filename"];
	$currentpage = $_REQUEST["currentpage"];
	$db->updateRoom($roomname, $filename, $currentpage);
}
else if($controlType == "getRoomInformation"){
	$roomname = $_REQUEST["roomname"];
	$result = $db->queryRoom($roomname);
	$output["filename"] = $result["file"];
	$output["currentpage"] = $result["currentpage"];
	echo json_encode($output);
}

?>
