<%@ page import="com.zimbra.cs.taglib.bean.BeanUtils" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<!--

-->
<%
	String contextPath = request.getContextPath();
	if(contextPath.equals("/")) {
		contextPath = "";
	}

	final String SKIN_COOKIE_NAME = "ZM_SKIN";
	String skin = application.getInitParameter("zimbraDefaultSkin");
    Cookie[] cookies = request.getCookies();
	String requestSkin = request.getParameter("skin");
	if (requestSkin != null) {
		skin = requestSkin;
	} else if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(SKIN_COOKIE_NAME)) {
				skin = cookie.getValue();
			}
		}
	}
    String vers = (String)request.getAttribute("version");
    String ext = (String)request.getAttribute("fileExtension");
    String mode = (String) request.getAttribute("mode");
    if (vers == null){
       vers = "";
    }
    if (ext == null){
       ext = "";
    }
    Boolean inDevMode = (mode != null) && (mode.equalsIgnoreCase("mjsf"));
    Boolean inSkinDebugMode = (mode != null) && (mode.equalsIgnoreCase("skindebug"));

	pageContext.setAttribute("skin", skin);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Zimbra ALE - Prototype</title>
      <link rel="stylesheet" href="<%= contextPath %>/css/common,dwt,msgview,login,zm,spellcheck,wiki,spreadsheet,images,skin.css?v=<%= vers %><%= inSkinDebugMode || inDevMode ? "&debug=1" : "" %>&skin=${zm:cook(skin)}" />
      <style type="text/css">
        @import url( style.css );
        -->
      </style>
	  <jsp:include page="Resources.jsp">
		  <jsp:param name="res" value="I18nMsg,AjxMsg,ZMsg,ZmMsg,AjxKeys" />
		  <jsp:param name="skin" value="${zm:cook(skin)}" />
	  </jsp:include>
      <jsp:include page="Boot.jsp"/>
      <%
        String packages = "Ajax,SpreadsheetEmbed";

        String extraPackages = request.getParameter("packages");
        if (extraPackages != null) packages += ","+ BeanUtils.cook(extraPackages);

        String pprefix = inDevMode ? "public/jsp" : "js";
        String psuffix = inDevMode ? ".jsp" : "_all.js";

        String pattern = "\\.|\\/|\\\\";
        String[] pnames = packages.split(",");
        for (String pname : pnames) {
           //bug: 52944
          // Security: Avoid including external pages inline
           if (pname.matches(pattern)) {
               continue;
           }
            String pageurl = "/"+pprefix+"/"+pname+psuffix;
            if (inDevMode) { %>
                <jsp:include>
                    <jsp:attribute name='page'><%=pageurl%></jsp:attribute>
                </jsp:include>
            <% } else { %>
                <script type="text/javascript" src="<%=contextPath%><%=pageurl%><%=ext%>?v=<%=vers%>"></script>
            <% } %>
        <% }
      %>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script type="text/javascript" language="JavaScript">
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.NONE, null, false);
 	    	Test.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
    </body>
</html>