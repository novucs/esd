<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="registerStatus" type="java.lang.String"%>

<c:choose>
    <c:when test="${registerStatus == 'success'}">
      <t:registersuccess />
   </c:when>
    <c:when test="${registerStatus == 'fail'}">
      <t:registerfail />
   </c:when>
   <c:otherwise>
      <t:registerform />
   </c:otherwise>
</c:choose>
