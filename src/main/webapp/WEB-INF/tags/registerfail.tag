<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="nice-container col s8 push-s2 card horizontal">
    <div class="card-image">
        <img />
        <span class="card-title">Error</span>
    </div>
    <div class="card-stacked">
        <div class="card-content">
            <div class="card-title">
                Unsuccessful :(
            </div>
            <div class="row">
                <div class="col s12">
                    It looks like an account already exists with some of the entered
                    credentials, you can attempt to
                    <a href="${pageContext.request.contextPath}/login">login here</a> or you can
                    <a href="javascript:window.history.back();">go back to the registration</a>
                    to update your details.
                </div>
            </div>
        </div>
    </div>
</div>
