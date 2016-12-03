<%@page import="com.CMD.CMDManager.ReadObj"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.CMD.CMDManager"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
	ReadObj read = CMDManager.instance.read(request);
	JSONObject jb = new JSONObject();
	if (read == null||read.key==null) {
		jb.put("msg", "");
		jb.put("key", "");
		jb.put("title", read.title);
	}else if(read.msg==null){
		jb.put("msg", "");
		jb.put("key",read.key);
		jb.put("title", read.title);
	} 
	else {
		
		
		jb.put("msg", read.msg);
		jb.put("key",read.key);
		jb.put("title", read.title);
	}
	//System.err.println(jb.toString());
%>
<%=jb.toString()%>