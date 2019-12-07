<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="formName" required="true" type="java.lang.String"%>

<select name="page-size" id="page-size" form="${formName}">
    <c:forEach begin="0" end="${fn:length(pageSizes) - 1}" varStatus="loop">
        <c:choose>
            <c:when test="${pageSizes[loop.index] == ps}">
                <option selected value="${pageSizes[loop.index]}">
                        ${pageSizes[loop.index]}
                </option>
            </c:when>
            <c:otherwise>
                <option value="${pageSizes[loop.index]}">
                        ${pageSizes[loop.index]}
                </option>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</select>
<label for="page-size">Page Size</label>