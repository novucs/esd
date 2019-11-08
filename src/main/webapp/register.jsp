<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<body>
<link rel="stylesheet" href="css/register.css">
<div class="container">
    <div class="row">
        <div id="register-component-container" class="col s6 push-s3 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <h3>Register</h3>
                </div>
            </div>
            <div class="row">
                <form method="post" action="register">
                    <div class="col s12">
                        <div class="row">
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="full-name" id="full-name"
                                           type="text" required/>
                                    <label for="full-name">Full Name</label>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="username" id="username" type="email"
                                           class="validate" required/>
                                    <label for="username">Email Address</label>
                                </div>
                            </div>
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="dob" id="dob" type="date"
                                           class="validate" required/>
                                    <label for="dob">Date of Birth</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col s12">
                        <div class="row">
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="address-name" id="address-name"
                                           type="text" required/>
                                    <label for="address-name">House Name</label>
                                </div>
                            </div>
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="address-street" id="address-street"
                                           type="text" required/>
                                    <label for="address-street">Street</label>
                                </div>
                            </div>
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="address-city" id="address-city"
                                           type="text" required/>
                                    <label for="address-city">City</label>
                                </div>
                            </div>

                            <div class="col s6">
                                <div class="input-field">
                                    <input name="address-county" id="address-county"
                                           type="text" required/>
                                    <label for="address-county">County</label>
                                </div>
                            </div>
                            <div class="col s6">
                                <div class="input-field">
                                    <input name="address-postcode"
                                           id="address-postcode" type="text" class=validate
                                           minLength=7 required/>
                                    <label for="address-postcode">Postcode</label>
                                </div>
                            </div>
                            <div class="col s6">
                                <div style="padding-top: 30px;">
                                    <button id="register-button"
                                            class="waves-effect btn secondary-content left-align"
                                            type="submit" name="register">
                                        Register
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col s12 center-align">
                        Already have an account? <a href="login">Login here</a>.
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>