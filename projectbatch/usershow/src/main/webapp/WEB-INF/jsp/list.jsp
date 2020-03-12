
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<table class="table">
    <tr>
        <td>编号</td>
        <td>姓名</td>
        <td>时间</td>
    </tr>
    <c:forEach items="${list}" var="li">
        <tr>
            <td>${li.sid}</td>
            <td>${li.sname}</td>
            <td>${li.datea}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
