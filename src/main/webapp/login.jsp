<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="container">
    <div class="row no-padding">
        <div class="nice-container col s6 push-s3 card horizontal large">
            <div class="card-image">
                <img />
                <span class="card-title">XYZ Association</span>
            </div>
            <div class="card-stacked">
                <div class="card-content">
                    <div class="card-title">
                        Login
                    </div>
                    <div class="row no-after">
                        <div class="col s12">
                            <form method="post" action="login">
                                <div class="row">
                                    <div class="input-field col s12">
                                        <i class="material-icons prefix">person</i>
                                        <input class="validate" name="username" id="username"
                                               type="text" autocomplete="off" placeholder=""
                                               required />
                                        <label for="username" data-error="wrong" data-success="right">
                                            Username
                                        </label>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="input-field col s12">
                                        <i class="material-icons prefix">lock_outline</i>
                                        <input name="password" placeholder=""
                                               id="password" type="password" minlength="3"
                                               required />
                                        <label for="password">Password</label>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="input-field col s12">
                                        <button type="submit"
                                                class="btn waves-effect waves-light col s12">
                                            Login
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="card-action">
                    <a href="${pageContext.request.contextPath}/register">
                        Register for an account
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
