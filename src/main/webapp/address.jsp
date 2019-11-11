<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
<div class="container">
    <div class="row">
        <div id="address-component-container" class="col s6 push-s3 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <h3>Address Lookup (Testing)</h3>
                </div>
            </div>
            <div class="row">
                <form method="post" action="${pageContext.request.contextPath}/api/addressLookup.do">
                    <input type="hidden" name="action" value="lookup"/>

                    <div class="col s12">
                        <div class="row">
                            <div class="col s4">
                                <div class="input-field">
                                    <input name="houseno" id="houseno" type="text" value="<%=request.getParameter("houseno")%>"
                                           required/>
                                    <label for="houseno">House Number</label>
                                </div>
                            </div>
                            <div class="col s5">
                                <div class="input-field">
                                    <input name="postcode" id="postcode" type="text" value="<%=request.getParameter("postcode")%>"
                                           required/>
                                    <label for="postcode">Postal Code</label>
                                </div>
                            </div>
                            <div class="col s3 text-center">
                                <div style="padding-top: 30px;">
                                    <button id="register-button"
                                            class="waves-effect btn secondary-content left-align"
                                            type="submit" name="lookup">
                                        Lookup
                                    </button>
                                </div>
                            </div>
                        </div>
                        <% if (request.getAttribute("addressData") != null) { %>
                            We did a request for addressData:<br />
                            <% for (String address : (List<String>) request.getAttribute("addressData")) { %>
                                <%=address%><br />
                            <% } %>
                        <% } %>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>