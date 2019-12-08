<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<% User user = ((Session) request.getAttribute("session")).getUser(); %>
<% Boolean hasUpdated = (Boolean) request.getAttribute("updated"); %>
<% String notice = (String) request.getAttribute("notice"); %>

<% if (hasUpdated != null) { %>
<div class="row no-bottom-margin">
    <div class="col s12">
        <div class="card green">
            <div class="card-content">
                You've successfully updated your account settings.
            </div>
        </div>
    </div>
</div>
<% } %>
<% if (notice != null) { %>
<div class="row no-bottom-margin">
    <div class="col s12">
        <div class="card orange">
            <div class="card-content">
                <%=notice%>
            </div>
        </div>
    </div>
</div>
<% } %>

<div class="row">
    <div class="col s12 m6 l4">
        <div class="card">
            <div class="card-content bold-text">
                <span class="card-title text-darken-4"><%=user.getName()%></span>
                <p><i class="material-icons prefix">person</i> <%=user.getUsername()%>
                </p>
                <p><i class="material-icons prefix">email</i> <%=user.getEmail()%>
                </p>
            </div>
        </div>
        <% if (user.getNeedsPasswordChange() == 1) { %>
        <div class="card orange white-text">
            <div class="card-content bold-text">
                <span class="card-title text-darken-4">Change your password</span>
                <p>Please update your password so it is no longer the generated password.</p>
            </div>
        </div>
        <% } %>
    </div>

    <div class="col s12 m12 l8">
        <ul id="projects-collection" class="collection card">
            <li class="collection-item">
                <span class="collection-header">Details</span>
                <table class="no-border-last">
                    <tr>
                        <td class="w200px bold-text">Password</td>
                        <td>************</td>
                    </tr>
                    <tr>
                        <td class="w200px bold-text">Date of Birth</td>
                        <td><%=user.getDateOfBirth().toLocalDate()%>
                        </td>
                    </tr>
                    <tr>
                        <td class="w200px bold-text">Roles</td>
                        <td>
                            <c:forEach var="role" items="${session.getRoles()}">
                                <span class="label label-success">${role.name}</span>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr>
                        <td class="w200px bold-text">Address</td>
                        <td><%=user.getAddress()%>
                        </td>
                    </tr>
                </table>
            </li>
            <li class="collection-item text-right">
                <button data-target="accModal" class="btn modal-trigger orange">
                    Edit Details
                </button>
            </li>
        </ul>
    </div>


    <c:choose>
        <c:when test="${actions != null}">
            <div class="col s12 m6 l4">
                <div class="card">
                    <div class="card-content bold-text">
                        <span class="card-title text-darken-4">Actions Required:</span>
                        <ul>
                            <c:forEach var="action" items="${actions}">
                                <li>
                                    <a>
                                        Pay
                                    </a>
                                    <strong>&pound;${action.pounds}.${action.pence}</strong> by
                                    <fmt:parseDate value="${action.complete_by.toLocalDate()}"
                                                   type="date" pattern="yyyy-MM-dd"
                                                   var="parsedDate"/>
                                    <fmt:formatDate value="${parsedDate}" type="date"
                                                    pattern="dd-MM-yyyy" var="formattedDate"
                                    />
                                        ${formattedDate}
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>

        </c:otherwise>
    </c:choose>


</div>


<!-- Modal Structure -->
<form method="post" action="" id="accModal" class="modal">
    <div class="modal-content">
        <h4>Edit Details</h4>

        <div class="row">
            <div class="input-field col s6">
                <input placeholder="Full Name" name="fullname"
                       id="fullname" type="text"
                       class="validate"
                       value="<%= user.getName() %>">
                <label for="fullname">Full name</label>
            </div>
            <div class="input-field col s6">
                <input type="date" class="date"
                       name="date_of_birth"
                       value="<%= user.getDateOfBirth().toLocalDate() %>">
                <label>Date of Birth</label>
            </div>
        </div>

        <div class="row">
            <div class="input-field col s6">
                <input id="new_email" name="email" type="text"
                       class="validate"
                       value="<%= user.getEmail() %>">
                <label for="new_email">Email</label>
            </div>
            <div class="input-field col s6">
                <input id="address" class="validate" type="text"
                       name="address"
                       value="<%= user.getAddress() %>">
                <label>Address</label>
            </div>
        </div>

        <div class="row">
            <div class="input-field col s6">
                <input placeholder="Current Password" id="password"
                       name="current_password" type="password"
                       class="validate">
                <label for="password">Enter Current Password</label>
            </div>
            <div class="input-field col s6">
                <input placeholder="New Password" id="new_password"
                       name="new_password" type="password"
                       class="validate">
                <label for="new_password">Enter New Password</label>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="submit"
                class="waves-effect waves-light btn xyz-button"
                name="change-page-size">
            Update
        </button>
    </div>
</form>

