<?php

class mDatabase{
	private $mDB = null;
	private $queryStatment = null;
	private $insertStatment = null;
	private $DSN = "mysql:host=localhost;dbname=Islider";
	private $insertSql = "INSERT INTO user(username, password, email)
				 		  VALUES(:username, :password, :email)";
	private $querySql  = "SELECT *
			 	  		  FROM user
						  WHERE username = :username";
	private $roomQuerySql = "SELECT *
							 FROM room
							 WHERE roomname = :roomname";
	private $roomUpdateSql = "UPDATE room
							  SET file = :filename, currentpage = :currentpage, pagenum = :pagenum
							  WHERE roomname = :roomname";
	private $roomCreateSql = "INSERT INTO room(roomname, host)
							VALUES(:roomname, :host)";

	function __construct(){
		try{
			$this->mDB = new PDO($this->DSN, 'root', '');
		}catch(PDOException $e){
			echo $e->getMessage();
		}
		$this->insertStatment = $this->mDB->prepare($this->insertSql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
		$this->queryStatment = $this->mDB->prepare($this->querySql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
		$this->roomQueryStatment = $this->mDB->prepare($this->roomQuerySql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
		$this->roomUpdateStatment = $this->mDB->prepare($this->roomUpdateSql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
		$this->roomCreateStatment = $this->mDB->prepare($this->roomCreateSql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
	}

	function addUser($username, $password, $email){
		$success = $this->insertStatment->execute(array(":username"=>$username, ':password'=>$password, ":email"=>$email));
		return $success;
	}

	function queryUser($username){
		$this->queryStatment->execute(array(":username"=>$username));
		$result = $this->queryStatment->fetch(PDO::FETCH_ASSOC);
		return $result;
	}

	function modifyUser($username, $password, $email){
		//TODO
	}

	function addRoom($username, $host){
		$this->roomCreateStatment->execute(array(":roomname"=>$username, ":host"=>$host));
	}

	function queryRoom($roomname){
		$this->roomQueryStatment->execute(array(":roomname"=>$roomname));
		$result = $this->roomQueryStatment->fetch(PDO::FETCH_ASSOC);
		return $result;
	}

	function updateRoom($roomname, $file, $currentpage, $pagenum){
		$result = $this->roomUpdateStatment->execute(array(":filename"=>$file, ":currentpage"=>$currentpage, ":roomname"=>$roomname, ":pagenum"=>$pagenum));
		return $result;
	}

	function __destruct(){
		$this->mDB = null;
	}
}

?>
