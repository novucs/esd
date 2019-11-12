<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="row dashboard-container rounded-container">
    <div class="col s12">
        <!-- Main dashboard items -->
        <div class="row">
            <div class="col s12 m4">
                <t:dashboardcard title="Outstanding Member Applications"
                    content="<%=("There are currently " +
                        ((String) request.getAttribute("outstandingMemberApplications"))
                        + " membership requests pending review."
                        )%>"
                image_url="${pageContext.request.contextPath}/assets/outstanding-apps-card.svg"/>
            </div>
            <div class="col s12 m4">
                <t:dashboardcard title="Number of Members"
                    content="<%=("There are currently " +
                        ((String) request.getAttribute("currentMembers"))
                        + " members signed up to XYZ Drivers Association.")%>"
                     image_url="${pageContext.request.contextPath}/assets/all-members-card.png"/>
            </div>
            <div class="col s12 m4">
                <t:dashboardcard title="Outstanding Balances"
                     content="<%=("There are currently " +
                        ((String) request.getAttribute("outstandingBalances"))
                        + " members with outstanding balances.")%>"
                     image_url="${pageContext.request.contextPath}/assets/balances-card.svg"/>
            </div>
        </div>
        <!-- Sub dashboard items -->
        <div class="row">

        </div>
    </div>
</div>
<div class="fixed-action-btn toolbar">
    <a class="btn-floating btn-large black">
        <i class="large material-icons">mode_edit</i>
    </a>
    <ul>
        <li>
            <a class="btn-floating black tooltipped"
               href="${pageContext.request.contextPath}/admin/applications"
               data-position="top" data-tooltip="Process Member Applications">
                <i class="material-icons">
                    person_add
                </i>
            </a>
        </li>
        <li>
            <a class="btn-floating black tooltipped"
               href="${pageContext.request.contextPath}/admin/claims"
               data-position="top" data-tooltip="Process Member Claims">
                <i class="material-icons">
                    event_note
                </i>
            </a>
        </li>
        <li>
            <a class="btn-floating black tooltipped"
               href="${pageContext.request.contextPath}/admin/users"
               data-position="top" data-tooltip="View All Members">
                <i class="material-icons">
                    group
                </i>
            </a>
        </li>
        <li>
            <a class="btn-floating black tooltipped"
               href="${pageContext.request.contextPath}/admin/claims"
               data-position="top" data-tooltip="View all claims">
                <i class="material-icons">
                    poll
                </i>
            </a>
        </li>
        <li>
            <a class="btn-floating black tooltipped"
               data-position="top" data-tooltip="Suspend/Renew Membership">
                <i class="material-icons">
                    done_all
                </i>
            </a>
        </li>
        <li>
            <a class="btn-floating black tooltipped"
               data-position="top" data-tooltip="Report annual turnover">
                <i class="material-icons">
                    attach_money
                </i>
            </a>
        </li>
    </ul>
</div>
<script src="${pageContext.request.contextPath}/js/admin.dashboard.js"></script>