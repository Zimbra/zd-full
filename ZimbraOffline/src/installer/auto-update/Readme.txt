Folder structure maintained on update server

/var/www/html
    |——downloads    (package location)
    |  |——zdesktop
    |  |  |——7.3.0
    |  |  |  |——b13029
    |
    |——aus
    |  |——universal
    |  |  |——config.inc.php
    |  |  |——update.php
    |  |  |——zd
    |  |  |  |——zd7.xml


URL for checking auto-update is:
/aus/universal/update.php?chn=@channel@&ver=@version@&bid=@buildid@&bos=@buildos@


Host:
www.zimbra.com OR
zqa-246.eng.zimbra.com (Lab server)

update.php: 
Actual code which parse input parameters(channel, build id, OS, version) from URL and uses zd7.xml file to check if 
update is available.