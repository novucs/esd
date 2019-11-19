<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<% User userName = ((Session) request.getAttribute("session")).getUser(); %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<table>
    <thead>
    <tr>
        <th>User ID</th>
        <th>Full Name</th>
        <th>Email</th>
        <th>D.O.B</th>
        <th>Address</th>
        <th class="collection-header"><%= userName.getName() %>'s Role</th>
        <th>Edit</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td><%= userName.getId() %>
        </td>
        <td><%= userName.getName() %>
        </td>
        <td><%= userName.getEmail() %>
        </td>
        <td><%= userName.getDateOfBirth().toLocalDate() %>
        </td>
        <td><%= userName.getAddress() %>
        </td>
        <td>
            <c:forEach var="role" items="${session.getRoles()}">
                ${role.name}
            </c:forEach>
        </td>
        <td>
            <!-- Modal Trigger -->
            <button data-target="modal1" class="btn modal-trigger">Edit account details</button>

            <!-- Modal Structure -->
            <form method="post" action="">
                <div id="modal1" class="modal">
                    <card>
                        <div>
                            <h3><%= userName.getName() %>'s New Profile</h3>
                            <div class="row">
                                <div class="col s24">
                                    <div class="row">
                                        <div class="input-field col s6">
                                            <input placeholder="Full Name" name="fullname"
                                                   id="fullname" type="text"
                                                   class="validate" required>
                                            <label for="fullname">Full name</label>
                                        </div>
                                        <div class="input-field col s6">
                                            <input type="date" class="date"
                                                   name="date_of_birth"
                                                   required>
                                            <label>Date of Birth</label>
                                        </div>
                                        <div class="input-field col s6">
                                            <input id="new_email" name="email" type="text"
                                                   class="validate" required>
                                            <label for="new_email">Email</label>
                                        </div>
                                        <div class="input-field col s6">
                                            <input id="address" class="validate" type="text"
                                                   name="address"
                                                   required>
                                            <label>Address</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit"
                                        class="waves-effect waves-light btn xyz-button"
                                        name="change-page-size">
                                    Update
                                </button>
                            </div>
                        </div>
                    </card>
                </div>
            </form>
        </td>
    </tr>
    </tbody>
</table>
<div>
    <div>
        <h4>Password Reset</h4>
    </div>
    <div class="input-field col s6">
        <input placeholder="Current Password" id="password" type="text" class="validate">
        <label for="password">Enter Current Password</label>
    </div>
    <div class="input-field col s6">
        <input placeholder="New Password" id="new_password" type="text" class="validate">
        <label for="new_password">Enter New Password</label>
    </div>
    <div>
        <button class="btn waves-effect waves-light" type="submit" name="action" onclick="">Change
            Password
            <i class="material-icons right">send</i>
        </button>
    </div>
</div>

</html>
