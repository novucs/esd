<%@tag description="Generic Header Contents" pageEncoding="UTF-8" %>
<%@attribute name="title" required="true" type="java.lang.String" %>
<%@attribute name="label" required="true" type="java.lang.String" %>
<%@attribute name="icon" required="true" type="java.lang.String" %>
<%@attribute name="labelLink" type="java.lang.String" %>
<%@attribute name="colour" type="java.lang.String" %>
<%@attribute name="size" type="java.lang.String" %>
<div class="col ${empty size ? "s4" : size}">
    <div class="card ${empty colour ? "black" : colour} white-text">
        <div class="card-content valign-wrapper">
            <div class="card-text">
                <h6>${title}</h6>
                <p>
                    <% if (labelLink == null) { %>
                        ${label}
                    <% } else { %>
                        <a href="${labelLink}">${label}</a>
                    <% } %>
                </p>
            </div>
            <div class="card-icon">
                <i class="material-icons medium valign">${icon}</i>
            </div>
        </div>
    </div>
</div>