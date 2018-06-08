<!-- 

-->

<%@ page language="java" import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>Memory Leak tests</title>

		<jsp:include page="../../public/Messages.jsp"/>
        <jsp:include page="../../public/Boot.jsp"/>
		<jsp:include page="../../public/Ajax.jsp"/>
		<jsp:include page="../../public/jsp/Zimbra.jsp"/>
		<jsp:include page="../../public/jsp/ZimbraCore.jsp"/>

		<script type="text/javascript" src="MemLeakTests.js"></script>

		<script language="JavaScript">   	
			function launch() {
				DBG = new AjxDebug(AjxDebug.NONE);
				MemLeakTests.run(document.domain);
			};
			AjxCore.addOnloadListener(launch);
		</script>

	</head>

	<body>
	</body>

</html>
