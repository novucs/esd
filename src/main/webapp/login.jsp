<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="css/login.css">
<div class="container">
    <div class="row">
        <div id="login-component-container" class="col s6 push-s3 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <h3 id="login-title">
                        Login
                    </h3>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    <form method="post" action="login">
                        <div class="row">
                            <div class="col s12">
                                <div class="input-field">
                                    <input name="username" placeholder="" id="username" type="email"
                                           class="validate"
                                           autocomplete="off" required/>
                                    <label for="username">Username</label>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col  s12">
                                <div class="input-field">
                                    <input placeholder="" name="password" id="password"
                                           type="password" class="validate"
                                           minlength="3" required/>
                                    <label for="password">Password</label>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col s12 left">
                                <button id="login-button"
                                        class="waves-effect btn secondary-content left-align"
                                        type="submit" name="login">
                                    Submit
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    Not have an account? Register <a href="register">here</a>.
                </div>
            </div>
        </div>
    </div>

</div>
