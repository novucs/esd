<div>
    <form class="row" method="post" action="" role="form">
        <div class="col s12 rounded-container info-section">
            <div class="row">
                <div class="col s12">
                    <h4>User Info</h4>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <input name="name" id="name" type="text" class="validate"
                           value="${user.name}" required disabled>
                    <label for="name">Name</label>
                </div>
                <div class="input-field col s6">
                    <input name="date_of_birth" id="date_of_birth" type="date" class="validate"
                           value="${user.dateOfBirth}" required disabled>
                    <label class="active" for="date_of_birth">Date of Birth</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <input name="email" id="email" type="email" class="validate"
                           value="${user.email}" required disabled>
                    <label for="email">Email</label>
                </div>
            </div>
        </div>
        <hr/>
        <div class="col s12 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <ul class="tabs">
                        <li class="tab col s4"><a class="active" href="#membership">Membership</a></li>
                        <li class="tab col s4"><a href="#claims">Claims</a></li>
                        <li class="tab col s4"><a href="#log">Log</a></li>
                    </ul>
                </div>
                <div id="membership" class="col s12">Membership Details</div>
                <div id="claims" class="col s12">Claim Details</div>
                <div id="log" class="col s12">User Log</div>
            </div>
        </div>
        <hr/>
    </form>
</div>
<script src="${pageContext.request.contextPath}/js/admin.viewuser.js"></script>
