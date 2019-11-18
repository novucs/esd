<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>

<div class="container">
    <div class="row">
        <div id="make-claim-component-container" class="col s6 push-s3 rounded-container">
            <div class="col s12 center-align">
                <h4>Make a new claim</h4>
            </div>
            <div class="row">
                <div class="col s12 center-align">
                    <t:makeclaim />
                </div>
            </div>
        </div>
    </div>
</div>