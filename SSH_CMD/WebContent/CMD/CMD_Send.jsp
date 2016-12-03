<%@page import="org.json.JSONObject"%>
<%@page import="jdk.nashorn.api.scripting.JSObject"%>
<%@page import="com.CMD.CMDManager"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%
    JSONObject jb=new JSONObject();
    jb.put("flag", CMDManager.instance.cmd(request)) ;
    %>
