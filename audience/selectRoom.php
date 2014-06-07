<!DOCTYPE HTML>

<html>
	<head>
		<title>Room</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
	</head>
	<body>
		<?php
		if(isset($_REQUEST["error_code"]) && $_REQUEST["error_code"] == 105){
			?>
			<p>Room Not Existed!</p>
			<?php
		}
		?>
		<form action="../control/requestHandler.php?controlType=listen" method="post">
			<input type="text" name="roomname"/>
			<input type="submit"/>
		</form>	
	</body>
</html>
