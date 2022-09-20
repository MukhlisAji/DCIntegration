<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<html>
 
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  	<title>BMC Remedy Integration Connection Testing</title>
</head>
 
<body>
 
<h1>BMC Remedy Integration Testing</h1>
 
<b>Result</b><br/>
<c:out value="${connectionResult1}" /><br/>
<c:out value="${connectionResult}" /><br/>
 
</body>
</html>