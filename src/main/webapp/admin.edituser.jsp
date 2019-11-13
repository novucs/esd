<%@ page import="net.novucs.esd.model.User" %>
<% User editUser = (User) request.getAttribute("editUser"); %>
<% Boolean hasUpdated = (Boolean) request.getAttribute("updated"); %>
<% String hasError = (String) request.getAttribute("error"); %>
<% if (hasUpdated != null) { %>
<div class="row">
    <div class="col s12">
        <div class="card <%= hasUpdated ? "green" : "red" %>">
            <div class="card-content">
                <%= hasUpdated ? "This user has been successfully updated." : hasError %>
            </div>
        </div>
    </div>
</div>
<% } %>
<div class="row">
    <form class="col s12" method="post" action="" role="form">
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
        <div class="row">
            <div class="input-field col s6">&nbsp;</div>
            <div class="input-field col s6 text-right">
                <button class="btn btn-large btn-register waves-effect waves-light"
                        type="submit" name="action">Save
                    <i class="material-icons right">done</i>
                </button>
            </div>
        </div>
    </form>
</div>
