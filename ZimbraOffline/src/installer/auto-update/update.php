<?php
require_once("config.inc.php");
$product = "ZD";
$versions = array();
$xml = new XMLReader();

if ($product == "ZD") {
	$buildid = 0;
	$currentBuild = array();
	$oldchn = $_REQUEST['chn'];
	$oldver = $_REQUEST['ver'];
	$oldbid = $_REQUEST['bid'];

	$os = "macos"; //macos, win32, linux
	if (isset($_REQUEST["bos"])) {
	  $os = $_REQUEST['bos'];
	}
	$includeBetas = false;
	if (strcasecmp($oldchn, "beta") == 0) {
	  $includeBetas = true;
	}

	//read all ZD versions
	if (is_dir($ZD_UPDATES_FOLDER)) {
    	if ($dh = opendir($ZD_UPDATES_FOLDER)) {
        	while (($filename = readdir($dh)) !== false) {
        		$filePath = $ZD_UPDATES_FOLDER.DIRECTORY_SEPARATOR.$filename;
        		if(is_file($filePath)) {
            		$xml->open($filePath);
        			while ($xml->read()) {
						if ($xml->name == 'version') {

							//check channel
							$buildtype = $xml->getAttribute("buildtype"); //BETA, RELEASE
							if((strcasecmp($buildtype, "beta") == 0) && !$includeBetas) {
								//skip beta builds
								continue;
							}
							//check platform
							$platform = $xml->getAttribute("platform"); //MACOSXx86, WIN32, LINUX...
							if(strcasecmp(substr($platform, 0, 5), $os) != 0) {
								//skip other platforms
								continue;
							}

							//check build number
							$buildnumber = $xml->getAttribute("buildnumber");
							if($buildnumber <= $buildid) {
								continue;
							}
							$buildid = $buildnumber;
							//create build object
							$majorversion = $xml->getAttribute("majorversion");
							$minorversion = $xml->getAttribute("minorversion");
							$microversion = $xml->getAttribute("microversion");

					    	$currentBuild = array();
					    	$currentBuild["shortversion"] = strval($majorversion).".".strval($minorversion).".".strval($microversion);
					    	$currentBuild["version"] = strval($majorversion).".".strval($minorversion).".".strval($microversion)."_".$xml->getAttribute("releasetype");
					    	$currentBuild["buildnum"] = strval($buildnumber);
					    	$currentBuild["description"] = strval($xml->getAttribute("description"));
					    	$currentBuild["platform"] = strval($platform);
					    	$currentBuild["buildtype"] = strval($buildtype);
					    	$currentBuild["download_url_prefix"] = strval($xml->getAttribute("download_url_prefix"));
					    	$currentBuild["details_url"] = strval($xml->getAttribute("details_url"));
							$currentBuild["license_url"] = strval($xml->getAttribute("license_url"));
					    	$currentBuild["size"] = $xml->getAttribute("size");
					    	$currentBuild["hash"] = strval($xml->getAttribute("hash"));
					    	$currentBuild["file_media"] = strval($xml->getAttribute("file_media"));
					    	$currentBuild["extension_version"] = strval($xml->getAttribute("extension_version"));

						}
					}
            		$xml->close();
        		}
        	}
        	closedir($dh);
    	}
	}
	$download_url = $currentBuild["download_url_prefix"] . "/" . $currentBuild["shortversion"] . "/b" . $buildid . "/" . $currentBuild["file_media"];
	$build_text = $currentBuild["shortversion"] . " build " . $buildid;

	header('Content-Type: text/xml');
	header('Cache-Control: no-cache');

	echo "<?xml version=\"1.0\"?>\n";
?>
<updates>
<?php if ($buildid > $oldbid) { ?>
  <update type="minor" version="<?php echo $build_text?>" extensionVersion="<?php echo $currentBuild["extension_version"]?>" detailsURL="<?php echo $currentBuild["details_url"]?>" licenseURL="<?php echo $currentBuild["license_url"]?>">
    <patch type="complete" URL="<?php echo $download_url?>" hashFunction="md5" hashValue="<?php echo $currentBuild["hash"]?>" size="<?php echo $currentBuild["size"]?>"/>
  </update>
<?php } ?>
</updates>
<?php
}
?>
