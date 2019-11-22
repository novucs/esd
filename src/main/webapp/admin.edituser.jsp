<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.model.Role" %>
<% User editUser = (User) request.getAttribute("editUser"); %>
<% List<Role> editUserRoles = (List<Role>) request.getAttribute("editUserRoles"); %>
<% Boolean hasUpdated = (Boolean) request.getAttribute("updated"); %>
<% String hasError = (String) request.getAttribute("error"); %>
<% String hasNotice = (String) request.getAttribute("notice"); %>
<% if (hasUpdated != null) { %>
<div class="row">
    <div class="col s12">
        <div class="card <%= hasUpdated ? "green" : "red" %>">
            <div class="card-content">
                <%= hasUpdated ? "This user has been successfully updated." : hasError %>
            </div>
        </div>
        <% if (hasNotice != null && !hasNotice.isEmpty()) { %>
        <div class="card orange">
            <div class="card-content">
                <%=hasNotice%>
            </div>
        </div>
        <% } %>
    </div>
</div>
<% } %>
<div class="row">
    <form class="col s12" method="post" action="" role="form">
        <div class="row no-bottom-margin">
            <div class="col s12">
                <h4>User Settings</h4>
                <p>
                    Any changes to the below fields will be updated when saving this user.
                </p>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <input name="name" id="name" type="text" class="validate"
                       value="<%=editUser.getName()%>" required>
                <label for="name">Name</label>
            </div>
            <div class="input-field col s6">
                <input name="date_of_birth" id="date_of_birth" type="date" class="validate"
                       value="<%=editUser.getDateOfBirth().toLocalDate()%>" required>
                <label class="active" for="date_of_birth">Date of Birth</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12">
                <input name="email" id="email" type="email" class="validate"
                       value="<%=editUser.getEmail()%>" required>
                <label for="email">Email</label>
            </div>
        </div>
        <hr/>
        <div class="row no-bottom-margin">
            <div class="col s12">
                <h4>Change User Password</h4>
                <p>
                    Leave the fields below blank if you do not want to update the user's password
                </p>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12 m6">
                <input name="password1" id="password1" type="password" value="">
                <label for="password1">Password</label>
            </div>
            <div class="input-field col s12 m6">
                <input name="password2" id="password2" type="password" value="">
                <label for="password2" data-error="Passwords do not match">Repeat Password</label>
            </div>
        </div>
        <hr/>
        <div class="row no-bottom-margin">
            <div class="col s12">
                <h4>User Roles</h4>
                <p>
                    This is a multi-select field, Ctrl + Click to select or deselect roles.
                </p>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12 m12">
                <select name="roles" id="roles" multiple>
                    <% for (Role role : ((List<Role>) request.getAttribute("availableRoles"))) { %>
                    <option value="<%=role.getId()%>" <%=(editUserRoles.contains(role.getId())
                            ? "selected" : "")%>><%=role.getName()%>
                    </option>
                    <% } %>
                </select>
                <label for="roles">Roles</label>
            </div>
        </div>
        <hr/>
        <div class="row">
            <div class="input-field col s6">&nbsp;</div>
            <div class="input-field col s6 text-right">
                <button class="btn btn-large btn-register waves-effect waves-light"
                        type="submit" name="action">
                    Save
                    <i class="material-icons right">done</i>
                </button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/js/admin.edituser.js"></script>