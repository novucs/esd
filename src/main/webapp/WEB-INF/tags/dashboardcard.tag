<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="title" type="java.lang.String"%>
<%@attribute name="content" type="java.lang.String"%>
<%@attribute name="image_url" type="java.lang.String"%>

<div class="card large">
    <div class="card-image waves-effect waves-block waves-light">
        <img class="activator" src="${empty image_url ? "./" : image_url}">
    </div>
    <div class="card-content">
        <span class="card-title activator grey-text text-darken-4">
            ${empty title ? "MISSING_TITLE" : title}
            <i class="material-icons right">
                more_vert
            </i>
        </span>
    </div>
    <div class="card-reveal center-align">
        <span class="card-title grey-text text-darken-4">
                <i class="material-icons right">
                    close
                </i>
        </span>
        <p class="dashboard-card-text-content">
            ${empty content ? "MISSING_CONTENT" : content}
        </p>
    </div>
</div>
