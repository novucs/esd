<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/pagination" %>
<script src="${pageContext.request.contextPath}/js/editpayment.js"></script>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div>
    <div class="row no-bottom-margin">
        <div class="col s3 input-field">
            <label for="search-payment-query">Filter</label>
            <input type="text" id="search-payment-query" placeholder="Search payments..."
                   name="search-payment-query" form="search-form"/>
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
                        <th>Manage Status</th>
                    </tr>
                    <c:forEach var="payment" items="${payments}">
                        <tr>
                            <td>${payment.id}</td>
                            <td>${payment.userId}</td>
                            <td>&pound;&nbsp;<fmt:formatNumber type="number" maxFractionDigits="2" value="${payment.getAmount()}"/></td>
                            <td>${payment.stripeId != null ? "Card Payment" : payment.reference}</td>
                            <td class="managed-icon">
                                <c:choose>
                                    <c:when test="${payment.approvalStatus == 'VERIFIED'}">
                                        <i class="material-icons small">
                                            check
                                        </i>
                                    </c:when>
                                    <c:when test="${payment.approvalStatus == 'PENDING'}">
                                        <i class="material-icons small">
                                            av_timer
                                        </i>
                                    </c:when>
                                    <c:when test="${payment.approvalStatus == 'DECLINED'}">
                                        <i class="material-icons small">
                                            pan_tool
                                        </i>
                                    </c:when>
                                </c:choose>
                            </td>
                             <td>
                                <div id="managePayment">
                                    <form id="verifyPayment" name="verifyPayment" method="post" action="editpayment">
                                        <input
                                              type="hidden"
                                              id="verify-payment"
                                              name="verify-payment"
                                              value="${true}"/>
                                        <input
                                              type="hidden"
                                              id="payment-id"
                                              name="paymentId"
                                              value="${payment.id}"/>
                                        <a class="managed-icon" href="javascript: verifyPayment()" disabled="${payment.approvalStatus == 'VERIFIED'}">
                                           <i class="material-icons small">
                                               check
                                           </i>
                                        </a>
                                    </form>
                                  <form id="declinePayment" name="declinePayment" method="post" action="editpayment">
                                        <input
                                              type="hidden"
                                              id="decline-payment"
                                              name="decline-payment"
                                              value="${true}"/>
                                        <input
                                              type="hidden"
                                              id="payment-id"
                                              name="paymentId"
                                              value="${payment.id}"/>
                                        <a class="managed-icon" href="javascript: declinePayment()"  disabled="${payment.approvalStatus == 'REJECTED'}">
                                           <i class="material-icons small">
                                               pan_tool
                                           </i>
                                        </a>
                                        </form>
                                        <form id="pendingPayment" name="pendingPayment" method="post" action="editpayment">
                                        <input
                                              type="hidden"
                                              id="pending-payment"
                                              name="pending-payment"
                                              value="${true}"/>
                                        <input
                                              type="hidden"
                                                  id="payment-id"
                                                  name="paymentId"
                                                  value="${payment.id}"/>
                                        <a href="javascript: pendingPayment()"  disabled="${payment.approvalStatus == 'PENDING'}">
                                           <i class="material-icons small">
                                               av_timer
                                           </i>
                                        </a>
                                    </form>
                                </div>
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
                <p:pagination />
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/pagination.js"></script>
