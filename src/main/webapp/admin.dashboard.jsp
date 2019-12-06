<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<% Session userSession = ((Session) request.getAttribute("session")); %>
<% User user = userSession.getUser(); %>
<div>
    <div class="row no-bottom-margin">
        <div class="col s12">
            <div class="row no-bottom-margin">
                <div class="col s12" %>
                    <div class="card black white-text">
                        <div class="card-content valign-wrapper">
                            <div class="card-text">
                                <h6>
                                    Welcome back, <strong><%=user.getName()%></strong>
                                </h6>
                            </div>
                            <div class="card-icon">
                                <i class="material-icons medium valign">face</i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row no-bottom-margin">
        <div class="col s12">
            <!-- Main dashboard items -->
            <div class="row no-bottom-margin">
                <t:tile title="<%=String.valueOf(request.getAttribute("outstandingMemberApplications"))%>"
                        label="Outstanding Membership Applications"
                        icon="person_add"/>
                <t:tile title="<%=String.valueOf(request.getAttribute("outstandingBalances"))%>"
                        label="Outstanding Claims"
                        icon="event_note" colour="red"/>
                <t:tile title="<%=String.valueOf(request.getAttribute("currentMembers"))%>"
                        label="Number of Members"
                        icon="people_outline"/>
                <t:tile title="<%=String.valueOf(request.getAttribute("monthlyClaimCost"))%>"
                        label="Monthly claim cost"
                        icon="attach_money"/>
                <t:tile title="<%=String.valueOf(request.getAttribute("quarterlyClaimCost"))%>"
                        label="Quarterly claim cost"
                        icon="date_range"/>
                <a class="reporting" href="${pageContext.request.contextPath}/admin/reporting"">
                    <t:tile title=""
                            colour="black reporting"
                            label="Reporting"
                            icon="insert_chart"/>
                </a>
            </div>
        </div>
    </div>
</div>
