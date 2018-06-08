<!-- 

-->

<%@ page language="java" import="java.io.*, java.util.*, javax.naming.*"%>
<%
	String name = (String) request.getParameter("name");
	String path = (String) request.getParameter("path");
	String subject = (String) request.getParameter("subject");
	String id = (String) request.getParameter("id");
	PrintWriter pw = response.getWriter();
    if (name == null) 
	    pw.println("id=" + id + "; subject=" + subject);
	else 
		pw.println("name=" + name + "; path=" + path); 
%>
