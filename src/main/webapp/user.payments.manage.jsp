<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/pagination" %>
<script src="${pageContext.request.contextPath}/js/cancelclaim.js"></script>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div>
    <div class="row no-bottom-margin">
        <div class="col s3 input-field">
            <label for="search-users-query">Filter</label>
            <input type="text" id="search-users-query" placeholder="Search your claims..."
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
                        <th>Payment ID</th>
                        <th>User ID</th>
                        <th>Amount</th>
                        <th>Reference</th>
                        <th>Status</th>
                    </tr>
                    <c:forEach var="payment" items="${payments}">
                        <tr>
                            <td>${payment.id}</td>
                            <td>${payment.userId}</td>
                            <td>&pound;&nbsp;<fmt:formatNumber type="number" maxFractionDigits="2" value="${payment.getAmount()}"/></td>
                            <td>${payment.stripeId != null ? "Card Payment" : payment.reference}</td>
                            <td><i class="material-icons small">${payment.approvalStatus == 'VERIFIED' ? 'check' : 'av_timer'}</i></td>
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
                <p:pagination />
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/admin.manageusers.js"></script>
