<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="row">
    <div id="register-fail-component-container" class="col s6 push-s3 rounded-container">
        <div class="row">
            <div class="col s12 center-align">
                <h4>Registration Unsuccessful</h4>
            </div>
        </div>
        <div class="row">
            <div class="col s12 center-align">
                It looks like an account already exists with some of the entered
                credentials, you can attempt to
                <a href="${pageContext.request.contextPath}/login">login here</a> or you can
                <a href="javascript:window.history.back();">go back to the registration</a>
                to update your details.
            </div>
        </div>
    </div>
</div>
