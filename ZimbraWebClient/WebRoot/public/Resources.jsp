<%@ page session="false" %>
<!--

-->
<%
    String contextPath = request.getContextPath();
    if(contextPath.equals("/")) {
        contextPath = "";
    }

    String isDev = (String) request.getParameter("dev");
    if (isDev != null) {
        request.setAttribute("mode", "mjsf");
    }

    String mode = (String) request.getAttribute("mode");
    boolean inDevMode = (mode != null) && (mode.equalsIgnoreCase("mjsf"));
    boolean inSkinDebugMode = (mode != null) && (mode.equalsIgnoreCase("skindebug"));

	String userAgent = request.getHeader("User-Agent");
	boolean isIE = userAgent == null || userAgent.indexOf("MSIE") != -1;

   String vers = (String)request.getAttribute("version");
   String ext = (String)request.getAttribute("fileExtension");
   if (vers == null){
      vers = "";
   }
   if (ext == null || isIE){
      ext = "";
   }

    String localeQs = "";
    String localeId = (String) request.getAttribute("localeId");
	if (localeId == null) localeId = request.getParameter("localeId");
	if (localeId != null) {
        int index = localeId.indexOf("_");
        if (index == -1) {
            localeQs = "&language=" + localeId;
        } else {
            localeQs = "&language=" + localeId.substring(0, index) +
                       "&country=" + localeId.substring(localeId.length() - 2);
        }
    }
	localeQs = localeQs.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");

	String skin = request.getParameter("skin");
	if (skin == null) {
		skin = application.getInitParameter("zimbraDefaultSkin");
	}
	skin = skin.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");

	String resources = (String)request.getAttribute("res");
	if (resources == null) {
		resources = request.getParameter("res");
	}
    resources = resources.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    
	String query = "v="+vers+(inSkinDebugMode||inDevMode?"&debug=1":"")+localeQs+"&skin="+skin;

%><script type="text/javascript" src="<%=contextPath%>/res/<%=resources%>.js<%=ext%>?<%=query%>"></script>
 