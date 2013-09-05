<?php
	require_one('..\conn.php');
    $handle = fopen('./estates_with_lng_lat.txt', 'r');
    while(!feof($handle)){
        $estate = trim(fgets($handle, 1024));
        if ("" == $estate){
            echo "empty line found";
            continue;
        }
        $items = split(",", $estate);
        $queryText = "insert into estates('name','price','area','longitude','latitude') values(".
            $items[0].",".$items[1].",".$items[2].",".$items[3].",".$items[4].")";
        //echo $queryText . "\n";
		mysql_query($queryText);
    }
    fclose($handle);
//require_once('conn.php');

//$query = "SELECT * FROM 'pricebox' WHERE square =480000";
//echo $query;
//$result = mysql_query($query);
//echo $result;
//while($row=mysql_fetch_row($result)){   
//	echo $row[1];
//	echo '<br>'
//}
//f(!result){echo 'fuck';}
//else {echo 'ok';}
//$whatsup="Maybe not OK.";
//echo $whatsup;
//echo "Mynot be ok";
?>
