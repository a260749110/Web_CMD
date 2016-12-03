<%@page import="com.CMD.CMDManager"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	JSONObject jb = new JSONObject();
	jb.put("key", CMDManager.instance.land(request));
%>
<%=jb.toString()%>