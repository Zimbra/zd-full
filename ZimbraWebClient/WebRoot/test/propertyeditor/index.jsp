<!-- 

-->

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Property Editor test</title>
<% 
   String contextPath = (String)request.getContextPath(); 
   String vers = (String)request.getAttribute("version");
   String ext = (String)request.getAttribute("fileExtension");
   if (vers == null){
      vers = "";
   }
   if (ext == null){
      ext = "";
   }
%>
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>
    <script type="text/javascript" src="<%= contextPath %>/js/ajax/dwt/widgets/DwtPropertyEditor.js<%= ext %>?v=<%= vers %>"></script>
    <script type="text/javascript" src="script.js"></script>

<style type="text/css">
<!--
        @import url(/zimbra/img/imgs.css?v=<%= vers %>);
        @import url(/zimbra/img/skins/steel/skin.css?v=<%= vers %>);
        @import url(/zimbra/js/zimbraMail/config/style/dwt.css?v=<%= vers %>);
        @import url(/zimbra/js/zimbraMail/config/style/common.css?v=<%= vers %>);
        @import url(/zimbra/js/zimbraMail/config/style/zm.css?v=<%= vers %>);
        @import url(/zimbra/js/zimbraMail/config/style/spellcheck.css?v=<%= vers %>);
        @import url(/zimbra/skins/steel/skin.css?v=<%= vers %>);
-->
</style>
    <style type="text/css">
      <!--
        @import url(style.css);
      -->
    </style>

  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.NONE, null, false);
 	    	App.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
    </body>
</html>

