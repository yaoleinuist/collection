<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
 
<%@ page import="java.util.*"%>
<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires",0);
String base_path = request.getContextPath();
String base_basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+base_path+"/";
String websocket_url="ws://"+request.getServerName()+":"+request.getServerPort()+base_path+"/websocket";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<head>
   	<base href="<%=base_basePath%>">
<script>
    var websocketUrl="<%=websocket_url%>";
</script>
 	<script src="${ctx}/statics/common/js/jquery-2.2.3.min.js"></script>
  	<script src="${ctx}/statics/common/js/sockjs-0.3.4.min.js"></script>
</head>
<body>
</body>
</html>
