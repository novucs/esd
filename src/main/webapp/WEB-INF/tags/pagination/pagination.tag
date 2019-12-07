<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="pagination">
    <c:choose>
        <c:when test="${pn == 1 || maxPages == 1}">
            <li class="disabled"><a href="#!"><i class="material-icons">chevron_left</i></a>
            </li>
        </c:when>
        <c:otherwise>
            <li class="waves-effect">
                <a href="${pageContext.request.contextPath}/admin/users?pn=${pn - 1}">
                    <i class="material-icons">
                        chevron_left
                    </i>
                </a>
            </li>
        </c:otherwise>
    </c:choose>
    <c:forEach begin="1" end="${maxPages}" varStatus="loop">
        <c:choose>
            <c:when test="${loop.index == pn}">
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/users?pn=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:when>
            <c:otherwise>
                <li class="waves-effect">
                    <a href="${pageContext.request.contextPath}/admin/users?pn=${loop.index}">
                            ${loop.index}
                    </a>
                </li>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:choose>
        <c:when test="${param.pn == maxPages || maxPages == 1}">
            <li class="disabled"><a href="#!"><i
                    class="material-icons">chevron_right</i></a></li>
        </c:when>
        <c:otherwise>
            <li class="waves-effect"><a
                    href="${pageContext.request.contextPath}/admin/users?pn=${pn + 1}"><i
                    class="material-icons">chevron_right</i></a></li>
        </c:otherwise>
    </c:choose>
</ul>
