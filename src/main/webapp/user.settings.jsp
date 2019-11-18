<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page import="net.novucs.esd.model.UserRole" %>
<%@ page import="net.novucs.esd.model.Role" %>
<%@ page import="java.util.List" %>
<% User userName = ((Session) request.getAttribute("ses/**/sion")).getUser(); %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<%--${session.getRoles()}--%>
<table>
    <thead>
    <tr>
        <th>Full Name</th>
        <th>Email</th>
        <th>D.O.B</th>
        <th>Address</th>
        <th class="collection-header"><h5><%= userName.getName() %>'s Roles</h5></th>
        <th>Edit</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td><%= userName.getName() %>,</td>
        <td><%= userName.getEmail() %>
        </td>
        <td><%= userName.getDateOfBirth() %>
        </td>
        <td><%= userName.getAddress() %>
        </td>
        <td>
            <c:forEach var="role" items="${session.getRoles()}">
                ${role.name}
            </c:forEach>
        </td>
        <td>

            <button class="btn waves-effect waves-light" type="submit" name="action" onclick="">Edit
                <i class="material-icons right">send</i>
            </button>

        </td>
    </tr>
    </tbody>
</table>
<div>
    <div>
        <h2>Password Reset</h2>
    </div>
    <div class="input-field col s6">
        <input placeholder="Current Password" id="password" type="text" class="validate">
        <label for="password">Enter Current Password</label>
    </div>
    <div class="input-field col s6">
        <input placeholder="New Password" id="new_password" type="text" class="validate">
        <label for="new_password">Enter New Password</label>
    </div>
    <div>
        <button class="btn waves-effect waves-light" type="submit" name="action" onclick="">Change
            Password
            <i class="material-icons right">send</i>
        </button>
    </div>
</div>

</html>
