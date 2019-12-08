<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/pagination" %>

<div>
    <div class="row no-bottom-margin">
        <div class="col s3 input-field">
            <label for="search-users-query">Filter</label>
            <input type="text" id="search-users-query" placeholder="Search for users.."
                   name="search-users-query" form="search-form"/>
        </div>
        <div class="col s2 input-field">
            <form method="post" id="search-form">
                <button type="submit" class="waves-effect btn waves-light xyz-button">
                    Search
                </button>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col s12">
            <div id="users-table">
                <table class="highlight">
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>View</th>
                        <th>Edit</th>
                    </tr>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.username}</td>
                            <td>${user.email}</td>
                            <td>${user.name}</td>
                            <td class="managed-icon">
                                <a href="${pageContext.request.contextPath}/admin/viewuser?userId=${user.id}">
                                    <i class="material-icons small">
                                        search
                                    </i>
                                </a>
                            </td>
                            <td class="managed-icon">
                                <a href="${pageContext.request.contextPath}/admin/edituser?userId=${user.id}">
                                    <i class="material-icons small">
                                        edit
                                    </i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
    <div class="row no-bottom-margin">
        <div class="col s2 input-field">
            <p:pagesize formName="filter-form" />
        </div>
        <div class="col s1 input-field">
            <form method="post" id="filter-form">
                <button type="submit" class="waves-effect waves-light btn xyz-button"
                        name="change-page-size">
                    Update
                </button>
            </form>
        </div>
    </div>
    <div class="row" class="pagination-control">
        <div class="col s12 center-align">
            <ul class="pagination">
                <p:pagination path="${pageContext.request.contextPath}/admin/users" />
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/pagination.js" type="text/javascript"></script>
