FROM jenkins/jenkins:2.190.1-alpine
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Djava.awt.headless=true -Dhudson.model.DirectoryBrowserSupport.CSP=\"default-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self' 'unsafe-inline' data:;\""
COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
COPY --chown=jenkins:jenkins jenkins.model.JenkinsLocationConfiguration.xml /var/jenkins_home/jenkins.model.JenkinsLocationConfiguration.xml
COPY --chown=jenkins:jenkins esd-job.xml /var/jenkins_home/jobs/esd/config.xml
