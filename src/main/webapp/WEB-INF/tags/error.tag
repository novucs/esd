<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="title" type="java.lang.String"%>
<%@attribute name="message" type="java.lang.String"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>${empty title ? "Unknown Error" : title}</title>
    <t:head />
  </head>
  <body>
    <t:navigation isMember="false" isAdmin="false" hasSession="false" />
    <main>
      <div>
        <div class="container align-center">
          <div class="card">
            <div class="card-content">
              <div class="text-center">
                <p>
                  ${empty message ? "An unknown error has occured." : message}
                </p>
                <br/>
                <a href="javascript:window.history.back();" class="waves-effect waves-light btn-large">Go Back</a>
                <a href="${pageContext.request.contextPath}/" class="waves-effect waves-light btn-large">Homepage</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <t:footer />
  </body>
</html>
