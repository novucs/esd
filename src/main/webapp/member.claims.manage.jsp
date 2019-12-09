<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/pagination" %>
<script src="${pageContext.request.contextPath}/js/cancelclaim.js"></script>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div>
    <div class="row no-bottom-margin">
        <div class="col s3 input-field">
            <label for="search-claims-query">Filter</label>
            <input type="text" id="search-claims-query" placeholder="Search your claims..."
                   name="search-claims-query" form="search-form"/>
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
                        <th>Claim ID</th>
                        <th>Membership ID</th>
                        <th>Amount</th>
                        <th>Rationale</th>
                        <th>Status</th>
                        <th>Edit</th>
                        <th>Cancel</th>
                    </tr>
                    <c:forEach var="claim" items="${claims}">
                        <tr>
                            <td>${claim.id}</td>
                            <td>${claim.membershipId}</td>
                            <td>&pound;&nbsp;<fmt:formatNumber type="number" maxFractionDigits="2" value="${claim.getAmount()}"/></td>
                            <td>${claim.rationale}</td>
                            <td>${claim.status}</td>
                            <c:choose>
                                <c:when test="${claim.status == 'PENDING'}">
                                    <td class="managed-icon">
                                        <a href="${pageContext.request.contextPath}/member/editclaim?claimId=${claim.id}">
                                            <i class="material-icons small">
                                                edit
                                            </i>
                                        </a>
                                    </td>
                                    <td class="managed-icon">
                                        <form id="cancelClaim" name="cancelClaim" method="post" action="editclaim">
                                            <input
                                                  type="hidden"
                                                  id="cancel-claim"
                                                  name="cancel-claim"
                                                  value="${true}"/>
                                            <input
                                                  type="hidden"
                                                  id="claim-id"
                                                  name="claimId"
                                                  value="${claim.id}"/>
                                            <a href="javascript: submitForm()">
                                               <i class="material-icons small">
                                                   delete
                                               </i>
                                            </a>
                                        </form>
                                    </td>
                                </c:when>
                                <c:when test="${claim.status == 'APPROVED'}">
                                    <td class="managed-icon">
                                        <i class="material-icons small">
                                            done
                                        </i>
                                    </td>
                                    <td class="managed-icon">
                                         <i class="material-icons small">
                                             delete_outline
                                         </i>
                                    </td>
                                </c:when>
                                <c:when test="${claim.status == 'REJECTED'}">
                                    <td class="managed-icon">
                                        <i class="material-icons small">
                                            pan_tool
                                        </i>
                                    </td>
                                    <td class="managed-icon">
                                         <i class="material-icons small">
                                             delete_outline
                                         </i>
                                    </td>
                                </c:when>
                                <c:when test="${claim.status == 'CANCELLED'}">
                                    <td class="managed-icon">
                                         <i class="material-icons small">
                                             lock
                                         </i>
                                     </td>
                                    <td class="managed-icon">
                                         <i class="material-icons small">
                                             delete_outline
                                         </i>
                                    </td>
                                </c:when>
                            </c:choose>
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
                <p:pagination path="${pageContext.request.contextPath}/members/claims"/>
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/admin.manageusers.js"></script>
