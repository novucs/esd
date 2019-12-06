<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                        <th>Email</th>
                        <th>Name</th>
                        <th>Status</th>
                        <th>View</th>
                        <th>Edit</th>
                    </tr>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.email}</td>
                            <td>${user.name}</td>
                            <td>${user.status}</td>
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
            <select name="page-size" id="page-size" form="filter-form">
                <c:forEach begin="0" end="${fn:length(pageSizes) - 1}" varStatus="loop">
                    <c:choose>
                        <c:when test="${pageSizes[loop.index] == ps}">
                            <option selected value="${pageSizes[loop.index]}">
                                    ${pageSizes[loop.index]}
                            </option>
                        </c:when>
                        <c:otherwise>
                            <option value="${pageSizes[loop.index]}">
                                    ${pageSizes[loop.index]}
                            </option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
            <label for="page-size">Page Size</label>
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
                <c:choose>
                    <c:when test="${pn == 1 || maxPages == 1}">
                        <li class="disabled"><a href="#!"><i class="material-icons">chevron_left</i></a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="waves-effect">
                            <a href="${pageContext.request.contextPath}/admin/users?pn=${pn - 1}">
                                <i class="material-icons">
                                    chevron_left
                                </i>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
                <c:forEach begin="1" end="${maxPages}" varStatus="loop">
                    <c:choose>
                        <c:when test="${loop.index == pn}">
                            <li class="active">
                                <a href="${pageContext.request.contextPath}/admin/users?pn=${loop.index}">
                                        ${loop.index}
                                </a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="waves-effect">
                                <a href="${pageContext.request.contextPath}/admin/users?pn=${loop.index}">
                                        ${loop.index}
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:choose>
                    <c:when test="${param.pn == maxPages || maxPages == 1}">
                        <li class="disabled"><a href="#!"><i
                                class="material-icons">chevron_right</i></a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="waves-effect"><a
                                href="${pageContext.request.contextPath}/admin/users?pn=${pn + 1}"><i
                                class="material-icons">chevron_right</i></a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/admin.manageusers.js"></script>
