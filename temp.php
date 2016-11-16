<form action="" method="post">
	<h3>Sensor emulator</h3>
	Temp/current: <input type="text" name="temperature" value="<?=isset($_POST['temperature']) ? $_POST['temperature'] : ''?>"> (c)
	<br><br>
	<input type="submit" name="" value="Send">
</form> 

<?php

if( isset($_POST['temperature'])) {

	require("phpMQTT.php");
	$host = "hostname"; 
	$port = 17667;
	$username = "x"; 
	$password = "x"; 

	$topic = "temp/current";


	$temperature = $_POST['temperature'];

	$mqtt = new phpMQTT("m20.cloudmqtt.com", 17667, "mqtt-bt-test-php"); //Change client name to something unique
	if ($mqtt->connect(true, null, $username, $password)) {
		
		$mqtt->publish($topic, $temperature, 0);
	//	$mqtt->publish("bluerhinos/phpMQTT/examples/publishtest","Hello World! at ".date("r"),0);


		$mqtt->close();
	} else {
		echo "Can't connect to mqtt srv";
	}
}

?>