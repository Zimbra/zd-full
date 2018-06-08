<!-- 

-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Shell Busy Overlay Test</title>
    <style type="text/css">
      <!--
        @import url(/zimbra/img/imgs.css);
        @import url(/zimbra/js/zimbraMail/config/style/dwt.css);
        @import url(/zimbra/js/zimbraMail/config/style/common.css);
        @import url(/zimbra/js/zimbraMail/config/style/zm.css);
      -->
    </style>
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Boot.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <script type="text/javascript" src="BusyOverlayTest.js"></script>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.DBG1, null, false);
	    	BusyOverlayTest.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
  </head>
    <body>
    </body>
</html>

