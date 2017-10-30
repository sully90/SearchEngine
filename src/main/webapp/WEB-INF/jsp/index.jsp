<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link id="contextPathHolder" data-contextPath="${pageContext.request.contextPath}"/>
        <style type="text/css" media="screen">
          @import url( <c:url value="/css/style.css"/> );
        </style>
        <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
        <title>REST Bookstore Sample</title>
    </head>
<form class="form-wrapper cf">
  	<input id="searchBox" type="text" onkeyup="myFunction()" placeholder="Search here..." required>
	  <!--<button id="searchButton" type="submit">Search</button>-->
</form>
        <script type="text/javascript"><c:import url="/js/test.js" /></script>
</html>