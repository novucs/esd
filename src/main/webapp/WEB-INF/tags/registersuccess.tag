<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="username" type="java.lang.String" %>
<%@attribute name="email" type="java.lang.String" %>
<%@attribute name="password" type="java.lang.String" %>

<div class="nice-container col s8 push-s2 card horizontal">
    <div class="card-image">
        <img/>
        <span class="card-title">Welcome!</span>
    </div>
    <div class="card-stacked">
        <div class="card-content">
            <div class="card-title">
                Success!
            </div>
            <div class="row">
                <div class="col s12">
                    You have registered an account with the email <strong>${email}</strong>.
                    <br/><br/>
                    <table>
                        <tr>
                            <td class="w100px bold-text">Username</td>
                            <td class="c-pointer">
                                <span>${username}</span>
                                <small class="f-right">(Click to copy)</small>
                            </td>
                        </tr>
                        <tr>
                            <td class="w100px bold-text">Password</td>
                            <td class="c-pointer">
                                <span>${password}</span>
                                <small class="f-right">(Click to copy)</small>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                    <span class="red-text">
                                        The above details are your generated credentials.
                                    </span>
                                <a href="${pageContext.request.contextPath}/login">
                                    Login here.
                                </a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/register.success.js" />