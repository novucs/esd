<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/pagination" %>

<div>
    <form method="post" id="process-form">
        <div class="fixed-action-btn">
            <a class="btn-floating btn-large blue-grey">
                <i class="large material-icons">mode_edit</i>
            </a>
            <ul>
                <li><a href="javascript:{}" id="approve-all" class="btn-floating green"><i
                        class="material-icons">done_all</i></a></li>
                <li><a href="javascript:{}" id="approve-selection" class="btn-floating lime"><i
                        class="material-icons">done</i></a></li>
                <li><a href="javascript:{}" id="deny-selection" class="btn-floating amber"><i
                        class="material-icons">clear</i></a></li>
                <li><a href="javascript:{}" id="deny-all" class="btn-floating red"><i
                        class="material-icons">gavel</i></a></li>
            </ul>
        </div>
        <div class="row">
            <div class="col s12">
                <div id="applications-table">
                    <table class="highlight">
                        <tr>
                            <th>Select</th>
                            <th>Application ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Address</th>
                            <th>Edit</th>
                        </tr>
                        <c:forEach var="result" items="${results}">
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" class="filled-in"
                                               name="application-id"
                                               value="${result.application.id}"/>
                                        <span></span>
                                    </label>
                                </td>
                                <td>${result.application.id}</td>
                                <td>${result.user.username}</td>
                                <td>${result.user.email}</td>
                                <td>${result.user.name}</td>
                                <td>${result.user.address}</td>
                                <td class="managed-icon">
                                    <a href="${pageContext.request.contextPath}/admin/edituser?userId=${result.user.id}">
                                        <i class="material-icons small">edit</i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </div>
    </form>
    <div class="row no-bottom-margin">
        <div class="col s2 input-field">
            <p:pagesize formName="filter-form"/>
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
                <p:pagination/>
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/admin.manageapplications.js"></script>
<script src="${pageContext.request.contextPath}/js/pagination.js"></script>