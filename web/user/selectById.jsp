<%--
  Created by IntelliJ IDEA.
  User: holoi
  Date: 4/13/20
  Time: 20:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SelectUserById</title>
</head>
<body>
<center>
<h1>SelectUserById</h1>
<h1>
    <a href="users?action=users">List all users</a>
</h1>
    <div align="center">
        <form>
            <div>Enter id: <input type="text" id="id" name="id" size="30"></div>
            <input type="submit" id="submit" name="submit" value="Search">

            <div align="center">
                <table border="1" cellpadding="5">
                    <caption><h4>Select users by id</h4></caption>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Country</th>
                    </tr>
                    <c:forEach var="user" items="${id}">
                        <tr>
                            <td><c:out value="${user.id}"/></td>
                            <td><c:out value="${user.name}"/></td>
                            <td><c:out value="${user.email}"/></td>
                            <td><c:out value="${user.country}"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </form>
    </div>
</center>
</body>
</html>
