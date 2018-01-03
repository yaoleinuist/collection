<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<meta charset="UTF-8">
<title>TestSubmit</title>
</head>
<body>
	<form name="upform" action="upload.do" method="POST"
		enctype="multipart/form-data">
		 <input type="text"  name="username" /><br /> 
		    <input type="file" name="file1" /><br />  
		    <input type="file" name="file2" /><br /> 
		    <input type="submit" value="Submit" /><br />
	</form>
</body>
</html>
