<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String error = request.getParameter("error");
	if (error == null) {
		error = "INCORRECT_CREDENTIALS";
	}
	request.getSession(false).invalidate();
%>
<jsp:forward page="/public/login.jsp">
	<jsp:param name="error" value="<%= error %>"/>
</jsp:forward>
